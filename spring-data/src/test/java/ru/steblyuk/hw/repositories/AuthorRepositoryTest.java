package ru.steblyuk.hw.repositories;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import ru.steblyuk.hw.models.Author;

import java.util.List;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;


@DisplayName("Репозиторий на основе JPA для работы с авторами")
@DataJpaTest(showSql = false)
public class AuthorRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private AuthorRepository authorRepository;

    private List<Author> dbAuthors;

    @BeforeEach
    void setUp() {
        dbAuthors = getDbAuthors();
    }

    @DisplayName("Должен загрузить автора по id")
    @Test
    public void shouldReturnCorrectAuthorById() {
        dbAuthors.forEach(expected -> {
            var actual = authorRepository.findById(expected.getId());
            assertThat(actual).isPresent()
                    .get()
                    .isEqualTo(expected);

        });
    }

    @DisplayName("Должен загрузить список всех авторов")
    @Test
    public void shouldReturnCorrectAuthorsList() {
        var actualAuthors = authorRepository.findAll();
        assertThat(actualAuthors).containsExactlyElementsOf(dbAuthors);
        actualAuthors.forEach(System.out::println);
    }

    private List<Author> getDbAuthors() {
        return IntStream.range(1, 4).boxed()
                .map(id -> entityManager.find(Author.class, id))
                .toList();
    }
}
