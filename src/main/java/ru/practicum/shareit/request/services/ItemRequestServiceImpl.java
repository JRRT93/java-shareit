package ru.practicum.shareit.request.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.repository.ItemJpaRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestJpaRepository;
import ru.practicum.shareit.user.exceptions.EntityNotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserJpaRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRequestJpaRepository repository;
    private final UserJpaRepository userRepository;
    private final ItemJpaRepository itemRepository;
    private final ItemRequestMapper mapper;
    private final ItemMapper itemMapper;

    @Override
    public ItemRequestDto save(Long authorId, ItemRequestDto itemRequestDto) throws EntityNotFoundException {
        User author = userRepository.findById(authorId).orElseThrow(
                () -> new EntityNotFoundException(String.format("%s with id = %d does not exist in database", "User", authorId)));
        ItemRequest itemRequest = mapper.dtoToModel(itemRequestDto);
        itemRequest.setAuthor(author);
        LocalDateTime now = LocalDateTime.now();
        itemRequest.setCreated(now);
        repository.save(itemRequest);
        log.debug(String.format("ItemRequest with id = %d saved", itemRequest.getId()));
        return mapper.modelToDto(itemRequest);
    }

    @Override
    public ItemRequestDto findById(Long requestId, Long askerId) throws EntityNotFoundException {
        userRepository.findById(askerId).orElseThrow(
                () -> new EntityNotFoundException(String.format("%s with id = %d does not exist in database", "User", askerId)));
        ItemRequest itemRequest = repository.findById(requestId).orElseThrow(
                () -> new EntityNotFoundException(String.format("%s with id = %d does not exist in database", "ItemRequest",
                        requestId)));
        List<ItemDto> requestAnswersList = itemRepository.findByRequestIdOrderByIdAsc(requestId).stream()
                .map(itemMapper::modelToDto)
                .collect(Collectors.toList());
        ItemRequestDto itemRequestDto = mapper.modelToDto(itemRequest);
        itemRequestDto.setItems(requestAnswersList);
        return itemRequestDto;
    }

    @Override
    public Collection<ItemRequestDto> findMyItemRequests(Long authorId) throws EntityNotFoundException {
        userRepository.findById(authorId).orElseThrow(
                () -> new EntityNotFoundException(String.format("%s with id = %d does not exist in database", "User", authorId)));
        Collection<ItemRequestDto> myItemRequests = repository.findByAuthorIdOrderByCreatedDesc(authorId).stream()
                .map(mapper::modelToDto)
                .collect(Collectors.toList());
        return findAndSetAnswers(myItemRequests);
    }

    @Override
    public Collection<ItemRequestDto> findTheirItemRequest(Long authorId, Integer startingEntry, Integer size)
            throws EntityNotFoundException {
        userRepository.findById(authorId).orElseThrow(
                () -> new EntityNotFoundException(String.format("%s with id = %d does not exist in database", "User", authorId)));
        Collection<ItemRequestDto> itemRequests;
        if (startingEntry != null && size != null) {
            Pageable pageRequest = PageRequest.of(startingEntry / size, size);
            itemRequests = repository.findByAuthorIdNotOrderByCreatedDesc(authorId, pageRequest)
                    .stream()
                    .map(mapper::modelToDto)
                    .collect(Collectors.toList());
        } else {
            itemRequests = repository.findByAuthorIdNotOrderByCreatedDesc(authorId)
                    .stream()
                    .map(mapper::modelToDto)
                    .collect(Collectors.toList());
        }
        return findAndSetAnswers(itemRequests);
    }

    private Collection<ItemRequestDto> findAndSetAnswers(Collection<ItemRequestDto> itemRequests) {
        List<Long> itemRequestsId = itemRequests.stream()
                .map(ItemRequestDto::getId)
                .collect(Collectors.toList());
        List<ItemDto> itemList = itemRepository.findByRequestIdIn(itemRequestsId).stream()
                .map(itemMapper::modelToDto)
                .collect(Collectors.toList());
        for (ItemRequestDto itemRequestDto : itemRequests) {
            List<ItemDto> requestAnswersList = new ArrayList<>();
            for (ItemDto itemDto : itemList) {
                if (itemDto.getRequestId().equals(itemRequestDto.getId())) {
                    requestAnswersList.add(itemDto);
                }
            }
            itemRequestDto.setItems(requestAnswersList);
        }
        return itemRequests;
    }
}