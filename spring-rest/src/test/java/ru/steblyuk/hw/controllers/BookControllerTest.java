package ru.steblyuk.hw.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.steblyuk.hw.dto.AuthorDto;
import ru.steblyuk.hw.dto.BookDto;
import ru.steblyuk.hw.dto.GenreDto;
import ru.steblyuk.hw.exceptions.EntityNotFoundException;
import ru.steblyuk.hw.services.BookService;

import java.util.List;
import java.util.Map;
import java.util.stream.LongStream;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookController.class)
public class BookControllerTest {

    private static final Object NOT_FOUND_ERROR_RESULT = Map.entry("message", "Nothing was found by this id");

    @MockitoBean
    private BookService bookService;

    @Autowired
    private MockMvc mockMvc;

    private List<AuthorDto> dbAuthors;

    private List<GenreDto> dbGenres;

    private List<BookDto> dbBooks;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        dbAuthors = getDbAuthors();
        dbGenres = getDbGenres();
        dbBooks = getDbBooks(dbAuthors, dbGenres);
    }

    @DisplayName("Должен загружать книгу по id")
    @ParameterizedTest
    @MethodSource("getDbBooks")
    void shouldReturnCorrectBookById(BookDto expectedBook) throws Exception {
        long bookId = expectedBook.id();
        String expectedBookJson = objectMapper.writeValueAsString(expectedBook);
        when(bookService.findById(bookId)).thenReturn(expectedBook);
        mockMvc.perform(get("/books/{id}", bookId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(expectedBookJson));
    }

    @DisplayName("Должен вернуть статус ошибки при несуществующем id книги")
    @Test
    void shouldReturnBadRequestForIncorrectBookId() throws Exception {
        long bookId = -1;
        var expectedBookJson = objectMapper.writeValueAsString(NOT_FOUND_ERROR_RESULT);
        when(bookService.findById(bookId)).thenThrow(EntityNotFoundException.class);
        mockMvc.perform(get("/books/{id}", bookId))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(expectedBookJson));
    }

    @DisplayName("Должен загружать список всех книг")
    @Test
    void shouldReturnCorrectBooksList() throws Exception {
        var expectedBooksJson = objectMapper.writeValueAsString(dbBooks);
        when(bookService.findAll()).thenReturn(dbBooks);
        mockMvc.perform(get("/books"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(expectedBooksJson));
    }

    @DisplayName("Должен сохранять новую книгу и возвращать сохраненную")
    @Test
    void shouldSaveNewBook() throws Exception {
        var newBook = new BookDto(null, "BookTitle_10500", dbAuthors.get(0),
                List.of(dbGenres.get(0), dbGenres.get(1)));
        var savedBook = new BookDto(42L, "BookTitle_10500", dbAuthors.get(0),
                List.of(dbGenres.get(0), dbGenres.get(1)));
        var newBookJson = objectMapper.writeValueAsString(newBook);
        var expectedBookJson = objectMapper.writeValueAsString(savedBook);
        when(bookService.save(newBook)).thenReturn(savedBook);
        mockMvc.perform(post("/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(newBookJson))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(expectedBookJson));
    }

    @DisplayName("Должен сохранять измененную книгу")
    @Test
    void shouldSaveUpdatedBook() throws Exception {
        var expectedBook = new BookDto(1L, "BookTitle_10500", dbAuthors.get(1),
                List.of(dbGenres.get(3), dbGenres.get(4)));
        String expectedBookJson = objectMapper.writeValueAsString(expectedBook);
        when(bookService.save(expectedBook)).thenReturn(expectedBook);
        mockMvc.perform(put("/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(expectedBookJson))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(expectedBookJson));
    }

    @DisplayName("Должен выводить ошибки, если сохраняемая новая книга некорректна")
    @Test
    void shouldShowErrorsWhenSavingNewBookNotValid() throws Exception {
        var incorrectBook = new BookDto(null, "", null, List.of());
        var incorrectJson = objectMapper.writeValueAsString(incorrectBook);
        mockMvc.perform(post("/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(incorrectJson))
                .andExpect(status().isBadRequest());
    }

    @DisplayName("Должен удалять книгу по id и возвращать корректный статус")
    @Test
    void shouldDeleteBook() throws Exception {
        mockMvc.perform(delete("/books/{id}", 1L))
                .andExpect(status().isNoContent());
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
