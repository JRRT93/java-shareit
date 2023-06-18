package ru.practicum.shareit.user.services;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.exceptions.EntityNotFoundException;
import ru.practicum.shareit.user.exceptions.NotUniqueUserEmail;

import java.util.List;

public interface UserService {
    UserDto save(UserDto userDto) throws NotUniqueUserEmail;

    UserDto findById(Long id) throws EntityNotFoundException;

    UserDto update(UserDto userDto, Long id) throws EntityNotFoundException, NotUniqueUserEmail;

    void deleteById(Long id) throws EntityNotFoundException;

    List<UserDto> findAll();
}