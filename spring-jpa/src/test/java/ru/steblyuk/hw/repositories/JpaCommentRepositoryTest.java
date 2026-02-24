package ru.steblyuk.hw.repositories;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import ru.steblyuk.hw.models.Book;
import ru.steblyuk.hw.models.Comment;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Репозиторий на основе JPA для работы с комментариями")
@DataJpaTest(showSql = false)
@Import({JpaCommentRepository.class})
public class JpaCommentRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private JpaCommentRepository commentRepository;

    private List<Comment> dbComments;

    @BeforeEach
    void setUp() {
        dbComments = getDbComments();
    }

    @DisplayName("Должен загружать комментарий по id")
    @Test
    public void shouldReturnCorrectCommentById() {
        dbComments.forEach(expected -> {
            Optional<Comment> actual = commentRepository.findById(expected.getId());
            assertThat(actual).isPresent()
                    .get()
                    .isEqualTo(expected);
        });
    }

    @DisplayName("Должен загружать список комментариев по id книги")
    @Test
    public void shouldReturnCorrectCommentsListByBookId() {
        var expected = dbComments.stream()
                .filter(comment -> Objects.equals(comment.getBook().getId(), 2L))
                .toList();
        var actual = commentRepository.findByBookId(2L);
        assertThat(actual).containsExactlyElementsOf(expected);
    }

    @DisplayName("Должен сохранять новый комментарий к книге")
    @Test
    void shouldSaveNewComment() {
        var book = entityManager.find(Book.class, 3L);
        var comment = new Comment(0, "Comment_42", book);
        var returnedComment = commentRepository.save(comment);
        assertThat(entityManager.find(Comment.class, returnedComment.getId()))
                .isEqualTo(returnedComment);
    }

    @DisplayName("Должен сохранять измененный комментарий к книге")
    @Test
    void shouldSaveUpdatedComment() {
        var book = entityManager.find(Book.class, 3L);
        var expectedComment = new Comment(6L, "Comment_42", book);

        assertThat(entityManager.find(Comment.class, expectedComment.getId()))
                .isNotEqualTo(expectedComment);

        var returnedComment = commentRepository.save(expectedComment);
        assertThat(returnedComment).isNotNull()
                .matches(comment -> comment.getId() > 0)
                .usingRecursiveComparison()
                .ignoringExpectedNullFields()
                .isEqualTo(expectedComment);

        assertThat(entityManager.find(Comment.class, returnedComment.getId()))
                .isEqualTo(returnedComment);
    }

    @DisplayName("Должен удалять комментарий по id ")
    @Test
    void shouldDeleteComment() {
        assertThat(entityManager.find(Comment.class, 9L)).isNotNull();
        commentRepository.deleteById(9L);
        assertThat(entityManager.find(Comment.class, 9L)).isNull();
    }

    private List<Comment> getDbComments() {
        return IntStream.range(1, 13).boxed()
                .map(id -> entityManager.find(Comment.class, id))
                .toList();
    }
}
