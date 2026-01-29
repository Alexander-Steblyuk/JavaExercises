package ru.steblyuk.hw.repositories;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSourceUtils;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import ru.steblyuk.hw.exceptions.EntityNotFoundException;
import ru.steblyuk.hw.models.Author;
import ru.steblyuk.hw.models.Book;
import ru.steblyuk.hw.models.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static java.util.Optional.ofNullable;

@Repository
@RequiredArgsConstructor
public class JdbcBookRepository implements BookRepository {
    private static final String ID_PARAM_NAME = "id";
    private static final String BOOK_ID_PARAM_NAME = "bookId";
    private static final String TITLE_PARAM_NAME = "title";
    private static final String AUTHOR_ID_PARAM_NAME = "authorId";

    private static final String BOOK_ID_COLUMN_NAME = "book_id";
    private static final String GENRE_ID_COLUMN_NAME = "genre_id";
    private static final String GENRE_NAME_COLUMN_NAME = "name";
    private static final String ID_COLUMN_NAME = "id";
    private static final String TITLE_COLUMN_NAME = "title";
    private static final String AUTHOR_ID_COLUMN_NAME = "author_id";
    private static final String AUTHOR_FULL_NAME_COLUMN_NAME = "full_name";

    private final GenreRepository genreRepository;
    private final NamedParameterJdbcOperations namedJdbcOperations;

    @Override
    public Optional<Book> findById(long id) {
        SqlParameterSource parameterSource = new MapSqlParameterSource(ID_PARAM_NAME, id);
        var book = namedJdbcOperations.query("select books.id, books.title, books.author_id, authors.full_name, " +
                "genres.id genre_id, genres.name from books " +
                "join authors on books.author_id = authors.id " +
                "join books_genres on books.id = books_genres.book_id " +
                "join genres on genres.id = books_genres.genre_id " +
                "where books.id = :id", parameterSource, new BookResultSetExtractor());
        return ofNullable(book);
    }

    @Override
    public List<Book> findAll() {
        var genres = genreRepository.findAll();
        var books = getAllBooksWithoutGenres();
        var relations = getAllGenreRelations();
        mergeBooksInfo(books, genres, relations);
        return books;
    }

    @Override
    public Book save(Book book) {
        if (book.getId() == 0) {
            return insert(book);
        }
        return update(book);
    }

    @Override
    public void deleteById(long id) {
        SqlParameterSource parameterSource = new MapSqlParameterSource(ID_PARAM_NAME, id);
        namedJdbcOperations.update("delete from books where id = :id", parameterSource);
    }

    private List<Book> getAllBooksWithoutGenres() {
        return namedJdbcOperations.query("select books.id, books.title, books.author_id, authors.full_name" +
                " from books join authors on books.author_id = authors.id", new BookRowMapper());
    }

    private List<BookGenreRelation> getAllGenreRelations() {
        return namedJdbcOperations.query("select book_id, genre_id from books_genres", new BookGenreRelationRowMapper());
    }

    private void mergeBooksInfo(List<Book> booksWithoutGenres, List<Genre> genres,
                                List<BookGenreRelation> relations) {
        Map<Long, Book> booksById = booksWithoutGenres.stream()
                        .collect(Collectors.toMap(Book::getId, Function.identity()));
        Map<Long, Genre> genresById = genres.stream()
                        .collect(Collectors.toMap(Genre::getId, Function.identity()));
        relations.forEach(relation -> mergeBookInfo(relation, genresById, booksById));
    }

    private void mergeBookInfo(BookGenreRelation relation, Map<Long, Genre> genres, Map<Long, Book> books) {
        var book = books.get(relation.bookId);
        var genre = genres.get(relation.genreId);
        if (nonNull(book) && nonNull(genre)) {
            var bookGenres = ofNullable(book.getGenres())
                    .orElseGet(ArrayList::new);
            bookGenres.add(genre);
            book.setGenres(bookGenres);
        }
    }

