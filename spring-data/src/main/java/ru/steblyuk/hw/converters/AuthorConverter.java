package ru.steblyuk.hw.converters;

import org.springframework.stereotype.Component;
import ru.steblyuk.hw.dto.AuthorDto;

@Component
public class AuthorConverter {
    public String authorToString(AuthorDto author) {
        return "Id: %d, FullName: %s".formatted(author.id(), author.fullName());
    }
}
