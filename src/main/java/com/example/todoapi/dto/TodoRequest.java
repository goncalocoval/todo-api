package com.example.todoapi.dto;

import jakarta.validation.constraints.NotBlank;

public record TodoRequest(
        @NotBlank(message = "Title cannot be blank")
        String title,

        String description
) {}
