package com.zorvyn.finance.controller;

import com.zorvyn.finance.DTOs.UserResponseDTO;
import com.zorvyn.finance.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<Page<UserResponseDTO>> getAllUsers(Pageable pageable) {
        return ResponseEntity.ok(userService.getAllUsers(pageable));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{displayId}")
    public ResponseEntity<UserResponseDTO> getUserById(@PathVariable String displayId) {
        return ResponseEntity.ok(userService.getUserById(displayId));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'ANALYST', 'VIEWER')")
    @GetMapping("/profile")
    public ResponseEntity<UserResponseDTO> getProfile(java.security.Principal principal) {
        return ResponseEntity.ok(userService.getUserProfile(principal.getName()));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @org.springframework.web.bind.annotation.PutMapping("/{displayId}/role")
    public ResponseEntity<UserResponseDTO> updateRole(@PathVariable String displayId, @org.springframework.web.bind.annotation.RequestParam com.zorvyn.finance.model.Role role) {
        return ResponseEntity.ok(userService.updateUserRole(displayId, role));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @org.springframework.web.bind.annotation.PutMapping("/{displayId}/status")
    public ResponseEntity<UserResponseDTO> updateStatus(@PathVariable String displayId, @org.springframework.web.bind.annotation.RequestParam boolean active) {
        return ResponseEntity.ok(userService.updateUserStatus(displayId, active));
    }
}
