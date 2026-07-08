package ru.steblyuk.hw.services;

import ru.steblyuk.hw.dto.BookDto;

import java.util.List;

public interface BookService {
    BookDto findById(long id);

    List<BookDto> findAll();

    BookDto save(BookDto bookDto);

    void deleteById(long id);
}
