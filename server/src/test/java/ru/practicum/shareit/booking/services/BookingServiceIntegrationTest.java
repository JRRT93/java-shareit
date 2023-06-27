package ru.practicum.shareit.booking.services;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDtoComplete;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.State;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.exceptions.CommentWithoutCompletedBooking;
import ru.practicum.shareit.item.repository.ItemJpaRepository;
import ru.practicum.shareit.item.services.ItemService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.exceptions.EntityNotFoundException;
import ru.practicum.shareit.user.exceptions.NotUniqueUserEmail;
import ru.practicum.shareit.user.repository.UserJpaRepository;
import ru.practicum.shareit.user.services.UserService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
@SpringBootTest(
        properties = "db.name=test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class BookingServiceIntegrationTest {
    @Autowired
    BookingService bookingService;
    @Autowired
    ItemService itemService;
    @Autowired
    UserService userService;
    @Autowired
    ItemJpaRepository itemRepository;
    @Autowired
    UserJpaRepository userRepository;
    @Autowired
    BookingRepository bookingRepository;

    @Test
    void pastFindAllUsersBookingsByNoPagination() throws EntityNotFoundException {
        List<BookingDtoComplete> bookings = new ArrayList<>(
                bookingService.findAllUsersBookingsByState(2L, State.PAST, Pageable.unpaged()));
        List<Long> bookingsId = bookings.stream().map(BookingDtoComplete::getId).collect(Collectors.toList());

        assertEquals(1, bookings.size());
        assertTrue(bookingsId.contains(1L));

        assertEquals(LocalDateTime.of(2000, 1, 1, 1, 1, 1, 1),
                bookings.get(0).getStart());
        assertEquals(LocalDateTime.of(2001, 1, 1, 1, 1, 1, 1),
                bookings.get(0).getEnd());
        Assertions.assertEquals(1L, bookings.get(0).getItem().getId());
        assertEquals(Status.APPROVED, bookings.get(0).getStatus());
    }

    @Test
    void currentFindAllUsersBookingsByNoPagination() throws EntityNotFoundException {
        List<BookingDtoComplete> bookings = new ArrayList<>(
                bookingService.findAllUsersBookingsByState(3L, State.CURRENT, Pageable.unpaged()));
        List<Long> bookingsId = bookings.stream().map(BookingDtoComplete::getId).collect(Collectors.toList());

        assertEquals(1, bookings.size());
        assertTrue(bookingsId.contains(6L));

        assertEquals(LocalDateTime.of(2022, 1, 1, 1, 1, 1, 1),
                bookings.get(0).getStart());
        assertEquals(LocalDateTime.of(2039, 1, 1, 1, 1, 1, 1),
                bookings.get(0).getEnd());
        Assertions.assertEquals(2L, bookings.get(0).getItem().getId());
        assertEquals(Status.APPROVED, bookings.get(0).getStatus());
    }

    @Test
    void futureFindAllUsersBookingsByNoPagination() throws EntityNotFoundException {
        List<BookingDtoComplete> bookings = new ArrayList<>(
                bookingService.findAllUsersBookingsByState(3L, State.FUTURE, Pageable.unpaged()));
        List<Long> bookingsId = bookings.stream().map(BookingDtoComplete::getId).collect(Collectors.toList());

        assertEquals(2, bookings.size());
        assertTrue(bookingsId.contains(5L));
        assertTrue(bookingsId.contains(4L));

        assertEquals(LocalDateTime.of(2035, 1, 1, 1, 1, 1, 1),
                bookings.get(0).getStart());
        assertEquals(LocalDateTime.of(2040, 1, 1, 1, 1, 1, 1),
                bookings.get(0).getEnd());
        Assertions.assertEquals(2L, bookings.get(0).getItem().getId());
        assertEquals(Status.WAITING, bookings.get(0).getStatus());

        assertEquals(LocalDateTime.of(2029, 1, 1, 1, 1, 1, 1),
                bookings.get(1).getStart());
        assertEquals(LocalDateTime.of(2032, 1, 1, 1, 1, 1, 1),
                bookings.get(1).getEnd());
        Assertions.assertEquals(1L, bookings.get(1).getItem().getId());
        assertEquals(Status.APPROVED, bookings.get(1).getStatus());
    }

    @Test
    void waitingFindAllUsersBookingsByNoPagination() throws EntityNotFoundException {
        List<BookingDtoComplete> bookings = new ArrayList<>(
                bookingService.findAllUsersBookingsByState(2L, State.WAITING, Pageable.unpaged()));
        List<Long> bookingsId = bookings.stream().map(BookingDtoComplete::getId).collect(Collectors.toList());

        assertEquals(1, bookings.size());
        assertTrue(bookingsId.contains(2L));

        assertEquals(LocalDateTime.of(2024, 1, 1, 1, 1, 1, 1),
                bookings.get(0).getStart());
        assertEquals(LocalDateTime.of(2025, 1, 1, 1, 1, 1, 1),
                bookings.get(0).getEnd());
        Assertions.assertEquals(1L, bookings.get(0).getItem().getId());
        assertEquals(Status.WAITING, bookings.get(0).getStatus());
    }

    @Test
    void rejectedFindAllUsersBookingsByNoPagination() throws EntityNotFoundException {
        List<BookingDtoComplete> bookings = new ArrayList<>(
                bookingService.findAllUsersBookingsByState(2L, State.REJECTED, Pageable.unpaged()));
        List<Long> bookingsId = bookings.stream().map(BookingDtoComplete::getId).collect(Collectors.toList());

        assertEquals(1, bookings.size());
        assertTrue(bookingsId.contains(3L));

        assertEquals(LocalDateTime.of(2030, 1, 1, 1, 1, 1, 1),
                bookings.get(0).getStart());
        assertEquals(LocalDateTime.of(2031, 1, 1, 1, 1, 1, 1),
                bookings.get(0).getEnd());
        Assertions.assertEquals(1L, bookings.get(0).getItem().getId());
        assertEquals(Status.REJECTED, bookings.get(0).getStatus());
    }

    @Test
    void allFindAllUsersBookingsByNoPagination() throws EntityNotFoundException {
        List<BookingDtoComplete> bookings = new ArrayList<>(
                bookingService.findAllUsersBookingsByState(2L, State.ALL, Pageable.unpaged()));
        List<Long> bookingsId = bookings.stream().map(BookingDtoComplete::getId).collect(Collectors.toList());

        assertEquals(3, bookings.size());
        assertTrue(bookingsId.contains(1L));
        assertTrue(bookingsId.contains(2L));
        assertTrue(bookingsId.contains(3L));

        assertEquals(LocalDateTime.of(2030, 1, 1, 1, 1, 1, 1),
                bookings.get(0).getStart());
        assertEquals(LocalDateTime.of(2031, 1, 1, 1, 1, 1, 1),
                bookings.get(0).getEnd());
        Assertions.assertEquals(1L, bookings.get(0).getItem().getId());
        assertEquals(Status.REJECTED, bookings.get(0).getStatus());

        assertEquals(LocalDateTime.of(2024, 1, 1, 1, 1, 1, 1),
                bookings.get(1).getStart());
        assertEquals(LocalDateTime.of(2025, 1, 1, 1, 1, 1, 1),
                bookings.get(1).getEnd());
        Assertions.assertEquals(1L, bookings.get(1).getItem().getId());
        assertEquals(Status.WAITING, bookings.get(1).getStatus());

        assertEquals(LocalDateTime.of(2000, 1, 1, 1, 1, 1, 1),
                bookings.get(2).getStart());
        assertEquals(LocalDateTime.of(2001, 1, 1, 1, 1, 1, 1),
                bookings.get(2).getEnd());
        Assertions.assertEquals(1L, bookings.get(2).getItem().getId());
        assertEquals(Status.APPROVED, bookings.get(2).getStatus());
    }

    @Test
    void pastFindAllUsersBookingsByPagination() throws EntityNotFoundException {
        List<BookingDtoComplete> bookings = new ArrayList<>(
                bookingService.findAllUsersBookingsByState(2L, State.PAST, PageRequest.of(0, 1)));
        List<Long> bookingsId = bookings.stream().map(BookingDtoComplete::getId).collect(Collectors.toList());

        assertEquals(1, bookings.size());
        assertTrue(bookingsId.contains(1L));

        assertEquals(LocalDateTime.of(2000, 1, 1, 1, 1, 1, 1),
                bookings.get(0).getStart());
        assertEquals(LocalDateTime.of(2001, 1, 1, 1, 1, 1, 1),
                bookings.get(0).getEnd());
        Assertions.assertEquals(1L, bookings.get(0).getItem().getId());
        assertEquals(Status.APPROVED, bookings.get(0).getStatus());
    }

    @Test
    void currentFindAllUsersBookingsByPagination() throws EntityNotFoundException {
        List<BookingDtoComplete> bookings = new ArrayList<>(
                bookingService.findAllUsersBookingsByState(3L, State.CURRENT, PageRequest.of(0, 1)));
        List<Long> bookingsId = bookings.stream().map(BookingDtoComplete::getId).collect(Collectors.toList());

        assertEquals(1, bookings.size());
        assertTrue(bookingsId.contains(6L));

        assertEquals(LocalDateTime.of(2022, 1, 1, 1, 1, 1, 1),
                bookings.get(0).getStart());
        assertEquals(LocalDateTime.of(2039, 1, 1, 1, 1, 1, 1),
                bookings.get(0).getEnd());
        Assertions.assertEquals(2L, bookings.get(0).getItem().getId());
        assertEquals(Status.APPROVED, bookings.get(0).getStatus());
    }

    @Test
    void futureFindAllUsersBookingsByPagination() throws EntityNotFoundException {
        List<BookingDtoComplete> bookings = new ArrayList<>(
                bookingService.findAllUsersBookingsByState(3L, State.FUTURE, PageRequest.of(1, 1)));
        List<Long> bookingsId = bookings.stream().map(BookingDtoComplete::getId).collect(Collectors.toList());

        assertEquals(1, bookings.size());
        assertTrue(bookingsId.contains(4L));

        assertEquals(LocalDateTime.of(2029, 1, 1, 1, 1, 1, 1),
                bookings.get(0).getStart());
        assertEquals(LocalDateTime.of(2032, 1, 1, 1, 1, 1, 1),
                bookings.get(0).getEnd());
        Assertions.assertEquals(1L, bookings.get(0).getItem().getId());
        assertEquals(Status.APPROVED, bookings.get(0).getStatus());
    }

    @Test
    void waitingFindAllUsersBookingsByPagination() throws EntityNotFoundException {
        List<BookingDtoComplete> bookings = new ArrayList<>(
                bookingService.findAllUsersBookingsByState(2L, State.WAITING, PageRequest.of(0, 1)));
        List<Long> bookingsId = bookings.stream().map(BookingDtoComplete::getId).collect(Collectors.toList());

        assertEquals(1, bookings.size());
        assertTrue(bookingsId.contains(2L));

        assertEquals(LocalDateTime.of(2024, 1, 1, 1, 1, 1, 1),
                bookings.get(0).getStart());
        assertEquals(LocalDateTime.of(2025, 1, 1, 1, 1, 1, 1),
                bookings.get(0).getEnd());
        Assertions.assertEquals(1L, bookings.get(0).getItem().getId());
        assertEquals(Status.WAITING, bookings.get(0).getStatus());
    }

    @Test
    void rejectedFindAllUsersBookingsByPagination() throws EntityNotFoundException {
        List<BookingDtoComplete> bookings = new ArrayList<>(
                bookingService.findAllUsersBookingsByState(2L, State.REJECTED, PageRequest.of(0, 1)));
        List<Long> bookingsId = bookings.stream().map(BookingDtoComplete::getId).collect(Collectors.toList());

        assertEquals(1, bookings.size());
        assertTrue(bookingsId.contains(3L));

        assertEquals(LocalDateTime.of(2030, 1, 1, 1, 1, 1, 1),
                bookings.get(0).getStart());
        assertEquals(LocalDateTime.of(2031, 1, 1, 1, 1, 1, 1),
                bookings.get(0).getEnd());
        Assertions.assertEquals(1L, bookings.get(0).getItem().getId());
        assertEquals(Status.REJECTED, bookings.get(0).getStatus());
    }

    @Test
    void allFindAllUsersBookingsByPagination() throws EntityNotFoundException {
        List<BookingDtoComplete> bookings = new ArrayList<>(
                bookingService.findAllUsersBookingsByState(2L, State.ALL, PageRequest.of(0, 2)));
        List<Long> bookingsId = bookings.stream().map(BookingDtoComplete::getId).collect(Collectors.toList());

        assertEquals(2, bookings.size());
        assertTrue(bookingsId.contains(2L));
        assertTrue(bookingsId.contains(3L));

        assertEquals(LocalDateTime.of(2030, 1, 1, 1, 1, 1, 1),
                bookings.get(0).getStart());
        assertEquals(LocalDateTime.of(2031, 1, 1, 1, 1, 1, 1),
                bookings.get(0).getEnd());
        Assertions.assertEquals(1L, bookings.get(0).getItem().getId());
        assertEquals(Status.REJECTED, bookings.get(0).getStatus());

        assertEquals(LocalDateTime.of(2024, 1, 1, 1, 1, 1, 1),
                bookings.get(1).getStart());
        assertEquals(LocalDateTime.of(2025, 1, 1, 1, 1, 1, 1),
                bookings.get(1).getEnd());
        Assertions.assertEquals(1L, bookings.get(1).getItem().getId());
        assertEquals(Status.WAITING, bookings.get(1).getStatus());
    }

    @Test
    void pastFindAllOwnersBookingsByNoPagination() throws EntityNotFoundException {
        List<BookingDtoComplete> bookings = new ArrayList<>(
                bookingService.findAllOwnersBookingsByState(1L, State.PAST, Pageable.unpaged()));
        List<Long> bookingsId = bookings.stream().map(BookingDtoComplete::getId).collect(Collectors.toList());

        assertEquals(1, bookings.size());
        assertTrue(bookingsId.contains(1L));

        assertEquals(LocalDateTime.of(2000, 1, 1, 1, 1, 1, 1),
                bookings.get(0).getStart());
        assertEquals(LocalDateTime.of(2001, 1, 1, 1, 1, 1, 1),
                bookings.get(0).getEnd());
        Assertions.assertEquals(1L, bookings.get(0).getItem().getId());
        assertEquals(Status.APPROVED, bookings.get(0).getStatus());
    }

    @Test
    void currentFindAllOwnersBookingsByNoPagination() throws EntityNotFoundException {
        List<BookingDtoComplete> bookings = new ArrayList<>(
                bookingService.findAllOwnersBookingsByState(1L, State.CURRENT, Pageable.unpaged()));
        List<Long> bookingsId = bookings.stream().map(BookingDtoComplete::getId).collect(Collectors.toList());

        assertEquals(1, bookings.size());
        assertTrue(bookingsId.contains(6L));

        assertEquals(LocalDateTime.of(2022, 1, 1, 1, 1, 1, 1),
                bookings.get(0).getStart());
        assertEquals(LocalDateTime.of(2039, 1, 1, 1, 1, 1, 1),
                bookings.get(0).getEnd());
        Assertions.assertEquals(2L, bookings.get(0).getItem().getId());
        assertEquals(Status.APPROVED, bookings.get(0).getStatus());
    }

    @Test
    void futureFindAllOwnersBookingsByNoPagination() throws EntityNotFoundException {
        List<BookingDtoComplete> bookings = new ArrayList<>(
                bookingService.findAllOwnersBookingsByState(1L, State.FUTURE, Pageable.unpaged()));
        List<Long> bookingsId = bookings.stream().map(BookingDtoComplete::getId).collect(Collectors.toList());

        assertEquals(3, bookings.size());
        assertTrue(bookingsId.contains(5L));
        assertTrue(bookingsId.contains(4L));
        assertTrue(bookingsId.contains(2L));

        assertEquals(LocalDateTime.of(2035, 1, 1, 1, 1, 1, 1),
                bookings.get(0).getStart());
        assertEquals(LocalDateTime.of(2040, 1, 1, 1, 1, 1, 1),
                bookings.get(0).getEnd());
        Assertions.assertEquals(2L, bookings.get(0).getItem().getId());
        assertEquals(Status.WAITING, bookings.get(0).getStatus());

        assertEquals(LocalDateTime.of(2029, 1, 1, 1, 1, 1, 1),
                bookings.get(1).getStart());
        assertEquals(LocalDateTime.of(2032, 1, 1, 1, 1, 1, 1),
                bookings.get(1).getEnd());
        Assertions.assertEquals(1L, bookings.get(1).getItem().getId());
        assertEquals(Status.APPROVED, bookings.get(1).getStatus());

        assertEquals(LocalDateTime.of(2024, 1, 1, 1, 1, 1, 1),
                bookings.get(2).getStart());
        assertEquals(LocalDateTime.of(2025, 1, 1, 1, 1, 1, 1),
                bookings.get(2).getEnd());
        Assertions.assertEquals(1L, bookings.get(2).getItem().getId());
        assertEquals(Status.WAITING, bookings.get(2).getStatus());
    }

    @Test
    void waitingFindAllOwnersBookingsByNoPagination() throws EntityNotFoundException {
        List<BookingDtoComplete> bookings = new ArrayList<>(
                bookingService.findAllOwnersBookingsByState(1L, State.WAITING, Pageable.unpaged()));
        List<Long> bookingsId = bookings.stream().map(BookingDtoComplete::getId).collect(Collectors.toList());

        assertEquals(2, bookings.size());
        assertTrue(bookingsId.contains(2L));
        assertTrue(bookingsId.contains(5L));

        assertEquals(LocalDateTime.of(2035, 1, 1, 1, 1, 1, 1),
                bookings.get(0).getStart());
        assertEquals(LocalDateTime.of(2040, 1, 1, 1, 1, 1, 1),
                bookings.get(0).getEnd());
        Assertions.assertEquals(2L, bookings.get(0).getItem().getId());
        assertEquals(Status.WAITING, bookings.get(0).getStatus());

        assertEquals(LocalDateTime.of(2024, 1, 1, 1, 1, 1, 1),
                bookings.get(1).getStart());
        assertEquals(LocalDateTime.of(2025, 1, 1, 1, 1, 1, 1),
                bookings.get(1).getEnd());
        Assertions.assertEquals(1L, bookings.get(1).getItem().getId());
        assertEquals(Status.WAITING, bookings.get(1).getStatus());
    }

    @Test
    void rejectedFindAllOwnersBookingsByNoPagination() throws EntityNotFoundException {
        List<BookingDtoComplete> bookings = new ArrayList<>(
                bookingService.findAllOwnersBookingsByState(1L, State.REJECTED, Pageable.unpaged()));
        List<Long> bookingsId = bookings.stream().map(BookingDtoComplete::getId).collect(Collectors.toList());

        assertEquals(1, bookings.size());
        assertTrue(bookingsId.contains(3L));

        assertEquals(LocalDateTime.of(2030, 1, 1, 1, 1, 1, 1),
                bookings.get(0).getStart());
        assertEquals(LocalDateTime.of(2031, 1, 1, 1, 1, 1, 1),
                bookings.get(0).getEnd());
        Assertions.assertEquals(1L, bookings.get(0).getItem().getId());
        assertEquals(Status.REJECTED, bookings.get(0).getStatus());
    }

    @Test
    void allFindAllOwnersBookingsByNoPagination() throws EntityNotFoundException {
        List<BookingDtoComplete> bookings = new ArrayList<>(
                bookingService.findAllOwnersBookingsByState(1L, State.ALL, Pageable.unpaged()));
        List<Long> bookingsId = bookings.stream().map(BookingDtoComplete::getId).collect(Collectors.toList());

        assertEquals(6, bookings.size());
        assertTrue(bookingsId.contains(1L));
        assertTrue(bookingsId.contains(2L));
        assertTrue(bookingsId.contains(3L));
        assertTrue(bookingsId.contains(4L));
        assertTrue(bookingsId.contains(5L));
        assertTrue(bookingsId.contains(6L));

        assertEquals(LocalDateTime.of(2000, 1, 1, 1, 1, 1, 1),
                bookings.get(5).getStart());
        assertEquals(LocalDateTime.of(2001, 1, 1, 1, 1, 1, 1),
                bookings.get(5).getEnd());
        Assertions.assertEquals(1L, bookings.get(5).getItem().getId());
        assertEquals(Status.APPROVED, bookings.get(5).getStatus());

        assertEquals(LocalDateTime.of(2022, 1, 1, 1, 1, 1, 1),
                bookings.get(4).getStart());
        assertEquals(LocalDateTime.of(2039, 1, 1, 1, 1, 1, 1),
                bookings.get(4).getEnd());
        Assertions.assertEquals(2L, bookings.get(4).getItem().getId());
        assertEquals(Status.APPROVED, bookings.get(4).getStatus());

        assertEquals(LocalDateTime.of(2024, 1, 1, 1, 1, 1, 1),
                bookings.get(3).getStart());
        assertEquals(LocalDateTime.of(2025, 1, 1, 1, 1, 1, 1),
                bookings.get(3).getEnd());
        Assertions.assertEquals(1L, bookings.get(3).getItem().getId());
        assertEquals(Status.WAITING, bookings.get(3).getStatus());

        assertEquals(LocalDateTime.of(2029, 1, 1, 1, 1, 1, 1),
                bookings.get(2).getStart());
        assertEquals(LocalDateTime.of(2032, 1, 1, 1, 1, 1, 1),
                bookings.get(2).getEnd());
        Assertions.assertEquals(1L, bookings.get(2).getItem().getId());
        assertEquals(Status.APPROVED, bookings.get(2).getStatus());

        assertEquals(LocalDateTime.of(2030, 1, 1, 1, 1, 1, 1),
                bookings.get(1).getStart());
        assertEquals(LocalDateTime.of(2031, 1, 1, 1, 1, 1, 1),
                bookings.get(1).getEnd());
        Assertions.assertEquals(1L, bookings.get(1).getItem().getId());
        assertEquals(Status.REJECTED, bookings.get(1).getStatus());

        assertEquals(LocalDateTime.of(2035, 1, 1, 1, 1, 1, 1),
                bookings.get(0).getStart());
        assertEquals(LocalDateTime.of(2040, 1, 1, 1, 1, 1, 1),
                bookings.get(0).getEnd());
        Assertions.assertEquals(2L, bookings.get(0).getItem().getId());
        assertEquals(Status.WAITING, bookings.get(0).getStatus());
    }

    @Test
    void pastFindAllOwnersBookingsByPagination() throws EntityNotFoundException {
        List<BookingDtoComplete> bookings = new ArrayList<>(
                bookingService.findAllOwnersBookingsByState(1L, State.PAST, PageRequest.of(0, 1)));
        List<Long> bookingsId = bookings.stream().map(BookingDtoComplete::getId).collect(Collectors.toList());

        assertEquals(1, bookings.size());
        assertTrue(bookingsId.contains(1L));

        assertEquals(LocalDateTime.of(2000, 1, 1, 1, 1, 1, 1),
                bookings.get(0).getStart());
        assertEquals(LocalDateTime.of(2001, 1, 1, 1, 1, 1, 1),
                bookings.get(0).getEnd());
        Assertions.assertEquals(1L, bookings.get(0).getItem().getId());
        assertEquals(Status.APPROVED, bookings.get(0).getStatus());
    }

    @Test
    void currentFindAllOwnersBookingsByPagination() throws EntityNotFoundException {
        List<BookingDtoComplete> bookings = new ArrayList<>(
                bookingService.findAllOwnersBookingsByState(1L, State.CURRENT, PageRequest.of(0, 1)));
        List<Long> bookingsId = bookings.stream().map(BookingDtoComplete::getId).collect(Collectors.toList());

        assertEquals(1, bookings.size());
        assertTrue(bookingsId.contains(6L));

        assertEquals(LocalDateTime.of(2022, 1, 1, 1, 1, 1, 1),
                bookings.get(0).getStart());
        assertEquals(LocalDateTime.of(2039, 1, 1, 1, 1, 1, 1),
                bookings.get(0).getEnd());
        Assertions.assertEquals(2L, bookings.get(0).getItem().getId());
        assertEquals(Status.APPROVED, bookings.get(0).getStatus());
    }

    @Test
    void futureFindAllOwnersBookingsByPagination() throws EntityNotFoundException {
        List<BookingDtoComplete> bookings = new ArrayList<>(
                bookingService.findAllOwnersBookingsByState(1L, State.FUTURE, PageRequest.of(0, 3)));
        List<Long> bookingsId = bookings.stream().map(BookingDtoComplete::getId).collect(Collectors.toList());

        assertEquals(3, bookings.size());
        assertTrue(bookingsId.contains(5L));
        assertTrue(bookingsId.contains(4L));
        assertTrue(bookingsId.contains(2L));

        assertEquals(LocalDateTime.of(2035, 1, 1, 1, 1, 1, 1),
                bookings.get(0).getStart());
        assertEquals(LocalDateTime.of(2040, 1, 1, 1, 1, 1, 1),
                bookings.get(0).getEnd());
        Assertions.assertEquals(2L, bookings.get(0).getItem().getId());
        assertEquals(Status.WAITING, bookings.get(0).getStatus());

        assertEquals(LocalDateTime.of(2029, 1, 1, 1, 1, 1, 1),
                bookings.get(1).getStart());
        assertEquals(LocalDateTime.of(2032, 1, 1, 1, 1, 1, 1),
                bookings.get(1).getEnd());
        Assertions.assertEquals(1L, bookings.get(1).getItem().getId());
        assertEquals(Status.APPROVED, bookings.get(1).getStatus());

        assertEquals(LocalDateTime.of(2024, 1, 1, 1, 1, 1, 1),
                bookings.get(2).getStart());
        assertEquals(LocalDateTime.of(2025, 1, 1, 1, 1, 1, 1),
                bookings.get(2).getEnd());
        Assertions.assertEquals(1L, bookings.get(2).getItem().getId());
        assertEquals(Status.WAITING, bookings.get(2).getStatus());
    }

    @Test
    void waitingFindAllOwnersBookingsByPagination() throws EntityNotFoundException {
        List<BookingDtoComplete> bookings = new ArrayList<>(
                bookingService.findAllOwnersBookingsByState(1L, State.WAITING, PageRequest.of(0, 2)));
        List<Long> bookingsId = bookings.stream().map(BookingDtoComplete::getId).collect(Collectors.toList());

        assertEquals(2, bookings.size());
        assertTrue(bookingsId.contains(2L));
        assertTrue(bookingsId.contains(5L));

        assertEquals(LocalDateTime.of(2035, 1, 1, 1, 1, 1, 1),
                bookings.get(0).getStart());
        assertEquals(LocalDateTime.of(2040, 1, 1, 1, 1, 1, 1),
                bookings.get(0).getEnd());
        Assertions.assertEquals(2L, bookings.get(0).getItem().getId());
        assertEquals(Status.WAITING, bookings.get(0).getStatus());

        assertEquals(LocalDateTime.of(2024, 1, 1, 1, 1, 1, 1),
                bookings.get(1).getStart());
        assertEquals(LocalDateTime.of(2025, 1, 1, 1, 1, 1, 1),
                bookings.get(1).getEnd());
        Assertions.assertEquals(1L, bookings.get(1).getItem().getId());
        assertEquals(Status.WAITING, bookings.get(1).getStatus());
    }

    @Test
    void rejectedFindAllOwnersBookingsByPagination() throws EntityNotFoundException {
        List<BookingDtoComplete> bookings = new ArrayList<>(
                bookingService.findAllOwnersBookingsByState(1L, State.REJECTED, PageRequest.of(0, 1)));
        List<Long> bookingsId = bookings.stream().map(BookingDtoComplete::getId).collect(Collectors.toList());

        assertEquals(1, bookings.size());
        assertTrue(bookingsId.contains(3L));

        assertEquals(LocalDateTime.of(2030, 1, 1, 1, 1, 1, 1),
                bookings.get(0).getStart());
        assertEquals(LocalDateTime.of(2031, 1, 1, 1, 1, 1, 1),
                bookings.get(0).getEnd());
        Assertions.assertEquals(1L, bookings.get(0).getItem().getId());
        assertEquals(Status.REJECTED, bookings.get(0).getStatus());
    }

    @Test
    void allFindAllOwnersBookingsByPagination() throws EntityNotFoundException {
        List<BookingDtoComplete> bookings = new ArrayList<>(
                bookingService.findAllOwnersBookingsByState(1L, State.ALL, PageRequest.of(1, 5)));
        List<Long> bookingsId = bookings.stream().map(BookingDtoComplete::getId).collect(Collectors.toList());

        assertEquals(1, bookings.size());
        assertTrue(bookingsId.contains(1L));

        assertEquals(LocalDateTime.of(2000, 1, 1, 1, 1, 1, 1),
                bookings.get(0).getStart());
        assertEquals(LocalDateTime.of(2001, 1, 1, 1, 1, 1, 1),
                bookings.get(0).getEnd());
        Assertions.assertEquals(1L, bookings.get(0).getItem().getId());
        assertEquals(Status.APPROVED, bookings.get(0).getStatus());
    }

    @Test
    void findAllUsersBookingsByPaginationShouldThrow() {
        assertThrows(EntityNotFoundException.class, () -> bookingService.findAllUsersBookingsByState(99L,
                State.WAITING, Pageable.unpaged()));
    }

    @BeforeEach
    void setUp() throws NotUniqueUserEmail, EntityNotFoundException, CommentWithoutCompletedBooking {
        UserDto user1 = new UserDto();
        user1.setName("First User");
        user1.setEmail("First@mail.ru");

        UserDto user2 = new UserDto();
        user2.setName("Second User");
        user2.setEmail("Second@mail.ru");

        UserDto user3 = new UserDto();
        user3.setName("Third User");
        user3.setEmail("Third@mail.ru");
        userService.save(user1);
        userService.save(user2);
        userService.save(user3);

        ItemDto itemDto1 = new ItemDto();
        itemDto1.setName("First Item");
        itemDto1.setDescription("First Description");
        itemDto1.setAvailable(true);

        ItemDto itemDto2 = new ItemDto();
        itemDto2.setName("Second Item");
        itemDto2.setDescription("Second Description");
        itemDto2.setAvailable(true);

        ItemDto itemDto3 = new ItemDto();
        itemDto3.setName("Third Item");
        itemDto3.setDescription("Third Description");
        itemDto3.setAvailable(true);

        ItemDto itemDto4 = new ItemDto();
        itemDto4.setName("Fourth Item");
        itemDto4.setDescription("Fourth Description");
        itemDto4.setAvailable(false);
        itemService.save(1L, itemDto1);
        itemService.save(1L, itemDto2);
        itemService.save(1L, itemDto3);
        itemService.save(2L, itemDto4);

        Booking booking1 = new Booking();
        booking1.setStart(LocalDateTime.of(2000, 1, 1, 1, 1, 1, 1));
        booking1.setEnd(LocalDateTime.of(2001, 1, 1, 1, 1, 1, 1));
        booking1.setStatus(Status.APPROVED);
        booking1.setItem(itemRepository.findById(1L).orElseThrow());
        booking1.setBooker(userRepository.findById(2L).orElseThrow());

        Booking booking2 = new Booking();
        booking2.setStart(LocalDateTime.of(2024, 1, 1, 1, 1, 1, 1));
        booking2.setEnd(LocalDateTime.of(2025, 1, 1, 1, 1, 1, 1));
        booking2.setStatus(Status.WAITING);
        booking2.setItem(itemRepository.findById(1L).orElseThrow());
        booking2.setBooker(userRepository.findById(2L).orElseThrow());

        Booking booking3 = new Booking();
        booking3.setStart(LocalDateTime.of(2030, 1, 1, 1, 1, 1, 1));
        booking3.setEnd(LocalDateTime.of(2031, 1, 1, 1, 1, 1, 1));
        booking3.setStatus(Status.REJECTED);
        booking3.setItem(itemRepository.findById(1L).orElseThrow());
        booking3.setBooker(userRepository.findById(2L).orElseThrow());

        Booking booking4 = new Booking();
        booking4.setStart(LocalDateTime.of(2029, 1, 1, 1, 1, 1, 1));
        booking4.setEnd(LocalDateTime.of(2032, 1, 1, 1, 1, 1, 1));
        booking4.setStatus(Status.APPROVED);
        booking4.setItem(itemRepository.findById(1L).orElseThrow());
        booking4.setBooker(userRepository.findById(3L).orElseThrow());

        Booking booking5 = new Booking();
        booking5.setStart(LocalDateTime.of(2035, 1, 1, 1, 1, 1, 1));
        booking5.setEnd(LocalDateTime.of(2040, 1, 1, 1, 1, 1, 1));
        booking5.setStatus(Status.WAITING);
        booking5.setItem(itemRepository.findById(2L).orElseThrow());
        booking5.setBooker(userRepository.findById(3L).orElseThrow());

        Booking booking6 = new Booking();
        booking6.setStart(LocalDateTime.of(2022, 1, 1, 1, 1, 1, 1));
        booking6.setEnd(LocalDateTime.of(2039, 1, 1, 1, 1, 1, 1));
        booking6.setStatus(Status.APPROVED);
        booking6.setItem(itemRepository.findById(2L).orElseThrow());
        booking6.setBooker(userRepository.findById(3L).orElseThrow());
        bookingRepository.save(booking1);
        bookingRepository.save(booking2);
        bookingRepository.save(booking3);
        bookingRepository.save(booking4);
        bookingRepository.save(booking5);
        bookingRepository.save(booking6);

        CommentDto commentDto1 = new CommentDto();
        commentDto1.setText("First Comment");
        itemService.saveComment(2L, 1L, commentDto1);
    }
}