package com.zorvyn.finance.repository;

import com.zorvyn.finance.model.FinancialRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface FinancialRecordRepository extends JpaRepository<FinancialRecord, UUID>, JpaSpecificationExecutor<FinancialRecord> {
    Optional<FinancialRecord> findByDisplayId(String displayId);
    List<FinancialRecord> findAllByCreator_Email(String email);
    Page<FinancialRecord> findAllByCreator_Email(String email, Pageable pageable);
    Optional<FinancialRecord> findByDisplayIdAndCreator_Email(String displayId, String email);

    @org.springframework.data.jpa.repository.Query("SELECT SUM(f.amount) FROM FinancialRecord f WHERE f.creator = :user AND f.type = 'EXPENSE' AND f.transactionDate >= :startDate AND f.transactionDate < :endDate AND f.category = :category")
    java.math.BigDecimal sumExpensesByCategory(
        @org.springframework.data.repository.query.Param("user") com.zorvyn.finance.model.User user,
        @org.springframework.data.repository.query.Param("startDate") java.time.LocalDateTime startDate,
        @org.springframework.data.repository.query.Param("endDate") java.time.LocalDateTime endDate,
        @org.springframework.data.repository.query.Param("category") com.zorvyn.finance.model.Category category
    );

    @org.springframework.data.jpa.repository.Query("SELECT SUM(f.amount) FROM FinancialRecord f WHERE f.creator = :user AND f.type = 'EXPENSE' AND f.transactionDate >= :startDate AND f.transactionDate < :endDate")
    java.math.BigDecimal sumTotalExpenses(
        @org.springframework.data.repository.query.Param("user") com.zorvyn.finance.model.User user,
        @org.springframework.data.repository.query.Param("startDate") java.time.LocalDateTime startDate,
        @org.springframework.data.repository.query.Param("endDate") java.time.LocalDateTime endDate
    );

    @Query("SELECT SUM(f.amount) FROM FinancialRecord f WHERE f.isDeleted = false")
    BigDecimal sumAllTransactions();

    @Query("SELECT f.category, COUNT(f), SUM(f.amount) FROM FinancialRecord f WHERE f.isDeleted = false GROUP BY f.category")
    List<Object[]> getCategoryWiseStats();

    @Query("SELECT f.creator.email, SUM(f.amount) FROM FinancialRecord f WHERE f.type = 'EXPENSE' AND f.isDeleted = false GROUP BY f.creator.email HAVING SUM(f.amount) > :threshold")
    List<Object[]> findHighSpenders(BigDecimal threshold);

    @Query("SELECT SUM(f.amount) FROM FinancialRecord f " +
            "WHERE f.isDeleted = false AND f.transactionDate >= :start AND f.transactionDate < :end")
    BigDecimal sumVolumeInRange(
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );

    @Query("SELECT AVG(f.amount) FROM FinancialRecord f WHERE f.creator.id = :userId AND f.type = 'EXPENSE' AND f.isDeleted = false")
    BigDecimal getAverageExpenseForUser(@Param("userId") UUID userId);

    List<FinancialRecord> findAllByTransactionDateAfterAndIsDeletedFalse(LocalDateTime date);

    @Query("SELECT f.category.name, SUM(f.amount) FROM FinancialRecord f " +
            "WHERE f.creator.id = :userId " +
            "AND f.type = 'EXPENSE' " +
            "AND f.isDeleted = false " +
            "AND f.transactionDate >= :startDate " +
            "GROUP BY f.category.name")
    List<Object[]> getSpendingByCategoryCustom(
            @Param("userId") UUID userId,
            @Param("startDate") LocalDateTime startDate
    );

}
