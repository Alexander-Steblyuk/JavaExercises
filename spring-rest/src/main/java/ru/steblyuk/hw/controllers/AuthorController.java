package ru.steblyuk.hw.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.steblyuk.hw.dto.AuthorDto;
import ru.steblyuk.hw.services.AuthorService;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("${books-api.context-path}")
public class AuthorController {

    private final AuthorService authorService;

    @GetMapping("/authors")
    public List<AuthorDto> getAll() {
        return authorService.findAll();
    }
}
