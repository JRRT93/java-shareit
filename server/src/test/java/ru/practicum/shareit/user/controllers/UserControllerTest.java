package ru.practicum.shareit.user.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.exceptions.EntityNotFoundException;
import ru.practicum.shareit.user.exceptions.NotUniqueUserEmail;
import ru.practicum.shareit.user.services.UserService;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@WebMvcTest(controllers = UserController.class)
class UserControllerTest {
    @MockBean
    UserService userService;
    @Autowired
    MockMvc mockMvc;

    @Test
    public void testSave() throws Exception {
        UserDto userDto = new UserDto();
        userDto.setEmail("test@example.com");
        userDto.setName("Test User");

        UserDto savedUserDto = new UserDto();
        savedUserDto.setId(1L);
        savedUserDto.setEmail("test@example.com");
        savedUserDto.setName("Test User");

        when(userService.save(any(UserDto.class))).thenReturn(savedUserDto);

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(userDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.email", is("test@example.com")))
                .andExpect(jsonPath("$.name", is("Test User")));

        verify(userService, times(1)).save(any(UserDto.class));
    }

    @Test
    public void testSaveShouldThrowEmail() throws Exception {
        UserDto userDto = new UserDto();
        userDto.setEmail("test@example.com");
        userDto.setName("Test User");

        when(userService.save(any(UserDto.class))).thenThrow(new NotUniqueUserEmail("error"));

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(userDto)))
                .andExpect(status().isConflict());
        verify(userService, times(1)).save(any(UserDto.class));
    }

    @Test
    public void testFindByIdShouldThrowNotFound() throws Exception {
        when(userService.findById(any(Long.class))).thenThrow(new EntityNotFoundException("error"));

        mockMvc.perform(get("/users/99"))
                .andExpect(status().isNotFound());

        verify(userService, times(1)).findById(any(Long.class));
    }

    @Test
    public void testFindById() throws Exception {
        UserDto userDto = new UserDto();
        userDto.setId(1L);
        userDto.setEmail("test@example.com");
        userDto.setName("Test User");

        when(userService.findById(1L)).thenReturn(userDto);

        mockMvc.perform(get("/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.email", is("test@example.com")))
                .andExpect(jsonPath("$.name", is("Test User")));

        verify(userService, times(1)).findById(1L);
    }

    @Test
    public void testUpdate() throws Exception {
        UserDto userDto = new UserDto();
        userDto.setId(1L);
        userDto.setEmail("test@example.com");
        userDto.setName("Test User");

        UserDto updatedUserDto = new UserDto();
        updatedUserDto.setId(1L);
        updatedUserDto.setEmail("updated@example.com");
        updatedUserDto.setName("Updated User");

        when(userService.update(any(UserDto.class), eq(1L))).thenReturn(updatedUserDto);

        mockMvc.perform(patch("/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(userDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.email", is("updated@example.com")))
                .andExpect(jsonPath("$.name", is("Updated User")));

        verify(userService, times(1)).update(any(UserDto.class), eq(1L));
    }

    @Test
    public void testDelete() throws Exception {
        mockMvc.perform(delete("/users/1"))
                .andExpect(status().isOk());

        verify(userService, times(1)).deleteById(1L);
    }

    @Test
    public void testFindAll() throws Exception {
        UserDto userDto1 = new UserDto();
        userDto1.setId(1L);
        userDto1.setEmail("test1@example.com");
        userDto1.setName("Test User 1");

        UserDto userDto2 = new UserDto();
        userDto2.setId(2L);
        userDto2.setEmail("test2@example.com");
        userDto2.setName("Test User 2");

        List<UserDto> userDtoList = Arrays.asList(userDto1, userDto2);

        when(userService.findAll()).thenReturn(userDtoList);

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].email", is("test1@example.com")))
                .andExpect(jsonPath("$[0].name", is("Test User 1")))
                .andExpect(jsonPath("$[1].id", is(2)))
                .andExpect(jsonPath("$[1].email", is("test2@example.com")))
                .andExpect(jsonPath("$[1].name", is("Test User 2")));

        verify(userService, times(1)).findAll();
    }

    private static String asJsonString(Object obj) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(obj);
    }
}