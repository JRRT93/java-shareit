package ru.practicum.shareit.booking.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoComplete;
import ru.practicum.shareit.booking.exceptions.BookerAndOwnerAreSameUser;
import ru.practicum.shareit.booking.exceptions.ItemNotAvailableException;
import ru.practicum.shareit.booking.exceptions.StatusAlreadyConfirmed;
import ru.practicum.shareit.booking.model.State;
import ru.practicum.shareit.booking.services.BookingService;
import ru.practicum.shareit.item.exceptions.WrongOwnerException;
import ru.practicum.shareit.user.exceptions.EntityNotFoundException;

import java.util.Collection;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
public class BookingController {
    private final BookingService bookingService;
    private final String xSharerUserId = "X-Sharer-User-Id";

    @PostMapping
    public BookingDtoComplete createBooking(@RequestHeader(xSharerUserId) Long userId,
                                            @RequestBody BookingDto bookingDto)
            throws EntityNotFoundException, ItemNotAvailableException, BookerAndOwnerAreSameUser {
        log.info("POST request for /bookings received");
        return bookingService.save(userId, bookingDto);
    }

    @PatchMapping("/{bookingId}")
    public BookingDtoComplete confirmBooking(@RequestHeader(xSharerUserId) Long ownerId, @PathVariable Long bookingId,
                                             @RequestParam(value = "approved") boolean isApproved)
            throws WrongOwnerException, EntityNotFoundException, StatusAlreadyConfirmed {
        log.info(String.format("PATCH request for /bookings/%d/approved=%s received", bookingId, isApproved));
        return bookingService.confirmBooking(ownerId, bookingId, isApproved);
    }

    @GetMapping("/{bookingId}")
    public BookingDtoComplete findById(@RequestHeader(xSharerUserId) Long userId, @PathVariable Long bookingId)
            throws WrongOwnerException, EntityNotFoundException {
        log.info(String.format("GET request for /bookings/%d received", bookingId));
        return bookingService.findById(userId, bookingId);
    }

    @GetMapping()
    public Collection<BookingDtoComplete> findAllUsersBookingsByState(@RequestHeader(xSharerUserId) Long bookerId,
                                                                      @RequestParam(value = "state", defaultValue = "ALL") String stateString,
                                                                      @RequestParam(value = "from", defaultValue = "0", required = false)
                                                                      Integer startingEntry,
                                                                      @RequestParam(value = "size", defaultValue = "10", required = false)
                                                                      Integer size)
            throws EntityNotFoundException {
        Pageable pageable;
        if (size != null && startingEntry != null) {
            pageable = PageRequest.of(startingEntry / size, size);
        } else {
            pageable = Pageable.unpaged();
        }
        log.info(String.format("GET request for /bookings?state=%s", stateString));
        return bookingService.findAllUsersBookingsByState(bookerId, State.valueOf(stateString), pageable);
    }

    @GetMapping("/owner")
    public Collection<BookingDtoComplete> findAllOwnersBookingsByState(@RequestHeader(xSharerUserId) Long ownerId,
                                                                       @RequestParam(value = "state", defaultValue = "ALL") String stateString,
                                                                       @RequestParam(value = "from", defaultValue = "0", required = false)
                                                                       Integer startingEntry,
                                                                       @RequestParam(value = "size", defaultValue = "10", required = false)
                                                                       Integer size)
            throws EntityNotFoundException {
        Pageable pageable;
        if (size != null && startingEntry != null) {
            pageable = PageRequest.of(startingEntry / size, size);
        } else {
            pageable = Pageable.unpaged();
        }
        log.info(String.format("GET request for /bookings/owner?state=%s", stateString));
        return bookingService.findAllOwnersBookingsByState(ownerId, State.valueOf(stateString), pageable);
    }
}