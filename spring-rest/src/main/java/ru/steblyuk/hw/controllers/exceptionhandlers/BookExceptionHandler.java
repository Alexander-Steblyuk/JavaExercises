package ru.steblyuk.hw.controllers.exceptionhandlers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.steblyuk.hw.exceptions.EntityNotFoundException;

import java.util.Map;

@RestControllerAdvice
public class BookExceptionHandler {

    private static final String NOT_FOUND_ERROR_MESSAGE_TEMPLATE = "Nothing was found by this id";

    @ExceptionHandler(exception = EntityNotFoundException.class)
    public ResponseEntity<?> handleEntityNotFoundException(EntityNotFoundException e) {
        Map<String, Object> contentMap = Map.of("message", NOT_FOUND_ERROR_MESSAGE_TEMPLATE);
        return new ResponseEntity<>(contentMap, HttpStatus.NOT_FOUND);
    }
}
