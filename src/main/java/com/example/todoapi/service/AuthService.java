package com.example.todoapi.service;

import com.example.todoapi.dto.LoginRequest;
import com.example.todoapi.dto.LoginResponse;
import com.example.todoapi.dto.RegisterRequest;
import com.example.todoapi.exception.EmailAlreadyExistsException;
import com.example.todoapi.model.User;
import com.example.todoapi.repository.UserRepository;
import com.example.todoapi.security.JwtService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService, AuthenticationManager authenticationManager){
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
    }

    public LoginResponse register(RegisterRequest request){
        if(userRepository.existsByEmail(request.email())){
            throw new EmailAlreadyExistsException(request.email());
        }

        User user = new User(
                request.email(),
                passwordEncoder.encode(request.password()),
                "ROLE_USER"
        );

        userRepository.save(user);
        String token = jwtService.generateToken(user);
        return new LoginResponse(token, user.getEmail(), user.getRole());
    }

    public LoginResponse login(LoginRequest request){
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password())
        );

        User user = userRepository.findByEmail(request.email()).orElseThrow();

        String token = jwtService.generateToken(user);
        return new LoginResponse(token, user.getEmail(), user.getRole());
    }
}
