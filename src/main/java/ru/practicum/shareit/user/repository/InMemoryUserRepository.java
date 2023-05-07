package ru.practicum.shareit.user.repository;

import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.exceptions.NotUniqueUserEmail;
import ru.practicum.shareit.user.model.User;

import java.util.*;

@Component
@Repository
public class InMemoryUserRepository implements UserRepository {
    private final Map<Long, User> userMap = new HashMap<>();
    private final Set<String> emails = new HashSet<>();
    private Long id = 1L;

    @Override
    public User save(User user) throws NotUniqueUserEmail {
        if (isUniqueEmail(user.getEmail())) {
            user.setId(id);
            id++;
            userMap.put(user.getId(), user);
            emails.add(user.getEmail());
            return user;
        } else {
            throw new NotUniqueUserEmail(String.format("Email %s is already in use", user.getEmail()));
        }
    }

    @Override
    public Optional<User> findById(Long id) {
        return Optional.ofNullable(userMap.get(id));
    }

    @Override
    public User update(User user) throws NotUniqueUserEmail {
        User userToUpdate = userMap.get(user.getId());
        String updatedEmail = user.getEmail();
        String updatedName = user.getName();

        if (updatedEmail != null) {
            if (isUniqueEmail(updatedEmail) || updatedEmail.equals(userToUpdate.getEmail())) {
                emails.remove(userToUpdate.getEmail());
                userToUpdate.setEmail(updatedEmail);
                emails.add(updatedEmail);
            } else {
                throw new NotUniqueUserEmail(String.format("User can't be updated. Email %s is already in use", user.getEmail()));
            }
        }
        if (updatedName != null) userToUpdate.setName(updatedName);
        return userToUpdate;
    }

    @Override
    public void deleteById(Long id) {
        emails.remove(userMap.get(id).getEmail());
        userMap.remove(id);
    }

    @Override
    public List<User> findAll() {
        return new ArrayList<>(userMap.values());
    }

    private boolean isUniqueEmail(String email) {
        return !emails.contains(email);
    }
}