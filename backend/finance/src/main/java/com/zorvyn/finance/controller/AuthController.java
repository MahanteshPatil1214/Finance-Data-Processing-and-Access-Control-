package com.zorvyn.finance.controller;

import com.zorvyn.finance.DTOs.UserRegistrationRequestDTO;
import com.zorvyn.finance.DTOs.UserResponseDTO;
import com.zorvyn.finance.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final org.springframework.security.authentication.AuthenticationManager authenticationManager;
    private final com.zorvyn.finance.security.CustomUserDetailsService userDetailsService;
    private final com.zorvyn.finance.security.JwtUtil jwtUtil;

    @PostMapping("/register")
    public ResponseEntity<UserResponseDTO> register(@Valid @RequestBody UserRegistrationRequestDTO request) {
        UserResponseDTO response = userService.registerUser(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

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
