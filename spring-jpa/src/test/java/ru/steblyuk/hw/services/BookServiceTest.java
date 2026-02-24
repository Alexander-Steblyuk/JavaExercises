package ru.steblyuk.hw.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.shell.boot.ShellRunnerAutoConfiguration;
import org.springframework.test.annotation.DirtiesContext;
import ru.steblyuk.hw.dto.AuthorDto;
import ru.steblyuk.hw.dto.BookDto;
import ru.steblyuk.hw.dto.GenreDto;

import java.util.List;
import java.util.Set;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Сервис для работы с книгами")
@SpringBootTest
@EnableAutoConfiguration(exclude = ShellRunnerAutoConfiguration.class)
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
        assertThat(actualBook).isPresent()
                .get()
                .isEqualTo(expectedBook);
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
        var returnedBook = bookService.insert("BookTitle_10500", 1, Set.of(1L, 2L));
        assertThat(bookService.findById(returnedBook.id()))
                .isPresent()
                .get()
                .isEqualTo(returnedBook);
    }

    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    @DisplayName("Должен сохранять измененную книгу")
    @Test
    void shouldSaveUpdatedBook() {
        var expectedBook = new BookDto(1L, "BookTitle_10500", dbAuthors.get(1),
                List.of(dbGenres.get(3), dbGenres.get(4)));
        assertThat(bookService.findById(1)).isPresent()
                .get()
                .isNotEqualTo(expectedBook);

        var returnedBook = bookService.update(1L, "BookTitle_10500", 2, Set.of(4L, 5L));
        assertThat(returnedBook).isNotNull()
                .matches(book -> book.id() > 0)
                .usingRecursiveComparison()
                .ignoringExpectedNullFields()
                .isEqualTo(expectedBook);

        assertThat(bookService.findById(returnedBook.id())).isPresent()
                .get()
                .isEqualTo(returnedBook);
    }

    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    @DisplayName("Должен удалять книгу по id ")
    @Test
    void shouldDeleteBook() {
        assertThat(bookService.findById(1L)).isPresent();
        bookService.deleteById(1L);
        assertThat(bookService.findById(1L)).isEmpty();
    }

    private static List<AuthorDto> getDbAuthors() {
        return IntStream.range(1, 4).boxed()
                .map(id -> new AuthorDto(id, "Author_" + id))
                .toList();
    }

    private static List<GenreDto> getDbGenres() {
        return IntStream.range(1, 7).boxed()
                .map(id -> new GenreDto(id, "Genre_" + id))
                .toList();
    }

    private static List<BookDto> getDbBooks(List<AuthorDto> dbAuthors, List<GenreDto> dbGenres) {
        return IntStream.range(1, 4).boxed()
                .map(id -> new BookDto(id,
                        "BookTitle_" + id,
                        dbAuthors.get(id - 1),
                        dbGenres.subList((id - 1) * 2, (id - 1) * 2 + 2))
                )
                .toList();
    }

    private static List<BookDto> getDbBooks() {
        var dbAuthors = getDbAuthors();
        var dbGenres = getDbGenres();
        return getDbBooks(dbAuthors, dbGenres);
    }
}
