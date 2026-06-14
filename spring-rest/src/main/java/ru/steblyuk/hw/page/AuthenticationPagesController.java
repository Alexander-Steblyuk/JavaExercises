package ru.steblyuk.hw.page;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AuthenticationPagesController {

    @GetMapping("/auth")
    public String getAuthors() {
        return "auth";
    }
}
