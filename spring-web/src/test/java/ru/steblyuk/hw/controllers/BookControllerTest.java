package ru.steblyuk.hw.controllers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.steblyuk.hw.dto.AuthorDto;
import ru.steblyuk.hw.dto.BookDto;
import ru.steblyuk.hw.dto.CommentDto;
import ru.steblyuk.hw.dto.GenreDto;
import ru.steblyuk.hw.exceptions.EntityNotFoundException;
import ru.steblyuk.hw.formatters.AuthorDtoFormatter;
import ru.steblyuk.hw.formatters.GenreDtoFormatter;
import ru.steblyuk.hw.services.AuthorService;
import ru.steblyuk.hw.services.BookService;
import ru.steblyuk.hw.services.CommentService;
import ru.steblyuk.hw.services.GenreService;

import java.util.List;
import java.util.Objects;
import java.util.stream.LongStream;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookController.class)
@Import({AuthorDtoFormatter.class, GenreDtoFormatter.class})
public class BookControllerTest {

    private static final String BOOKS_MODEL_ATTRIBUTE_NAME = "books";
    private static final String ALL_AUTHORS_MODEL_ATTRIBUTE_NAME = "allAuthors";
    private static final String ALL_GENRES_MODEL_ATTRIBUTE_NAME = "allGenres";

    private static final String BOOK_MODEL_ATTRIBUTE_NAME = "book";
    private static final String TITLE_MODEL_ATTRIBUTE_NAME = "title";
    private static final String AUTHOR_MODEL_ATTRIBUTE_NAME = "author";
    private static final String GENRES_MODEL_ATTRIBUTE_NAME = "genres";
    private static final String COMMENTS_MODEL_ATTRIBUTE_NAME = "comments";

    private static final String NOT_FOUND_ERROR_MESSAGE_TEMPLATE = "Nothing was found by this id";

    @MockitoBean
    private BookService bookService;

    @MockitoBean
    private AuthorService authorService;

    @MockitoBean
    private GenreService genreService;

    @MockitoBean
    private CommentService commentService;

    @Autowired
    private MockMvc mockMvc;

    private List<AuthorDto> dbAuthors;

    private List<GenreDto> dbGenres;

    private List<BookDto> dbBooks;

    private List<CommentDto> dbComments;

    @BeforeEach
    void setUp() {
        dbAuthors = getDbAuthors();
        dbGenres = getDbGenres();
        dbComments = getDbComments();
        dbBooks = getDbBooks(dbAuthors, dbGenres);
        when(authorService.findAll()).thenReturn(dbAuthors);
        when(genreService.findAll()).thenReturn(dbGenres);
        when(bookService.findAll()).thenReturn(dbBooks);
    }

    @DisplayName("Должен загружать книгу по id")
    @ParameterizedTest
    @MethodSource("getDbBooks")
    void shouldReturnCorrectBookById(BookDto expectedBook) throws Exception {
        long bookId = expectedBook.id();
        List<CommentDto> expectedComments = getDbCommentsByBookId(bookId);
        when(bookService.findById(bookId)).thenReturn(expectedBook);
        when(commentService.findByBookId(bookId)).thenReturn(expectedComments);
        mockMvc.perform(get("/books/{id}", bookId))
                .andExpect(status().isOk())
                .andExpect(model().attribute(BOOK_MODEL_ATTRIBUTE_NAME, expectedBook))
                .andExpect(model().attribute(COMMENTS_MODEL_ATTRIBUTE_NAME, expectedComments));
    }

    @DisplayName("Должен вернуть статус ошибки при несуществующем id книги")
    @Test
    void shouldReturnBadRequestForIncorrectBookId() throws Exception {
        long bookId = -1;
        when(bookService.findById(bookId)).thenThrow(EntityNotFoundException.class);
        mockMvc.perform(get("/books/{id}", bookId))
                .andExpect(status().isBadRequest())
                .andExpect(model().attribute("message", NOT_FOUND_ERROR_MESSAGE_TEMPLATE));
    }

    @DisplayName("Должен загружать список всех книг")
    @Test
    void shouldReturnCorrectBooksList() throws Exception {
        mockMvc.perform(get("/books"))
                .andExpect(status().isOk())
                .andExpect(model().attribute(BOOKS_MODEL_ATTRIBUTE_NAME, dbBooks));
    }

    @DisplayName("Должен возвращать пустую форму создания новой книги для заполнения")
    @Test
    void shouldReturnEmptyBookForAdd() throws Exception {
        mockMvc.perform(get("/books/add"))
                .andExpect(status().isOk())
                .andExpect(model().attribute(BOOK_MODEL_ATTRIBUTE_NAME, BookDto.EMPTY))
                .andExpect(model().attribute(ALL_AUTHORS_MODEL_ATTRIBUTE_NAME, authorService.findAll()))
                .andExpect(model().attribute(ALL_GENRES_MODEL_ATTRIBUTE_NAME, genreService.findAll()));
    }

