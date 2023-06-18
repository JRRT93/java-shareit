package ru.practicum.shareit.users.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.users.client.UserClient;
import ru.practicum.shareit.users.dto.UserDto;

import javax.validation.Valid;

@Controller
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Slf4j
@Validated
public class UserController {
    private final UserClient userClient;

    @PostMapping
    public ResponseEntity<Object> save(@RequestBody @Valid UserDto userDto) {
        log.info("POST request for /users received");
        return userClient.createUser(userDto);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> findById(@PathVariable Long id) {
        log.info(String.format("GET request for /users/%d received", id));
        return userClient.findById(id);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Object> update(@PathVariable Long id, @RequestBody UserDto userDto) {
        log.info(String.format("PATCH request for /users/%d received", id));
        return userClient.update(userDto, id);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> delete(@PathVariable Long id) {
        log.info(String.format("DELETE request for /users/%d received", id));
        return userClient.deleteById(id);
    }

    @GetMapping
    public ResponseEntity<Object> findAll() {
        log.info("GET request for /users received");
        return userClient.findAll();
    }
}