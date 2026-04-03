package com.zorvyn.finance.service;

import com.zorvyn.finance.DTOs.UserResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserService {
    Page<UserResponseDTO> getAllUsers(Pageable pageable);
    UserResponseDTO getUserById(String displayId);
    UserResponseDTO registerUser(com.zorvyn.finance.DTOs.UserRegistrationRequestDTO request);
    UserResponseDTO getUserProfile(String email);
    UserResponseDTO updateUserRole(String displayId, com.zorvyn.finance.model.Role role);
    UserResponseDTO updateUserStatus(String displayId, boolean active);
}
