package ru.practicum.shareit.user.repository;

import ru.practicum.shareit.user.exceptions.EntityNotFoundException;
import ru.practicum.shareit.user.exceptions.NotUniqueUserEmail;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserRepository {
    User save(User user) throws NotUniqueUserEmail;

    User findById(Long id) throws EntityNotFoundException;

    User update(User user) throws NotUniqueUserEmail;

    void deleteById(Long id);

    List<User> findAll();
}