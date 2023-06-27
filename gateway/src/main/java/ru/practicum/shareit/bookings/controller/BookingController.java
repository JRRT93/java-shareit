package ru.practicum.shareit.bookings.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.bookings.client.BookingClient;
import ru.practicum.shareit.bookings.dto.BookingDto;
import ru.practicum.shareit.bookings.dto.State;
import ru.practicum.shareit.bookings.exceptions.IncorrectBookingStartEndDate;
import ru.practicum.shareit.bookings.exceptions.UnknownState;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
@Validated
public class BookingController {
    private final BookingClient bookingClient;
    private final String xSharerUserId = "X-Sharer-User-Id";

    @PostMapping
    public ResponseEntity<Object> createBooking(@RequestHeader(xSharerUserId) Long userId,
                                                @RequestBody @Valid BookingDto bookingDto)
            throws IncorrectBookingStartEndDate {
        checkStartEndDates(bookingDto);
        log.info("POST request for /bookings received");
        return bookingClient.createBooking(userId, bookingDto);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> confirmBooking(@RequestHeader(xSharerUserId) Long ownerId, @PathVariable Long bookingId,
                                                 @RequestParam(value = "approved") boolean isApproved) {
        log.info(String.format("PATCH request for /bookings/%d/approved=%s received", bookingId, isApproved));
        return bookingClient.confirmBooking(ownerId, bookingId, isApproved);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> findById(@RequestHeader(xSharerUserId) Long userId, @PathVariable Long bookingId) {
        log.info(String.format("GET request for /bookings/%d received", bookingId));
        return bookingClient.findById(userId, bookingId);
    }

    @GetMapping()
    public ResponseEntity<Object> findAllUsersBookingsByState(@RequestHeader(xSharerUserId) Long bookerId,
                                                              @RequestParam(value = "state", defaultValue = "ALL") String stateString,
                                                              @PositiveOrZero @RequestParam(value = "from", defaultValue = "0", required = false)
                                                              Integer startingEntry,
                                                              @Positive @RequestParam(value = "size", defaultValue = "10", required = false)
                                                              Integer size)
            throws UnknownState {
        State state = checkAndSetStatus(stateString);
        log.info(String.format("GET request for /bookings?state=%s", state));
        return bookingClient.findAllUsersBookingsByState(bookerId, state, startingEntry, size);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> findAllOwnersBookingsByState(@RequestHeader(xSharerUserId) Long ownerId,
                                                               @RequestParam(value = "state", defaultValue = "ALL") String stateString,
                                                               @PositiveOrZero @RequestParam(value = "from", defaultValue = "0", required = false)
                                                               Integer startingEntry,
                                                               @Positive @RequestParam(value = "size", defaultValue = "10", required = false)
                                                               Integer size)
            throws UnknownState {
        State state = checkAndSetStatus(stateString);
        log.info(String.format("GET request for /bookings/owner?state=%s", state));
        return bookingClient.findAllOwnersBookingsByState(ownerId, state, startingEntry, size);
    }

    private void checkStartEndDates(BookingDto bookingDto) throws IncorrectBookingStartEndDate {
        LocalDateTime start = bookingDto.getStart();
        LocalDateTime end = bookingDto.getEnd();
        LocalDateTime now = LocalDateTime.now();
        if (end.isBefore(start) || end.isBefore(now) || end.equals(start) || start.isBefore(now)) {
            throw new IncorrectBookingStartEndDate("Check dates, please");
        }
    }

    private State checkAndSetStatus(String stateString) throws UnknownState {
        try {
            return State.valueOf(stateString);
        } catch (Exception e) {
            throw new UnknownState("Unknown state: UNSUPPORTED_STATUS");
        }
    }
}