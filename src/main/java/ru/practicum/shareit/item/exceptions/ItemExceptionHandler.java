package ru.practicum.shareit.item.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class ItemExceptionHandler {
    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> handleWrongOwner(final WrongOwnerException e) {
        return Map.of("Incorrect data", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> CommentWithoutCompletedBooking(final CommentWithoutCompletedBooking e) {
        return Map.of("Incorrect data", e.getMessage());
    }
}