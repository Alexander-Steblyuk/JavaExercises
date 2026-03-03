package ru.steblyuk.hw.dto;

public record AuthorDto(Long id, String fullName) {
    @Override
    public String toString() {
        return "AuthorDto[" +
                "id=" + id +
                "; fullName=" + fullName +
                ']';
    }
}
