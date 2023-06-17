package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    Collection<Booking> findAllByBookerIdAndEndBeforeOrderByStartDesc(Long bookerId, LocalDateTime now);

    Collection<Booking> findAllByItemOwnerIdOrderByStartDesc(Long ownerId);

    Collection<Booking> findAllByItemIdOrderByStartDesc(Long itemId);

    List<Booking> findAllByBookerIdAndEndBeforeOrderByStartDesc(Long bookerId, LocalDateTime now, Pageable pageRequest);

    List<Booking> findAllByBookerIdAndStartAfterAndStatusNotOrderByStartDesc(Long bookerId, LocalDateTime now,
                                                                             Status status, Pageable pageRequest);

    List<Booking> findAllByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(Long bookerId, LocalDateTime now1,
                                                                             LocalDateTime now2, Pageable pageRequest);

    List<Booking> findAllByBookerIdAndStatusOrderByStartDesc(Long bookerId, Status status, Pageable pageRequest);

    List<Booking> findAllByBookerIdOrderByStartDesc(Long bookerId, Pageable pageRequest);

    List<Booking> findAllByItemOwnerIdAndEndBeforeOrderByStartDesc(Long ownerId, LocalDateTime now, Pageable pageRequest);

    List<Booking> findAllByItemOwnerIdAndStartAfterAndStatusNotOrderByStartDesc(Long ownerId, LocalDateTime now,
                                                                                Status status, Pageable pageRequest);

    List<Booking> findAllByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(Long ownerId, LocalDateTime now1,
                                                                                LocalDateTime now2, Pageable pageRequest);

    List<Booking> findAllByItemOwnerIdAndStatusOrderByStartDesc(Long ownerId, Status status, Pageable pageRequest);

    List<Booking> findAllByItemOwnerIdOrderByStartDesc(Long ownerId, Pageable pageRequest);
}