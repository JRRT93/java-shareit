package ru.practicum.shareit.booking.dto;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.booking.model.Booking;

@Mapper
public interface BookingMapperForItems {
    @Mapping(target = "id", source = "bookingDtoForItems.id")
    @Mapping(target = "end", source = "bookingDtoForItems.end")
    @Mapping(target = "start", source = "bookingDtoForItems.start")
    @Mapping(target = "status", source = "bookingDtoForItems.status")
    Booking dtoToModel(BookingDtoForItems bookingDtoForItems);

    @Mapping(target = "id", source = "booking.id")
    @Mapping(target = "end", source = "booking.end")
    @Mapping(target = "start", source = "booking.start")
    @Mapping(target = "bookerId", source = "booking.booker.id")
    @Mapping(target = "status", source = "booking.status")
    BookingDtoForItems modelToDto(Booking booking);
}