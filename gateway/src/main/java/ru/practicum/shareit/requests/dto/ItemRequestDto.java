package ru.practicum.shareit.requests.dto;

import lombok.Getter;
import lombok.Setter;
import ru.practicum.shareit.items.dto.ItemDto;
import ru.practicum.shareit.users.dto.UserDto;

import javax.validation.constraints.NotEmpty;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class ItemRequestDto {
    private Long id;
    @NotEmpty
    private String description;
    private LocalDateTime created;
    private UserDto author;
    private List<ItemDto> items;
}
