package ru.steblyuk.hw.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.steblyuk.hw.dto.AuthorDto;
import ru.steblyuk.hw.dto.BookDto;
import ru.steblyuk.hw.dto.GenreDto;
import ru.steblyuk.hw.exceptions.EntityNotFoundException;

import java.util.List;
import java.util.stream.LongStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("Сервис для работы с книгами")
@SpringBootTest
public class BookServiceTest {

    @Autowired
    private BookService bookService;

    private List<AuthorDto> dbAuthors;

    private List<GenreDto> dbGenres;

    private List<BookDto> dbBooks;

    @BeforeEach
    void setUp() {
        dbAuthors = getDbAuthors();
        dbGenres = getDbGenres();
        dbBooks = getDbBooks(dbAuthors, dbGenres);
    }

    @DisplayName("Должен загружать книгу по id")
    @ParameterizedTest
    @MethodSource("getDbBooks")
    void shouldReturnCorrectBookById(BookDto expectedBook) {
        var actualBook = bookService.findById(expectedBook.id());
        assertThat(actualBook).isEqualTo(expectedBook);
    }

    @DisplayName("Должен бросать ошибку при попытке получить не существующую книгу")
    @Test
    public void shouldThrowEntityNotFoundExceptionWhenFindBookByIncorrectId() {
        assertThatThrownBy(() -> bookService.findById(-1)).isInstanceOf(EntityNotFoundException.class);
    }

    @DisplayName("Должен загружать список всех книг")
    @Test
    void shouldReturnCorrectBooksList() {
        var actualBooks = bookService.findAll();
        assertThat(actualBooks).containsExactlyElementsOf(dbBooks);
        actualBooks.forEach(System.out::println);
    }

    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    @DisplayName("Должен сохранять новую книгу")
    @Test
    void shouldSaveNewBook() {
        BookDto bookDto = new BookDto(null, "BookTitle_10500", dbAuthors.get(0), List.of(dbGenres.get(0), dbGenres.get(1)));
        var returnedBook = bookService.save(bookDto);
        assertThat(bookService.findById(returnedBook.id())).isEqualTo(returnedBook);
    }

    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    @DisplayName("Должен сохранять измененную книгу")
    @Test
    void shouldSaveUpdatedBook() {
        var expectedBook = new BookDto(1L, "BookTitle_10500", dbAuthors.get(1),
                List.of(dbGenres.get(3), dbGenres.get(4)));
        assertThat(bookService.findById(1)).isNotEqualTo(expectedBook);

        var returnedBook = bookService.save(expectedBook);
        assertThat(returnedBook).isNotNull()
                .matches(book -> book.id() > 0)
                .usingRecursiveComparison()
                .ignoringExpectedNullFields()
                .isEqualTo(expectedBook);

        assertThat(bookService.findById(returnedBook.id())).isEqualTo(returnedBook);
    }

    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    @DisplayName("Должен удалять книгу по id ")
    @Test
    void shouldDeleteBook() {
        var expected = dbBooks.get(0);
        assertThat(bookService.findById(1L)).isEqualTo(expected);
        bookService.deleteById(1L);
        assertThatThrownBy(() -> bookService.findById(1L)).isInstanceOf(EntityNotFoundException.class);
    }

    private static List<AuthorDto> getDbAuthors() {
        return LongStream.range(1, 4).boxed()
                .map(id -> new AuthorDto(id, "Author_" + id))
                .toList();
    }

    private static List<GenreDto> getDbGenres() {
        return LongStream.range(1, 7).boxed()
                .map(id -> new GenreDto(id, "Genre_" + id))
                .toList();
    }

    private static List<BookDto> getDbBooks(List<AuthorDto> dbAuthors, List<GenreDto> dbGenres) {
        return LongStream.range(1, 4).boxed()
                .map(id -> new BookDto(id,
                        "BookTitle_" + id,
                        dbAuthors.get((int) (id - 1)),
                        dbGenres.subList((int) ((id - 1) * 2), (int) ((id - 1) * 2 + 2)))
                )
                .toList();
    }

    private static List<BookDto> getDbBooks() {
        var dbAuthors = getDbAuthors();
        var dbGenres = getDbGenres();
        return getDbBooks(dbAuthors, dbGenres);
    }
}
