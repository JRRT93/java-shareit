package ru.practicum.shareit.bookings.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.validation.ValidationException;
import java.util.Map;

@RestControllerAdvice
public class BookingExceptionHandler {
    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleUserValidationException(final ValidationException e) {
        return Map.of("Incorrect data", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleIncorrectStartEndDate(final IncorrectBookingStartEndDate e) {
        return Map.of("Incorrect data", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleUnknownState(final UnknownState e) {
        return Map.of("error", e.getMessage());
    }
}