package com.zorvyn.finance.controller;

import com.zorvyn.finance.DTOs.UserRegistrationRequestDTO;
import com.zorvyn.finance.DTOs.UserResponseDTO;
import com.zorvyn.finance.security.CustomUserDetailsService;
import com.zorvyn.finance.security.JwtUtil;
import com.zorvyn.finance.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * REST controller for managing authentication.
 * Provides endpoints for user registration and login.
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Endpoints for user registration and authentication")
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Autowired
    private JwtUtil jwtUtil;

    /**
     * Registers a new user.
     *
     * @param request the user registration details
     * @return the registered user details
     */
    @Operation(summary = "Register a new user", description = "Creates a new user account.")
    @ApiResponse(responseCode = "201", description = "User successfully registered")
    @PostMapping("/register")
    public ResponseEntity<UserResponseDTO> register(@Valid @RequestBody UserRegistrationRequestDTO request) {
        UserResponseDTO response = userService.registerUser(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    /**
     * Authenticates a user and generates a JWT token.
     *
     * @param request the login credentials (email and password)
     * @return a JWT token if authentication is successful
     */
    @Operation(summary = "Authenticate a user", description = "Validates credentials and returns a JWT token.")
    @PostMapping("/login")
    public ResponseEntity<com.zorvyn.finance.DTOs.AuthResponseDTO> login(@Valid @RequestBody com.zorvyn.finance.DTOs.AuthRequestDTO request) {
        authenticationManager.authenticate(
                new org.springframework.security.authentication.UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        final org.springframework.security.core.userdetails.UserDetails userDetails = userDetailsService.loadUserByUsername(request.getEmail());
        final String jwt = jwtUtil.generateToken(userDetails);

        return ResponseEntity.ok(new com.zorvyn.finance.DTOs.AuthResponseDTO(jwt));
    }
}
