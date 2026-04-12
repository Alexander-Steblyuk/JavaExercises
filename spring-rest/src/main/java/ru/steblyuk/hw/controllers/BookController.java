package ru.steblyuk.hw.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import ru.steblyuk.hw.dto.BookDto;
import ru.steblyuk.hw.services.BookService;

import java.util.List;

@RequiredArgsConstructor
@RestController
public class BookController {

    private final BookService bookService;

    @GetMapping("/books")
    public List<BookDto> getAll() {
        return bookService.findAll();
    }

    @GetMapping("/books/{id}")
    public BookDto getById(@PathVariable Long id) {
        return bookService.findById(id);
    }

    @PostMapping("/books")
    public BookDto create(@RequestBody @Valid BookDto bookDto) {
        return bookService.save(bookDto);
    }

    @PutMapping("/books")
    public BookDto update(@RequestBody @Valid BookDto bookDto) {
        return bookService.save(bookDto);
    }

    @DeleteMapping("/books/{id}")
    public void delete(@PathVariable Long id) {
        bookService.deleteById(id);
    }
}
