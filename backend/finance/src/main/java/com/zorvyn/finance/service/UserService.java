package com.zorvyn.finance.service;

import com.zorvyn.finance.DTOs.UserRegistrationRequestDTO;
import com.zorvyn.finance.DTOs.UserResponseDTO;
import com.zorvyn.finance.model.Role;
import com.zorvyn.finance.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.Map;

/**
 * Service interface for managing user-related operations.
 * Handles user registration, profile retrieval, administrative role management,
 * and user-specific financial analytics.
 */
public interface UserService {

    /**
     * Retrieves a paginated list of all users in the system.
     * @param pageable Pagination and sorting information.
     * @return A {@link Page} of {@link UserResponseDTO} containing non-sensitive user details.
     */
    Page<UserResponseDTO> getAllUsers(Pageable pageable);

    /**
     * Finds a specific user by their unique display identifier.
     * @param displayId The public-facing unique ID (e.g., ZORV-USR-123).
     * @return The corresponding {@link UserResponseDTO}.
     */
    UserResponseDTO getUserById(String displayId);

    /**
     * Processes a new user registration request.
     * @param request DTO containing email, password, and initial user details.
     * @return The newly created {@link UserResponseDTO}.
     */
    UserResponseDTO registerUser(UserRegistrationRequestDTO request);

    /**
     * Fetches the profile details of the currently authenticated user.
     * @param email The email address of the user (extracted from Security Context).
     * @return The user's profile information as a {@link UserResponseDTO}.
     */
    UserResponseDTO getUserProfile(String email);

    /**
     * Updates the authorization level of a specific user.
     * Usually restricted to ADMIN users.
     * @param displayId The identifier of the user to update.
     * @param role The new {@link com.zorvyn.finance.model.Role} to assign.
     * @return The updated {@link UserResponseDTO}.
     */
    UserResponseDTO updateUserRole(String displayId, Role role);

    /**
     * Enables or disables a user account.
     * Used for administrative locking or account deactivation.
     * @param displayId The identifier of the user.
     * @param active True to enable, false to disable.
     * @return The updated {@link UserResponseDTO}.
     */
    UserResponseDTO updateUserStatus(String displayId, boolean active);

    /**
     * Calculates the total expenditure per category for a specific user within the current month.
     * @param user The {@link User} entity for whom the breakdown is calculated.
     * @return A Map where keys are category names and values are the total amounts spent.
     */
    Map<String, BigDecimal> getMonthlyCategoryBreakdown(User user);
}
