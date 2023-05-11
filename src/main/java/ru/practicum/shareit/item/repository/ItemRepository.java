package ru.practicum.shareit.item.repository;

import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.exceptions.EntityNotFoundException;

import java.util.List;

public interface ItemRepository {
    Item save(Item item);

    Item findById(Long id) throws EntityNotFoundException;

    Item update(Item item);

    List<Item> findAllMyItems(Long ownerId);

    List<Item> findByNameOrDescription(String text);
}