package ru.practicum.shareit.booking.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.booking.dto.*;
import ru.practicum.shareit.booking.exceptions.BookerAndOwnerAreSameUser;
import ru.practicum.shareit.booking.exceptions.IncorrectBookingStartEndDate;
import ru.practicum.shareit.booking.exceptions.ItemNotAvailableException;
import ru.practicum.shareit.booking.exceptions.StatusAlreadyConfirmed;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.exceptions.WrongOwnerException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemJpaRepository;
import ru.practicum.shareit.user.exceptions.EntityNotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserJpaRepository;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
class BookingServiceTest {
    @Mock
    BookingRepository bookingRepository;
    @Autowired
    BookingMapper bookingMapper;
    @Autowired
    BookingMapperComplete bookingMapperComplete;
    @Mock
    UserJpaRepository userRepository;
    @Mock
    ItemJpaRepository itemRepository;
    BookingService bookingService;
    ItemDto itemDto;
    BookingDto bookingDto;
    Booking booking;
    Item item;
    User booker;
    User owner;

    @BeforeEach
    void inject() {
        bookingService = new BookingServiceImpl(bookingRepository, bookingMapper, bookingMapperComplete, userRepository,
                itemRepository);

        itemDto = new ItemDto();
        itemDto.setName("Knife");
        itemDto.setDescription("Sharp fine knife");
        itemDto.setAvailable(true);

        item = new Item();
        item.setId(1L);
        item.setName("Knife");
        item.setDescription("Sharp fine knife");
        item.setAvailable(true);
        item.setOwnerId(2L);

        booker = new User();
        booker.setId(1L);
        booker.setEmail("booker@mail.ru");
        booker.setName("Booker");

        owner = new User();
        owner.setId(2L);
        owner.setEmail("owner@mail.ru");
        owner.setName("Owner");

        bookingDto = new BookingDto();
        bookingDto.setItemId(1L);
        bookingDto.setEnd(LocalDateTime.of(2025, 1, 1, 1, 1, 1, 1));
        bookingDto.setStart(LocalDateTime.of(2024, 1, 1, 1, 1, 1, 1));

        booking = new Booking();
        booking.setId(1L);
        booking.setEnd(LocalDateTime.of(2025, 1, 1, 1, 1, 1, 1));
        booking.setStart(LocalDateTime.of(2024, 1, 1, 1, 1, 1, 1));
        booking.setStatus(Status.WAITING);
        booking.setBooker(booker);
        booking.setItem(item);
    }

    @Test
    void saveShouldThrowOwnerAndBookerSamePerson() {
        Mockito
                .when(itemRepository.findById(1L))
                .thenReturn(Optional.ofNullable(item));

        assertThrows(BookerAndOwnerAreSameUser.class, () -> bookingService.save(2L, bookingDto));
    }

    @Test
    void saveShouldThrowEntityNotFound() {
        Mockito
                .when(itemRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> bookingService.save(2L, bookingDto));
    }

    @Test
    void saveShouldThrowIncorrectBookingStartEndDate() {
        Mockito
                .when(userRepository.findById(1L))
                .thenReturn(Optional.ofNullable(booker));
        Mockito
                .when(itemRepository.findById(1L))
                .thenReturn(Optional.ofNullable(item));
        Mockito
                .when(bookingRepository.save(booking))
                .thenReturn(booking);
        bookingDto.setStart(LocalDateTime.of(2000, 1, 1, 1, 1, 1, 1));

        assertThrows(IncorrectBookingStartEndDate.class, () -> bookingService.save(1L, bookingDto));
    }

    @Test
    void saveShouldThrowNotAvailable() {
        item.setAvailable(false);

        Mockito
                .when(itemRepository.findById(1L))
                .thenReturn(Optional.ofNullable(item));

        assertThrows(ItemNotAvailableException.class, () -> bookingService.save(1L, bookingDto));
    }

