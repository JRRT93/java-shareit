package ru.practicum.shareit.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Comment;

import java.util.Collection;

@Repository
public interface CommentJpaRepository extends JpaRepository<Comment, Long> {
    Collection<Comment> findAllByItemIdOrderByIdAsc(Long itemId);

    Collection<Comment> findAllByItemOwnerIdOrderByIdAsc(Long ownerId);
}