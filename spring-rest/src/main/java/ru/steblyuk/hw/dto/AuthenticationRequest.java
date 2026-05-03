package ru.steblyuk.hw.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AuthenticationRequest(@NotBlank @Size(min = 3, max = 255) String login,
                                    @NotBlank @Size(min = 4, max = 255) String password) {
}
