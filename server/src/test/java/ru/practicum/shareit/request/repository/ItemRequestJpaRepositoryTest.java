package ru.practicum.shareit.request.repository;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.repository.ItemJpaRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserJpaRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@Transactional
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class ItemRequestJpaRepositoryTest {
    @Autowired
    ItemJpaRepository itemJpaRepository;
    @Autowired
    UserJpaRepository userJpaRepository;
    @Autowired
    ItemRequestJpaRepository itemRequestJpaRepository;
    static User user1;
    static User user2;
    static ItemRequest itemRequest1;
    static ItemRequest itemRequest2;
    static ItemRequest itemRequest3;

    @Test
    void findByAuthorIdOrderByCreatedDesc() {
        user1 = userJpaRepository.save(user1);
        user2 = userJpaRepository.save(user2);
        itemRequest1.setAuthor(user1);
        itemRequest2.setAuthor(user1);
        itemRequest3.setAuthor(user2);
        itemRequestJpaRepository.save(itemRequest1);
        itemRequestJpaRepository.save(itemRequest2);
        itemRequestJpaRepository.save(itemRequest3);

        List<ItemRequest> requests = new ArrayList<>(itemRequestJpaRepository.findByAuthorIdOrderByCreatedDesc(2L));

        assertEquals(1, requests.size());
        assertEquals(3L, requests.get(0).getId());
        assertEquals(2L, requests.get(0).getAuthor().getId());
    }

    @Test
    void findByAuthorIdNotOrderByCreatedDesc() {
        user1 = userJpaRepository.save(user1);
        user2 = userJpaRepository.save(user2);
        itemRequest1.setAuthor(user1);
        itemRequest2.setAuthor(user1);
        itemRequest3.setAuthor(user2);
        itemRequestJpaRepository.save(itemRequest1);
        itemRequestJpaRepository.save(itemRequest2);
        itemRequestJpaRepository.save(itemRequest3);

        List<ItemRequest> requests = new ArrayList<>(itemRequestJpaRepository.findByAuthorIdOrderByCreatedDesc(1L));

        assertEquals(2, requests.size());
        assertEquals(2L, requests.get(0).getId());
        assertEquals(1L, requests.get(0).getAuthor().getId());
        assertEquals(1L, requests.get(1).getId());
        assertEquals(1L, requests.get(1).getAuthor().getId());
    }

    @Test
    void testFindByAuthorIdNotOrderByCreatedDesc() {
        user1 = userJpaRepository.save(user1);
        user2 = userJpaRepository.save(user2);
        itemRequest1.setAuthor(user1);
        itemRequest2.setAuthor(user1);
        itemRequest3.setAuthor(user2);
        itemRequestJpaRepository.save(itemRequest1);
        itemRequestJpaRepository.save(itemRequest2);
        itemRequestJpaRepository.save(itemRequest3);

        List<ItemRequest> requests = itemRequestJpaRepository.findByAuthorIdNotOrderByCreatedDesc(2L, PageRequest.of(0, 1));

        assertEquals(1, requests.size());
        assertEquals(2L, requests.get(0).getId());
        assertEquals(1L, requests.get(0).getAuthor().getId());
    }

    @BeforeAll
    static void setUp() {
        user1 = new User();
        user1.setName("First User");
        user1.setEmail("First@mail.ru");

        user2 = new User();
        user2.setName("Second User");
        user2.setEmail("Second@mail.ru");

        itemRequest1 = new ItemRequest();
        itemRequest1.setDescription("First Description");
        itemRequest1.setCreated(LocalDateTime.of(2000, 1, 1, 1, 1, 1));

        itemRequest2 = new ItemRequest();
        itemRequest2.setDescription("Second Description");
        itemRequest2.setCreated(LocalDateTime.of(2001, 1, 1, 1, 1, 1));

        itemRequest3 = new ItemRequest();
        itemRequest3.setDescription("Third Description");
        itemRequest3.setCreated(LocalDateTime.of(2002, 1, 1, 1, 1, 1));
    }
}