package ru.steblyuk.hw.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import ru.steblyuk.hw.dto.CommentDto;
import ru.steblyuk.hw.services.CommentService;

import java.util.List;

@RequiredArgsConstructor
@RestController
public class CommentController {

    private final CommentService commentService;

    @GetMapping("/books/{id}/comments")
    public List<CommentDto> getByBookId(@PathVariable Long id) {
        return commentService.findByBookId(id);
    }
}
