package ru.practicum.shareit.bookings.exceptions;

public class UnknownState extends Exception {
    public UnknownState(String message) {
        super(message);
    }
}