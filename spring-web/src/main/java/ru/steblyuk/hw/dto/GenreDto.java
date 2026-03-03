package ru.steblyuk.hw.dto;

public record GenreDto(Long id, String name) {
    @Override
    public String toString() {
        return "GenreDto[" +
                "id=" + id +
                "; name=" + name +
                ']';
    }
}
