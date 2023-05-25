package ru.practicum.shareit.item.exceptions;

import java.io.IOException;

public class WrongOwnerException extends IOException {
    public WrongOwnerException(String message) {
        super(message);
    }
}