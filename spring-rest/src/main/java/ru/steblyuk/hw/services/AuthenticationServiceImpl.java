package ru.steblyuk.hw.services;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;
import ru.steblyuk.hw.dto.AuthenticationRequest;
import ru.steblyuk.hw.dto.AuthenticationResponse;

@RequiredArgsConstructor
@Service
public class AuthenticationServiceImpl implements AuthenticationService {

    private final UserService userService;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @Override
    public AuthenticationResponse signIn(AuthenticationRequest request) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                request.login(),
                request.password()
        ));

        var user = userService.getDetailsService()
                .loadUserByUsername(request.login());

        var jwt = jwtService.generateToken(user);
        return new AuthenticationResponse(jwt);
    }
}
