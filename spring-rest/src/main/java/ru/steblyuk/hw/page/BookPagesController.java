package ru.steblyuk.hw.page;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class BookPagesController {

    @GetMapping("/books")
    public String getBooks() {
        return "books";
    }

    @GetMapping("/books/{id}")
    public String getBook() {
        return "book";
    }

    @GetMapping({"/books/add", "/books/edit/{id}"})
    public String getBookEdit() {
        return "book-edit";
    }
}
