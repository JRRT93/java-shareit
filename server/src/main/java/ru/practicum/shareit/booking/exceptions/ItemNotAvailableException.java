package ru.practicum.shareit.booking.exceptions;

public class ItemNotAvailableException extends Exception {
    public ItemNotAvailableException(String message) {
        super(message);
    }
}