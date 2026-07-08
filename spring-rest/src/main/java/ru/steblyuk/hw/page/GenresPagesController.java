package ru.steblyuk.hw.page;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class GenresPagesController {

    @GetMapping("/genres")
    public String getGenres() {
        return "genres";
    }
}
