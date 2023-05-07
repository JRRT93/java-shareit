package ru.practicum.shareit.user.exceptions;

import java.io.IOException;

public class NotUniqueUserEmail extends IOException {
    public NotUniqueUserEmail(String message) {
        super(message);
    }
}