package ru.steblyuk.hw.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.steblyuk.hw.dto.BookDto;
import ru.steblyuk.hw.exceptions.EntityNotFoundException;
import ru.steblyuk.hw.mappers.BookDtoMapper;
import ru.steblyuk.hw.mappers.BookMapper;
import ru.steblyuk.hw.models.Author;
import ru.steblyuk.hw.repositories.BookRepository;

import java.util.List;

import static java.util.Optional.ofNullable;
import static org.springframework.util.CollectionUtils.isEmpty;

@RequiredArgsConstructor
@Service
public class BookServiceImpl implements BookService {

    private static final String BOOK_NOT_FOUND_ERROR_MESSAGE_TEMPLATE = "Book with id(%s) is not found!";

    private final BookRepository bookRepository;

    private final BookMapper bookMapper;

    private final BookDtoMapper bookDtoMapper;

    @Transactional(readOnly = true)
    @Override
    public BookDto findById(long id) {
        return bookRepository.findById(id)
                .map(bookMapper::mapToDto)
                .orElseThrow(() -> new EntityNotFoundException(BOOK_NOT_FOUND_ERROR_MESSAGE_TEMPLATE.formatted(id)));
    }

    @Transactional(readOnly = true)
    @Override
    public List<BookDto> findAll() {
        return bookRepository.findAll().stream()
                .map(bookMapper::mapToDto)
                .toList();
    }

    @Transactional
    @Override
    public BookDto save(BookDto bookDto) {
        var book = bookDtoMapper.mapToEntity(bookDto);

        if (isEmpty(book.getGenres())) {
            throw new IllegalArgumentException("Genres must not be empty or null");
        }

        ofNullable(book.getAuthor())
                .map(Author::getId)
                .orElseThrow(() -> new IllegalArgumentException("Book must have no empty author"));

        book = bookRepository.save(book);
        return bookMapper.mapToDto(book);
    }

    @Transactional
    @Override
    public void deleteById(long id) {
        bookRepository.deleteById(id);
    }
}
