package ru.practicum.shareit.booking.dto;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.booking.model.Booking;

@Mapper(componentModel = "spring")
public interface BookingMapper {
    @Mapping(target = "start", source = "bookingDto.start")
    @Mapping(target = "end", source = "bookingDto.end")
    Booking dtoToModel(BookingDto bookingDto);

    @Mapping(target = "start", source = "booking.start")
    @Mapping(target = "end", source = "booking.end")
    BookingDto modelToDto(Booking booking);
}