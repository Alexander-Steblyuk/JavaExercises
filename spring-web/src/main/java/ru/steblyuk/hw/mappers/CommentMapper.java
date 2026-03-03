package ru.steblyuk.hw.mappers;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.steblyuk.hw.dto.CommentDto;
import ru.steblyuk.hw.models.Comment;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface CommentMapper {

    @Mapping(target = "id", source = "id")
    @Mapping(target = "content", source = "content")
    @Mapping(target = "bookId", source = "book.id")
    CommentDto mapToDto(Comment comment);
}
