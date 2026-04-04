package com.zorvyn.finance.model;

import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;

@Entity
@Table(name = "categories")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Category extends AbstractMappedEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(unique = true, nullable = false)
    private String name; // e.g., "Food", "Salary"

    private String description;

    private boolean active = true; // Admin can "deactivate" instead of delete
}
