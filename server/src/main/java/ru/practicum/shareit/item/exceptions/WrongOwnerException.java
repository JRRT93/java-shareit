package ru.practicum.shareit.item.exceptions;

public class WrongOwnerException extends Exception {
    public WrongOwnerException(String message) {
        super(message);
    }
}