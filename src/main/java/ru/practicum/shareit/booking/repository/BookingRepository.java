package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;

import java.time.LocalDateTime;
import java.util.Collection;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    Collection<Booking> findAllByBookerIdAndEndBeforeOrderByStartDesc(Long bookerId, LocalDateTime now);

    Collection<Booking> findAllByBookerIdAndStartAfterAndStatusNotOrderByStartDesc(Long bookerId, LocalDateTime now,
                                                                                   Status status);

    Collection<Booking> findAllByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(Long bookerId, LocalDateTime now1,
                                                                                   LocalDateTime now2);

    Collection<Booking> findAllByBookerIdAndStatusOrderByStartDesc(Long bookerId, Status status);

    Collection<Booking> findAllByBookerIdOrderByStartDesc(Long bookerId);

    Collection<Booking> findAllByItemOwnerIdAndEndBeforeOrderByStartDesc(Long ownerId, LocalDateTime now);

    Collection<Booking> findAllByItemOwnerIdAndStartAfterAndStatusNotOrderByStartDesc(Long ownerId, LocalDateTime now,
                                                                                      Status status);

    Collection<Booking> findAllByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(Long ownerId, LocalDateTime now1,
                                                                                      LocalDateTime now2);

    Collection<Booking> findAllByItemOwnerIdAndStatusOrderByStartDesc(Long ownerId, Status status);

    Collection<Booking> findAllByItemOwnerIdOrderByStartDesc(Long ownerId);

    Collection<Booking> findAllByItemIdOrderByStartDesc(Long itemId);

}