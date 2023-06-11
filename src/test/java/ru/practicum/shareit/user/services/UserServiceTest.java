package ru.practicum.shareit.user.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.exceptions.EntityNotFoundException;
import ru.practicum.shareit.user.exceptions.NotUniqueUserEmail;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserJpaRepository;

import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;

@SpringBootTest
class UserServiceTest {
    @Mock
    UserJpaRepository userJpaRepository;
    UserMapper userMapper = Mappers.getMapper(UserMapper.class);
    UserService userService;
    UserDto userDto;
    User user;

    @BeforeEach
    void inject() {
        userService = new UserServiceImpl(userJpaRepository, userMapper);
        userDto = new UserDto();
        userDto.setEmail("test@mail.ru");
        userDto.setName("TestUser");
        user = new User();
        user.setId(1L);
        user.setEmail("test@mail.ru");
        user.setName("TestUser");
    }

    @Test
    void saveShouldCallRepositorySaveOneTime() throws NotUniqueUserEmail {
        Mockito
                .when(userJpaRepository.save(user))
                .thenReturn(user);

        userService.save(userDto);

        Mockito.verify(userJpaRepository, Mockito.times(1)).save(any(User.class));
    }

    @Test
    void findByIdShouldCallRepositoryFindByIdOneTime() throws EntityNotFoundException {
        userDto.setId(1L);
        Mockito
                .when(userJpaRepository.findById(1L))
                .thenReturn(Optional.ofNullable(user));

        UserDto foundedUser = userService.findById(1L);

        Mockito.verify(userJpaRepository, Mockito.times(1)).findById(1L);
        assertEquals(userDto.getEmail(), foundedUser.getEmail());
        assertEquals(userDto.getName(), foundedUser.getName());
        assertEquals(userDto.getId(), foundedUser.getId());
    }

    @Test
    void findByIdShouldThrowException() {
        Mockito
                .when(userJpaRepository.findById(2L))
                .thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> userService.findById(2L));
    }

    @Test
    void updateShouldThrowException() {
        Mockito
                .when(userJpaRepository.findById(2L))
                .thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> userService.update(userDto, 2L));
    }

    @Test
    void updateOnlyName() throws NotUniqueUserEmail, EntityNotFoundException {
        UserDto userDtoOnlyNameForUpdate = new UserDto();
        userDtoOnlyNameForUpdate.setName("UpdatedNAME");

        User updatedUser = new User();
        updatedUser.setId(1L);
        updatedUser.setName("UpdatedNAME");
        updatedUser.setEmail("test@mail.ru");

        Mockito
                .when(userJpaRepository.findById(1L))
                .thenReturn(Optional.ofNullable(user));
        Mockito
                .when(userJpaRepository.save(updatedUser))
                .thenReturn(updatedUser);

        UserDto userDtoUpdated = userService.update(userDtoOnlyNameForUpdate, 1L);

        assertEquals("test@mail.ru", userDtoUpdated.getEmail());
        assertEquals("UpdatedNAME", userDtoUpdated.getName());
        assertEquals(1L, userDtoUpdated.getId());
    }

    @Test
    void updateOnlyEmailUnique() throws NotUniqueUserEmail, EntityNotFoundException {
        UserDto userDtoOnlyEmailUniqueForUpdate = new UserDto();
        userDtoOnlyEmailUniqueForUpdate.setEmail("Updated_Unique_EMAIL");

        User updatedUser = new User();
        updatedUser.setId(1L);
        updatedUser.setName("TestUser");
        updatedUser.setEmail("Updated_Unique_EMAIL");

        Mockito
                .when(userJpaRepository.findById(1L))
                .thenReturn(Optional.ofNullable(user));
        Mockito
                .when(userJpaRepository.save(updatedUser))
                .thenReturn(updatedUser);
        Mockito
                .when(userJpaRepository.findByEmail("Updated_Unique_EMAIL"))
                .thenReturn(null);

        UserDto userDtoUpdated = userService.update(userDtoOnlyEmailUniqueForUpdate, 1L);

        assertEquals("Updated_Unique_EMAIL", userDtoUpdated.getEmail());
        assertEquals("TestUser", userDtoUpdated.getName());
        assertEquals(1L, userDtoUpdated.getId());
    }

    @Test
    void updateOnlyEmailNotUniqueButSameUser() throws NotUniqueUserEmail, EntityNotFoundException {
        UserDto userDtoOnlyEmailUniqueForUpdate = new UserDto();
        userDtoOnlyEmailUniqueForUpdate.setEmail("Updated_Unique_EMAIL");

        User updatedUser = new User();
        updatedUser.setId(1L);
        updatedUser.setName("TestUser");
        updatedUser.setEmail("Updated_Unique_EMAIL");

        Mockito
                .when(userJpaRepository.findById(1L))
                .thenReturn(Optional.ofNullable(user));
        Mockito
                .when(userJpaRepository.save(updatedUser))
                .thenReturn(updatedUser);
        Mockito
                .when(userJpaRepository.findByEmail("Updated_Unique_EMAIL"))
                .thenReturn(updatedUser);

        UserDto userDtoUpdated = userService.update(userDtoOnlyEmailUniqueForUpdate, 1L);

        assertEquals("Updated_Unique_EMAIL", userDtoUpdated.getEmail());
        assertEquals("TestUser", userDtoUpdated.getName());
        assertEquals(1L, userDtoUpdated.getId());
    }

    @Test
    void updateOnlyEmailNotUniqueShouldThrow() {
        UserDto userOnlyEmailNotUniqueForUpdate = new UserDto();
        userOnlyEmailNotUniqueForUpdate.setEmail("Updated_NOT_Unique_EMAIL");

        User updatedUser = new User();
        updatedUser.setId(1L);
        updatedUser.setName("TestUser");
        updatedUser.setEmail("Updated_Unique_EMAIL");

        User anotherUser = new User();
        updatedUser.setId(2L);
        updatedUser.setName("SecondTestUser");
        updatedUser.setEmail("Updated_NOT_Unique_EMAIL");

        Mockito
                .when(userJpaRepository.findById(1L))
                .thenReturn(Optional.ofNullable(user));
        Mockito
                .when(userJpaRepository.findByEmail("Updated_NOT_Unique_EMAIL"))
                .thenReturn(anotherUser);

        assertThrows(NotUniqueUserEmail.class, () -> userService.update(userOnlyEmailNotUniqueForUpdate, 1L));
    }

    @Test
    void deleteByIdShouldThrow() {
        Mockito
                .when(userJpaRepository.findById(99L))
                .thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> userService.deleteById(99L));
    }

    @Test
    void deleteById() throws EntityNotFoundException {
        Mockito
                .when(userJpaRepository.findById(1L))
                .thenReturn(Optional.ofNullable(user));

        userService.deleteById(1L);

        Mockito.verify(userJpaRepository, Mockito.times(1)).deleteById(1L);
    }

    @Test
    void findAll() {
        Mockito
                .when(userJpaRepository.findAll())
                .thenReturn(new ArrayList<>());

        userMapper.modelToDto(null);
        userMapper.dtoToModel(null);
        userService.findAll();

        Mockito.verify(userJpaRepository, Mockito.times(1)).findAll();
    }
}