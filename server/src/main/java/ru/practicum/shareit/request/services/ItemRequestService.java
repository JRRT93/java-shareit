package ru.practicum.shareit.request.services;

import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.exceptions.EntityNotFoundException;

import java.util.Collection;

public interface ItemRequestService {
    ItemRequestDto save(Long authorId, ItemRequestDto itemRequestDto) throws EntityNotFoundException;

    ItemRequestDto findById(Long requestId, Long askerId) throws EntityNotFoundException;

    Collection<ItemRequestDto> findMyItemRequests(Long authorId) throws EntityNotFoundException;

    Collection<ItemRequestDto> findTheirItemRequest(Long authorId, Pageable pageable)
            throws EntityNotFoundException;
}