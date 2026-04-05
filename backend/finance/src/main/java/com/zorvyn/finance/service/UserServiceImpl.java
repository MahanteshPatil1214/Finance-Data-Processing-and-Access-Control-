package com.zorvyn.finance.service;

import com.zorvyn.finance.DTOs.UserResponseDTO;
import com.zorvyn.finance.exception.ResourceNotFoundException;
import com.zorvyn.finance.model.User;
import com.zorvyn.finance.repository.FinancialRecordRepository;
import com.zorvyn.finance.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Implementation of the {@link UserService} providing business logic for user management.
 * Interfaces with {@link UserRepository} for persistence and {@link FinancialRecordRepository}
 * for user-specific financial analytics.
 */
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    @Autowired
    private  FinancialRecordRepository recordRepository;

    @Autowired
    private  UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * Fetches all registered users with pagination support.
     * Maps the internal User entity to a public-facing {@link UserResponseDTO}.
     */
    @Override
    public Page<UserResponseDTO> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable).map(UserResponseDTO::new);
    }

    /**
     * Handles the creation of new user accounts.
     * Sets default role to VIEWER and encodes the password before saving.
     * * @throws IllegalArgumentException if the provided email is already registered.
     */
    @Override
    public UserResponseDTO getUserById(String displayId) {
        User user = userRepository.findByDisplayId(displayId)
                .orElseThrow(() -> new ResourceNotFoundException("User with id " + displayId + " not found."));
        return new UserResponseDTO(user);
    }

    /**
     * Handles the creation of new user accounts.
     * Sets default role to VIEWER and encodes the password before saving.
     * * @throws IllegalArgumentException if the provided email is already registered.
     */
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

    /**
     * Retrieves the profile details for a specific user based on their unique email.
     * This is typically used to fetch data for the currently authenticated user's
     * profile page or dashboard.
     * * @param email The unique email address of the user to look up.
     * @return A {@link UserResponseDTO} containing the user's profile information.
     * @throws ResourceNotFoundException if no user exists with the provided email.
     */
    @Override
    public UserResponseDTO getUserProfile(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User profile not found."));
        return new UserResponseDTO(user);
    }

    /**
     * Updates an existing user's role (e.g., upgrading a VIEWER to ANALYST).
     * @throws ResourceNotFoundException if the displayId does not match any user.
     */
    @Override
    public UserResponseDTO updateUserRole(String displayId, com.zorvyn.finance.model.Role role) {
        User user = userRepository.findByDisplayId(displayId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + displayId));
        user.setRole(role);
        return new UserResponseDTO(userRepository.save(user));
    }

    /**
     * Updates the account status (enabled/disabled) for a specific user.
     * Setting the status to 'false' effectively deactivates the account,
     * preventing the user from logging in or performing transactions.
     * * @param displayId The public-facing unique identifier of the user (e.g., ZORV-USR-XXXX).
     * @param active    The new status: {@code true} to enable the account, {@code false} to disable it.
     * @return The updated {@link UserResponseDTO} reflecting the new status.
     * @throws ResourceNotFoundException if the provided displayId does not match any user.
     */
    @Override
    public UserResponseDTO updateUserStatus(String displayId, boolean active) {
        User user = userRepository.findByDisplayId(displayId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + displayId));
        user.setActive(active);
        return new UserResponseDTO(userRepository.save(user));
    }

    /**
     * Calculates user spending per category for the current calendar month.
     * * <p>Implementation detail: Normalizes the start of the month to 00:00:00
     * on the 1st day to ensure accurate transaction filtering.</p>
     * * @param user The user entity for whom metrics are being calculated.
     * @return A map of Category Names to the sum of expenses for that category.
     */
    @Override
    public Map<String, BigDecimal> getMonthlyCategoryBreakdown(User user) {
        // Logic: First day of current month (April 1st)
        LocalDateTime startOfMonth = LocalDateTime.now()
                .withDayOfMonth(1)
                .withHour(0)
                .withMinute(0)
                .withSecond(0)
                .withNano(0);

        List<Object[]> results = recordRepository.getSpendingByCategoryCustom(user.getId(), startOfMonth);

        Map<String, BigDecimal> breakdown = new HashMap<>();
        for (Object[] result : results) {
            breakdown.put((String) result[0], (BigDecimal) result[1]);
        }
        return breakdown;
    }
}
