package ru.steblyuk.hw.services;

import ru.steblyuk.hw.dto.GenreDto;

import java.util.List;
import java.util.Optional;

public interface GenreService {
    List<GenreDto> findAll();

    GenreDto findById(long id);

    GenreDto save(GenreDto genre);

    void deleteById(long id);
}
