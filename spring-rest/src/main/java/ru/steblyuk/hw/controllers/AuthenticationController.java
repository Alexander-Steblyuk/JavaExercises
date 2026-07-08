package ru.steblyuk.hw.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.steblyuk.hw.dto.AuthenticationRequest;
import ru.steblyuk.hw.dto.AuthenticationResponse;
import ru.steblyuk.hw.services.AuthenticationService;

@RequiredArgsConstructor
@RestController
@RequestMapping("${books-api.context-path}")
public class AuthenticationController {
    private final AuthenticationService authenticationService;

    @PostMapping("/auth/sign-in")
    public AuthenticationResponse signIn(@RequestBody @Valid AuthenticationRequest request) {
        return authenticationService.signIn(request);
    }
}
