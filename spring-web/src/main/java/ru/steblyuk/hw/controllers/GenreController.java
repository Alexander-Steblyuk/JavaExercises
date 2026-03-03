package ru.steblyuk.hw.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import ru.steblyuk.hw.dto.GenreDto;
import ru.steblyuk.hw.services.GenreService;

import java.util.List;

@RequiredArgsConstructor
@Controller
public class GenreController {

    private final GenreService genreService;

    @GetMapping("/genres")
    public String getAll(Model model) {
        List<GenreDto> genres = genreService.findAll();
        model.addAttribute("genres", genres);
        return "genres";
    }
}
