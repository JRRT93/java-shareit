package ru.practicum.shareit.item.repository;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserJpaRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@Transactional
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class ItemJpaRepositoryTest {
    @Autowired
    ItemJpaRepository itemJpaRepository;
    @Autowired
    UserJpaRepository userJpaRepository;
    static User user1;
    static User user2;
    static Item item1;
    static Item item2;
    static Item item3;

    @BeforeAll
    static void setUp() {
        user1 = new User();
        user1.setName("First User");
        user1.setEmail("First@mail.ru");

        user2 = new User();
        user2.setName("Second User");
        user2.setEmail("Second@mail.ru");

        item1 = new Item();
        item1.setName("Item 1");
        item1.setDescription("Item 1 Description");
        item1.setAvailable(true);
        item1.setOwnerId(1L);

        item2 = new Item();
        item2.setName("Item 2");
        item2.setDescription("Item 2 Description");
        item2.setAvailable(false);
        item2.setOwnerId(1L);

        item3 = new Item();
        item3.setName("Item 3");
        item3.setDescription("Item 3 Description");
        item3.setAvailable(true);
        item3.setOwnerId(2L);
    }

    @Test
    void testFindByOwnerIdOrderByIdAsc() {
        userJpaRepository.save(user1);
        userJpaRepository.save(user2);
        itemJpaRepository.save(item1);
        itemJpaRepository.save(item2);
        itemJpaRepository.save(item3);

        List<Item> items = itemJpaRepository.findByOwnerIdOrderByIdAsc(1L, PageRequest.of(0, 1));

        assertEquals(1, items.size());
        assertEquals("Item 1", items.get(0).getName());
        assertEquals(1L, items.get(0).getId());
    }

    @Test
    void findByNameOrDescriptionContainsIgnoreCaseAndAvailable() {
        userJpaRepository.save(user1);
        userJpaRepository.save(user2);
        itemJpaRepository.save(item1);
        itemJpaRepository.save(item2);
        itemJpaRepository.save(item3);

        List<Item> items = itemJpaRepository.findByNameOrDescriptionContainsIgnoreCaseAndAvailable(
                "DesCRIPtiOn", "DesCRIPtiOn", true, PageRequest.of(0, 1));

        assertEquals(1, items.size());
        assertEquals("Item 1", items.get(0).getName());
        assertEquals(1L, items.get(0).getId());
    }

    @Test
    void testFindByNameOrDescriptionContainsIgnoreCaseAndAvailable() {
        userJpaRepository.save(user1);
        userJpaRepository.save(user2);
        itemJpaRepository.save(item1);
        itemJpaRepository.save(item2);
        itemJpaRepository.save(item3);

        List<Item> items = itemJpaRepository.findByNameOrDescriptionContainsIgnoreCaseAndAvailable(
                "DesCRIPtiOn", "DesCRIPtiOn", true, Pageable.unpaged());

        assertEquals(2, items.size());
        assertEquals("Item 1", items.get(0).getName());
        assertEquals(1L, items.get(0).getId());

        assertEquals("Item 3", items.get(1).getName());
        assertEquals(3L, items.get(1).getId());
    }
}