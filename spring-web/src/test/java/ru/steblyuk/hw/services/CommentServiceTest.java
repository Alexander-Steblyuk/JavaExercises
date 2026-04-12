package ru.steblyuk.hw.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.steblyuk.hw.dto.CommentDto;
import ru.steblyuk.hw.exceptions.EntityNotFoundException;

import java.util.List;
import java.util.Objects;
import java.util.stream.LongStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("Сервис для работы с комментариями")
@SpringBootTest
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
        CommentDto actual = commentService.findById(expected.id());
        assertThat(actual).isEqualTo(expected);
    }

    @DisplayName("Должен бросать ошибку при попытке получить не существующий комментарий")
    @Test
    public void shouldThrowEntityNotFoundExceptionWhenFindBookByIncorrectId() {
        assertThatThrownBy(() -> commentService.findById(-1)).isInstanceOf(EntityNotFoundException.class);
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
        var newComment = new CommentDto(null, "Comment_42", 3L);
        var returnedComment = commentService.save(newComment);
        assertThat(commentService.findById(returnedComment.id())).isEqualTo(returnedComment);
    }

    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    @DisplayName("Должен сохранять измененный комментарий к книге")
    @Test
    void shouldSaveUpdatedComment() {
        var expectedComment = new CommentDto(6L, "Comment_42", 3L);

        assertThat(commentService.findById(6L)).isNotEqualTo(expectedComment);

        var returnedComment = commentService.save(expectedComment);
        assertThat(returnedComment).isNotNull()
                .matches(comment -> comment.id() > 0)
                .usingRecursiveComparison()
                .ignoringExpectedNullFields()
                .isEqualTo(expectedComment);

        assertThat(commentService.findById(returnedComment.id())).isEqualTo(returnedComment);
    }

    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    @DisplayName("Должен удалять комментарий по id ")
    @Test
    void shouldDeleteComment() {
        var expected = dbComments.get(8);
        assertThat(commentService.findById(9L)).isEqualTo(expected);
        commentService.deleteById(9L);
        assertThatThrownBy(() -> commentService.findById(9L)).isInstanceOf(EntityNotFoundException.class);
    }

    private static List<CommentDto> getDbComments() {
        return LongStream.range(1, 13).boxed()
                .map(id -> new CommentDto(id, "Comment_" + id, (id - 1) / 4L + 1))
                .toList();
    }
}
