package ru.steblyuk.hw.services;

import ru.steblyuk.hw.dto.AuthenticationRequest;
import ru.steblyuk.hw.dto.AuthenticationResponse;

public interface AuthenticationService {
    AuthenticationResponse signIn(AuthenticationRequest request);
}
