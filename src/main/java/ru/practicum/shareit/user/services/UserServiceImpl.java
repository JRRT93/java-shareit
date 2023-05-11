package ru.practicum.shareit.user.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.exceptions.EntityNotFoundException;
import ru.practicum.shareit.user.exceptions.NotUniqueUserEmail;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper = Mappers.getMapper(UserMapper.class);

    @Override
    public UserDto save(UserDto userDto) throws NotUniqueUserEmail {
        User user = userMapper.dtoToModel(userDto);
        userRepository.save(user);
        log.debug(String.format("User with id = %d saved", user.getId()));
        return userMapper.modelToDto(user);
    }

    @Override
    public UserDto findById(Long id) throws EntityNotFoundException {
        User user = userRepository.findById(id);
        log.debug(String.format("User with id = %d founded", user.getId()));
        return userMapper.modelToDto(user);
    }

    @Override
    public UserDto update(UserDto userDto, Long id) throws EntityNotFoundException, NotUniqueUserEmail {
        findById(id);
        userDto.setId(id);
        log.debug("For DTO Entity ID initialized to provide UPDATE operation");
        User updatedUser = userRepository.update(userMapper.dtoToModel(userDto));
        log.debug(String.format("User with id = %d updated", id));
        return userMapper.modelToDto(updatedUser);
    }

    @Override
    public void deleteById(Long id) throws EntityNotFoundException {
        findById(id);
        userRepository.deleteById(id);
        log.debug(String.format("User with id = %d deleted", id));
    }

    @Override
    public List<UserDto> findAll() {
        return userRepository.findAll()
                .stream()
                .map(userMapper::modelToDto)
                .collect(Collectors.toList());
    }
}