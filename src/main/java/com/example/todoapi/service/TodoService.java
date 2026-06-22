package com.example.todoapi.service;

import com.example.todoapi.dto.TodoRequest;
import com.example.todoapi.dto.TodoResponse;
import com.example.todoapi.exception.TodoNotFoundException;
import com.example.todoapi.model.Todo;
import com.example.todoapi.model.User;
import com.example.todoapi.repository.TodoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TodoService {

    private final TodoRepository todoRepository;

    public TodoService(TodoRepository todoRepository){
        this.todoRepository = todoRepository;
    }

    public TodoResponse create(TodoRequest request, User user){
        Todo todo = new Todo(request.title(), request.description(), user);
        return toResponse(todoRepository.save(todo));
    }

    public List<TodoResponse> getAll(User user){
        return todoRepository.findByUserId(user.getId())
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public TodoResponse getById(Long id, User user){
        Todo todo = todoRepository.findByIdAndUserId(id, user.getId())
                .orElseThrow(() -> new TodoNotFoundException(id));
        return toResponse(todo);
    }

    public TodoResponse update(Long id, TodoRequest request, User user){
        Todo todo = todoRepository.findByIdAndUserId(id, user.getId())
                .orElseThrow(() -> new TodoNotFoundException(id));
        todo.setTitle(request.title());
        todo.setDescription(request.description());
        return toResponse(todoRepository.save(todo));
    }

    public TodoResponse toggleCompleted(Long id, User user){
        Todo todo = todoRepository.findByIdAndUserId(id, user.getId())
                .orElseThrow(() -> new TodoNotFoundException(id));
        todo.setCompleted(!todo.isCompleted());
        return toResponse(todoRepository.save(todo));
    }

    public void delete(Long id, User user){
        Todo todo = todoRepository.findByIdAndUserId(id, user.getId())
                .orElseThrow(() -> new TodoNotFoundException(id));
        todoRepository.delete(todo);
    }

    private TodoResponse toResponse(Todo todo){
        return new TodoResponse(
                todo.getId(),
                todo.getTitle(),
                todo.getDescription(),
                todo.isCompleted(),
                todo.getCreatedAt()
        );
    }

}
