package ru.steblyuk.hw.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record BookDto(Long id,
                      @NotBlank(message = "Title must not be null or empty!") String title,
                      @NotNull(message = "Author must not be null!") AuthorDto author,
                      @NotEmpty(message = "Genres must not be null or empty!") List<GenreDto> genres) {
}
