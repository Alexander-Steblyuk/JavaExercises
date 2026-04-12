package ru.steblyuk.hw.controllers.exceptionhandlers;

import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import ru.steblyuk.hw.exceptions.EntityNotFoundException;

@ControllerAdvice
public class BookExceptionHandler {

    private static final String NOT_FOUND_ERROR_MESSAGE_TEMPLATE = "Nothing was found by this id";

    @ExceptionHandler(exception = EntityNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleEntityNotFoundException(EntityNotFoundException e, Model model) {
        model.addAttribute("message", NOT_FOUND_ERROR_MESSAGE_TEMPLATE);
        return "error";
    }
}
