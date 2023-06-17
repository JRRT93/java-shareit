package ru.practicum.shareit.request.model;

import lombok.Getter;
import lombok.Setter;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "requests")
public class ItemRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "request_id")
    private Long id;
    @Column(nullable = false)
    private String description;
    @Column(name = "creation_date", nullable = false)
    private LocalDateTime created;
    @ManyToOne
    @JoinColumn(name = "author_id", nullable = false)
    private User author;
}