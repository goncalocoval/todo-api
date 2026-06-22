package com.example.todoapi.dto;

public record LoginResponse(
        String token,
        String email,
        String role
) {}
