package ru.practicum.shareit.user.repository;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.user.model.User;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@Transactional
class UserJpaRepositoryTest {
    @Autowired
    UserJpaRepository userJpaRepository;
    static User user1;
    static User user2;

    @BeforeAll
    static void setUp() {
        user1 = new User();
        user1.setName("First User");
        user1.setEmail("First@mail.ru");

        user2 = new User();
        user2.setName("Second User");
        user2.setEmail("Second@mail.ru");
    }

    @Test
    void findByEmail() {
        userJpaRepository.save(user1);
        userJpaRepository.save(user2);

        User user = userJpaRepository.findByEmail("Second@mail.ru");

        assertEquals(2L, user.getId());
        assertEquals("Second@mail.ru", user.getEmail());
        assertEquals("Second User", user.getName());
    }
}