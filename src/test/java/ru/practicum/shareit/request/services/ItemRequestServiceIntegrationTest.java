package ru.practicum.shareit.request.services;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.exceptions.CommentWithoutCompletedBooking;
import ru.practicum.shareit.item.repository.ItemJpaRepository;
import ru.practicum.shareit.item.services.ItemService;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.exceptions.EntityNotFoundException;
import ru.practicum.shareit.user.exceptions.NotUniqueUserEmail;
import ru.practicum.shareit.user.repository.UserJpaRepository;
import ru.practicum.shareit.user.services.UserService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
@SpringBootTest(
        properties = "db.name=test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class ItemRequestServiceIntegrationTest {
    @Autowired
    ItemRequestService itemRequestService;
    @Autowired
    ItemService itemService;
    @Autowired
    UserService userService;
    @Autowired
    ItemJpaRepository itemRepository;
    @Autowired
    UserJpaRepository userRepository;
    @Autowired
    BookingRepository bookingRepository;

    @Test
    void findMyItemRequestsShouldThrow() {
        assertThrows(EntityNotFoundException.class, () -> itemRequestService.findMyItemRequests(99L));
    }

    @Test
    void findTheirItemRequestShouldThrow() {
        assertThrows(EntityNotFoundException.class, () -> itemRequestService.findMyItemRequests(99L));
    }

    @Test
    void findTheirItemRequestNoPagination() throws EntityNotFoundException {
        List<ItemRequestDto> requests = new ArrayList<>(itemRequestService.findTheirItemRequest(3L, null, null));
        List<Long> requestsId = requests.stream().map(ItemRequestDto::getId).collect(Collectors.toList());

        assertEquals(1, requests.size());
        assertTrue(requestsId.contains(1L));

        assertTrue(requests.get(0).getCreated() != null
                && requests.get(0).getCreated().isBefore(LocalDateTime.now()));
        assertEquals("Хачю пиццу", requests.get(0).getDescription());
        assertEquals(1L, requests.get(0).getId());
        assertEquals(2L, requests.get(0).getAuthor().getId());
        assertEquals(1, requests.get(0).getItems().size());
    }

    @Test
    void findTheirItemRequestPagination() throws EntityNotFoundException {
        List<ItemRequestDto> requests = new ArrayList<>(itemRequestService.findTheirItemRequest(2L, 0, 2));
        List<Long> requestsId = requests.stream().map(ItemRequestDto::getId).collect(Collectors.toList());

        assertEquals(2, requests.size());
        assertTrue(requestsId.contains(3L));
        assertTrue(requestsId.contains(2L));

        assertTrue(requests.get(1).getCreated() != null
                && requests.get(1).getCreated().isBefore(LocalDateTime.now()));
        assertEquals("Люстрации", requests.get(0).getDescription());
        assertEquals(3L, requests.get(0).getId());
        assertEquals(3L, requests.get(0).getAuthor().getId());
        assertEquals(0, requests.get(0).getItems().size());

        assertTrue(requests.get(1).getCreated() != null
                && requests.get(1).getCreated().isBefore(LocalDateTime.now()));
        assertEquals("Сталин 3000", requests.get(1).getDescription());
        assertEquals(2L, requests.get(1).getId());
        assertEquals(3L, requests.get(1).getAuthor().getId());
        assertEquals(2, requests.get(1).getItems().size());
    }

    @BeforeEach
    void setUp() throws NotUniqueUserEmail, EntityNotFoundException, CommentWithoutCompletedBooking {
        UserDto user1 = new UserDto();
        user1.setName("First User");
        user1.setEmail("First@mail.ru");

        UserDto user2 = new UserDto();
        user2.setName("Second User");
        user2.setEmail("Second@mail.ru");

        UserDto user3 = new UserDto();
        user3.setName("Third User");
        user3.setEmail("Third@mail.ru");
        userService.save(user1);
        userService.save(user2);
        userService.save(user3);

        ItemRequestDto itemRequestDto1 = new ItemRequestDto();
        itemRequestDto1.setDescription("Хачю пиццу");
        ItemRequestDto itemRequestDto2 = new ItemRequestDto();
        itemRequestDto2.setDescription("Сталин 3000");
        ItemRequestDto itemRequestDto3 = new ItemRequestDto();
        itemRequestDto3.setDescription("Люстрации");
        itemRequestService.save(2L, itemRequestDto1);
        itemRequestService.save(3L, itemRequestDto2);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        itemRequestService.save(3L, itemRequestDto3);

        ItemDto itemDto1 = new ItemDto();
        itemDto1.setName("First Item");
        itemDto1.setDescription("First Description");
        itemDto1.setAvailable(true);

        ItemDto itemDto2 = new ItemDto();
        itemDto2.setName("Second Item");
        itemDto2.setDescription("Second Description");
        itemDto2.setAvailable(true);
        itemDto2.setRequestId(1L);

        ItemDto itemDto3 = new ItemDto();
        itemDto3.setName("Third Item");
        itemDto3.setDescription("Third Description");
        itemDto3.setAvailable(true);
        itemDto3.setRequestId(2L);

        ItemDto itemDto4 = new ItemDto();
        itemDto4.setName("Fourth Item");
        itemDto4.setDescription("Fourth Description");
        itemDto4.setAvailable(false);
        itemDto4.setRequestId(2L);
        itemService.save(1L, itemDto1);
        itemService.save(1L, itemDto2);
        itemService.save(1L, itemDto3);
        itemService.save(2L, itemDto4);

        Booking booking1 = new Booking();
        booking1.setStart(LocalDateTime.of(2000, 1, 1, 1, 1, 1, 1));
        booking1.setEnd(LocalDateTime.of(2001, 1, 1, 1, 1, 1, 1));
        booking1.setStatus(Status.APPROVED);
        booking1.setItem(itemRepository.findById(1L).orElseThrow());
        booking1.setBooker(userRepository.findById(2L).orElseThrow());

        Booking booking2 = new Booking();
        booking2.setStart(LocalDateTime.of(2024, 1, 1, 1, 1, 1, 1));
        booking2.setEnd(LocalDateTime.of(2025, 1, 1, 1, 1, 1, 1));
        booking2.setStatus(Status.WAITING);
        booking2.setItem(itemRepository.findById(1L).orElseThrow());
        booking2.setBooker(userRepository.findById(2L).orElseThrow());

        Booking booking3 = new Booking();
        booking3.setStart(LocalDateTime.of(2030, 1, 1, 1, 1, 1, 1));
        booking3.setEnd(LocalDateTime.of(2031, 1, 1, 1, 1, 1, 1));
        booking3.setStatus(Status.REJECTED);
        booking3.setItem(itemRepository.findById(1L).orElseThrow());
        booking3.setBooker(userRepository.findById(2L).orElseThrow());

        Booking booking4 = new Booking();
        booking4.setStart(LocalDateTime.of(2029, 1, 1, 1, 1, 1, 1));
        booking4.setEnd(LocalDateTime.of(2032, 1, 1, 1, 1, 1, 1));
        booking4.setStatus(Status.APPROVED);
        booking4.setItem(itemRepository.findById(1L).orElseThrow());
        booking4.setBooker(userRepository.findById(3L).orElseThrow());

        Booking booking5 = new Booking();
        booking5.setStart(LocalDateTime.of(2035, 1, 1, 1, 1, 1, 1));
        booking5.setEnd(LocalDateTime.of(2040, 1, 1, 1, 1, 1, 1));
        booking5.setStatus(Status.WAITING);
        booking5.setItem(itemRepository.findById(2L).orElseThrow());
        booking5.setBooker(userRepository.findById(3L).orElseThrow());

        Booking booking6 = new Booking();
        booking6.setStart(LocalDateTime.of(2022, 1, 1, 1, 1, 1, 1));
        booking6.setEnd(LocalDateTime.of(2039, 1, 1, 1, 1, 1, 1));
        booking6.setStatus(Status.APPROVED);
        booking6.setItem(itemRepository.findById(2L).orElseThrow());
        booking6.setBooker(userRepository.findById(3L).orElseThrow());
        bookingRepository.save(booking1);
        bookingRepository.save(booking2);
        bookingRepository.save(booking3);
        bookingRepository.save(booking4);
        bookingRepository.save(booking5);
        bookingRepository.save(booking6);

        CommentDto commentDto1 = new CommentDto();
        commentDto1.setText("First Comment");
        itemService.saveComment(2L, 1L, commentDto1);
    }
}