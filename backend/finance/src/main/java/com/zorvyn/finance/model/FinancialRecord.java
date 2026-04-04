package com.zorvyn.finance.model;


import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "financial_records")
@org.hibernate.annotations.SQLDelete(sql = "UPDATE financial_records SET is_deleted = true, deleted_at = CURRENT_TIMESTAMP, updated_at = CURRENT_TIMESTAMP WHERE id = ?")
@org.hibernate.annotations.Where(clause = "is_deleted = false")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FinancialRecord extends AbstractMappedEntity {

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal amount; // Using BigDecimal for financial accuracy

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionType type; // INCOME or EXPENSE

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    private String description;

    @Column(nullable = false)
    private LocalDateTime transactionDate; // The actual date of the expense

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User creator; // The user who added this record
}
