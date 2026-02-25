package ru.steblyuk.hw.services;

import ru.steblyuk.hw.dto.CommentDto;

import java.util.List;
import java.util.Optional;

public interface CommentService {
    Optional<CommentDto> findById(long id);

    List<CommentDto> findByBookId(long id);

    CommentDto insert(String content, long bookId);

    CommentDto update(long id, String content, long bookId);

    void deleteById(long id);
}
