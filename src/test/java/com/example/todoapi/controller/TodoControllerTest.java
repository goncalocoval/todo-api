package com.example.todoapi.controller;

import com.example.todoapi.dto.TodoRequest;
import com.example.todoapi.dto.TodoResponse;
import com.example.todoapi.exception.TodoNotFoundException;
import com.example.todoapi.model.User;
import com.example.todoapi.service.TodoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TodoController.class)
@Import(TodoControllerTest.TestConfig.class)
class TodoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private TodoService todoService;

    private User user;
    private TodoResponse todoResponse;

    @TestConfiguration
    static class TestConfig {
        @Bean
        public com.example.todoapi.security.JwtService jwtService(){
            return org.mockito.Mockito.mock(com.example.todoapi.security.JwtService.class);
        }

        @Bean
        public org.springframework.security.core.userdetails.UserDetailsService userDetailsService(){
            return org.mockito.Mockito.mock(org.springframework.security.core.userdetails.UserDetailsService.class);
        }
    }

    @BeforeEach
    void setUp(){
        user = new User("user@example.com", "password", "ROLE_USER");
        todoResponse = new TodoResponse(1L, "Buy groceries", "Milk and eggs", false, LocalDateTime.now());
    }

    @Test
    @WithMockUser
    void shouldReturnAllTodos() throws Exception{
        when(todoService.getAll(any(User.class))).thenReturn(List.of(todoResponse));

        mockMvc.perform(get("/todos").with(user(user)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Buy groceries"))
                .andExpect(jsonPath("$[0].completed").value(false));

    }

    @Test
    @WithMockUser
    void shouldReturnTodoById() throws Exception{
        when(todoService.getById(eq(1L), any(User.class))).thenReturn(todoResponse);

        mockMvc.perform(get("/todos/1").with(user(user)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Buy groceries"))
                .andExpect(jsonPath("$.description").value("Milk and eggs"));
    }

    @Test
    @WithMockUser
    void shouldReturn404WhenTodoNotFound() throws Exception{
        when(todoService.getById(eq(99L), any(User.class))).thenThrow(new TodoNotFoundException(99L));

        mockMvc.perform(get("/todos/99").with(user(user)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    @WithMockUser
    void shouldCreateTodo() throws Exception{
        TodoRequest request = new TodoRequest("Buy groceries", "Milk and eggs");
        when(todoService.create(any(TodoRequest.class), any(User.class))).thenReturn(todoResponse);

        mockMvc.perform(post("/todos").with(user(user)).with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Buy groceries"));
    }

    @Test
    @WithMockUser
    void shouldReturn400WhenTitleIsBlank() throws Exception{
        TodoRequest request = new TodoRequest("", "Description");

        mockMvc.perform(post("/todos").with(user(user)).with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

}
