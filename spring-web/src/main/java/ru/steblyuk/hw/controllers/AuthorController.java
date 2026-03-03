package ru.steblyuk.hw.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import ru.steblyuk.hw.dto.AuthorDto;
import ru.steblyuk.hw.services.AuthorService;

import java.util.List;

@RequiredArgsConstructor
@Controller
public class AuthorController {

    private final AuthorService authorService;

    @GetMapping("/authors")
    public String getAll(Model model) {
        List<AuthorDto> authors = authorService.findAll();
        model.addAttribute("authors", authors);
        return "authors";
    }
}
