package ru.practicum.shareit.item.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.exceptions.WrongOwnerException;
import ru.practicum.shareit.item.services.ItemService;
import ru.practicum.shareit.user.exceptions.EntityNotFoundException;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/items")
public class ItemController {
    private final ItemService itemService;
    private final String xSharerUserId = "X-Sharer-User-Id";

    @PostMapping
    public ItemDto createItem(@RequestHeader(xSharerUserId) Long userId, @RequestBody @Valid ItemDto itemDto) throws EntityNotFoundException {
        log.info("POST request for /items received");
        return itemService.save(userId, itemDto);
    }

    @GetMapping("/{itemId}")
    public ItemDto findById(@PathVariable Long itemId) throws EntityNotFoundException {
        log.info(String.format("GET request for /items/%d received", itemId));
        return itemService.findById(itemId);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@RequestHeader(xSharerUserId) Long ownerId, @PathVariable Long itemId,
                              @RequestBody ItemDto itemDto) throws WrongOwnerException, EntityNotFoundException {
        log.info(String.format("PATCH request for /items/%d received from user id = %d", itemId, ownerId));
        return itemService.update(ownerId, itemId, itemDto);
    }

    @GetMapping
    public List<ItemDto> getAllUsersItems(@RequestHeader(xSharerUserId) Long ownerId) throws EntityNotFoundException {
        log.info(String.format("GET request for /items received from user id = %d", ownerId));
        return itemService.findAllMyItems(ownerId);
    }

    @GetMapping("/search")
    public List<ItemDto> findByNameOrDescription(@RequestParam(value = "text") String text) {
        log.info(String.format("GET request for /items received, text for search = %s", text));
        return itemService.findByNameOrDescription(text.toLowerCase());
    }
}