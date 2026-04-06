package com.zorvyn.finance.model;


import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import org.hibernate.annotations.Where;

import java.math.BigDecimal;
import java.time.LocalDateTime;
/**
 * Core entity representing a specific financial transaction (Income or Expense).
 * <p>
 * This entity serves as the primary data point for all platform analytics,
 * including spending trends and budget utilization. It inherits unique
 * identification and auditing capabilities from {@link AbstractMappedEntity}.
 * </p>
 *
 * <p><b>Soft Delete:</b> Records are logically deleted via SQL updates to
 * preserve historical audit trails for financial reporting.</p>
 */
@Entity
@Table(name = "financial_records")
@SQLDelete(sql = "UPDATE financial_records SET is_deleted = true, deleted_at = CURRENT_TIMESTAMP, updated_at = CURRENT_TIMESTAMP WHERE id = ?")
@SQLRestriction("is_deleted = false")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FinancialRecord extends AbstractMappedEntity {

    /**
     * The monetary value of the transaction.
     * High-precision (19,4) ensures no rounding errors occur during
     * large-scale aggregations or currency conversions.
     */
    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal amount;

    /**
     * Categorizes the movement of funds.
     * Stored as a String in the database for better readability during manual SQL audits.
     * @see TransactionType
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionType type;

    /**
     * The classification category associated with this record.
     * Fetched lazily to optimize memory usage when only transaction totals are required.
     */
    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    /**
     * User-provided notes or details regarding the purpose of the transaction.
     */
    private String description;

    /**
     * The specific date and time when the transaction occurred.
     * This field is used for time-series analysis and MoM (Month-over-Month) trends.
     */
    @Column(nullable = false)
    private LocalDateTime transactionDate;

    /**
     * The user who created and owns this record.
     * Ensures data isolation and facilitates per-user dashboard summaries.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User creator;
}