    @DisplayName("Должен сохранять новую книгу и возвращать на список книг")
    @Test
    void shouldSaveNewBook() throws Exception {
        BookDto newBook = new BookDto(null, "BookTitle_10500", dbAuthors.get(0), List.of(dbGenres.get(0), dbGenres.get(1)));
        mockMvc.perform(post("/books/save")
                        .param(TITLE_MODEL_ATTRIBUTE_NAME, newBook.title())
                        .param(AUTHOR_MODEL_ATTRIBUTE_NAME, newBook.author().toString())
                        .param(GENRES_MODEL_ATTRIBUTE_NAME, dbGenres.get(0).toString(), dbGenres.get(1).toString()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/books"));
    }

    @DisplayName("Должен выводить ошибки, если сохраняемая новая книга некорректна")
    @Test
    void shouldShowErrorsWhenSavingNewBookNotValid() throws Exception {
        mockMvc.perform(post("/books/save")
                        .param(TITLE_MODEL_ATTRIBUTE_NAME, ""))
                .andExpect(status().isOk())
                .andExpect(model().errorCount(3))
                .andExpect(model().attributeHasFieldErrorCode(BOOK_MODEL_ATTRIBUTE_NAME, TITLE_MODEL_ATTRIBUTE_NAME, "NotBlank"))
                .andExpect(model().attributeHasFieldErrorCode(BOOK_MODEL_ATTRIBUTE_NAME, AUTHOR_MODEL_ATTRIBUTE_NAME, "NotNull"))
                .andExpect(model().attributeHasFieldErrorCode(BOOK_MODEL_ATTRIBUTE_NAME, GENRES_MODEL_ATTRIBUTE_NAME, "NotEmpty"));
    }

    @DisplayName("Должен возвращать существующую книгу для изменения")
    @Test
    void shouldReturnExistingBookForEdit() throws Exception {
        var expectedBook = dbBooks.get(0);
        Long bookId = expectedBook.id();
        when(bookService.findById(bookId)).thenReturn(expectedBook);
        mockMvc.perform(get("/books/edit/{id}", expectedBook.id()))
                .andExpect(status().isOk())
                .andExpect(model().attribute(BOOK_MODEL_ATTRIBUTE_NAME, expectedBook))
                .andExpect(model().attribute(ALL_AUTHORS_MODEL_ATTRIBUTE_NAME, authorService.findAll()))
                .andExpect(model().attribute(ALL_GENRES_MODEL_ATTRIBUTE_NAME, genreService.findAll()));
    }

    @DisplayName("Должен возвращать ошибку для не существующей книги для изменения")
    @Test
    void shouldReturnBadRequestFoNotExistingBookForEdit() throws Exception {
        long bookId = -2L;
        when(bookService.findById(bookId)).thenThrow(EntityNotFoundException.class);
        mockMvc.perform(get("/books/edit/{id}", bookId))
                .andExpect(status().isBadRequest())
                .andExpect(model().attribute("message", NOT_FOUND_ERROR_MESSAGE_TEMPLATE));
    }

    @DisplayName("Должен сохранять измененную книгу и возвращать на список книг")
    @Test
    void shouldSaveUpdatedBook() throws Exception {
        var expectedBook = new BookDto(1L, "BookTitle_10500", dbAuthors.get(1),
                List.of(dbGenres.get(3), dbGenres.get(4)));
        mockMvc.perform(post("/books/save", expectedBook.id())
                        .param(TITLE_MODEL_ATTRIBUTE_NAME, expectedBook.title())
                        .param(AUTHOR_MODEL_ATTRIBUTE_NAME, expectedBook.author().toString())
                        .param(GENRES_MODEL_ATTRIBUTE_NAME, dbGenres.get(3).toString(), dbGenres.get(4).toString()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/books"));
    }

    @DisplayName("Должен удалять книгу по id и возвращать на список книг")
    @Test
    void shouldDeleteBook() throws Exception {
        mockMvc.perform(delete("/books/{id}", 1L))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/books"));
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

    private static List<CommentDto> getDbComments() {
        return LongStream.range(1, 13).boxed()
                .map(id -> new CommentDto(id, "Comment_" + id, (id - 1) / 4L + 1))
                .toList();
    }

    private static List<BookDto> getDbBooks() {
        var dbAuthors = getDbAuthors();
        var dbGenres = getDbGenres();
        return getDbBooks(dbAuthors, dbGenres);
    }

    private List<CommentDto> getDbCommentsByBookId(long bookId) {
        return dbComments.stream()
                .filter(comment -> Objects.equals(bookId, comment.bookId()))
                .toList();
    }
}
