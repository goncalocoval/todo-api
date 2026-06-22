package com.example.todoapi.dto;

public record UserResponse(
        Long id,
        String email,
        String role
) {}