    private Book insert(Book book) {
        var keyHolder = new GeneratedKeyHolder();
        var bookAuthor = book.getAuthor();
        Map<String, Object> params = Map.of(TITLE_PARAM_NAME, book.getTitle(), AUTHOR_ID_PARAM_NAME, bookAuthor.getId());
        SqlParameterSource parameterSource = new MapSqlParameterSource(params);
        namedJdbcOperations.update("insert into books (title, author_id)  values (:title, :authorId)",
                parameterSource, keyHolder);
        //noinspection DataFlowIssue
        book.setId(keyHolder.getKeyAs(Long.class));
        batchInsertGenresRelationsFor(book);
        return book;
    }

    private Book update(Book book) {
        var bookAuthor = book.getAuthor();
        Map<String, Object> params = Map.of(
                TITLE_PARAM_NAME, book.getTitle(),
                AUTHOR_ID_PARAM_NAME, bookAuthor.getId(),
                ID_PARAM_NAME, book.getId());
        SqlParameterSource parameterSource = new MapSqlParameterSource(params);

        int updated = namedJdbcOperations.update("update books set title = :title, author_id = :authorId " +
                        "where id = :id", parameterSource);
        if (updated < 1) {
            String message = "Failed to update Book(id = %s), because it is not found!".formatted(book.getId());
            throw new EntityNotFoundException(message);
        }

        removeGenresRelationsFor(book);
        batchInsertGenresRelationsFor(book);

        return book;
    }

    private void batchInsertGenresRelationsFor(Book book) {
        var relations = book.getGenres().stream()
                .map(genre -> new BookGenreRelation(book.getId(), genre.getId()))
                .toList();
        SqlParameterSource[] parameterSource = SqlParameterSourceUtils.createBatch(relations);
        namedJdbcOperations.batchUpdate("insert into books_genres values (:bookId, :genreId)", parameterSource);
    }

    private void removeGenresRelationsFor(Book book) {
        SqlParameterSource parameterSource = new MapSqlParameterSource(BOOK_ID_PARAM_NAME, book.getId());
        namedJdbcOperations.update("delete from books_genres where book_id = :bookId", parameterSource);
    }

    private static class BookRowMapper implements RowMapper<Book> {

        @Override
        public Book mapRow(ResultSet rs, int rowNum) throws SQLException {
            long id = rs.getLong(ID_COLUMN_NAME);
            String title = rs.getString(TITLE_COLUMN_NAME);
            long authorId = rs.getLong(AUTHOR_ID_COLUMN_NAME);
            String authorFullName = rs.getString(AUTHOR_FULL_NAME_COLUMN_NAME);
            return new Book(id, title, new Author(authorId, authorFullName), new ArrayList<>());
        }
    }

    // Использовать для findById
    @SuppressWarnings("ClassCanBeRecord")
    @RequiredArgsConstructor
    private static class BookResultSetExtractor implements ResultSetExtractor<Book> {
        BookRowMapper rowMapper = new BookRowMapper();

        @Override
        public Book extractData(ResultSet rs) throws SQLException, DataAccessException {
            Book book = null;
            while (rs.next()) {
                if (isNull(book)) {
                    book = rowMapper.mapRow(rs, rs.getRow());
                }
                List<Genre> genres = book.getGenres();
                long genreId = rs.getLong(GENRE_ID_COLUMN_NAME);
                String genreName = rs.getString(GENRE_NAME_COLUMN_NAME);
                Genre genre = new Genre(genreId, genreName);
                genres.add(genre);
            }
            return book;
        }
    }

    private record BookGenreRelation(long bookId, long genreId) {
    }

    private static class BookGenreRelationRowMapper implements RowMapper<BookGenreRelation> {

        @Override
        public BookGenreRelation mapRow(ResultSet rs, int rowNum) throws SQLException {
            long bookId = rs.getLong(BOOK_ID_COLUMN_NAME);
            long genreId = rs.getLong(GENRE_ID_COLUMN_NAME);
            return new BookGenreRelation(bookId, genreId);
        }
    }
}
