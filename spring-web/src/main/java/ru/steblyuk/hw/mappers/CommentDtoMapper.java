package ru.steblyuk.hw.mappers;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;
import ru.steblyuk.hw.dto.CommentDto;
import ru.steblyuk.hw.models.Book;
import ru.steblyuk.hw.models.Comment;
import ru.steblyuk.hw.repositories.BookRepository;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public abstract class CommentDtoMapper {

    @Autowired
    private BookRepository bookRepository;

    @Mapping(target = "id", source = "id")
    @Mapping(target = "content", source = "content")
    @Mapping(target = "book", source = "bookId")
    public abstract Comment mapToEntity(CommentDto comment);

    protected Book map(Long id) {
        return bookRepository.findById(id)
                .orElse(null);
    }
}
