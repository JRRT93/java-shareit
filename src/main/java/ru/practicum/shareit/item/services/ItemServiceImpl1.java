package ru.practicum.shareit.item.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.exceptions.WrongOwnerException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.exceptions.EntityNotFoundException;
import ru.practicum.shareit.user.services.UserService;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemServiceImpl1 implements ItemService {
    private final ItemRepository itemRepository;
    private final UserService userService;
    private final ItemMapper itemMapper = Mappers.getMapper(ItemMapper.class);

    @Override
    public ItemDto save(Long ownerId, ItemDto itemDto) throws EntityNotFoundException {
        userService.findById(ownerId);
        Item item = itemMapper.dtoToModel(itemDto);
        item.setOwnerId(ownerId);
        itemRepository.save(item);
        log.debug(String.format("Item with id = %d saved", item.getId()));
        return itemMapper.modelToDto(item);
    }

    @Override
    public ItemDto findById(Long id) throws EntityNotFoundException {
        Item item = itemRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException(String.format("%s with id = %d does not exist in database", "Item", id)));
        log.debug(String.format("Item with id = %d founded", id));
        return itemMapper.modelToDto(item);
    }

    @Override
    public ItemDto update(Long ownerId, Long itemId, ItemDto itemDto) throws EntityNotFoundException, WrongOwnerException {
        userService.findById(ownerId);
        Item item = itemRepository.findById(itemId).orElseThrow(
                () -> new EntityNotFoundException(String.format("%s with id = %d does not exist in database", "Item", itemId)));
        if (item.getOwnerId().longValue() == ownerId.longValue()) {
            itemDto.setId(itemId);
            log.debug("For DTO Entity ID initialized to provide UPDATE operation");
            Item updatedItem = itemRepository.update(itemMapper.dtoToModel(itemDto));
            log.debug(String.format("Item with id = %d updated", itemId));
            return itemMapper.modelToDto(updatedItem);
        } else {
            throw new WrongOwnerException(String.format("User id = %d is not owner of item id = %d", ownerId, itemId));
        }
    }

    @Override
    public List<ItemDto> findAllMyItems(Long ownerId) throws EntityNotFoundException {
        userService.findById(ownerId);
        return itemRepository.findAllMyItems(ownerId).stream().map(itemMapper::modelToDto).collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> findByNameOrDescription(String text) {
        if (text.isEmpty()) return new ArrayList<>();
        return itemRepository.findByNameOrDescription(text).stream()
                .map(itemMapper::modelToDto)
                .collect(Collectors.toList());
    }
}