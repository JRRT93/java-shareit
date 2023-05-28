package ru.practicum.shareit.item.dto;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.item.model.Comment;

@Mapper(componentModel = "spring")
public interface CommentDtoMapper {
    @Mapping(target = "text", source = "commentDto.text")
    Comment dtoToModel(CommentDto commentDto);

    @Mapping(target = "id", source = "comment.id")
    @Mapping(target = "text", source = "comment.text")
    @Mapping(target = "authorName", source = "comment.author.name")
    @Mapping(target = "created", source = "comment.creationDate")
    CommentDto modelToDto(Comment comment);
}