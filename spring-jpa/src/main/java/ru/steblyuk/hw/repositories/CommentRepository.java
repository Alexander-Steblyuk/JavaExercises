package ru.steblyuk.hw.repositories;

import ru.steblyuk.hw.models.Comment;

import java.util.List;
import java.util.Optional;

public interface CommentRepository {
    Optional<Comment> findById(long id);

    List<Comment> findByBookId(long id);

    Comment save(Comment comment);

    void deleteById(long id);
}
