package ru.practicum.shareit.request.dto;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.request.model.ItemRequest;

@Mapper(componentModel = "spring")
public interface ItemRequestMapper {
    @Mapping(target = "id", source = "itemRequestDto.id")
    @Mapping(target = "author", source = "itemRequestDto.author")
    @Mapping(target = "description", source = "itemRequestDto.description")
    @Mapping(target = "created", source = "itemRequestDto.created")
    ItemRequest dtoToModel(ItemRequestDto itemRequestDto);

    @Mapping(target = "id", source = "itemRequest.id")
    @Mapping(target = "author", source = "itemRequest.author")
    @Mapping(target = "description", source = "itemRequest.description")
    @Mapping(target = "created", source = "itemRequest.created")
    ItemRequestDto modelToDto(ItemRequest itemRequest);
}