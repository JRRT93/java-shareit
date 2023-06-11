package ru.practicum.shareit.item.dto;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.item.model.Item;

@Mapper(componentModel = "spring")
public interface ItemAnswerRequestDtoMapper {
    @Mapping(target = "id", source = "itemAnswerRequestDto.id")
    @Mapping(target = "description", source = "itemAnswerRequestDto.description")
    @Mapping(target = "ownerId", source = "itemAnswerRequestDto.ownerId")
    Item dtoToModel(ItemAnswerRequestDto itemAnswerRequestDto);

    @Mapping(target = "id", source = "item.id")
    @Mapping(target = "description", source = "item.description")
    @Mapping(target = "ownerId", source = "item.ownerId")
    ItemAnswerRequestDto modelToDto(Item item);
}