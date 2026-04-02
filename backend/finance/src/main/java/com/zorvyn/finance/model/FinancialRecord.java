package com.zorvyn.finance.model;


import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "financial_records")
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

    @Column(nullable = false)
    private String category; // e.g., Salary, Food, Rent

    private String description;

    @Column(nullable = false)
    private LocalDateTime transactionDate = LocalDateTime.now();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User creator; // The user who added this record
}
