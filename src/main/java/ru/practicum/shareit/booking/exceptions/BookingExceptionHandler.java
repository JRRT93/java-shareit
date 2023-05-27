package ru.practicum.shareit.booking.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.shareit.item.exceptions.WrongOwnerException;
import ru.practicum.shareit.user.exceptions.EntityNotFoundException;

import java.util.Map;

@RestControllerAdvice
public class BookingExceptionHandler {
    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> handleWrongOwner(final WrongOwnerException e) {
        return Map.of("Incorrect data", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleIncorrectStartEndDate(final IncorrectBookingStartEndDate e) {
        return Map.of("Incorrect data", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleNotAvailableItem(final ItemNotAvailableException e) {
        return Map.of("Incorrect data", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleUnknownState(final UnknownState e) {
        return Map.of("error", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleTryingToChangeStatusAfterConfirmation(final StatusAlreadyConfirmed e) {
        return Map.of("error", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> handleBookerAndOwnerAreSameUser(final BookerAndOwnerAreSameUser e) {
        return Map.of("error", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> handleEntityNotFound(final EntityNotFoundException e) {
        return Map.of("Incorrect data", e.getMessage());
    }
}