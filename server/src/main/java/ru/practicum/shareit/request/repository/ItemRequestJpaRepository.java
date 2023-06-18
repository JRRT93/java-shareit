package ru.practicum.shareit.request.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.Collection;
import java.util.List;

public interface ItemRequestJpaRepository extends JpaRepository<ItemRequest, Long> {
    Collection<ItemRequest> findByAuthorIdOrderByCreatedDesc(Long authorId);

    List<ItemRequest> findByAuthorIdNotOrderByCreatedDesc(Long authorId, Pageable pageRequest);
}