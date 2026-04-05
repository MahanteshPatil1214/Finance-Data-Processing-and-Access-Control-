package com.zorvyn.finance.controller;

import com.zorvyn.finance.DTOs.UserResponseDTO;
import com.zorvyn.finance.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * REST controller for managing users.
 * Provides endpoints for retrieving and updating user details.
 */
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "User Management", description = "Endpoints for managing users")
public class UserController {

    @Autowired
    private  UserService userService;

    /**
     * Retrieves all users.
     *
     * @param pageable pagination details
     * @return a paginated list of users
     */
    @Operation(summary = "Get all users", description = "Retrieves a paginated list of all registered users. Requires ADMIN role.")
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<Page<UserResponseDTO>> getAllUsers(Pageable pageable) {
        return ResponseEntity.ok(userService.getAllUsers(pageable));
    }

    /**
     * Retrieves a user by their display ID.
     *
     * @param displayId the user's public display ID
     * @return the user details
     */
    @Operation(summary = "Get a user by ID", description = "Retrieves details of a specific user by display ID. Requires ADMIN role.")
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{displayId}")
    public ResponseEntity<UserResponseDTO> getUserById(@PathVariable String displayId) {
        return ResponseEntity.ok(userService.getUserById(displayId));
    }

    /**
     * Retrieves the currently authenticated user's profile.
     *
     * @param principal the authenticated user principal
     * @return the profile details of the current user
     */
    @Operation(summary = "Get user profile", description = "Retrieves the profile of the currently authenticated user.")
    @PreAuthorize("hasAnyRole('ADMIN', 'ANALYST', 'VIEWER')")
    @GetMapping("/profile")
    public ResponseEntity<UserResponseDTO> getProfile(java.security.Principal principal) {
        return ResponseEntity.ok(userService.getUserProfile(principal.getName()));
    }

    /**
     * Updates a user's role.
     *
     * @param displayId the user's public display ID
     * @param role the new role to assign
     * @return the updated user details
     */
    @Operation(summary = "Update user role", description = "Updates the role of a specific user. Requires ADMIN role.")
    @PreAuthorize("hasRole('ADMIN')")
    @org.springframework.web.bind.annotation.PutMapping("/{displayId}/role")
    public ResponseEntity<UserResponseDTO> updateRole(@PathVariable String displayId, @org.springframework.web.bind.annotation.RequestParam com.zorvyn.finance.model.Role role) {
        return ResponseEntity.ok(userService.updateUserRole(displayId, role));
    }

    /**
     * Updates a user's status.
     *
     * @param displayId the user's public display ID
     * @param active whether the user is active or not
     * @return the updated user details
     */
    @Operation(summary = "Update user status", description = "Activates or deactivates a user. Requires ADMIN role.")
    @PreAuthorize("hasRole('ADMIN')")
    @org.springframework.web.bind.annotation.PutMapping("/{displayId}/status")
    public ResponseEntity<UserResponseDTO> updateStatus(@PathVariable String displayId, @org.springframework.web.bind.annotation.RequestParam boolean active) {
        return ResponseEntity.ok(userService.updateUserStatus(displayId, active));
    }
}
