package ru.steblyuk.hw.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import ru.steblyuk.hw.dto.BookDto;
import ru.steblyuk.hw.services.AuthorService;
import ru.steblyuk.hw.services.BookService;
import ru.steblyuk.hw.services.CommentService;
import ru.steblyuk.hw.services.GenreService;

import java.util.List;

@RequiredArgsConstructor
@Controller
public class BookController {
    
    private static final String BOOKS_MODEL_ATTRIBUTE_NAME = "books";
    private static final String ALL_AUTHORS_MODEL_ATTRIBUTE_NAME = "allAuthors";
    private static final String ALL_GENRES_MODEL_ATTRIBUTE_NAME = "allGenres";
    private static final String BOOK_MODEL_ATTRIBUTE_NAME = "book";
    private static final String COMMENTS_MODEL_ATTRIBUTE_NAME = "comments";

    private final BookService bookService;

    private final AuthorService authorService;

    private final GenreService genreService;

    private final CommentService commentService;

    @GetMapping("/books")
    public String getAll(Model model) {
        List<BookDto> books = bookService.findAll();
        model.addAttribute(BOOKS_MODEL_ATTRIBUTE_NAME, books);
        return "books";
    }

    @GetMapping("/books/{id}")
    public String getById(@PathVariable String id, Model model) {
        BookDto book = bookService.findById(Long.parseLong(id));
        model.addAttribute(BOOK_MODEL_ATTRIBUTE_NAME, book);
        model.addAttribute(COMMENTS_MODEL_ATTRIBUTE_NAME, commentService.findByBookId(book.id()));
        return "book";
    }

    @GetMapping("/books/add")
    public String getNewBookForAdd(Model model) {
        model.addAttribute(BOOK_MODEL_ATTRIBUTE_NAME, BookDto.EMPTY);
        model.addAttribute(ALL_AUTHORS_MODEL_ATTRIBUTE_NAME, authorService.findAll());
        model.addAttribute(ALL_GENRES_MODEL_ATTRIBUTE_NAME, genreService.findAll());
        return "book-edit";
    }

    @GetMapping("/books/edit/{id}")
    public String getBookForEdit(@PathVariable String id, Model model) {
        BookDto book = bookService.findById(Long.parseLong(id));
        model.addAttribute(BOOK_MODEL_ATTRIBUTE_NAME, book);
        model.addAttribute(ALL_AUTHORS_MODEL_ATTRIBUTE_NAME, authorService.findAll());
        model.addAttribute(ALL_GENRES_MODEL_ATTRIBUTE_NAME, genreService.findAll());
        return "book-edit";
    }

    @PostMapping("/books/save")
    public String save(@ModelAttribute("book") @Valid BookDto bookDto, BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute(ALL_AUTHORS_MODEL_ATTRIBUTE_NAME, authorService.findAll());
            model.addAttribute(ALL_GENRES_MODEL_ATTRIBUTE_NAME, genreService.findAll());
            return "book-edit";
        }
        bookService.save(bookDto);
        return "redirect:/books";
    }

    @DeleteMapping("/books/{id}")
    public String delete(@PathVariable String id) {
        bookService.deleteById(Long.parseLong(id));
        return "redirect:/books";
    }
}
