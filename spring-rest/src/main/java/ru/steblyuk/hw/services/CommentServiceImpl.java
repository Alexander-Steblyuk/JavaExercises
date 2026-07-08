package ru.steblyuk.hw.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.steblyuk.hw.dto.CommentDto;
import ru.steblyuk.hw.exceptions.EntityNotFoundException;
import ru.steblyuk.hw.mappers.CommentDtoMapper;
import ru.steblyuk.hw.mappers.CommentMapper;
import ru.steblyuk.hw.models.Book;
import ru.steblyuk.hw.repositories.CommentRepository;

import java.util.List;

import static java.util.Objects.isNull;
import static java.util.Optional.ofNullable;

@RequiredArgsConstructor
@Service
public class CommentServiceImpl implements CommentService {

    private static final String COMMENT_NOT_FOUND_ERROR_MESSAGE_TEMPLATE = "Comment with id(%s) is not found!";

    private final CommentRepository commentRepository;

    private final CommentMapper commentMapper;

    private final CommentDtoMapper commentDtoMapper;

    @Transactional(readOnly = true)
    @Override
    public CommentDto findById(long id) {
        return commentRepository.findById(id)
                .map(commentMapper::mapToDto)
                .orElseThrow(() -> new EntityNotFoundException(COMMENT_NOT_FOUND_ERROR_MESSAGE_TEMPLATE.formatted(id)));
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
    public CommentDto save(CommentDto commentDto) {
        var comment = commentDtoMapper.mapToEntity(commentDto);
        if (isNull(comment.getContent()) || comment.getContent().isBlank()) {
            throw new IllegalArgumentException("Comment content must not be null or empty");
        }

        ofNullable(comment.getBook())
                .map(Book::getId)
                .orElseThrow(() -> new EntityNotFoundException("Book with id %d not found".formatted(commentDto.bookId())));
        comment = commentRepository.save(comment);
        return commentMapper.mapToDto(comment);
    }

    @Transactional
    @Override
    public void deleteById(long id) {
        commentRepository.deleteById(id);
    }
}