    @Test
    void saveShouldSetStatusBookerItem() throws ItemNotAvailableException, BookerAndOwnerAreSameUser, EntityNotFoundException,
            IncorrectBookingStartEndDate {
        Mockito
                .when(userRepository.findById(1L))
                .thenReturn(Optional.ofNullable(booker));
        Mockito
                .when(itemRepository.findById(1L))
                .thenReturn(Optional.ofNullable(item));
        Mockito
                .when(bookingRepository.save(booking))
                .thenReturn(booking);

        BookingDtoComplete savedBooking = bookingService.save(1L, bookingDto);
        assertEquals(Status.WAITING, savedBooking.getStatus());
        assertEquals(1L, savedBooking.getItem().getId());
        assertEquals(1L, savedBooking.getBooker().getId());
    }

    @Test
    void confirmBookingApproved() throws WrongOwnerException, StatusAlreadyConfirmed, EntityNotFoundException {
        Mockito
                .when(bookingRepository.findById(1L))
                .thenReturn(Optional.ofNullable(booking));
        Mockito
                .when(bookingRepository.save(booking))
                .thenReturn(booking);

        BookingDtoComplete savedBooking = bookingService.confirmBooking(2L, 1L, true);
        assertEquals(Status.APPROVED, savedBooking.getStatus());
    }

    @Test
    void confirmBookingRejected() throws WrongOwnerException, StatusAlreadyConfirmed, EntityNotFoundException {
        Mockito
                .when(bookingRepository.findById(1L))
                .thenReturn(Optional.ofNullable(booking));
        Mockito
                .when(bookingRepository.save(booking))
                .thenReturn(booking);

        BookingDtoComplete savedBooking = bookingService.confirmBooking(2L, 1L, false);
        assertEquals(Status.REJECTED, savedBooking.getStatus());
    }

    @Test
    void confirmBookingDoubleConfirmationShouldThrow() {
        Mockito
                .when(bookingRepository.findById(1L))
                .thenReturn(Optional.ofNullable(booking));
        Mockito
                .when(bookingRepository.save(booking))
                .thenReturn(booking);
        booking.setStatus(Status.APPROVED);

        assertThrows(StatusAlreadyConfirmed.class, () -> bookingService.confirmBooking(2L, 1L, true));
    }

    @Test
    void confirmBookingByWrongOwnerShouldThrow() {
        Mockito
                .when(bookingRepository.findById(1L))
                .thenReturn(Optional.ofNullable(booking));
        Mockito
                .when(bookingRepository.save(booking))
                .thenReturn(booking);

        assertThrows(WrongOwnerException.class, () -> bookingService.confirmBooking(1L, 1L, true));
    }

    @Test
    void confirmBookingByWrongOwnerShouldThrowNotFound() {
        Mockito
                .when(bookingRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> bookingService.confirmBooking(1L, 1L, true));
    }

    @Test
    void findByIdShouldThrow() {
        Mockito
                .when(bookingRepository.findById(99L))
                .thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> bookingService.findById(1L, 99L));
    }

    @Test
    void findByIdShouldTGiveBookingCompleteDto() throws WrongOwnerException, EntityNotFoundException {
        Mockito
                .when(bookingRepository.findById(1L))
                .thenReturn(Optional.ofNullable(booking));
        BookingDtoComplete bookingDtoComplete = bookingService.findById(2L, 1L);
    }

    @Test
    void findByIdShouldShouldThrowIfRequestNotFromOwnerOrBooker() {
        Mockito
                .when(bookingRepository.findById(1L))
                .thenReturn(Optional.ofNullable(booking));

        assertThrows(WrongOwnerException.class, () -> bookingService.findById(3L, 1L));
    }

    @Test
    void dtoToNullAndBack() {
        bookingMapper.modelToDto(booking);
        bookingMapper.modelToDto(null);
        bookingMapper.dtoToModel(null);
        BookingMapperForItems bm = Mappers.getMapper(BookingMapperForItems.class);
        bm.modelToDto(null);
        bm.dtoToModel(null);
        BookingDtoForItems bi = new BookingDtoForItems();
        bm.dtoToModel(bi);
        BookingMapperComplete bc = Mappers.getMapper(BookingMapperComplete.class);
        bc.modelToDto(null);
        bc.dtoToModel(null);
        BookingDtoComplete bcc = new BookingDtoComplete();
        bc.dtoToModel(bcc);
    }
}