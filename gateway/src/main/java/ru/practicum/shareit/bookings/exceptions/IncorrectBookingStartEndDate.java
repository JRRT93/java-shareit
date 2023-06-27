package ru.practicum.shareit.bookings.exceptions;

public class IncorrectBookingStartEndDate extends Exception {
    public IncorrectBookingStartEndDate(String message) {
        super(message);
    }
}