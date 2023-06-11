package ru.practicum.shareit.booking.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoComplete;
import ru.practicum.shareit.booking.exceptions.*;
import ru.practicum.shareit.booking.model.State;
import ru.practicum.shareit.booking.services.BookingService;
import ru.practicum.shareit.item.exceptions.WrongOwnerException;
import ru.practicum.shareit.user.exceptions.EntityNotFoundException;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.Collection;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
@Validated
public class BookingController {
    private final BookingService bookingService;
    private final String xSharerUserId = "X-Sharer-User-Id";

    @PostMapping
    public BookingDtoComplete createBooking(@RequestHeader(xSharerUserId) Long userId,
                                            @RequestBody @Valid BookingDto bookingDto)
            throws EntityNotFoundException, ItemNotAvailableException, IncorrectBookingStartEndDate,
            BookerAndOwnerAreSameUser {
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
                                                                      @PositiveOrZero @RequestParam(value = "from", defaultValue = "0", required = false)
                                                                      Integer startingEntry,
                                                                      @Positive @RequestParam(value = "size", defaultValue = "10", required = false)
                                                                      Integer size)
            throws EntityNotFoundException, UnknownState {
        State state;
        try {
            state = State.valueOf(stateString);
        } catch (Exception e) {
            throw new UnknownState("Unknown state: UNSUPPORTED_STATUS");
        }
        log.info(String.format("GET request for /bookings?state=%s", state));
        return bookingService.findAllUsersBookingsByState(bookerId, state, startingEntry, size);
    }

    @GetMapping("/owner")
    public Collection<BookingDtoComplete> findAllOwnersBookingsByState(@RequestHeader(xSharerUserId) Long ownerId,
                                                                       @RequestParam(value = "state", defaultValue = "ALL") String stateString,
                                                                       @PositiveOrZero @RequestParam(value = "from", defaultValue = "0", required = false)
                                                                       Integer startingEntry,
                                                                       @Positive @RequestParam(value = "size", defaultValue = "10", required = false)
                                                                       Integer size)
            throws EntityNotFoundException, UnknownState {
        State state;
        try {
            state = State.valueOf(stateString);
        } catch (Exception e) {
            throw new UnknownState("Unknown state: UNSUPPORTED_STATUS");
        }
        log.info(String.format("GET request for /bookings/owner?state=%s", state));
        return bookingService.findAllOwnersBookingsByState(ownerId, state, startingEntry, size);
    }
}