package ru.steblyuk.hw.repositories;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSourceUtils;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import ru.steblyuk.hw.exceptions.EntityNotFoundException;
import ru.steblyuk.hw.models.Author;
import ru.steblyuk.hw.models.Book;
import ru.steblyuk.hw.models.Genre;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static java.util.Objects.nonNull;

@Repository
@RequiredArgsConstructor
public class JdbcBookRepository implements BookRepository {
    private static final String ID_PARAM_NAME = "id";
    private static final String BOOK_ID_PARAM_NAME = "book_id";
    private static final String GENRE_ID_PARAM_NAME = "genre_id";
    private static final String GENRE_IDS_PARAM_NAME = "genreIds";
    private static final String ID_COLUMN_NAME = "id";
    private static final String TITLE_COLUMN_NAME = "title";
    private static final String AUTHOR_ID_COLUMN_NAME = "author_id";
    private static final String AUTHOR_FULL_NAME_COLUMN_NAME = "full_name";

    private final GenreRepository genreRepository;
    private final JdbcTemplate jdbcTemplate;
    private final NamedParameterJdbcTemplate namedJdbcTemplate;

    @Override
    public Optional<Book> findById(long id) {
        var genres = genreRepository.findAll();
        var relations = getAllGenreRelations();
        SqlParameterSource parameterSource = new MapSqlParameterSource(ID_PARAM_NAME, id);
        var book = namedJdbcTemplate.query("select * from books join authors on books.author_id = authors.id " +
                "where books.id = :id", parameterSource, new BookResultSetExtractor());
        mergeBookInfo(book, genres, relations);
        return Optional.ofNullable(book);
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
        namedJdbcTemplate.update("delete from books where id = :id", parameterSource);
    }

    private List<Book> getAllBooksWithoutGenres() {
        return jdbcTemplate.query("select * from books join authors on books.author_id = authors.id", new BookRowMapper());
    }

    private List<BookGenreRelation> getAllGenreRelations() {
        return jdbcTemplate.query("select * from books_genres", new BookGenreRelationRowMapper());
    }

    private void mergeBooksInfo(List<Book> booksWithoutGenres, List<Genre> genres,
                                List<BookGenreRelation> relations) {
        booksWithoutGenres.forEach(book -> mergeBookInfo(book, genres, relations));
    }

    private void mergeBookInfo(Book bookWithoutGenres, List<Genre> genres,
                               List<BookGenreRelation> relations) {
        if (nonNull(bookWithoutGenres)) {
            List<Long> genreIds = relations.stream()
                    .filter(bookGenreRelation -> Objects.equals(bookGenreRelation.bookId(), bookWithoutGenres.getId()))
                    .map(BookGenreRelation::genreId)
                    .toList();
            List<Genre> bookGenres = genres.stream()
                    .filter(genre -> genreIds.contains(genre.getId()))
                    .toList();
            bookWithoutGenres.setGenres(bookGenres);
        }
    }

    private Book insert(Book book) {
        var keyHolder = new GeneratedKeyHolder();
        Author bookAuthor = book.getAuthor();
        jdbcTemplate.update(con -> {
            PreparedStatement statement = con.prepareStatement("insert into books (title, author_id)  values (?, ?)",
                    Statement.RETURN_GENERATED_KEYS);
            statement.setString(1, book.getTitle());
            statement.setLong(2, bookAuthor.getId());
            return statement;
        }, keyHolder);
        //noinspection DataFlowIssue
        book.setId(keyHolder.getKeyAs(Long.class));
        batchInsertGenresRelationsFor(book);
        return book;
    }

    private Book update(Book book) {
        Author bookAuthor = book.getAuthor();
        jdbcTemplate.update("update books set title = ?, author_id = ? where id = ?", book.getTitle(),
                bookAuthor.getId(), book.getId());
        removeGenresRelationsFor(book);
        batchInsertGenresRelationsFor(book);

        return book;
    }

    private void batchInsertGenresRelationsFor(Book book) {
        List<BookGenreRelation> relations = book.getGenres().stream()
                .map(genre -> new BookGenreRelation(book.getId(), genre.getId()))
                .toList();
        SqlParameterSource[] parameterSource = SqlParameterSourceUtils.createBatch(relations);
        namedJdbcTemplate.batchUpdate("insert into books_genres values (:bookId, :genreId)", parameterSource);
    }

    private void removeGenresRelationsFor(Book book) {
        List<Long> genresIds = book.getGenres().stream()
                .map(Genre::getId)
                .toList();
        SqlParameterSource parameterSource = new MapSqlParameterSource(GENRE_IDS_PARAM_NAME, genresIds);
        int updated = namedJdbcTemplate.update("delete from books_genres where genre_id not in (:genreIds)", parameterSource);
        if (updated < 1) {
            String message = "Genre relations for book(id = %s) is not found!".formatted(book.getId());
            throw new EntityNotFoundException(message);
        }
    }

    private static class BookRowMapper implements RowMapper<Book> {

        @Override
        public Book mapRow(ResultSet rs, int rowNum) throws SQLException {
            long id = rs.getLong(ID_COLUMN_NAME);
            String title = rs.getString(TITLE_COLUMN_NAME);
            long authorId = rs.getLong(AUTHOR_ID_COLUMN_NAME);
            String authorFullName = rs.getString(AUTHOR_FULL_NAME_COLUMN_NAME);
            return new Book(id, title, new Author(authorId, authorFullName), null);
        }
    }

    // Использовать для findById
    @SuppressWarnings("ClassCanBeRecord")
    @RequiredArgsConstructor
    private static class BookResultSetExtractor implements ResultSetExtractor<Book> {

        @Override
        public Book extractData(ResultSet rs) throws SQLException, DataAccessException {
            if (rs.next()) {
                long id = rs.getLong(ID_COLUMN_NAME);
                String title = rs.getString(TITLE_COLUMN_NAME);
                long authorId = rs.getLong(AUTHOR_ID_COLUMN_NAME);
                String authorFullName = rs.getString(AUTHOR_FULL_NAME_COLUMN_NAME);
                return new Book(id, title, new Author(authorId, authorFullName), null);
            }
            return null;
        }
    }

    private record BookGenreRelation(long bookId, long genreId) {
    }

    private static class BookGenreRelationRowMapper implements RowMapper<BookGenreRelation> {
        @Override
        public BookGenreRelation mapRow(ResultSet rs, int rowNum) throws SQLException {
            long bookId = rs.getLong(BOOK_ID_PARAM_NAME);
            long genreId = rs.getLong(GENRE_ID_PARAM_NAME);
            return new BookGenreRelation(bookId, genreId);
        }
    }
}
