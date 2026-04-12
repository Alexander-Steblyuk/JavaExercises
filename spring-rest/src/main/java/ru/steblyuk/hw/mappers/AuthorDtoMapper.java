package ru.steblyuk.hw.mappers;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.steblyuk.hw.dto.AuthorDto;
import ru.steblyuk.hw.models.Author;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface AuthorDtoMapper {

    @Mapping(target = "id", source = "id")
    @Mapping(target = "fullName", source = "fullName")
    Author mapToEntity(AuthorDto author);
}
