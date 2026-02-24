package ru.steblyuk.hw.dto;

import java.util.List;

public record BookDto(long id, String title, AuthorDto author, List<GenreDto> genres) {
}
