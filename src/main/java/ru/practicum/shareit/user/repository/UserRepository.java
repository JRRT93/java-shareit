package ru.practicum.shareit.user.repository;

import ru.practicum.shareit.user.exceptions.NotUniqueUserEmail;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository {
    User save(User user) throws NotUniqueUserEmail;

    Optional<User> findById(Long id);

    User update(User user) throws NotUniqueUserEmail;

    void deleteById(Long id);

    List<User> findAll();
}
