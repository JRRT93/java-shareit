package ru.practicum.shareit.item.dto;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.item.model.Item;

@Mapper(componentModel = "spring")
public interface ItemOwnerDtoMapper {
    @Mapping(target = "id", source = "itemOwnerDto.id")
    @Mapping(target = "name", source = "itemOwnerDto.name")
    @Mapping(target = "description", source = "itemOwnerDto.description")
    @Mapping(target = "available", source = "itemOwnerDto.available")
    Item dtoToModel(ItemDto itemOwnerDto);

    @Mapping(target = "id", source = "item.id")
    @Mapping(target = "name", source = "item.name")
    @Mapping(target = "description", source = "item.description")
    @Mapping(target = "available", source = "item.available")
    ItemOwnerDto modelToDto(Item item);
}