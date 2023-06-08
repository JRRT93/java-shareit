package ru.practicum.shareit.booking.exceptions;

public class BookerAndOwnerAreSameUser extends Exception {
    public BookerAndOwnerAreSameUser(String message) {
        super(message);
    }
}