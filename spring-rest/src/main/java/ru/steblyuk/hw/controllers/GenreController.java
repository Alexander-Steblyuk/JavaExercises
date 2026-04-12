package ru.steblyuk.hw.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.steblyuk.hw.dto.GenreDto;
import ru.steblyuk.hw.services.GenreService;

import java.util.List;

@RequiredArgsConstructor
@RestController
public class GenreController {

    private final GenreService genreService;

    @GetMapping("/genres")
    public List<GenreDto> getAll() {
        return genreService.findAll();
    }
}
