package ru.practicum.shareit.booking.services;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoComplete;
import ru.practicum.shareit.booking.exceptions.BookerAndOwnerAreSameUser;
import ru.practicum.shareit.booking.exceptions.ItemNotAvailableException;
import ru.practicum.shareit.booking.exceptions.StatusAlreadyConfirmed;
import ru.practicum.shareit.booking.model.State;
import ru.practicum.shareit.item.exceptions.WrongOwnerException;
import ru.practicum.shareit.user.exceptions.EntityNotFoundException;

import java.util.Collection;

@Service
public interface BookingService {
    BookingDtoComplete save(Long bookerId, BookingDto bookingDto) throws EntityNotFoundException,
            ItemNotAvailableException, BookerAndOwnerAreSameUser;

    BookingDtoComplete confirmBooking(Long ownerId, Long bookingId, boolean isApproved) throws EntityNotFoundException,
            WrongOwnerException, StatusAlreadyConfirmed;

    BookingDtoComplete findById(Long userId, Long bookingId) throws EntityNotFoundException, WrongOwnerException;

    Collection<BookingDtoComplete> findAllUsersBookingsByState(Long bookerId, State state, Pageable pageable)
            throws EntityNotFoundException;

    Collection<BookingDtoComplete> findAllOwnersBookingsByState(Long ownerId, State state, Pageable pageable)
            throws EntityNotFoundException;
}