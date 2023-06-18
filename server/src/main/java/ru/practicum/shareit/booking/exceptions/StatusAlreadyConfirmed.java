package ru.practicum.shareit.booking.exceptions;

public class StatusAlreadyConfirmed extends Exception {
    public StatusAlreadyConfirmed(String message) {
        super(message);
    }
}