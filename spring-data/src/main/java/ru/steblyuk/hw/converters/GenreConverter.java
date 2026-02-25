package ru.steblyuk.hw.converters;

import org.springframework.stereotype.Component;
import ru.steblyuk.hw.dto.GenreDto;

@Component
public class GenreConverter {
    public String genreToString(GenreDto genre) {
        return "Id: %d, Name: %s".formatted(genre.id(), genre.name());
    }
}
