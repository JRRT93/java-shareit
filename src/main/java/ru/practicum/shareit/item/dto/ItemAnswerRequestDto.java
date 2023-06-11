package ru.practicum.shareit.item.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ItemAnswerRequestDto {
    private Long id;
    private String description;
    private Long ownerId;
}