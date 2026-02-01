package ru.steblyuk.hw.repositories;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import ru.steblyuk.hw.models.Genre;

import java.util.List;
import java.util.Set;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Репозиторий на основе JPA для работы с жанрами")
@DataJpaTest(showSql = false)
@Import({JpaGenreRepository.class})
public class JpaGenreRepositoryTest {
    private static final Set<Long> ANY_GENRES_IDS = Set.of(2L, 4L, 5L);

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private JpaGenreRepository genreRepository;

    private List<Genre> dbGenres;

    @BeforeEach
    void setUp() {
        dbGenres = getDbGenres();
    }

    @DisplayName("Должен загрузить список всех авторов")
    @Test
    public void shouldReturnCorrectAuthorsList() {
        var actualGenres = genreRepository.findAll();
        assertThat(actualGenres).containsExactlyElementsOf(dbGenres);
        actualGenres.forEach(System.out::println);
    }

    @DisplayName("Должен загрузить авторов по списку id")
    @Test
    public void shouldReturnCorrectAuthorsListById() {
        var expected = dbGenres.stream()
                .filter(genre -> ANY_GENRES_IDS.contains(genre.getId()))
                .toList();
        var actualGenres = genreRepository.findAllByIds(ANY_GENRES_IDS);

        assertThat(actualGenres).containsExactlyElementsOf(expected);
        actualGenres.forEach(System.out::println);
    }

    private List<Genre> getDbGenres() {
        return IntStream.range(1, 7).boxed()
                .map(id -> entityManager.find(Genre.class, id))
                .toList();
    }
}
