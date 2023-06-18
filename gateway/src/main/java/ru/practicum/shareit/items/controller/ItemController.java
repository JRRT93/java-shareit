package ru.practicum.shareit.items.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.items.client.ItemClient;
import ru.practicum.shareit.items.dto.CommentDto;
import ru.practicum.shareit.items.dto.ItemDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/items")
@Validated
public class ItemController {
    private final ItemClient itemClient;
    private final String xSharerUserId = "X-Sharer-User-Id";

    @PostMapping
    public ResponseEntity<Object> createItem(@RequestHeader(xSharerUserId) Long userId, @RequestBody @Valid ItemDto itemDto) {
        log.info("POST request for /items received");
        return itemClient.createItem(userId, itemDto);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> findById(@PathVariable Long itemId, @RequestHeader(xSharerUserId) Long userId) {
        log.info(String.format("GET request for /items/%d received", itemId));
        return itemClient.findById(itemId, userId);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> updateItem(@RequestHeader(xSharerUserId) Long ownerId, @PathVariable Long itemId,
                                             @RequestBody ItemDto itemDto) {
        log.info(String.format("PATCH request for /items/%d received from user id = %d", itemId, ownerId));
        return itemClient.updateItem(ownerId, itemId, itemDto);
    }

    @GetMapping
    public ResponseEntity<Object> getAllUsersItems(@RequestHeader(xSharerUserId) Long ownerId,
                                                   @PositiveOrZero @RequestParam(value = "from", defaultValue = "0", required = false)
                                                   Integer startingEntry,
                                                   @Positive @RequestParam(value = "size", defaultValue = "10", required = false)
                                                   Integer size) {
        log.info(String.format("GET request for /items received from user id = %d", ownerId));
        return itemClient.getAllUsersItems(ownerId, startingEntry, size);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> findByNameOrDescription(@RequestParam(value = "text") String text,
                                                          @PositiveOrZero @RequestParam(value = "from", defaultValue = "0", required = false)
                                                          Integer startingEntry,
                                                          @Positive @RequestParam(value = "size", defaultValue = "10", required = false)
                                                          Integer size) {
        log.info(String.format("GET request for /items received, text for search = %s", text));
        return itemClient.findByNameOrDescription(text.toLowerCase(), startingEntry, size);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> saveComment(@RequestHeader(xSharerUserId) Long bookerId, @PathVariable Long itemId,
                                              @Valid @RequestBody CommentDto commentDto) {
        log.info(String.format("POST request for /items/%d/comment received", itemId));
        return itemClient.saveComment(bookerId, itemId, commentDto);
    }
}