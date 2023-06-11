package ru.practicum.shareit.item.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.dto.BookingMapperForItems;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.exceptions.CommentWithoutCompletedBooking;
import ru.practicum.shareit.item.exceptions.WrongOwnerException;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentJpaRepository;
import ru.practicum.shareit.item.repository.ItemJpaRepository;
import ru.practicum.shareit.user.exceptions.EntityNotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserJpaRepository;
import ru.practicum.shareit.user.services.UserService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

@SpringBootTest
class ItemServiceTest {
    ItemService itemService;
    @Mock
    UserService userService;
    @Mock
    ItemJpaRepository itemRepository;
    @Mock
    UserJpaRepository userRepository;
    @Mock
    BookingRepository bookingRepository;
    @Mock
    CommentJpaRepository commentRepository;
    @Autowired
    ItemMapper itemMapper;
    @Autowired
    ItemOwnerDtoMapper itemOwnerMapper;
    @Autowired
    BookingMapperForItems bookingMapperForItems;
    @Autowired
    CommentDtoMapper commentMapper;
    ItemDto itemDto;
    Item item;
    User user;

    @BeforeEach
    void inject() {
        itemService = new ItemServiceImpl(userService, itemRepository, userRepository, bookingRepository, commentRepository,
                itemMapper, itemOwnerMapper, bookingMapperForItems, commentMapper);

        itemDto = new ItemDto();
        itemDto.setName("Knife");
        itemDto.setDescription("Sharp fine knife");
        itemDto.setAvailable(true);

        item = new Item();
        item.setId(1L);
        item.setName("Knife");
        item.setDescription("Sharp fine knife");
        item.setAvailable(true);
        item.setOwnerId(2L);

        user = new User();
        user.setId(1L);
        user.setEmail("test@mail.ru");
        user.setName("TestUser");
    }

    @Test
    void saveShouldCallRepositorySaveOneTime() throws EntityNotFoundException {
        Mockito
                .when(itemRepository.save(any(Item.class)))
                .thenReturn(item);
        Mockito
                .when(userRepository.findById(1L))
                .thenReturn(Optional.ofNullable(user));

        itemService.save(1L, itemDto);

        Mockito.verify(itemRepository, Mockito.times(1)).save(any(Item.class));
    }

    @Test
    void saveShouldThrow() {
        Mockito
                .when(userRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> itemService.save(99L, itemDto));
    }

    @Test
    void updateShouldThrowNotFound() {
        Mockito
                .when(itemRepository.findById(99L))
                .thenReturn(Optional.empty());
        Mockito
                .when(userRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> itemService.update(99L, 1L, itemDto));
        assertThrows(EntityNotFoundException.class, () -> itemService.update(1L, 99L, itemDto));
    }

    @Test
    void updateShouldThrowWrongOwner() {
        User notOwner = new User();
        notOwner.setId(99L);
        Mockito
                .when(itemRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.ofNullable(item));
        Mockito
                .when(userRepository.findById(99L))
                .thenReturn(Optional.of(notOwner));

        assertThrows(WrongOwnerException.class, () -> itemService.update(99L, 1L, itemDto));
    }

    @Test
    void updateOnlyName() throws EntityNotFoundException, WrongOwnerException {
        ItemDto itemOnlyNameForUpdate = new ItemDto();
        itemOnlyNameForUpdate.setName("UpdatedNAME");

        Item updatedItem = new Item();
        updatedItem.setId(1L);
        updatedItem.setName("UpdatedNAME");
        updatedItem.setDescription("Sharp fine knife");
        updatedItem.setAvailable(true);
        updatedItem.setOwnerId(2L);
        updatedItem.setRequestId(10L);

        user.setId(2L);

        Mockito
                .when(userRepository.findById(2L))
                .thenReturn(Optional.ofNullable(user));

        Mockito
                .when(itemRepository.findById(1L))
                .thenReturn(Optional.ofNullable(item));

        Mockito
                .when(itemRepository.save(any(Item.class)))
                .thenReturn(updatedItem);

        ItemDto itemDtoUpdated = itemService.update(2L, 1L, itemOnlyNameForUpdate);

        assertEquals("Sharp fine knife", itemDtoUpdated.getDescription());
        assertEquals("UpdatedNAME", itemDtoUpdated.getName());
        assertEquals(true, itemDtoUpdated.getAvailable());

        assertEquals(1L, itemDtoUpdated.getId());
        assertEquals(10L, itemDtoUpdated.getRequestId());
    }

