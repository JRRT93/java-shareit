package ru.practicum.shareit.booking.dto;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.booking.model.Booking;

@Mapper
public interface BookingMapperComplete {
    @Mapping(target = "id", source = "bookingDtoComplete.id")
    @Mapping(target = "end", source = "bookingDtoComplete.end")
    @Mapping(target = "start", source = "bookingDtoComplete.start")
    @Mapping(target = "item", source = "bookingDtoComplete.item")
    @Mapping(target = "booker", source = "bookingDtoComplete.booker")
    @Mapping(target = "status", source = "bookingDtoComplete.status")
    Booking dtoToModel(BookingDtoComplete bookingDtoComplete);

    @Mapping(target = "id", source = "booking.id")
    @Mapping(target = "end", source = "booking.end")
    @Mapping(target = "start", source = "booking.start")
    @Mapping(target = "item", source = "booking.item")
    @Mapping(target = "booker", source = "booking.booker")
    @Mapping(target = "status", source = "booking.status")
    BookingDtoComplete modelToDto(Booking booking);
}