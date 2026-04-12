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
import ru.steblyuk.hw.exceptions.EntityNotFoundException;

import java.util.List;
import java.util.stream.LongStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("Сервис для работы с авторами")
@SpringBootTest
public class AuthorServiceTest {
    @Autowired
    private AuthorService authorService;

    private List<AuthorDto> dbAuthors;

    @BeforeEach
    void setUp() {
        dbAuthors = getDbAuthors();
    }

    @DisplayName("Должен загружать авторов по id")
    @ParameterizedTest
    @MethodSource("getDbAuthors")
    public void shouldReturnCorrectAuthorsById(AuthorDto expected) {
        AuthorDto actual = authorService.findById(expected.id());
        assertThat(actual).isEqualTo(expected);
    }

    @DisplayName("Должен бросать ошибку при попытке получить не существующего автора")
    @Test
    public void shouldThrowEntityNotFoundExceptionWhenFindBookByIncorrectId() {
        assertThatThrownBy(() -> authorService.findById(-1)).isInstanceOf(EntityNotFoundException.class);
    }

    @DisplayName("Должен загружать список авторов")
    @Test
    public void shouldReturnCorrectAuthorsList() {
        var actual = authorService.findAll();
        assertThat(actual).containsExactlyElementsOf(dbAuthors);
    }

    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    @DisplayName("Должен сохранять нового автора")
    @Test
    void shouldSaveNewAuthor() {
        var newAuthor = new AuthorDto(null, "Author_4");
        var returnedAuthor = authorService.save(newAuthor);
        assertThat(authorService.findById(returnedAuthor.id())).isEqualTo(returnedAuthor);
    }

    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    @DisplayName("Должен сохранять измененного автора")
    @Test
    void shouldSaveUpdatedAuthor() {
        var expectedAuthor = new AuthorDto(2L, "Author_42");

        assertThat(authorService.findById(2L)).isNotEqualTo(expectedAuthor);

        var returnedAuthor = authorService.save(expectedAuthor);
        assertThat(returnedAuthor).isNotNull()
                .matches(author -> author.id() > 0)
                .usingRecursiveComparison()
                .ignoringExpectedNullFields()
                .isEqualTo(expectedAuthor);

        assertThat(authorService.findById(returnedAuthor.id())).isEqualTo(returnedAuthor);
    }

    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    @DisplayName("Должен удалять автора по id ")
    @Test
    void shouldDeleteAuthor() {
        var expected = dbAuthors.get(2);
        assertThat(authorService.findById(3L)).isEqualTo(expected);
        authorService.deleteById(3L);
        assertThatThrownBy(() -> authorService.findById(3L)).isInstanceOf(EntityNotFoundException.class);
    }

    private static List<AuthorDto> getDbAuthors() {
        return LongStream.range(1, 4).boxed()
                .map(id -> new AuthorDto(id, "Author_" + id))
                .toList();
    }
}
