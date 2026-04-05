package com.zorvyn.finance.model;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.userdetails.UserDetails;


import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

/**
 * Entity representing a registered user within the Zorvyn Finance System.
 * <p>
 * This class handles core authentication credentials and authorization roles.
 * It utilizes the modern Hibernate soft-delete pattern to deactivate accounts
 * without purging historical transaction data.
 * </p>
 *
 * @see AbstractMappedEntity for inherited auditing and unique display identifiers.
 */
@Entity
@Table(name = "users")
/* * Modern Soft Delete implementation:
 * SQLDelete intercepts the delete() call to perform an UPDATE instead.
 * SQLRestriction replaces the deprecated @Where to filter out deleted users from all queries.
 */
@SQLDelete(sql = "UPDATE users SET is_deleted = true, deleted_at = CURRENT_TIMESTAMP, updated_at = CURRENT_TIMESTAMP WHERE id = ?")
@SQLRestriction("is_deleted = false")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User extends AbstractMappedEntity {

    /**
     * The unique email address used for login and identity verification.
     * Guaranteed to be unique across the entire platform.
     */
    @Column(unique = true, nullable = false)
    private String email;

    /**
     * The BCrypt-encoded password hash.
     * <b>Security Note:</b> Raw passwords should never be stored in this field.
     */
    @Column(nullable = false)
    private String password;

    /**
     * The authorization level assigned to the user.
     * Determines access to administrative and analytical endpoints.
     * @see Role
     */
    @Enumerated(EnumType.STRING)
    private Role role;

    /**
     * Account status flag.
     * If set to {@code false}, the user is barred from accessing the system
     * regardless of their credentials.
     */
    private boolean active = true;
}