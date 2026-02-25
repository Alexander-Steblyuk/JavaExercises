package ru.steblyuk.hw.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.steblyuk.hw.dto.CommentDto;
import ru.steblyuk.hw.exceptions.EntityNotFoundException;
import ru.steblyuk.hw.mappers.CommentMapper;
import ru.steblyuk.hw.models.Comment;
import ru.steblyuk.hw.repositories.BookRepository;
import ru.steblyuk.hw.repositories.CommentRepository;

import java.util.List;
import java.util.Optional;

import static java.util.Objects.isNull;

@RequiredArgsConstructor
@Service
public class CommentServiceImpl implements CommentService {

    private final BookRepository bookRepository;

    private final CommentRepository commentRepository;

    private final CommentMapper commentMapper;

    @Transactional(readOnly = true)
    @Override
    public Optional<CommentDto> findById(long id) {
        return commentRepository.findById(id)
                .map(commentMapper::mapToDto);
    }

    @Transactional(readOnly = true)
    @Override
    public List<CommentDto> findByBookId(long id) {
        return commentRepository.findByBookId(id).stream()
                .map(commentMapper::mapToDto)
                .toList();
    }

    @Transactional
    @Override
    public CommentDto insert(String content, long bookId) {
        var saved = save(0, content, bookId);
        return commentMapper.mapToDto(saved);
    }

    @Transactional
    @Override
    public CommentDto update(long id, String content, long bookId) {
        var updated = save(id, content, bookId);
        return commentMapper.mapToDto(updated);
    }

    @Transactional
    @Override
    public void deleteById(long id) {
        commentRepository.deleteById(id);
    }

    private Comment save(long id, String content, long bookId) {
        if (isNull(content) || content.isBlank()) {
            throw new IllegalArgumentException("Comment content must not be null or empty");
        }

        var book = bookRepository.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException("Book with id %d not found".formatted(bookId)));

        var comment = new Comment(id, content, book);
        return commentRepository.save(comment);
    }
}
