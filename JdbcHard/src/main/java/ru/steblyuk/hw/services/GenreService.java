package ru.steblyuk.hw.services;

import ru.steblyuk.hw.models.Genre;

import java.util.List;

public interface GenreService {
    List<Genre> findAll();
}
