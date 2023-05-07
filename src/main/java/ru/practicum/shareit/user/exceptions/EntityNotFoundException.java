package ru.practicum.shareit.user.exceptions;

import java.io.IOException;

public class EntityNotFoundException extends IOException {
    public EntityNotFoundException(String message) {
        super(message);
    }
}