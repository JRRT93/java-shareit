package ru.practicum.shareit.user.services;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.exceptions.EntityNotFoundException;
import ru.practicum.shareit.user.exceptions.NotUniqueUserEmail;
import ru.practicum.shareit.user.repository.InMemoryUserRepository;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Slf4j
class UserServiceImplTest {
    private UserRepository userRepository;
    private UserMapper userMapper = Mappers.getMapper(UserMapper.class);
    private UserService userService;
    private UserDto firstUserDto;
    private UserDto secondUserDto;

    @BeforeEach
    void createUsers() {
        userRepository = new InMemoryUserRepository();
        userService = new UserServiceImpl(userRepository);

        firstUserDto = new UserDto();
        firstUserDto.setName("Dim Yurich");
        firstUserDto.setEmail("oper@mail.ru");

        secondUserDto = new UserDto();
        secondUserDto.setName("Klim Ssanich");
        secondUserDto.setEmail("zhukov@yandex.ru");
    }

    @Test
    void shouldSaveUserAndGiveId1() throws NotUniqueUserEmail {
        UserDto savedUser = userService.save(firstUserDto);
        firstUserDto.setId(1L);

        assertEquals(savedUser, firstUserDto);
    }

    @Test
    void shouldThrowEmailException() throws NotUniqueUserEmail {
        secondUserDto.setEmail("oper@mail.ru");

        UserDto savedUser = userService.save(firstUserDto);

        assertThrows(NotUniqueUserEmail.class, () -> userService.save(secondUserDto));
    }

    @Test
    void shouldFindById() throws NotUniqueUserEmail, EntityNotFoundException {
        UserDto savedUser = userService.save(firstUserDto);
        UserDto savedUser2 = userService.save(secondUserDto);

        UserDto expectedFindedUser = new UserDto();
        expectedFindedUser.setId(2L);
        expectedFindedUser.setName("Klim Ssanich");
        expectedFindedUser.setEmail("zhukov@yandex.ru");

        UserDto findedUser = userService.findById(2L);

        assertEquals(expectedFindedUser, findedUser);
    }

    @Test
    void shouldThrowNotFoundException() throws NotUniqueUserEmail {
        UserDto savedUser = userService.save(firstUserDto);
        UserDto savedUser2 = userService.save(secondUserDto);

        assertThrows(EntityNotFoundException.class, () -> userService.findById(-99L));
        assertThrows(EntityNotFoundException.class, () -> userService.findById(99L));
    }

    @Test
    void shouldUpdateOnlyName() throws NotUniqueUserEmail, EntityNotFoundException {
        UserDto dtoForUpdate = new UserDto();
        dtoForUpdate.setName("ISKRA");
        UserDto savedUser = userService.save(firstUserDto);
        UserDto expectedAfterUpdate = new UserDto();
        expectedAfterUpdate.setId(1L);
        expectedAfterUpdate.setName("ISKRA");
        expectedAfterUpdate.setEmail("oper@mail.ru");

        UserDto updatedUserDto = userService.update(dtoForUpdate, 1L);

        assertEquals(expectedAfterUpdate, updatedUserDto);
    }

    @Test
    void shouldUpdateOnlyEmail() throws NotUniqueUserEmail, EntityNotFoundException {
        UserDto dtoForUpdate = new UserDto();
        dtoForUpdate.setEmail("RAZVED@OPROS.RU");
        UserDto savedUser = userService.save(firstUserDto);
        UserDto expectedAfterUpdate = new UserDto();
        expectedAfterUpdate.setId(1L);
        expectedAfterUpdate.setName("Dim Yurich");
        expectedAfterUpdate.setEmail("RAZVED@OPROS.RU");

        UserDto updatedUserDto = userService.update(dtoForUpdate, 1L);

        assertEquals(expectedAfterUpdate, updatedUserDto);
    }

    @Test
    void shouldFullUpdate() throws NotUniqueUserEmail, EntityNotFoundException {
        UserDto dtoForUpdate = new UserDto();
        dtoForUpdate.setName("GOBLIN");
        dtoForUpdate.setEmail("RAZVED@OPROS.RU");
        UserDto savedUser = userService.save(firstUserDto);
        UserDto expectedAfterUpdate = new UserDto();
        expectedAfterUpdate.setId(1L);
        expectedAfterUpdate.setName("GOBLIN");
        expectedAfterUpdate.setEmail("RAZVED@OPROS.RU");

        UserDto updatedUserDto = userService.update(dtoForUpdate, 1L);

        assertEquals(expectedAfterUpdate, updatedUserDto);
    }

    @Test
    void shouldThrowEmailExceptionDuringUpdate() throws NotUniqueUserEmail, EntityNotFoundException {
        UserDto dtoForUpdate = new UserDto();
        dtoForUpdate.setName("GOBLIN");
        dtoForUpdate.setEmail("zhukov@yandex.ru");
        UserDto savedUser = userService.save(firstUserDto);
        UserDto savedUser2 = userService.save(secondUserDto);

        assertThrows(NotUniqueUserEmail.class, () -> userService.update(dtoForUpdate, 1L));
    }

    @Test
    void shouldThrowNotFoundAfterDeletion() throws NotUniqueUserEmail, EntityNotFoundException {
        UserDto savedUser = userService.save(firstUserDto);

        userService.deleteById(1L);

        assertThrows(EntityNotFoundException.class, () -> userService.findById(1L));
    }

    @Test
    void findAllUsers() throws NotUniqueUserEmail {
        userService.save(firstUserDto);
        userService.save(secondUserDto);

        UserDto expectedUser1 = new UserDto();
        expectedUser1.setId(1L);
        expectedUser1.setName("Dim Yurich");
        expectedUser1.setEmail("oper@mail.ru");

        UserDto expectedUser2 = new UserDto();
        expectedUser2.setId(2L);
        expectedUser2.setName("Klim Ssanich");
        expectedUser2.setEmail("zhukov@yandex.ru");

        List<UserDto> expectedList = new ArrayList<>();
        expectedList.add(expectedUser1);
        expectedList.add(expectedUser2);

        assertEquals(expectedList, userService.findAll());
    }
}