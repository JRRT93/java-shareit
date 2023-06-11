package ru.practicum.shareit.request.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemJpaRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestJpaRepository;
import ru.practicum.shareit.user.exceptions.EntityNotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserJpaRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ItemRequestServiceTest {
    ItemRequestService itemRequestService;
    @Mock
    ItemRequestJpaRepository repository;
    @Mock
    UserJpaRepository userRepository;
    @Mock
    ItemJpaRepository itemRepository;
    @Autowired
    ItemRequestMapper mapper;
    @Autowired
    ItemMapper itemMapper;
    User author;
    ItemRequestDto itemRequestDto;
    ItemRequest itemRequest;

    @BeforeEach
    void inject() {
        itemRequestService = new ItemRequestServiceImpl(repository, userRepository, itemRepository, mapper, itemMapper);

        author = new User();
        author.setId(1L);
        author.setEmail("test@mail.ru");
        author.setName("TestUser");

        itemRequestDto = new ItemRequestDto();
        itemRequestDto.setId(1L);

        itemRequest = new ItemRequest();
        itemRequest.setId(1L);

    }

    @Test
    void saveShouldThrowNotFind() {
        Mockito
                .when(userRepository.findById(99L))
                .thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> itemRequestService.save(99L, itemRequestDto));
    }

    @Test
    void save() throws EntityNotFoundException {
        Mockito
                .when(userRepository.findById(1L))
                .thenReturn(Optional.ofNullable(author));

        ItemRequestDto savedRequest = itemRequestService.save(1L, itemRequestDto);
        assertEquals(1L, savedRequest.getAuthor().getId());
        assertNotNull(savedRequest.getCreated());
    }

    @Test
    void findByIdShouldThrow() {
        Mockito
                .when(repository.findById(99L))
                .thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> itemRequestService.findById(99L, 1L));
    }

    @Test
    void findByIdShould() throws EntityNotFoundException {
        ItemDto itemWithAnswer = new ItemDto();
        itemWithAnswer.setId(2L);
        itemWithAnswer.setDescription("Answer on request");
        itemWithAnswer.setName("Test Answer");
        itemWithAnswer.setRequestId(1L);
        itemWithAnswer.setAvailable(true);
        List<Item> answers = new ArrayList<>();
        Item itemToSave = itemMapper.dtoToModel(itemWithAnswer);
        Item savedItem = itemRepository.save(itemToSave);
        answers.add(savedItem);

        Mockito
                .when(userRepository.findById(1L))
                .thenReturn(Optional.ofNullable(author));
        Mockito
                .when(repository.findById(1L))
                .thenReturn(Optional.ofNullable(itemRequest));
        Mockito
                .when(itemRepository.findByRequestIdOrderByIdAsc(1L))
                .thenReturn(answers);

        ItemRequestDto foundedItem = itemRequestService.findById(1L, 1L);
        assertEquals(1L, foundedItem.getItems().size());
    }
}