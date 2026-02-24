package ru.steblyuk.hw.converters;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.steblyuk.hw.dto.CommentDto;

@RequiredArgsConstructor
@Component
public class CommentConverter {

    public String commentToString(CommentDto comment) {
        return "Id: %d, content: %s, bookId: %s".formatted(
                comment.id(),
                comment.content(),
                comment.bookId());
    }
}
