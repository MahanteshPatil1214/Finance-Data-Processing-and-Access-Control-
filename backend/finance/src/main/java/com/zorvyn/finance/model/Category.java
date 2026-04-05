package com.zorvyn.finance.model;

import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;

/**
 * Entity representing a financial classification category (e.g., FOOD, SALARY, RENT).
 * <p>
 * Categories are used globally across the Zorvyn platform to group financial
 * records and define budget limits. This entity supports logical deactivation
 * to maintain historical data integrity.
 * </p>
 * * @see AbstractMappedEntity for inherited auditing and displayId fields.
 */
@Entity
@Table(name = "categories")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Category extends AbstractMappedEntity {

    /**
     * The unique, displayable name of the category.
     * <p>
     * <b>Business Rule:</b> Names are stored in uppercase (enforced in service layer)
     * to prevent duplicate entries with different casing.
     * </p>
     */
    @Column(unique = true, nullable = false)
    private String name;

    /**
     * A brief explanation of the types of transactions that fall under this category.
     */
    private String description;

    /**
     * Administrative status flag.
     * <p>
     * Instead of deleting a category (which would break foreign key constraints
     * on existing financial records), an admin can set this to {@code false}
     * to hide it from selection menus.
     * </p>
     */
    private boolean active = true;
}
