package ru.practicum.shareit.item.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemOwnerDto;
import ru.practicum.shareit.item.exceptions.CommentWithoutCompletedBooking;
import ru.practicum.shareit.item.exceptions.WrongOwnerException;
import ru.practicum.shareit.item.services.ItemService;
import ru.practicum.shareit.user.exceptions.EntityNotFoundException;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/items")
public class ItemController {
    private final ItemService itemService;
    private final String xSharerUserId = "X-Sharer-User-Id";

    @PostMapping
    public ItemDto createItem(@RequestHeader(xSharerUserId) Long userId, @RequestBody ItemDto itemDto)
            throws EntityNotFoundException {
        log.info("POST request for /items received");
        return itemService.save(userId, itemDto);
    }

    @GetMapping("/{itemId}")
    public ItemOwnerDto findById(@PathVariable Long itemId, @RequestHeader(xSharerUserId) Long userId)
            throws EntityNotFoundException {
        log.info(String.format("GET request for /items/%d received", itemId));
        return itemService.findById(itemId, userId);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@RequestHeader(xSharerUserId) Long ownerId, @PathVariable Long itemId,
                              @RequestBody ItemDto itemDto) throws WrongOwnerException, EntityNotFoundException {
        log.info(String.format("PATCH request for /items/%d received from user id = %d", itemId, ownerId));
        return itemService.update(ownerId, itemId, itemDto);
    }

    @GetMapping
    public List<ItemOwnerDto> getAllUsersItems(@RequestHeader(xSharerUserId) Long ownerId,
                                               @RequestParam(value = "from", defaultValue = "0", required = false)
                                               Integer startingEntry,
                                               @RequestParam(value = "size", defaultValue = "10", required = false)
                                               Integer size)
            throws EntityNotFoundException {
        Pageable pageable;
        if (size != null && startingEntry != null) {
            pageable = PageRequest.of(startingEntry / size, size);
        } else {
            pageable = Pageable.unpaged();
        }
        log.info(String.format("GET request for /items received from user id = %d", ownerId));
        return itemService.findAllMyItems(ownerId, pageable);
    }

    @GetMapping("/search")
    public List<ItemDto> findByNameOrDescription(@RequestParam(value = "text") String text,
                                                 @RequestParam(value = "from", defaultValue = "0", required = false)
                                                 Integer startingEntry,
                                                 @RequestParam(value = "size", defaultValue = "10", required = false)
                                                 Integer size) {
        Pageable pageable;
        if (size != null && startingEntry != null) {
            pageable = PageRequest.of(startingEntry / size, size);
        } else {
            pageable = Pageable.unpaged();
        }
        log.info(String.format("GET request for /items received, text for search = %s", text));
        return itemService.findByNameOrDescription(text.toLowerCase(), pageable);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto saveComment(@RequestHeader(xSharerUserId) Long bookerId, @PathVariable Long itemId,
                                  @RequestBody CommentDto commentDto) throws CommentWithoutCompletedBooking,
            EntityNotFoundException {
        log.info(String.format("POST request for /items/%d/comment received", itemId));
        return itemService.saveComment(bookerId, itemId, commentDto);
    }
}