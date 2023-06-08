package ru.practicum.shareit.item.exceptions;

public class CommentWithoutCompletedBooking extends Exception {
    public CommentWithoutCompletedBooking(String message) {
        super(message);
    }
}