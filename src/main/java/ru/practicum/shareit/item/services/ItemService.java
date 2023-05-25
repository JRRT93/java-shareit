package ru.practicum.shareit.item.services;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.exceptions.WrongOwnerException;
import ru.practicum.shareit.user.exceptions.EntityNotFoundException;

import java.util.List;

public interface ItemService {
    ItemDto save(Long ownerId, ItemDto itemDto) throws EntityNotFoundException;

    ItemDto update(Long ownerId, Long itemId, ItemDto itemDto) throws EntityNotFoundException, WrongOwnerException;

    ItemDto findById(Long id) throws EntityNotFoundException;

    List<ItemDto> findAllMyItems(Long ownerId) throws EntityNotFoundException;

    List<ItemDto> findByNameOrDescription(String text);
}