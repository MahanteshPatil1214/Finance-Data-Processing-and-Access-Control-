package com.zorvyn.finance.service;

import com.zorvyn.finance.DTOs.UserResponseDTO;
import com.zorvyn.finance.exception.ResourceNotFoundException;
import com.zorvyn.finance.model.User;
import com.zorvyn.finance.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;

    @Override
    public Page<UserResponseDTO> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable).map(UserResponseDTO::new);
    }

    @Override
    public UserResponseDTO getUserById(String displayId) {
        User user = userRepository.findByDisplayId(displayId)
                .orElseThrow(() -> new ResourceNotFoundException("User with id " + displayId + " not found."));
        return new UserResponseDTO(user);
    }

    @Override
    public UserResponseDTO registerUser(com.zorvyn.finance.DTOs.UserRegistrationRequestDTO request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email already in use");
        }
        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(com.zorvyn.finance.model.Role.VIEWER);
        user.setActive(true);
        user = userRepository.save(user);
        return new UserResponseDTO(user);
    }

    @Override
    public UserResponseDTO getUserProfile(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User profile not found."));
        return new UserResponseDTO(user);
    }

    @Override
    public UserResponseDTO updateUserRole(String displayId, com.zorvyn.finance.model.Role role) {
        User user = userRepository.findByDisplayId(displayId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + displayId));
        user.setRole(role);
        return new UserResponseDTO(userRepository.save(user));
    }

    @Override
    public UserResponseDTO updateUserStatus(String displayId, boolean active) {
        User user = userRepository.findByDisplayId(displayId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + displayId));
        user.setActive(active);
        return new UserResponseDTO(userRepository.save(user));
    }
}