    @Test
    void updateOnlyDescription() throws EntityNotFoundException, WrongOwnerException {
        ItemDto itemOnlyNameForUpdate = new ItemDto();
        itemOnlyNameForUpdate.setDescription("Updated");

        Item updatedItem = new Item();
        updatedItem.setId(1L);
        updatedItem.setName("Knife");
        updatedItem.setDescription("Updated");
        updatedItem.setAvailable(true);
        updatedItem.setOwnerId(2L);
        updatedItem.setRequestId(10L);

        user.setId(2L);

        Mockito
                .when(userRepository.findById(2L))
                .thenReturn(Optional.ofNullable(user));

        Mockito
                .when(itemRepository.findById(1L))
                .thenReturn(Optional.ofNullable(item));

        Mockito
                .when(itemRepository.save(any(Item.class)))
                .thenReturn(updatedItem);

        ItemDto itemDtoUpdated = itemService.update(2L, 1L, itemOnlyNameForUpdate);

        assertEquals("Updated", itemDtoUpdated.getDescription());
        assertEquals("Knife", itemDtoUpdated.getName());
        assertEquals(true, itemDtoUpdated.getAvailable());

        assertEquals(1L, itemDtoUpdated.getId());
        assertEquals(10L, itemDtoUpdated.getRequestId());
    }

    @Test
    void updateOnlyAvailable() throws EntityNotFoundException, WrongOwnerException {
        ItemDto itemOnlyNameForUpdate = new ItemDto();
        itemOnlyNameForUpdate.setAvailable(false);

        Item updatedItem = new Item();
        updatedItem.setId(1L);
        updatedItem.setName("Knife");
        updatedItem.setDescription("Sharp fine knife");
        updatedItem.setAvailable(false);
        updatedItem.setOwnerId(2L);
        updatedItem.setRequestId(10L);

        user.setId(2L);

        Mockito
                .when(userRepository.findById(2L))
                .thenReturn(Optional.ofNullable(user));

        Mockito
                .when(itemRepository.findById(1L))
                .thenReturn(Optional.ofNullable(item));

        Mockito
                .when(itemRepository.save(any(Item.class)))
                .thenReturn(updatedItem);

        ItemDto itemDtoUpdated = itemService.update(2L, 1L, itemOnlyNameForUpdate);

        assertEquals("Sharp fine knife", itemDtoUpdated.getDescription());
        assertEquals("Knife", itemDtoUpdated.getName());
        assertEquals(false, itemDtoUpdated.getAvailable());

        assertEquals(1L, itemDtoUpdated.getId());
        assertEquals(10L, itemDtoUpdated.getRequestId());
    }

    @Test
    void updateIsForbiddenForIdAndRequestId() throws EntityNotFoundException, WrongOwnerException {
        ItemDto itemOnlyNameForUpdate = new ItemDto();
        itemOnlyNameForUpdate.setId(999L);
        itemOnlyNameForUpdate.setRequestId(999L);

        Item updatedItem = new Item();
        updatedItem.setId(1L);
        updatedItem.setName("Knife");
        updatedItem.setDescription("Sharp fine knife");
        updatedItem.setAvailable(true);
        updatedItem.setOwnerId(2L);
        updatedItem.setRequestId(10L);

        user.setId(2L);

        Mockito
                .when(userRepository.findById(2L))
                .thenReturn(Optional.ofNullable(user));

        Mockito
                .when(itemRepository.findById(1L))
                .thenReturn(Optional.ofNullable(item));

        Mockito
                .when(itemRepository.save(any(Item.class)))
                .thenReturn(updatedItem);

        ItemDto itemDtoUpdated = itemService.update(2L, 1L, itemOnlyNameForUpdate);

        assertEquals("Sharp fine knife", itemDtoUpdated.getDescription());
        assertEquals("Knife", itemDtoUpdated.getName());
        assertEquals(true, itemDtoUpdated.getAvailable());

        assertEquals(1L, itemDtoUpdated.getId());
        assertEquals(10L, itemDtoUpdated.getRequestId());
    }

    @Test
    void findByIdShouldThrow() {
        Mockito
                .when(itemRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> itemService.findById(99L, 1L));
    }

    @Test
    void findByIdRequestFromNotOwner() throws EntityNotFoundException {
        Comment comment = new Comment();
        comment.setId(1L);
        comment.setAuthor(user);
        comment.setText("Amazing");
        Collection<Comment> comments = new ArrayList<>();
        comments.add(comment);

        Mockito
                .when(commentRepository.findAllByItemIdOrderByIdAsc(1L))
                .thenReturn(comments);
        Mockito
                .when(itemRepository.findById(1L))
                .thenReturn(Optional.ofNullable(item));

        ItemOwnerDto foundedItem = itemService.findById(1L, 1L);

        assertNull(foundedItem.getLastBooking());
        assertNull(foundedItem.getNextBooking());
        assertEquals(1, foundedItem.getComments().size());
    }

