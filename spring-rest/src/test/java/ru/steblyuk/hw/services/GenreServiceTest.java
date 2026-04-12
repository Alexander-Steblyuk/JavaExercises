package ru.steblyuk.hw.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.steblyuk.hw.dto.GenreDto;
import ru.steblyuk.hw.exceptions.EntityNotFoundException;

import java.util.List;
import java.util.stream.LongStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("Сервис для работы с жанрами")
@SpringBootTest
public class GenreServiceTest {
    @Autowired
    private GenreService genreService;

    private List<GenreDto> dbGenres;

    @BeforeEach
    void setUp() {
        dbGenres = getDbGenres();
    }

    @DisplayName("Должен загружать жанры по id")
    @ParameterizedTest
    @MethodSource("getDbGenres")
    public void shouldReturnCorrectGenresById(GenreDto expected) {
        GenreDto actual = genreService.findById(expected.id());
        assertThat(actual).isEqualTo(expected);
    }

    @DisplayName("Должен бросать ошибку при попытке получить не существующий жанр")
    @Test
    public void shouldThrowEntityNotFoundExceptionWhenFindGenreByIncorrectId() {
        assertThatThrownBy(() -> genreService.findById(-1)).isInstanceOf(EntityNotFoundException.class);
    }

    @DisplayName("Должен загружать список жанров")
    @Test
    public void shouldReturnCorrectGenresList() {
        var actual = genreService.findAll();
        assertThat(actual).containsExactlyElementsOf(dbGenres);
    }

    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    @DisplayName("Должен сохранять новый жанр")
    @Test
    void shouldSaveNewGenre() {
        var newGenre = new GenreDto(null, "Genre_7");
        var returnedGenre = genreService.save(newGenre);
        assertThat(genreService.findById(returnedGenre.id())).isEqualTo(returnedGenre);
    }

    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    @DisplayName("Должен сохранять измененный жанр")
    @Test
    void shouldSaveUpdatedGenre() {
        var expectedGenre = new GenreDto(5L, "Genre_42");

        assertThat(genreService.findById(5L)).isNotEqualTo(expectedGenre);

        var returnedGenre = genreService.save(expectedGenre);
        assertThat(returnedGenre).isNotNull()
                .matches(genre -> genre.id() > 0)
                .usingRecursiveComparison()
                .ignoringExpectedNullFields()
                .isEqualTo(expectedGenre);

        assertThat(genreService.findById(returnedGenre.id())).isEqualTo(returnedGenre);
    }

    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    @DisplayName("Должен удалять жанр по id ")
    @Test
    void shouldDeleteGenre() {
        var expected = dbGenres.get(4);
        assertThat(genreService.findById(5L)).isEqualTo(expected);
        genreService.deleteById(5L);
        assertThatThrownBy(() -> genreService.findById(5L)).isInstanceOf(EntityNotFoundException.class);
    }

    private static List<GenreDto> getDbGenres() {
        return LongStream.range(1, 7).boxed()
                .map(id -> new GenreDto(id, "Genre_" + id))
                .toList();
    }
}
