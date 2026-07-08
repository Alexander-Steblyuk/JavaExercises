package ru.steblyuk.hw.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.steblyuk.hw.dto.AuthorDto;
import ru.steblyuk.hw.exceptions.EntityNotFoundException;
import ru.steblyuk.hw.mappers.AuthorDtoMapper;
import ru.steblyuk.hw.mappers.AuthorMapper;
import ru.steblyuk.hw.repositories.AuthorRepository;

import java.util.List;

import static java.util.Objects.isNull;

@RequiredArgsConstructor
@Service
public class AuthorServiceImpl implements AuthorService {

    private static final String AUTHOR_NOT_FOUND_ERROR_MESSAGE_TEMPLATE = "Author with id(%s) is not found!";

    private final AuthorRepository authorRepository;

    private final AuthorMapper authorMapper;
    private final AuthorDtoMapper authorDtoMapper;

    @Transactional(readOnly = true)
    @Override
    public List<AuthorDto> findAll() {
        return authorRepository.findAll().stream()
                .map(authorMapper::mapToDto)
                .toList();
    }

    @Transactional(readOnly = true)
    @Override
    public AuthorDto findById(long id) {
        return authorRepository.findById(id)
                .map(authorMapper::mapToDto)
                .orElseThrow(() -> new EntityNotFoundException(AUTHOR_NOT_FOUND_ERROR_MESSAGE_TEMPLATE.formatted(id)));
    }

    @Transactional
    @Override
    public AuthorDto save(AuthorDto authorDto) {
        var author = authorDtoMapper.mapToEntity(authorDto);
        if (isNull(author.getFullName()) || author.getFullName().isBlank()) {
            throw new IllegalArgumentException("Author full name must not be null or empty");
        }
        author = authorRepository.save(author);
        return authorMapper.mapToDto(author);
    }

    @Transactional
    @Override
    public void deleteById(long id) {
        authorRepository.deleteById(id);
    }
}
