package ru.practicum.shareit.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

@Repository
public interface ItemJpaRepository extends JpaRepository<Item, Long> {
    List<Item> findByOwnerIdOrderByIdAsc(Long ownerId);

    List<Item> findByNameOrDescriptionContainsIgnoreCaseAndAvailable(String keyWord, String keyWord2, boolean isAvailable);
}