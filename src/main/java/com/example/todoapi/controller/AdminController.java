package com.example.todoapi.controller;

import com.example.todoapi.dto.UserResponse;
import com.example.todoapi.model.User;
import com.example.todoapi.service.AdminService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Admin", description = "Endpoints for admin management")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/admin")
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService){
        this.adminService = adminService;
    }

    @Operation(summary = "Get all users", description = "Returns a list of all registered users. Requires ROLE_ADMIN.")
    @GetMapping("/users")
    public ResponseEntity<List<UserResponse>> getAllUsers(){
        return ResponseEntity.ok(adminService.getAllUsers());
    }

    @Operation(summary = "Delete a user", description = "Deletes a user by ID. Requires ROLE_ADMIN.")
    @DeleteMapping("/users/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id){
        adminService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

}
