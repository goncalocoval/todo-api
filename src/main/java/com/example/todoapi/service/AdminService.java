package com.example.todoapi.service;

import com.example.todoapi.dto.UserResponse;
import com.example.todoapi.exception.UserNotFoundException;
import com.example.todoapi.model.User;
import com.example.todoapi.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdminService {

    private final UserRepository userRepository;

    public AdminService(UserRepository userRepository){
        this.userRepository = userRepository;
    }

    public List<UserResponse> getAllUsers(){
        return userRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public void deleteUser(Long id){
        User user = userRepository.findById(id).orElseThrow(() -> new UserNotFoundException(id));
        userRepository.delete(user);
    }

    public UserResponse toResponse(User user){
        return new UserResponse(user.getId(), user.getEmail(), user.getRole());
    }

}
