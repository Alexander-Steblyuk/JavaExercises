package ru.steblyuk.hw.page;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AuthorPagesController {

    @GetMapping("/authors")
    public String getAuthors() {
        return "authors";
    }
}
