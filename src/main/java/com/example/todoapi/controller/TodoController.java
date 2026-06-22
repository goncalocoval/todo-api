package com.example.todoapi.controller;

import com.example.todoapi.dto.TodoRequest;
import com.example.todoapi.dto.TodoResponse;
import com.example.todoapi.model.User;
import com.example.todoapi.service.TodoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Todos", description = "Endpoints for managing todos")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/todos")
public class TodoController {

    private final TodoService todoService;

    public TodoController(TodoService todoService){
        this.todoService = todoService;
    }

    @Operation(summary = "Create a todo")
    @PostMapping
    public ResponseEntity<TodoResponse> create(@Valid @RequestBody TodoRequest request, @AuthenticationPrincipal User user){
        return ResponseEntity.status(HttpStatus.CREATED).body(todoService.create(request, user));
    }

    @Operation(summary = "Get all todos")
    @GetMapping
    public ResponseEntity<List<TodoResponse>> getAll(@AuthenticationPrincipal User user){
        return ResponseEntity.ok(todoService.getAll(user));
    }

    @Operation(summary = "Get todo by ID")
    @GetMapping("/{id}")
    public ResponseEntity<TodoResponse> getById(@PathVariable Long id, @AuthenticationPrincipal User user){
        return ResponseEntity.ok(todoService.getById(id, user));
    }

    @Operation(summary = "Update a todo")
    @PutMapping("/{id}")
    public ResponseEntity<TodoResponse> update(@PathVariable Long id, @Valid @RequestBody TodoRequest request, @AuthenticationPrincipal User user){
        return ResponseEntity.ok(todoService.update(id, request, user));
    }

    @Operation(summary = "Toggle todo completed status")
    @PatchMapping("/{id}/toggle")
    public ResponseEntity<TodoResponse> toggle(@PathVariable Long id, @AuthenticationPrincipal User user){
        return ResponseEntity.ok(todoService.toggleCompleted(id, user));
    }

    @Operation(summary = "Delete a todo")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id, @AuthenticationPrincipal User user){
        todoService.delete(id, user);
        return ResponseEntity.noContent().build();
    }

}
