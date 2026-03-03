package ru.steblyuk.hw.dto;

public record CommentDto(Long id, String content, Long bookId) {
    @Override
    public String toString() {
        return "CommentDto[" +
                "id=" + id +
                ", content=" + content +
                ", bookId=" + bookId +
                ']';
    }
}
