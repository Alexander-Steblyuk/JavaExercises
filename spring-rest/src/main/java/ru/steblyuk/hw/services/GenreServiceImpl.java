package ru.steblyuk.hw.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.steblyuk.hw.dto.GenreDto;
import ru.steblyuk.hw.exceptions.EntityNotFoundException;
import ru.steblyuk.hw.mappers.GenreDtoMapper;
import ru.steblyuk.hw.mappers.GenreMapper;
import ru.steblyuk.hw.repositories.GenreRepository;

import java.util.List;

import static java.util.Objects.isNull;

@RequiredArgsConstructor
@Service
public class GenreServiceImpl implements GenreService {

    private static final String GENRE_NOT_FOUND_ERROR_MESSAGE_TEMPLATE = "Genre with id(%s) is not found!";

    private final GenreRepository genreRepository;

    private final GenreMapper genreMapper;

    private final GenreDtoMapper genreDtoMapper;

    @Transactional(readOnly = true)
    @Override
    public List<GenreDto> findAll() {
        return genreRepository.findAll().stream()
                .map(genreMapper::mapToDto)
                .toList();
    }

    @Transactional(readOnly = true)
    @Override
    public GenreDto findById(long id) {
        return genreRepository.findById(id)
                .map(genreMapper::mapToDto)
                .orElseThrow(() -> new EntityNotFoundException(GENRE_NOT_FOUND_ERROR_MESSAGE_TEMPLATE.formatted(id)));
    }

    @Transactional
    @Override
    public GenreDto save(GenreDto genreDto) {
        var genre = genreDtoMapper.mapToEntity(genreDto);
        if (isNull(genre.getName()) || genre.getName().isBlank()) {
            throw new IllegalArgumentException("Genre name must not be null or empty");
        }
        genre = genreRepository.save(genre);
        return genreMapper.mapToDto(genre);
    }

    @Transactional
    @Override
    public void deleteById(long id) {
        genreRepository.deleteById(id);
    }
}
