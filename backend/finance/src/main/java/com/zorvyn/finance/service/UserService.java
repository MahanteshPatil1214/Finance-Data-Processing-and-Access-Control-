package com.zorvyn.finance.service;

import com.zorvyn.finance.DTOs.UserResponseDTO;
import com.zorvyn.finance.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.Map;

public interface UserService {
    Page<UserResponseDTO> getAllUsers(Pageable pageable);
    UserResponseDTO getUserById(String displayId);
    UserResponseDTO registerUser(com.zorvyn.finance.DTOs.UserRegistrationRequestDTO request);
    UserResponseDTO getUserProfile(String email);
    UserResponseDTO updateUserRole(String displayId, com.zorvyn.finance.model.Role role);
    UserResponseDTO updateUserStatus(String displayId, boolean active);

    Map<String, BigDecimal> getMonthlyCategoryBreakdown(User user);
}
