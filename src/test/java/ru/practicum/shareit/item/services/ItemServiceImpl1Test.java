package ru.practicum.shareit.item.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mapstruct.factory.Mappers;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.exceptions.WrongOwnerException;
import ru.practicum.shareit.item.repository.InMemoryItemRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.exceptions.EntityNotFoundException;
import ru.practicum.shareit.user.exceptions.NotUniqueUserEmail;
import ru.practicum.shareit.user.repository.InMemoryUserRepository;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.services.UserService;
import ru.practicum.shareit.user.services.UserServiceImpl1;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ItemServiceImpl1Test {
    private UserRepository userRepository;
    private ItemRepository itemRepository;
    private UserService userService;
    private ItemService itemService;
    UserDto firstUserDto;
    UserDto secondUserDto;
    ItemDto firstItemDto;
    ItemDto secondItemDto;
    private final ItemMapper itemMapper = Mappers.getMapper(ItemMapper.class);


    @BeforeEach
    void createUsersAndItems() {
        userRepository = new InMemoryUserRepository();
        itemRepository = new InMemoryItemRepository();
        userService = new UserServiceImpl1(userRepository);
        itemService = new ItemServiceImpl1(itemRepository, userService);

        firstUserDto = new UserDto();
        firstUserDto.setName("Dim Yurich");
        firstUserDto.setEmail("oper@mail.ru");

        secondUserDto = new UserDto();
        secondUserDto.setName("Klim Ssanich");
        secondUserDto.setEmail("zhukov@yandex.ru");

        firstItemDto = new ItemDto();
        firstItemDto.setName("STALIN 3000");
        firstItemDto.setDescription("RAZMER - MOE POCHTENIE");
        firstItemDto.setAvailable(true);

        secondItemDto = new ItemDto();
        secondItemDto.setName("RUBBER PIG");
        secondItemDto.setDescription("DEMENTII NESI SVINEI. RAZMER!");
        secondItemDto.setAvailable(false);
    }

    @Test
    void shouldSaveUserAndGiveId1() throws NotUniqueUserEmail, EntityNotFoundException {
        userService.save(firstUserDto);
        ItemDto savedItem = itemService.save(1L, firstItemDto);
        firstItemDto.setId(1L);

        assertEquals(savedItem, firstItemDto);
    }

    @Test
    void shouldThrowUserNotFound() throws NotUniqueUserEmail, EntityNotFoundException {
        userService.save(firstUserDto);

        assertThrows(EntityNotFoundException.class, () -> itemService.save(-99L, firstItemDto));
        assertThrows(EntityNotFoundException.class, () -> itemService.save(66L, firstItemDto));
    }

    @Test
    void shouldFindById() throws NotUniqueUserEmail, EntityNotFoundException {
        userService.save(firstUserDto);
        userService.save(secondUserDto);
        itemService.save(2L, firstItemDto);
        itemService.save(2L, secondItemDto);

        ItemDto expectedFindedItem = new ItemDto();
        expectedFindedItem.setId(2L);
        expectedFindedItem.setName("RUBBER PIG");
        expectedFindedItem.setDescription("DEMENTII NESI SVINEI. RAZMER!");
        expectedFindedItem.setAvailable(false);

        ItemDto findedItem = itemService.findById(2L);

        assertEquals(expectedFindedItem, findedItem);
    }

    @Test
    void shouldThrowNotFoundException() throws NotUniqueUserEmail, EntityNotFoundException {
        userService.save(firstUserDto);
        itemService.save(1L, secondItemDto);

        assertThrows(EntityNotFoundException.class, () -> itemService.findById(-99L));
        assertThrows(EntityNotFoundException.class, () -> itemService.findById(99L));
    }

    @Test
    void shouldUpdateOnlyName() throws NotUniqueUserEmail, EntityNotFoundException, WrongOwnerException {
        userService.save(firstUserDto);
        itemService.save(1L, firstItemDto);

        ItemDto dtoForUpdate = new ItemDto();
        dtoForUpdate.setName("MOUSTAGE");

        ItemDto expectedAfterUpdate = new ItemDto();
        expectedAfterUpdate.setId(1L);
        expectedAfterUpdate.setName("MOUSTAGE");
        expectedAfterUpdate.setDescription("RAZMER - MOE POCHTENIE");
        expectedAfterUpdate.setAvailable(true);

        ItemDto updatedItemDto = itemService.update(1L, 1L, dtoForUpdate);

        assertEquals(expectedAfterUpdate, updatedItemDto);
    }

    @Test
    void shouldUpdateOnlyAvailable() throws NotUniqueUserEmail, EntityNotFoundException, WrongOwnerException {
        userService.save(firstUserDto);
        itemService.save(1L, firstItemDto);

        ItemDto dtoForUpdate = new ItemDto();
        dtoForUpdate.setAvailable(false);

        ItemDto expectedAfterUpdate = new ItemDto();
        expectedAfterUpdate.setId(1L);
        expectedAfterUpdate.setName("STALIN 3000");
        expectedAfterUpdate.setDescription("RAZMER - MOE POCHTENIE");
        expectedAfterUpdate.setAvailable(false);

        ItemDto updatedItemDto = itemService.update(1L, 1L, dtoForUpdate);

        assertEquals(expectedAfterUpdate, updatedItemDto);
    }

    @Test
    void shouldUpdateOnlyDescription() throws NotUniqueUserEmail, EntityNotFoundException, WrongOwnerException {
        userService.save(firstUserDto);
        itemService.save(1L, firstItemDto);

        ItemDto dtoForUpdate = new ItemDto();
        dtoForUpdate.setDescription("BLANK BLANK");

        ItemDto expectedAfterUpdate = new ItemDto();
        expectedAfterUpdate.setId(1L);
        expectedAfterUpdate.setName("STALIN 3000");
        expectedAfterUpdate.setDescription("BLANK BLANK");
        expectedAfterUpdate.setAvailable(true);

        ItemDto updatedItemDto = itemService.update(1L, 1L, dtoForUpdate);

        assertEquals(expectedAfterUpdate, updatedItemDto);
    }

    @Test
    void shouldThrownWrongOwner() throws NotUniqueUserEmail, EntityNotFoundException, WrongOwnerException {
        userService.save(firstUserDto);
        userService.save(secondUserDto);
        itemService.save(1L, firstItemDto);

        ItemDto dtoForUpdate = new ItemDto();
        dtoForUpdate.setDescription("BLANK BLANK");

        assertThrows(WrongOwnerException.class, () -> itemService.update(2L, 1L, dtoForUpdate));
    }

    @Test
    void findAllMyItems() throws NotUniqueUserEmail, EntityNotFoundException {
        userService.save(firstUserDto);
        userService.save(secondUserDto);
        itemService.save(2L, firstItemDto);
        itemService.save(2L, secondItemDto);

        firstItemDto.setId(1L);
        secondItemDto.setId(2L);

        List<ItemDto> expectedList = new ArrayList<>();
        expectedList.add(firstItemDto);
        expectedList.add(secondItemDto);

        assertEquals(expectedList, itemService.findAllMyItems(2L));

        expectedList = new ArrayList<>();
        assertEquals(expectedList, itemService.findAllMyItems(1L));
    }

    @Test
    void findByNameOrDescription() throws EntityNotFoundException, NotUniqueUserEmail, WrongOwnerException {
        userService.save(firstUserDto);
        userService.save(secondUserDto);
        itemService.save(2L, firstItemDto);
        itemService.save(2L, secondItemDto);

        firstItemDto.setId(1L);
        List<ItemDto> expectedList = new ArrayList<>();
        expectedList.add(firstItemDto);

        String text = "razmer";
        assertEquals(expectedList, itemService.findByNameOrDescription(text));

        ItemDto dtoForUpdate = new ItemDto();
        dtoForUpdate.setAvailable(true);
        itemService.update(2L, 2L, dtoForUpdate);

        secondItemDto.setId(2L);
        secondItemDto.setAvailable(true);
        expectedList.add(secondItemDto);
        assertEquals(expectedList, itemService.findByNameOrDescription(text));

        text = "";
        expectedList.clear();
        assertEquals(expectedList, itemService.findByNameOrDescription(text));
    }
}