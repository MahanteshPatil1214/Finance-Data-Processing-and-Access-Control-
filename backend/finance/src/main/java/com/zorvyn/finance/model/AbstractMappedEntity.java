package com.zorvyn.finance.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.UUID;
/**
 * Base abstract class for all database entities in the Zorvyn system.
 * <p>
 * Provides common fields and lifecycle hooks, including:
 * <ul>
 * <li>Primary Key management using UUIDs.</li>
 * <li>Public-facing unique display identifiers.</li>
 * <li>Automatic JPA Auditing for creation and modification timestamps.</li>
 * <li>Support for soft-delete logic.</li>
 * </ul>
 * </p>
 */
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
public abstract class AbstractMappedEntity {

    /**
     * Internal database primary key.
     * Uses UUID Strategy to prevent ID guessing and improve distributed system compatibility.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(updatable = false, nullable = false)
    private UUID id;

    /**
     * A human-readable unique identifier (e.g., ID-A1B2C3D4).
     * This is used in API endpoints and the frontend to identify records
     * without exposing internal database UUIDs.
     */
    @Column(unique = true, updatable = false, nullable = false)
    private String displayId;

    /**
     * Automatically populated timestamp indicating when the record was first persisted.
     */
    @CreatedDate
    @Column(updatable = false, nullable = false)
    private LocalDateTime createdAt;

    /**
     * Automatically updated timestamp reflecting the last time the record was modified.
     */
    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    /**
     * Flag indicating if the record has been logically deleted.
     */
    private boolean isDeleted = false;

    /**
     * Timestamp of when the logical deletion occurred.
     */
    private LocalDateTime deletedAt;

    /**
     * JPA Lifecycle hook that executes before the entity is persisted.
     * <p>
     * Ensures that a unique, 8-character alphanumeric displayId is generated
     * if one hasn't been manually assigned.
     * </p>
     */
    @PrePersist
    protected void onCreate() {
        if (this.displayId == null) {
            this.displayId = "ID-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        }
    }
}