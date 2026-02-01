package ru.steblyuk.hw.converters;

import org.springframework.stereotype.Component;
import ru.steblyuk.hw.models.Genre;

@Component
public class GenreConverter {
    public String genreToString(Genre genre) {
        return "Id: %d, Name: %s".formatted(genre.getId(), genre.getName());
    }
}
