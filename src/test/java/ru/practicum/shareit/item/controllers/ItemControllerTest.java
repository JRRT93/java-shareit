package ru.practicum.shareit.item.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemOwnerDto;
import ru.practicum.shareit.item.exceptions.CommentWithoutCompletedBooking;
import ru.practicum.shareit.item.exceptions.WrongOwnerException;
import ru.practicum.shareit.item.services.ItemService;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@WebMvcTest(controllers = ItemController.class)
class ItemControllerTest {
    @Autowired
    MockMvc mockMvc;
    @MockBean
    ItemService itemService;

    @Test
    public void testCreateItem() throws Exception {
        ItemDto itemDto = new ItemDto();
        itemDto.setName("Test Item");
        itemDto.setDescription("Test Item Description");
        itemDto.setAvailable(true);

        ItemDto expectedResult = new ItemDto();
        expectedResult.setId(1L);
        expectedResult.setName("Test Item");
        expectedResult.setDescription("Test Item Description");
        expectedResult.setAvailable(true);

        when(itemService.save(anyLong(), any(ItemDto.class))).thenReturn(expectedResult);

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(itemDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Test Item")));
    }

    @Test
    public void testFindById() throws Exception {
        ItemOwnerDto expectedResult = new ItemOwnerDto();
        expectedResult.setId(1L);
        expectedResult.setName("Test Item");

        when(itemService.findById(anyLong(), anyLong())).thenReturn(expectedResult);

        mockMvc.perform(get("/items/{itemId}", 1L)
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Test Item")));
    }

    @Test
    public void testUpdateItem() throws Exception {
        ItemDto itemDto = new ItemDto();
        itemDto.setName("Updated Item");

        ItemDto expectedResult = new ItemDto();
        expectedResult.setId(1L);
        expectedResult.setName("Updated Item");

        when(itemService.update(anyLong(), anyLong(), any(ItemDto.class))).thenReturn(expectedResult);

        mockMvc.perform(patch("/items/{itemId}", 1L)
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(itemDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Updated Item")));
    }

    @Test
    public void testUpdateItemShouldThrow() throws Exception {
        ItemDto itemDto = new ItemDto();
        itemDto.setName("Updated Item");
        when(itemService.update(anyLong(), anyLong(), any(ItemDto.class))).thenThrow(new WrongOwnerException("error"));

        mockMvc.perform(patch("/items/{itemId}", 1L)
                        .header("X-Sharer-User-Id", 199)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(itemDto)))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testGetAllUsersItems() throws Exception {
        List<ItemOwnerDto> expectedResult = new ArrayList<>();
        ItemOwnerDto item1 = new ItemOwnerDto();
        item1.setId(1L);
        item1.setName("Item 1");
        ItemOwnerDto item2 = new ItemOwnerDto();
        item2.setId(2L);
        item2.setName("Item 2");
        expectedResult.add(item1);
        expectedResult.add(item2);

        when(itemService.findAllMyItems(anyLong(), anyInt(), anyInt())).thenReturn(expectedResult);

        mockMvc.perform(get("/items")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].name", is("Item 1")))
                .andExpect(jsonPath("$[1].id", is(2)))
                .andExpect(jsonPath("$[1].name", is("Item 2")));

        verify(itemService).findAllMyItems(1L, 0, 10);
    }

    @Test
    public void testFindByNameOrDescription() throws Exception {
        List<ItemDto> expectedResult = new ArrayList<>();
        ItemDto item1 = new ItemDto();
        item1.setId(1L);
        item1.setName("Item 1");
        ItemDto item2 = new ItemDto();
        item2.setId(2L);
        item2.setName("Item 2");
        expectedResult.add(item1);
        expectedResult.add(item2);

        when(itemService.findByNameOrDescription(anyString(), anyInt(), anyInt())).thenReturn(expectedResult);

        mockMvc.perform(get("/items/search")
                        .param("text", "keyword")
                        .param("from", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].name", is("Item 1")))
                .andExpect(jsonPath("$[1].id", is(2)))
                .andExpect(jsonPath("$[1].name", is("Item 2")));

        verify(itemService).findByNameOrDescription("keyword", 0, 10);
    }

    @Test
    public void testSaveComment() throws Exception {
        CommentDto commentDto = new CommentDto();
        commentDto.setText("Test comment");

        CommentDto expectedResult = new CommentDto();
        expectedResult.setId(1L);
        expectedResult.setText("Test comment");

        when(itemService.saveComment(anyLong(), anyLong(), any(CommentDto.class))).thenReturn(expectedResult);

        mockMvc.perform(post("/items/{itemId}/comment", 1L)
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(commentDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.text", is("Test comment")));
    }

    @Test
    public void testSaveCommentShouldThrow() throws Exception {
        CommentDto commentDto = new CommentDto();
        commentDto.setText("Test comment");

        when(itemService.saveComment(anyLong(), anyLong(), any(CommentDto.class)))
                .thenThrow(new CommentWithoutCompletedBooking("error"));

        mockMvc.perform(post("/items/{itemId}/comment", 1L)
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(commentDto)))
                .andExpect(status().isBadRequest());
    }

    private static String asJsonString(Object obj) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(obj);
    }
}