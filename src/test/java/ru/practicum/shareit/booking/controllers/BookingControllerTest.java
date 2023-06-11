package ru.practicum.shareit.booking.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoComplete;
import ru.practicum.shareit.booking.exceptions.BookerAndOwnerAreSameUser;
import ru.practicum.shareit.booking.exceptions.IncorrectBookingStartEndDate;
import ru.practicum.shareit.booking.exceptions.ItemNotAvailableException;
import ru.practicum.shareit.booking.exceptions.StatusAlreadyConfirmed;
import ru.practicum.shareit.booking.model.State;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.services.BookingService;
import ru.practicum.shareit.item.exceptions.WrongOwnerException;
import ru.practicum.shareit.user.exceptions.EntityNotFoundException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@WebMvcTest(controllers = BookingController.class)
class BookingControllerTest {
    @Autowired
    MockMvc mockMvc;
    @MockBean
    BookingService bookingService;

    @Test
    void createBooking() throws Exception {
        BookingDto bookingDto = new BookingDto();
        bookingDto.setItemId(1L);
        bookingDto.setStart(LocalDateTime.of(2000, 1, 1, 1, 1, 1));
        bookingDto.setEnd(LocalDateTime.of(2001, 1, 1, 1, 1, 1));

        BookingDtoComplete bookingDtoComplete = new BookingDtoComplete();
        bookingDtoComplete.setId(1L);
        bookingDtoComplete.setStart(LocalDateTime.of(2000, 1, 1, 1, 1, 1));
        bookingDtoComplete.setEnd(LocalDateTime.of(2001, 1, 1, 1, 1, 1));

        when(bookingService.save(any(Long.class), any(BookingDto.class))).thenReturn(bookingDtoComplete);

        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(bookingDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.start", is("2000-01-01T01:01:01")))
                .andExpect(jsonPath("$.end", is("2001-01-01T01:01:01")));

        verify(bookingService, times(1)).save(anyLong(), any(BookingDto.class));
    }

    @Test
    void createBookingShouldNotFound() throws Exception {
        BookingDto bookingDto = new BookingDto();
        bookingDto.setItemId(1L);
        bookingDto.setStart(LocalDateTime.of(2000, 1, 1, 1, 1, 1));
        bookingDto.setEnd(LocalDateTime.of(2001, 1, 1, 1, 1, 1));
        when(bookingService.save(any(Long.class), any(BookingDto.class))).thenThrow(new EntityNotFoundException("error"));

        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(bookingDto)))
                .andExpect(status().isNotFound());
    }

    @Test
    void createBookingShouldBookerAndOwnerAreSameUser() throws Exception {
        BookingDto bookingDto = new BookingDto();
        bookingDto.setItemId(1L);
        bookingDto.setStart(LocalDateTime.of(2000, 1, 1, 1, 1, 1));
        bookingDto.setEnd(LocalDateTime.of(2001, 1, 1, 1, 1, 1));
        when(bookingService.save(any(Long.class), any(BookingDto.class))).thenThrow(new BookerAndOwnerAreSameUser("error"));

        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(bookingDto)))
                .andExpect(status().isNotFound());
    }

    @Test
    void createBookingShouldIncorrectBookingStartEndDate() throws Exception {
        BookingDto bookingDto = new BookingDto();
        bookingDto.setItemId(1L);
        bookingDto.setStart(LocalDateTime.of(2000, 1, 1, 1, 1, 1));
        bookingDto.setEnd(LocalDateTime.of(2001, 1, 1, 1, 1, 1));
        when(bookingService.save(any(Long.class), any(BookingDto.class))).thenThrow(new IncorrectBookingStartEndDate("error"));

        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(bookingDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createBookingShouldItemNotAvailableException() throws Exception {
        BookingDto bookingDto = new BookingDto();
        bookingDto.setItemId(1L);
        bookingDto.setStart(LocalDateTime.of(2000, 1, 1, 1, 1, 1));
        bookingDto.setEnd(LocalDateTime.of(2001, 1, 1, 1, 1, 1));
        when(bookingService.save(any(Long.class), any(BookingDto.class))).thenThrow(new ItemNotAvailableException("error"));

        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(bookingDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testConfirmBooking() throws Exception {
        BookingDtoComplete bookingDtoComplete = new BookingDtoComplete();
        bookingDtoComplete.setId(1L);
        bookingDtoComplete.setStart(LocalDateTime.of(2000, 1, 1, 1, 1, 1));
        bookingDtoComplete.setEnd(LocalDateTime.of(2001, 1, 1, 1, 1, 1));
        bookingDtoComplete.setStatus(Status.APPROVED);

        when(bookingService.confirmBooking(anyLong(), anyLong(), anyBoolean())).thenReturn(bookingDtoComplete);

        mockMvc.perform(patch("/bookings/1")
                        .header("X-Sharer-User-Id", 1L)
                        .param("approved", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("APPROVED")))
                .andExpect(jsonPath("$.start", is("2000-01-01T01:01:01")))
                .andExpect(jsonPath("$.end", is("2001-01-01T01:01:01")));

        verify(bookingService, times(1)).confirmBooking(anyLong(), anyLong(), anyBoolean());
    }

    @Test
    public void testConfirmBookingShouldThrowAlreadyConfirmed() throws Exception {
        when(bookingService.confirmBooking(anyLong(), anyLong(), anyBoolean())).thenThrow(new StatusAlreadyConfirmed("error"));

        mockMvc.perform(patch("/bookings/1")
                        .header("X-Sharer-User-Id", 1L)
                        .param("approved", "true"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testConfirmBookingShouldThrowWrongOwnerException() throws Exception {
        when(bookingService.confirmBooking(anyLong(), anyLong(), anyBoolean())).thenThrow(new WrongOwnerException("error"));

        mockMvc.perform(patch("/bookings/1")
                        .header("X-Sharer-User-Id", 1L)
                        .param("approved", "true"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testFindById() throws Exception {
        BookingDtoComplete bookingDtoComplete = new BookingDtoComplete();
        bookingDtoComplete.setId(1L);
        bookingDtoComplete.setStart(LocalDateTime.of(2000, 1, 1, 1, 1, 1));
        bookingDtoComplete.setEnd(LocalDateTime.of(2001, 1, 1, 1, 1, 1));
        bookingDtoComplete.setStatus(Status.APPROVED);

        when(bookingService.findById(anyLong(), anyLong())).thenReturn(bookingDtoComplete);

        mockMvc.perform(get("/bookings/1")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("APPROVED")))
                .andExpect(jsonPath("$.start", is("2000-01-01T01:01:01")))
                .andExpect(jsonPath("$.end", is("2001-01-01T01:01:01")));

        verify(bookingService, times(1)).findById(anyLong(), anyLong());
    }

    @Test
    public void testFindAllUsersBookingsByState() throws Exception {
        Collection<BookingDtoComplete> bookingDtoCompleteList = new ArrayList<>();
        BookingDtoComplete bookingDtoComplete1 = new BookingDtoComplete();
        bookingDtoComplete1.setId(1L);
        bookingDtoComplete1.setStart(LocalDateTime.of(2000, 1, 1, 1, 1, 1));
        bookingDtoComplete1.setEnd(LocalDateTime.of(2001, 1, 1, 1, 1, 1));
        bookingDtoComplete1.setStatus(Status.APPROVED);

        BookingDtoComplete bookingDtoComplete2 = new BookingDtoComplete();
        bookingDtoComplete2.setId(1L);
        bookingDtoComplete2.setStart(LocalDateTime.of(3000, 1, 1, 1, 1, 1));
        bookingDtoComplete2.setEnd(LocalDateTime.of(3001, 1, 1, 1, 1, 1));
        bookingDtoComplete2.setStatus(Status.APPROVED);
        bookingDtoCompleteList.add(bookingDtoComplete1);
        bookingDtoCompleteList.add(bookingDtoComplete2);

        when(bookingService.findAllUsersBookingsByState(anyLong(), any(State.class), anyInt(), anyInt())).thenReturn(bookingDtoCompleteList);

        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", 1L)
                        .param("state", "FUTURE")
                        .param("from", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].status", is("APPROVED")))
                .andExpect(jsonPath("$[0].start", is("2000-01-01T01:01:01")))
                .andExpect(jsonPath("$[0].end", is("2001-01-01T01:01:01")))
                .andExpect(jsonPath("$[1].status", is("APPROVED")))
                .andExpect(jsonPath("$[1].start", is("3000-01-01T01:01:01")))
                .andExpect(jsonPath("$[1].end", is("3001-01-01T01:01:01")));

        verify(bookingService, times(1)).findAllUsersBookingsByState(anyLong(), any(State.class), anyInt(), anyInt());
    }

    @Test
    public void testFindAllUsersBookingsByStateShouldThrow() throws Exception {
        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", 1L)
                        .param("state", "DIJFIJSFIJDIJFIOJS")
                        .param("from", "0")
                        .param("size", "10"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testFindAllOwnersBookingsByState() throws Exception {
        Collection<BookingDtoComplete> bookingDtoCompleteList = new ArrayList<>();
        BookingDtoComplete bookingDtoComplete1 = new BookingDtoComplete();
        bookingDtoComplete1.setId(1L);
        bookingDtoComplete1.setStart(LocalDateTime.of(2000, 1, 1, 1, 1, 1));
        bookingDtoComplete1.setEnd(LocalDateTime.of(2001, 1, 1, 1, 1, 1));
        bookingDtoComplete1.setStatus(Status.APPROVED);

        BookingDtoComplete bookingDtoComplete2 = new BookingDtoComplete();
        bookingDtoComplete2.setId(1L);
        bookingDtoComplete2.setStart(LocalDateTime.of(3000, 1, 1, 1, 1, 1));
        bookingDtoComplete2.setEnd(LocalDateTime.of(3001, 1, 1, 1, 1, 1));
        bookingDtoComplete2.setStatus(Status.APPROVED);
        bookingDtoCompleteList.add(bookingDtoComplete1);
        bookingDtoCompleteList.add(bookingDtoComplete2);

        when(bookingService.findAllUsersBookingsByState(anyLong(), any(State.class), anyInt(), anyInt())).thenReturn(bookingDtoCompleteList);

        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", 1L)
                        .param("state", "FUTURE")
                        .param("from", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].status", is("APPROVED")))
                .andExpect(jsonPath("$[0].start", is("2000-01-01T01:01:01")))
                .andExpect(jsonPath("$[0].end", is("2001-01-01T01:01:01")))
                .andExpect(jsonPath("$[1].status", is("APPROVED")))
                .andExpect(jsonPath("$[1].start", is("3000-01-01T01:01:01")))
                .andExpect(jsonPath("$[1].end", is("3001-01-01T01:01:01")));

        verify(bookingService, times(1)).findAllUsersBookingsByState(anyLong(), any(State.class), anyInt(), anyInt());
    }

    @Test
    public void testFindAllOwnersBookingsByStateShouldThrow() throws Exception {
        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", 1L)
                        .param("state", "UNSUPPORTED_STATE")
                        .param("from", "0")
                        .param("size", "10"))
                .andExpect(status().isBadRequest());
    }

    private static String asJsonString(Object obj) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();
        return objectMapper.writeValueAsString(obj);
    }
}