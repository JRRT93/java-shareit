package ru.practicum.shareit.user.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.exceptions.EntityNotFoundException;
import ru.practicum.shareit.user.exceptions.NotUniqueUserEmail;
import ru.practicum.shareit.user.services.UserService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Validated
@Slf4j
public class UserController {
    private final UserService userService;

    @PostMapping
    public UserDto save(@RequestBody @Valid UserDto userDto) throws NotUniqueUserEmail {
        log.info("POST request for /users received");
        return userService.save(userDto);
    }

    @GetMapping("/{id}")
    public UserDto findById(@PathVariable Long id) throws EntityNotFoundException {
        log.info(String.format("GET request for /users/%d received", id));
        return userService.findById(id);
    }

    @PatchMapping("/{id}")
    public UserDto update(@PathVariable Long id, @RequestBody UserDto userDto) throws EntityNotFoundException, NotUniqueUserEmail {
        log.info("PATCH request for /users received");
        return userService.update(userDto, id);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) throws EntityNotFoundException {
        log.info(String.format("DELETE request for /users/%d received", id));
        userService.deleteById(id);
    }

    @GetMapping
    public List<UserDto> findAll() {
        log.info("GET request for /users received");
        return userService.findAll();
    }
}