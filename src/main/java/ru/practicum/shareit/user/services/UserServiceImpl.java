package ru.practicum.shareit.user.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.exceptions.EntityNotFoundException;
import ru.practicum.shareit.user.exceptions.NotUniqueUserEmail;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserJpaRepository;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserJpaRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public UserDto save(UserDto userDto) throws NotUniqueUserEmail {
        User user = userMapper.dtoToModel(userDto);
        userRepository.save(user);
        log.debug(String.format("User with id = %d saved", user.getId()));
        return userMapper.modelToDto(user);
    }

    @Override
    public UserDto findById(Long id) throws EntityNotFoundException {
        User user = userRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException(String.format("%s with id = %d does not exist in database", "User", id)));
        log.debug(String.format("User with id = %d founded", user.getId()));
        return userMapper.modelToDto(user);
    }

    @Override
    public UserDto update(UserDto userDto, Long id) throws EntityNotFoundException, NotUniqueUserEmail {
        User user = userRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException(String.format("%s with id = %d does not exist in database", "User", id)));
        String updatedEmail = userDto.getEmail();
        String updatedName = userDto.getName();
        if (updatedEmail != null) {
            User userWithProbablySameEmail = userRepository.findByEmail(updatedEmail);
            if (userWithProbablySameEmail == null || Objects.equals(userWithProbablySameEmail.getId(), user.getId())) {
                user.setEmail(updatedEmail);
            } else {
                throw new NotUniqueUserEmail(String.format("User can't be updated. Email %s is already in use", user.getEmail()));
            }
        }
        if (updatedName != null) {
            user.setName(updatedName);
        }
        log.debug("For User Entity fields initialized to provide UPDATE operation");
        userRepository.save(user);
        log.debug(String.format("User with id = %d updated", id));
        return userMapper.modelToDto(user);
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