package ru.steblyuk.hw.services;

import ru.steblyuk.hw.dto.AuthorDto;

import java.util.List;

public interface AuthorService {
    List<AuthorDto> findAll();

    AuthorDto findById(long id);

    AuthorDto save(AuthorDto authorDto);

    void deleteById(long id);
}
