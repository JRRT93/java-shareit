package ru.practicum.shareit.booking.exceptions;

public class IncorrectBookingStartEndDate extends Exception {
    public IncorrectBookingStartEndDate(String message) {
        super(message);
    }
}