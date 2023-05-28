package ru.practicum.shareit.item.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Getter
@Setter
public class CommentDto {
    private Long id;
    @NotEmpty
    @NotNull
    private String text;
    private String authorName;
    private LocalDateTime created;
}