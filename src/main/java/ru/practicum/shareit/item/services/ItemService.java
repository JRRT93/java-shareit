package ru.practicum.shareit.item.services;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemOwnerDto;
import ru.practicum.shareit.item.exceptions.CommentWithoutCompletedBooking;
import ru.practicum.shareit.item.exceptions.WrongOwnerException;
import ru.practicum.shareit.user.exceptions.EntityNotFoundException;

import java.util.List;

public interface ItemService {
    ItemDto save(Long ownerId, ItemDto itemDto) throws EntityNotFoundException;

    ItemDto update(Long ownerId, Long itemId, ItemDto itemDto) throws EntityNotFoundException, WrongOwnerException;

    ItemOwnerDto findById(Long id, Long userId) throws EntityNotFoundException;

    List<ItemOwnerDto> findAllMyItems(Long ownerId) throws EntityNotFoundException;

    List<ItemDto> findByNameOrDescription(String text);

    CommentDto saveComment(Long bookerId, Long itemId, CommentDto commentDto) throws EntityNotFoundException,
            CommentWithoutCompletedBooking;
}