    @Test
    void findByIdRequestFromOwner() throws EntityNotFoundException {
        Collection<Comment> comments = new ArrayList<>();

        Booking booking1 = new Booking();
        booking1.setStart(LocalDateTime.of(2000, 1, 1, 1, 1, 1));
        booking1.setId(1L);
        booking1.setStatus(Status.APPROVED);

        Booking booking2 = new Booking();
        LocalDateTime expectedLast = LocalDateTime.of(2020, 1, 1, 1, 1, 1);
        booking2.setStart(expectedLast);
        booking2.setId(2L);
        booking2.setStatus(Status.APPROVED);

        Booking booking3 = new Booking();
        LocalDateTime expectedNext = LocalDateTime.of(3000, 1, 1, 1, 1, 1);
        booking3.setStart(expectedNext);
        booking3.setId(3L);
        booking3.setStatus(Status.WAITING);

        Booking booking4 = new Booking();
        booking4.setStart(LocalDateTime.of(2024, 1, 1, 1, 1, 1));
        booking4.setId(4L);
        booking4.setStatus(Status.REJECTED);

        Collection<Booking> bookings = new ArrayList<>();
        bookings.add(booking1);
        bookings.add(booking2);
        bookings.add(booking3);
        bookings.add(booking4);

        Mockito
                .when(bookingRepository.findAllByItemIdOrderByStartDesc(1L))
                .thenReturn(bookings);
        Mockito
                .when(commentRepository.findAllByItemIdOrderByIdAsc(1L))
                .thenReturn(comments);
        Mockito
                .when(itemRepository.findById(1L))
                .thenReturn(Optional.ofNullable(item));

        ItemOwnerDto foundedItem = itemService.findById(1L, 2L);

        assertEquals(0, foundedItem.getComments().size());
        assertEquals(expectedLast, foundedItem.getLastBooking().getStart());
        assertEquals(expectedNext, foundedItem.getNextBooking().getStart());
    }

    @Test
    void findByNameOrDescriptionEmptyString() {
        assertEquals(0, itemService.findByNameOrDescription("", 1, 1).size());
    }

    @Test
    void findByNameOrDescriptionNoPagination() {
        Item item1 = new Item();
        item1.setId(2L);
        List<Item> items = new ArrayList<>();
        items.add(item1);
        items.add(item);

        Mockito
                .when(itemRepository.findByNameOrDescriptionContainsIgnoreCaseAndAvailable(Mockito.anyString(),
                        Mockito.anyString(), Mockito.anyBoolean()))
                .thenReturn(items);

        assertEquals(2, itemService.findByNameOrDescription("text", null, null).size());
    }

    @Test
    void findByNameOrDescriptionWithPagination() {
        Item item1 = new Item();
        item1.setId(2L);
        List<Item> items = new ArrayList<>();
        items.add(item);

        Mockito
                .when(itemRepository.findByNameOrDescriptionContainsIgnoreCaseAndAvailable(Mockito.anyString(),
                        Mockito.anyString(), Mockito.anyBoolean(), any(Pageable.class)))
                .thenReturn(items);

        assertEquals(1, itemService.findByNameOrDescription("text", 0, 1).size());
    }

    @Test
    void saveCommentWithoutCompletedBookingShouldThrow() {
        LocalDateTime now = LocalDateTime.now();
        CommentDto comment = new CommentDto();
        comment.setId(1L);
        comment.setText("Amazing");

        Booking booking1 = new Booking();
        booking1.setStart(LocalDateTime.of(2000, 1, 1, 1, 1, 1));
        booking1.setId(1L);
        booking1.setBooker(user);
        booking1.setStatus(Status.APPROVED);
        Collection<Booking> bookings = new ArrayList<>();
        bookings.add(booking1);

        Mockito
                .when(bookingRepository.findAllByBookerIdAndEndBeforeOrderByStartDesc(Mockito.anyLong(), any(LocalDateTime.class)))
                .thenReturn(bookings);
        Mockito
                .when(userRepository.findById(5L))
                .thenReturn(Optional.ofNullable(user));

        Mockito
                .when(itemRepository.findById(1L))
                .thenReturn(Optional.ofNullable(item));

        assertThrows(CommentWithoutCompletedBooking.class, () -> itemService.saveComment(5L, 1L, comment));
    }

    @Test
    void saveCommentCompletedBooking() throws CommentWithoutCompletedBooking, EntityNotFoundException {
        CommentDto commentDto = new CommentDto();
        commentDto.setId(1L);
        commentDto.setText("Amazing");
        Comment comment = new Comment();
        comment.setId(1L);
        comment.setText("Amazing");

        Booking booking1 = new Booking();
        booking1.setStart(LocalDateTime.of(2000, 1, 1, 1, 1, 1));
        booking1.setId(1L);
        booking1.setBooker(user);
        booking1.setStatus(Status.APPROVED);
        Collection<Booking> bookings = new ArrayList<>();
        bookings.add(booking1);

        Mockito
                .when(bookingRepository.findAllByBookerIdAndEndBeforeOrderByStartDesc(Mockito.anyLong(), any(LocalDateTime.class)))
                .thenReturn(bookings);
        Mockito
                .when(userRepository.findById(1L))
                .thenReturn(Optional.ofNullable(user));

        Mockito
                .when(itemRepository.findById(1L))
                .thenReturn(Optional.ofNullable(item));
        Mockito
                .when(commentRepository.save(any(Comment.class)))
                .thenReturn(comment);

        CommentDto savedComment = itemService.saveComment(1L, 1L, commentDto);

        assertNotNull(savedComment.getCreated());
        assertEquals("TestUser", savedComment.getAuthorName());
    }
}