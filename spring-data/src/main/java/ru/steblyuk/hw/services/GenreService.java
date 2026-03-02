package ru.steblyuk.hw.services;

import ru.steblyuk.hw.dto.GenreDto;

import java.util.List;

public interface GenreService {
    List<GenreDto> findAll();
}
