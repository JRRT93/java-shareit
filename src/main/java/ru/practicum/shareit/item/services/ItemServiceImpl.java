package ru.practicum.shareit.item.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;
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
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemServiceImpl implements ItemService {
    private final UserService userService;
    private final ItemJpaRepository itemRepository;
    private final UserJpaRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentJpaRepository commentRepository;
    private final ItemMapper itemMapper = Mappers.getMapper(ItemMapper.class);
    private final ItemOwnerDtoMapper itemOwnerMapper = Mappers.getMapper(ItemOwnerDtoMapper.class);
    private final BookingMapperForItems bookingMapperForItems = Mappers.getMapper(BookingMapperForItems.class);
    private final CommentDtoMapper commentMapper = Mappers.getMapper(CommentDtoMapper.class);

    @Override
    public ItemDto save(Long ownerId, ItemDto itemDto) throws EntityNotFoundException {
        userService.findById(ownerId);
        Item item = itemMapper.dtoToModel(itemDto);
        item.setOwnerId(ownerId);
        itemRepository.save(item);
        log.debug(String.format("Item with id = %d saved", item.getId()));
        return itemMapper.modelToDto(item);
    }

    @Override
    public ItemOwnerDto findById(Long id, Long userId) throws EntityNotFoundException {
        Item item = itemRepository.findById(id).orElseThrow(() ->
                new EntityNotFoundException(String.format("%s with id = %d does not exist in database", "Item", id)));
        log.debug(String.format("Item with id = %d founded", id));
        Collection<CommentDto> comments = commentRepository.findAllByItemIdOrderByIdAsc(id).stream()
                .map(commentMapper::modelToDto)
                .collect(Collectors.toList());

        if (!item.getOwnerId().equals(userId)) {
            ItemOwnerDto itemOwnerDto = itemOwnerMapper.modelToDto(item);
            itemOwnerDto.setComments(comments);
            return itemOwnerDto;
        }

        Collection<Booking> bookingList = bookingRepository.findAllByItemIdOrderByStartDesc(id);
        ItemOwnerDto itemOwnerDto = itemOwnerMapper.modelToDto(item);
        LocalDateTime now = LocalDateTime.now();

        Booking lastBooking = null;
        Booking nextBooking = null;

        for (Booking booking : bookingList) {
            LocalDateTime startDate = booking.getStart();
            if (startDate.isBefore(now) && (lastBooking == null || startDate.isAfter(lastBooking.getStart()))) {
                lastBooking = booking;
            }
            if (startDate.isAfter(now) && (nextBooking == null || startDate.isBefore(nextBooking.getStart()))
                    && !booking.getStatus().equals(Status.REJECTED)) {
                nextBooking = booking;
            }
        }
        itemOwnerDto.setLastBooking(bookingMapperForItems.modelToDto(lastBooking));
        itemOwnerDto.setNextBooking(bookingMapperForItems.modelToDto(nextBooking));
        itemOwnerDto.setComments(comments);
        return itemOwnerDto;
    }

    @Override
    public ItemDto update(Long ownerId, Long itemId, ItemDto itemDto) throws
            EntityNotFoundException, WrongOwnerException {
        userService.findById(ownerId);
        Item itemToUpdate = itemRepository.findById(itemId).orElseThrow(() ->
                new EntityNotFoundException(String.format("%s with id = %d does not exist in database", "Item", itemId)));
        String updatedName = itemDto.getName();
        String updatedDescription = itemDto.getDescription();
        Boolean updatedAvailable = itemDto.getAvailable();
        if (updatedName != null) {
            itemToUpdate.setName(updatedName);
        }
        if (updatedDescription != null) {
            itemToUpdate.setDescription(updatedDescription);
        }
        if (updatedAvailable != null) {
            itemToUpdate.setAvailable(updatedAvailable);
        }

        if (itemToUpdate.getOwnerId().longValue() == ownerId.longValue()) {
            log.debug("For DTO Entity ID initialized to provide UPDATE operation");
            Item updatedItem = itemRepository.save(itemToUpdate);
            log.debug(String.format("Item with id = %d updated", itemId));
            return itemMapper.modelToDto(updatedItem);
        } else {
            throw new WrongOwnerException(String.format("User id = %d is not owner of Item id = %d", ownerId, itemId));
        }
    }

    public CommentDto saveComment(Long bookerId, Long itemId, CommentDto commentDto) throws EntityNotFoundException,
            CommentWithoutCompletedBooking {
        Item item = itemRepository.findById(itemId).orElseThrow(() ->
                new EntityNotFoundException(String.format("%s with id = %d does not exist in database", "Item", itemId)));
        User author = userRepository.findById(bookerId).orElseThrow(
                () -> new EntityNotFoundException(String.format("%s with id = %d does not exist in database", "User", bookerId)));
        LocalDateTime now = LocalDateTime.now();
        Comment comment = commentMapper.dtoToModel(commentDto);

        Collection<Long> bookersWithCompletedBooking =
                bookingRepository.findAllByBookerIdAndEndBeforeOrderByStartDesc(bookerId, now).stream()
                        .map(booking -> booking.getBooker().getId()).collect(Collectors.toList());
        if (bookersWithCompletedBooking.contains(bookerId)) {
            comment.setCreationDate(now);
            comment.setItem(item);
            comment.setAuthor(author);
            commentRepository.save(comment);
            return commentMapper.modelToDto(comment);
        } else {
            throw new CommentWithoutCompletedBooking("Comment allowed only for users with completed Booking");
        }
    }

    @Override
    public List<ItemOwnerDto> findAllMyItems(Long ownerId) throws EntityNotFoundException {
        userService.findById(ownerId);
        LocalDateTime now = LocalDateTime.now();
        Collection<Booking> bookingList = bookingRepository.findAllByItemOwnerIdOrderByStartDesc(ownerId);
        List<ItemOwnerDto> itemOwnerDtoList = itemRepository.findByOwnerIdOrderByIdAsc(ownerId).stream()
                .map(itemOwnerMapper::modelToDto)
                .collect(Collectors.toList());
        Collection<Comment> comments = commentRepository.findAllByItemOwnerIdOrderByIdAsc(ownerId);
        for (ItemOwnerDto item : itemOwnerDtoList) {
            Booking lastBooking = null;
            Booking nextBooking = null;
            LocalDateTime lastBookingDate = null;
            LocalDateTime nextBookingDate = null;

            for (Booking booking : bookingList) {
                if (booking.getItem().getId().equals(item.getId())) {
                    LocalDateTime startDate = booking.getStart();
                    if (startDate.isBefore(now) && (lastBookingDate == null || startDate.isAfter(lastBookingDate))) {
                        lastBookingDate = startDate;
                        lastBooking = booking;
                    }
                    if (startDate.isAfter(now) && (nextBookingDate == null || startDate.isBefore(nextBookingDate))
                            && !booking.getStatus().equals(Status.REJECTED)) {
                        nextBookingDate = startDate;
                        nextBooking = booking;
                    }
                }
            }
            item.setLastBooking(bookingMapperForItems.modelToDto(lastBooking));
            item.setNextBooking(bookingMapperForItems.modelToDto(nextBooking));
            Collection<CommentDto> itemComments = comments.stream()
                    .filter(comment -> comment.getItem().getId().equals(item.getId()))
                    .map(commentMapper::modelToDto)
                    .collect(Collectors.toList());
            item.setComments(itemComments);
        }
        return itemOwnerDtoList;
    }

    @Override
    public List<ItemDto> findByNameOrDescription(String text) {
        boolean isAvailable = true;
        if (text.isEmpty()) {
            return new ArrayList<>();
        }
        return itemRepository.findByNameOrDescriptionContainsIgnoreCaseAndAvailable(text, text, isAvailable).stream()
                .map(itemMapper::modelToDto)
                .collect(Collectors.toList());
    }
}