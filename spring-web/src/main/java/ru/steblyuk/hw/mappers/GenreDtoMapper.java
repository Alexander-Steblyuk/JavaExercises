package ru.steblyuk.hw.mappers;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.steblyuk.hw.dto.GenreDto;
import ru.steblyuk.hw.models.Genre;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface GenreDtoMapper {

    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    Genre mapToEntity(GenreDto genre);
}
