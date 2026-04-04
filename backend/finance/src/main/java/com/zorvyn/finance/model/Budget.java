package com.zorvyn.finance.model;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Table(name = "budgets")
@org.hibernate.annotations.SQLDelete(sql = "UPDATE budgets SET is_deleted = true, deleted_at = CURRENT_TIMESTAMP, updated_at = CURRENT_TIMESTAMP WHERE id = ?")
@org.hibernate.annotations.Where(clause = "is_deleted = false")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Budget extends AbstractMappedEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal limitAmount;

    @Column(nullable = false, name = "budget_year")
    private int year;

    @Column(nullable = false, name = "budget_month")
    private int month;
}
