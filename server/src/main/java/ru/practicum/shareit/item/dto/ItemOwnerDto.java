package ru.practicum.shareit.item.dto;

import lombok.Getter;
import lombok.Setter;
import ru.practicum.shareit.booking.dto.BookingDtoForItems;

import java.util.Collection;

@Getter
@Setter
public class ItemOwnerDto {
    private Long id;
    private String name;
    private String description;
    private Boolean available;
    private Long requestId;
    private BookingDtoForItems lastBooking;
    private BookingDtoForItems nextBooking;
    private Collection<CommentDto> comments;
}