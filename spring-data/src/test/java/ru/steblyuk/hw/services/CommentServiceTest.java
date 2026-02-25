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
import ru.steblyuk.hw.dto.CommentDto;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Сервис для работы с комментариями")
@SpringBootTest
@EnableAutoConfiguration(exclude = ShellRunnerAutoConfiguration.class)
public class CommentServiceTest {

    @Autowired
    private CommentService commentService;

    private List<CommentDto> dbComments;

    @BeforeEach
    void setUp() {
        dbComments = getDbComments();
    }

    @DisplayName("Должен загружать комментарий по id")
    @ParameterizedTest
    @MethodSource("getDbComments")
    public void shouldReturnCorrectCommentById(CommentDto expected) {
        Optional<CommentDto> actual = commentService.findById(expected.id());
        assertThat(actual).isPresent()
                .get()
                .isEqualTo(expected);
    }

    @DisplayName("Должен загружать список комментариев по id книги")
    @Test
    public void shouldReturnCorrectCommentsListByBookId() {
        var expected = dbComments.stream()
                .filter(comment -> Objects.equals(comment.bookId(), 2L))
                .toList();
        var actual = commentService.findByBookId(2L);
        assertThat(actual).containsExactlyElementsOf(expected);
    }

    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    @DisplayName("Должен сохранять новый комментарий к книге")
    @Test
    void shouldSaveNewComment() {
        var returnedComment = commentService.insert("Comment_42", 3L);
        assertThat(commentService.findById(returnedComment.id())).isPresent()
                .get()
                .isEqualTo(returnedComment);
    }

    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    @DisplayName("Должен сохранять измененный комментарий к книге")
    @Test
    void shouldSaveUpdatedComment() {
        var expectedComment = new CommentDto(6L, "Comment_42", 3L);

        assertThat(commentService.findById(6L)).isPresent()
                .get()
                .isNotEqualTo(expectedComment);

        var returnedComment = commentService.update(6L, "Comment_42", 3L);
        assertThat(returnedComment).isNotNull()
                .matches(comment -> comment.id() > 0)
                .usingRecursiveComparison()
                .ignoringExpectedNullFields()
                .isEqualTo(expectedComment);

        assertThat(commentService.findById(returnedComment.id())).isPresent()
                .get()
                .isEqualTo(returnedComment);
    }

    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    @DisplayName("Должен удалять комментарий по id ")
    @Test
    void shouldDeleteComment() {
        assertThat(commentService.findById(9L)).isPresent();
        commentService.deleteById(9L);
        assertThat(commentService.findById(9L)).isEmpty();
    }

    private static List<CommentDto> getDbComments() {
        return IntStream.range(1, 13).boxed()
                .map(id -> new CommentDto(id, "Comment_" + id, (id - 1) / 4L + 1))
                .toList();
    }
}
