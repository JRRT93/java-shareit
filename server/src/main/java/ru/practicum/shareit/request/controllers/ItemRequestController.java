package ru.practicum.shareit.request.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.services.ItemRequestService;
import ru.practicum.shareit.user.exceptions.EntityNotFoundException;

import java.util.Collection;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/requests")
public class ItemRequestController {
    private final ItemRequestService itemRequestService;
    private final String xSharerUserId = "X-Sharer-User-Id";

    @PostMapping
    public ItemRequestDto createItemRequest(@RequestHeader(xSharerUserId) Long authorId,
                                            @RequestBody ItemRequestDto itemRequestDto) throws EntityNotFoundException {
        log.info("POST request for /requests received");
        return itemRequestService.save(authorId, itemRequestDto);
    }

    @GetMapping("/{requestId}")
    public ItemRequestDto findById(@RequestHeader(xSharerUserId) Long askerId, @PathVariable Long requestId)
            throws EntityNotFoundException {
        log.info(String.format("GET request for /requests/%d received", requestId));
        return itemRequestService.findById(requestId, askerId);
    }

    @GetMapping
    public Collection<ItemRequestDto> findMyItemRequests(@RequestHeader(xSharerUserId) Long authorId)
            throws EntityNotFoundException {
        log.info("GET request for /requests from author received");
        return itemRequestService.findMyItemRequests(authorId);
    }

    @GetMapping("/all")
    public Collection<ItemRequestDto> findMyItemRequests(@RequestHeader(xSharerUserId) Long authorId,
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
        log.info("GET request for /requests/all received");
        return itemRequestService.findTheirItemRequest(authorId, pageable);
    }
}