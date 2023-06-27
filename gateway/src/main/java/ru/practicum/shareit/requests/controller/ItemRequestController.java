package ru.practicum.shareit.requests.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.requests.client.RequestClient;
import ru.practicum.shareit.requests.dto.ItemRequestDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/requests")
@Validated
public class ItemRequestController {
    private final RequestClient requestClient;
    private final String xSharerUserId = "X-Sharer-User-Id";

    @PostMapping
    public ResponseEntity<Object> createItemRequest(@RequestHeader(xSharerUserId) Long authorId,
                                                    @RequestBody @Valid ItemRequestDto itemRequestDto) {
        log.info("POST request for /requests received");
        return requestClient.createItemRequest(authorId, itemRequestDto);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> findById(@RequestHeader(xSharerUserId) Long askerId, @PathVariable Long requestId) {
        log.info(String.format("GET request for /requests/%d received", requestId));
        return requestClient.findById(requestId, askerId);
    }

    @GetMapping
    public ResponseEntity<Object> findMyItemRequests(@RequestHeader(xSharerUserId) Long authorId) {
        log.info("GET request for /requests from author received");
        return requestClient.findMyItemRequests(authorId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> findTheirItemRequest(@RequestHeader(xSharerUserId) Long authorId,
                                                       @PositiveOrZero @RequestParam(value = "from", defaultValue = "0", required = false)
                                                       Integer startingEntry,
                                                       @Positive @RequestParam(value = "size", defaultValue = "10", required = false)
                                                       Integer size) {
        log.info("GET request for /requests/all received");
        return requestClient.findTheirItemRequest(authorId, startingEntry, size);
    }
}
