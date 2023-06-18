package ru.practicum.shareit.booking.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoComplete;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.dto.BookingMapperComplete;
import ru.practicum.shareit.booking.exceptions.BookerAndOwnerAreSameUser;
import ru.practicum.shareit.booking.exceptions.ItemNotAvailableException;
import ru.practicum.shareit.booking.exceptions.StatusAlreadyConfirmed;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.State;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.exceptions.WrongOwnerException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemJpaRepository;
import ru.practicum.shareit.user.exceptions.EntityNotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserJpaRepository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final BookingMapper bookingMapper;
    private final BookingMapperComplete bookingMapperComplete;
    private final UserJpaRepository userRepository;
    private final ItemJpaRepository itemRepository;

    public BookingDtoComplete save(Long bookerId, BookingDto bookingDto) throws EntityNotFoundException,
            ItemNotAvailableException, BookerAndOwnerAreSameUser {
        Long itemId = bookingDto.getItemId();
        Booking booking = bookingMapper.dtoToModel(bookingDto);
        Item item = itemRepository.findById(itemId).orElseThrow(() ->
                new EntityNotFoundException(String.format("%s with id = %d does not exist in database", "Item", itemId)));
        Long ownerId = item.getOwnerId();
        if (Objects.equals(bookerId, ownerId)) {
            throw new BookerAndOwnerAreSameUser("You cant rent your own Item");
        }
        if (!item.getAvailable()) {
            throw new ItemNotAvailableException(String.format("Item with id = %d is not available for booking", itemId));
        }
        User booker = userRepository.findById(bookerId).orElseThrow(
                () -> new EntityNotFoundException(String.format("%s with id = %d does not exist in database", "User", bookerId)));
        log.debug(String.format("Booker with id = %d for new Booking found", bookerId));
        log.debug(String.format("Item with id = %d for new Booking found", itemId));
        booking.setBooker(booker);
        booking.setItem(item);
        booking.setStatus(Status.WAITING);
        log.debug("Booker, Item, Status fields for new Booking initialized");
        bookingRepository.save(booking);
        return bookingMapperComplete.modelToDto(booking);
    }

    public BookingDtoComplete confirmBooking(Long ownerId, Long bookingId, boolean isApproved) throws EntityNotFoundException,
            WrongOwnerException, StatusAlreadyConfirmed {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(
                () -> new EntityNotFoundException(String.format("%s with id = %d does not exist in database", "Booking",
                        bookingId)));
        Status currentStatus = booking.getStatus();
        Long bookedItemOwner = booking.getItem().getOwnerId();
        Long itemId = booking.getItem().getId();
        Status status = Status.REJECTED;
        if (isApproved) {
            status = Status.APPROVED;
        }

        if (Objects.equals(bookedItemOwner, ownerId)) {
            if (currentStatus.equals(status)) {
                throw new StatusAlreadyConfirmed(String.format("This status - %b - is already assigned to Booking with " +
                        "id = %d", isApproved, bookingId));
            }
            booking.setStatus(status);
            bookingRepository.save(booking);
            log.debug(String.format("For Booking with id = %d confirmed owner update status", bookingId));
            return bookingMapperComplete.modelToDto(booking);
        } else {
            throw new WrongOwnerException(String.format("User with id = %d is not owner of Item with id = %d", ownerId,
                    itemId));
        }
    }

    public BookingDtoComplete findById(Long userId, Long bookingId) throws EntityNotFoundException, WrongOwnerException {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(
                () -> new EntityNotFoundException(String.format("%s with id = %d does not exist in database", "Booking",
                        bookingId)));
        Long ownerId = booking.getItem().getOwnerId();
        Long booker = booking.getBooker().getId();
        if (userId.equals(ownerId) || userId.equals(booker)) {
            return bookingMapperComplete.modelToDto(booking);
        } else {
            throw new WrongOwnerException(String.format("For User with id = %d information about Booking with id = %d " +
                    "is not available, cause that user is not owner or booker", userId, bookingId));
        }
    }

    public Collection<BookingDtoComplete> findAllUsersBookingsByState(Long bookerId, State state, Pageable pageRequest)
            throws EntityNotFoundException {
        checkIsUserExistInDataBase(bookerId);
        LocalDateTime now = LocalDateTime.now();

        switch (state) {
            case PAST:
                return bookingRepository.findAllByBookerIdAndEndBeforeOrderByStartDesc(bookerId, now, pageRequest)
                        .stream().map(bookingMapperComplete::modelToDto).collect(Collectors.toList());
            case CURRENT:
                return bookingRepository.findAllByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(bookerId, now,
                                now, pageRequest)
                        .stream().map(bookingMapperComplete::modelToDto).collect(Collectors.toList());
            case FUTURE:
                return bookingRepository.findAllByBookerIdAndStartAfterAndStatusNotOrderByStartDesc(bookerId, now,
                                Status.REJECTED, pageRequest)
                        .stream().map(bookingMapperComplete::modelToDto).collect(Collectors.toList());
            case WAITING:
                return bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(bookerId, Status.WAITING,
                                pageRequest)
                        .stream().map(bookingMapperComplete::modelToDto).collect(Collectors.toList());
            case REJECTED:
                return bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(bookerId, Status.REJECTED,
                                pageRequest)
                        .stream().map(bookingMapperComplete::modelToDto).collect(Collectors.toList());
            case ALL:
                return bookingRepository.findAllByBookerIdOrderByStartDesc(bookerId, pageRequest)
                        .stream().map(bookingMapperComplete::modelToDto).collect(Collectors.toList());
            default:
                return null;

        }

    }

    public Collection<BookingDtoComplete> findAllOwnersBookingsByState(Long ownerId, State state, Pageable pageRequest)
            throws EntityNotFoundException {
        checkIsUserExistInDataBase(ownerId);
        LocalDateTime now = LocalDateTime.now();

        switch (state) {
            case PAST:
                return bookingRepository.findAllByItemOwnerIdAndEndBeforeOrderByStartDesc(ownerId, now, pageRequest)
                        .stream().map(bookingMapperComplete::modelToDto).collect(Collectors.toList());
            case CURRENT:
                return bookingRepository.findAllByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(ownerId,
                                now, now, pageRequest)
                        .stream().map(bookingMapperComplete::modelToDto).collect(Collectors.toList());
            case FUTURE:
                return bookingRepository.findAllByItemOwnerIdAndStartAfterAndStatusNotOrderByStartDesc(ownerId,
                                now, Status.REJECTED, pageRequest)
                        .stream().map(bookingMapperComplete::modelToDto).collect(Collectors.toList());
            case WAITING:
                return bookingRepository.findAllByItemOwnerIdAndStatusOrderByStartDesc(ownerId, Status.WAITING,
                                pageRequest)
                        .stream().map(bookingMapperComplete::modelToDto).collect(Collectors.toList());
            case REJECTED:
                return bookingRepository.findAllByItemOwnerIdAndStatusOrderByStartDesc(ownerId, Status.REJECTED,
                                pageRequest)
                        .stream().map(bookingMapperComplete::modelToDto).collect(Collectors.toList());
            case ALL:
                return bookingRepository.findAllByItemOwnerIdOrderByStartDesc(ownerId, pageRequest)
                        .stream().map(bookingMapperComplete::modelToDto).collect(Collectors.toList());
            default:
                return null;

        }
    }

    private boolean checkIsUserExistInDataBase(Long userId) throws EntityNotFoundException {
        if (!userRepository.existsById(userId)) {
            throw new EntityNotFoundException(String.format("%s with id = %d does not exist in database",
                    "User", userId));
        }
        return true;
    }
}