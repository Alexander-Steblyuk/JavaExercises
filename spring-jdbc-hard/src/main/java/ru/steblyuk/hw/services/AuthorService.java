package ru.steblyuk.hw.services;

import ru.steblyuk.hw.models.Author;

import java.util.List;

public interface AuthorService {
    List<Author> findAll();
}
