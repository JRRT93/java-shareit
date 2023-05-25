package ru.practicum.shareit.item.repository;

import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.exceptions.EntityNotFoundException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@Repository
public class InMemoryItemRepository implements ItemRepository {
    private final Map<Long, Item> itemMap = new HashMap<>();
    private Long id = 1L;

    @Override
    public Item save(Item item) {
        item.setId(id);
        id++;
        itemMap.put(item.getId(), item);
        return item;
    }

    @Override
    public Item findById(Long id) throws EntityNotFoundException {
        return Optional.ofNullable(itemMap.get(id)).orElseThrow(
                () -> new EntityNotFoundException(String.format("%s with id = %d does not exist in database", "Item", id)));
    }

    @Override
    public Item update(Item item) {
        Item itemToUpdate = itemMap.get(item.getId());
        String updatedName = item.getName();
        String updatedDescription = item.getDescription();
        Boolean updatedAvailable = item.getAvailable();

        if (updatedName != null) {
            itemToUpdate.setName(updatedName);
        }
        if (updatedDescription != null) {
            itemToUpdate.setDescription(updatedDescription);
        }
        if (updatedAvailable != null) {
            itemToUpdate.setAvailable(updatedAvailable);
        }

        return itemToUpdate;
    }

    @Override
    public List<Item> findAllMyItems(Long ownerId) {
        return itemMap.values().stream()
                .filter(item -> item.getOwnerId().longValue() == ownerId.longValue())
                .collect(Collectors.toList());
    }

    @Override
    public List<Item> findByNameOrDescription(String text) {
        return itemMap.values().stream()
                .filter(item -> (item.getName().toLowerCase().contains(text)
                        || item.getDescription().toLowerCase().contains(text)) && item.getAvailable())
                .collect(Collectors.toList());
    }
}