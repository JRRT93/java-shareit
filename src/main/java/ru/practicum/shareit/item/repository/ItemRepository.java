package ru.practicum.shareit.item.repository;

import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Optional;

public interface ItemRepository {
    Item save(Item item);

    Optional<Item> findById(Long id);

    Item update(Item item);

    List<Item> findAllMyItems(Long ownerId);

    List<Item> findByNameOrDescription(String text);
}