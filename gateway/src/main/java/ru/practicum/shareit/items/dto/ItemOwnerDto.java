package ru.practicum.shareit.items.dto;

import lombok.Getter;
import lombok.Setter;
import ru.practicum.shareit.bookings.dto.BookingDtoForItems;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Collection;

@Getter
@Setter
public class ItemOwnerDto {
    private Long id;
    @NotEmpty
    private String name;
    @NotEmpty
    private String description;
    @NotNull
    private Boolean available;
    private Long requestId;
    private BookingDtoForItems lastBooking;
    private BookingDtoForItems nextBooking;
    private Collection<CommentDto> comments;
}