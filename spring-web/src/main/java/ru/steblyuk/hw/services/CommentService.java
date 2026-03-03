package ru.steblyuk.hw.services;

import ru.steblyuk.hw.dto.CommentDto;

import java.util.List;
import java.util.Optional;

public interface CommentService {
    CommentDto findById(long id);

    List<CommentDto> findByBookId(long id);

    CommentDto save(CommentDto commentDto);

    void deleteById(long id);
}
