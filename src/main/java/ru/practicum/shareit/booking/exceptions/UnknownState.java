package ru.practicum.shareit.booking.exceptions;

public class UnknownState extends Exception {
    public UnknownState(String message) {
        super(message);
    }
}