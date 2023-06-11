package ru.practicum.shareit.request.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.services.ItemRequestService;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@WebMvcTest(controllers = ItemRequestController.class)
class ItemRequestControllerTest {
    @Autowired
    MockMvc mockMvc;
    @MockBean
    ItemRequestService itemRequestService;

    @Test
    void createItemRequest() throws Exception {
        ItemRequestDto itemRequestDto = new ItemRequestDto();
        itemRequestDto.setDescription("Description");

        ItemRequestDto savedItemRequestDto = new ItemRequestDto();
        savedItemRequestDto.setId(1L);
        savedItemRequestDto.setDescription("Description");

        when(itemRequestService.save(any(Long.class), any(ItemRequestDto.class)))
                .thenReturn(savedItemRequestDto);

        mockMvc.perform(post("/requests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L)
                        .content(asJsonString(itemRequestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.description", is("Description")));

        verify(itemRequestService, times(1)).save(any(Long.class), any(ItemRequestDto.class));
    }

    @Test
    public void testFindById() throws Exception {
        ItemRequestDto responseDto = new ItemRequestDto();
        responseDto.setId(1L);
        responseDto.setDescription("Description");
        when(itemRequestService.findById(anyLong(), anyLong())).thenReturn(responseDto);
        mockMvc.perform(get("/requests/{requestId}", 1)
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.description", is("Description")));
        verify(itemRequestService).findById(1L, 1L);
    }

    @Test
    public void testFindMyItemRequests() throws Exception {
        ItemRequestDto responseDto1 = new ItemRequestDto();
        responseDto1.setId(1L);
        responseDto1.setDescription("Test Request 1");
        ItemRequestDto responseDto2 = new ItemRequestDto();
        responseDto2.setId(2L);
        responseDto2.setDescription("Test Request 2");
        List<ItemRequestDto> responseList = Arrays.asList(responseDto1, responseDto2);

        when(itemRequestService.findMyItemRequests(anyLong())).thenReturn(responseList);

        mockMvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].description", is("Test Request 1")))
                .andExpect(jsonPath("$[1].id", is(2)))
                .andExpect(jsonPath("$[1].description", is("Test Request 2")));

        verify(itemRequestService).findMyItemRequests(1L);
    }

    @Test
    public void testFindTheirItemRequests() throws Exception {

        ItemRequestDto responseDto1 = new ItemRequestDto();
        responseDto1.setId(1L);
        responseDto1.setDescription("Test Request 1");
        ItemRequestDto responseDto2 = new ItemRequestDto();
        responseDto2.setId(2L);
        responseDto2.setDescription("Test Request 2");
        List<ItemRequestDto> responseList = Arrays.asList(responseDto1, responseDto2);

        when(itemRequestService.findTheirItemRequest(anyLong(), anyInt(), anyInt())).thenReturn(responseList);

        mockMvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", 1L)
                        .param("from", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].description", is("Test Request 1")))
                .andExpect(jsonPath("$[1].id", is(2)))
                .andExpect(jsonPath("$[1].description", is("Test Request 2")));

        verify(itemRequestService).findTheirItemRequest(1L, 0, 10);
    }

    private static String asJsonString(Object obj) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(obj);
    }
}