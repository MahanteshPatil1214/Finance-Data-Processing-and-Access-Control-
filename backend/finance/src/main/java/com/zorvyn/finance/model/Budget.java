package com.zorvyn.finance.model;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

/**
 * Entity representing a financial budget defined by a user for a specific category and period.
 * <p>
 * This class inherits common auditing and identification fields from {@link AbstractMappedEntity}.
 * It utilizes Hibernate's soft-delete pattern to ensure that budget history is preserved
 * even after a user "deletes" a budget entry.
 * </p>
 * *
 */
@Entity
@Table(name = "budgets")
@org.hibernate.annotations.SQLDelete(sql = "UPDATE budgets SET is_deleted = true, deleted_at = CURRENT_TIMESTAMP, updated_at = CURRENT_TIMESTAMP WHERE id = ?")
@org.hibernate.annotations.Where(clause = "is_deleted = false")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Budget extends AbstractMappedEntity {

    /**
     * The owner of this budget.
     * Uses Lazy fetching to optimize performance by only loading user details when explicitly accessed.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /**
     * The financial category associated with this budget (e.g., FOOD, RENT).
     * If left null, this budget may represent a global spending limit for the user.
     */
    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    /**
     * The maximum monetary amount allocated for this budget.
     * Stored with high precision (19,4) to prevent rounding errors in financial calculations.
     */
    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal limitAmount;

    /**
     * The calendar year for which this budget is defined (e.g., 2026).
     */
    @Column(nullable = false, name = "budget_year")
    private int year;

    /**
     * The calendar month (1-12) for which this budget is defined.
     */
    @Column(nullable = false, name = "budget_month")
    private int month;
}