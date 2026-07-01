package com.example.todoapi.service;

import com.example.todoapi.dto.TodoRequest;
import com.example.todoapi.dto.TodoResponse;
import com.example.todoapi.exception.TodoNotFoundException;
import com.example.todoapi.model.Todo;
import com.example.todoapi.model.User;
import com.example.todoapi.repository.TodoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TodoServiceTest {

    @Mock
    private TodoRepository todoRepository;

    @InjectMocks
    private TodoService todoService;

    private User user;
    private Todo todo;

    @BeforeEach
    void setUp(){
        user = new User("user@example.com", "password", "ROLE_USER");

        todo = new Todo("Buy groceries", "Milk and eggs", user);
    }

    @Test
    void shouldReturnTodoWhenExists(){

        when(todoRepository.findByIdAndUserId(1L, user.getId())).thenReturn(Optional.of(todo));

        TodoResponse response = todoService.getById(1L, user);

        assertThat(response.title()).isEqualTo("Buy groceries");
        assertThat(response.description()).isEqualTo("Milk and eggs");
        assertThat(response.completed()).isFalse();

    }

    @Test
    void shouldThrowWhenTodoNotFound(){

        when(todoRepository.findByIdAndUserId(99L, user.getId())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> todoService.getById(99L, user))
                .isInstanceOf(TodoNotFoundException.class)
                .hasMessageContaining("99");

    }

    @Test
    void shouldCreateTodo() {

        TodoRequest request = new TodoRequest("New Todo", "Description");
        when(todoRepository.save(any(Todo.class))).thenReturn(todo);

        TodoResponse response = todoService.create(request, user);

        assertThat(response.title()).isEqualTo("Buy groceries");
        verify(todoRepository, times(1)).save(any(Todo.class));

    }

    @Test
    void shouldReturnAllTodosForUser(){
        when(todoRepository.findByUserId(user.getId())).thenReturn(List.of(todo));

        List<TodoResponse> responses = todoService.getAll(user);

        assertThat(responses).hasSize(1);
        assertThat(responses.get(0).title()).isEqualTo("Buy groceries");
    }

    @Test
    void shouldToggleCompleted(){
        when(todoRepository.findByIdAndUserId(1L, user.getId())).thenReturn(Optional.of(todo));
        when(todoRepository.save(any(Todo.class))).thenReturn(todo);

        TodoResponse response = todoService.toggleCompleted(1L, user);

        verify(todoRepository, times(1)).save(any(Todo.class));
    }

    @Test
    void shouldDeleteTodo(){
        when(todoRepository.findByIdAndUserId(1L, user.getId())).thenReturn(Optional.of(todo));

        todoService.delete(1L, user);

        verify(todoRepository, times(1)).delete(todo);
    }

}
