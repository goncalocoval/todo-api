package com.example.todoapi.service;

import com.example.todoapi.dto.LoginRequest;
import com.example.todoapi.dto.LoginResponse;
import com.example.todoapi.dto.RegisterRequest;
import com.example.todoapi.exception.EmailAlreadyExistsException;
import com.example.todoapi.model.User;
import com.example.todoapi.repository.UserRepository;
import com.example.todoapi.security.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @Mock
    private AuthenticationManager authenticationManager;

    private AuthService authService;

    private User user;

    @BeforeEach
    void setUp(){
        authService = new AuthService(userRepository, passwordEncoder, jwtService, authenticationManager);
        user = new User("user@example.com", "encodedPassword", "ROLE_USER");
    }

    @Test
    void shouldRegisterNewUser(){
        RegisterRequest request = new RegisterRequest("user@example.com", "password123");

        when(userRepository.existsByEmail("user@example.com")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");
        when(jwtService.generateToken(any(User.class))).thenReturn("fake-jwt-token");

        LoginResponse response = authService.register(request);

        assertThat(response.token()).isEqualTo("fake-jwt-token");
        assertThat(response.email()).isEqualTo("user@example.com");
        assertThat(response.role()).isEqualTo("ROLE_USER");
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void shouldThrowWhenEmailAlreadyExists(){
        RegisterRequest request = new RegisterRequest("user@example.com", "password123");
        when(userRepository.existsByEmail("user@example.com")).thenReturn(true);

        assertThatThrownBy(() -> authService.register(request))
                .isInstanceOf(EmailAlreadyExistsException.class)
                .hasMessageContaining("user@example.com");

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void shouldLoginSuccessfully(){
        LoginRequest request = new LoginRequest("user@example.com", "password123");
        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));
        when(jwtService.generateToken(user)).thenReturn("fake-jwt-token");

        LoginResponse response = authService.login(request);

        assertThat(response.token()).isEqualTo("fake-jwt-token");
        assertThat(response.email()).isEqualTo("user@example.com");
        verify(authenticationManager, times(1)).authenticate(any());
    }

}
