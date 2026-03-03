package ru.steblyuk.hw.services;

import ru.steblyuk.hw.dto.AuthorDto;
import ru.steblyuk.hw.models.Author;

import java.util.List;
import java.util.Optional;

public interface AuthorService {
    List<AuthorDto> findAll();

    AuthorDto findById(long id);

    AuthorDto save(AuthorDto authorDto);

    void deleteById(long id);
}
