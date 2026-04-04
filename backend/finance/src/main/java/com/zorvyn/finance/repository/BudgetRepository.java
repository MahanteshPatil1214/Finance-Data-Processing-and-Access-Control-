package com.zorvyn.finance.repository;

import com.zorvyn.finance.model.Budget;
import com.zorvyn.finance.model.Category;
import com.zorvyn.finance.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BudgetRepository extends JpaRepository<Budget, Long> {
    List<Budget> findByUserAndYearAndMonth(User user, int year, int month);
    Optional<Budget> findByUserAndCategoryAndYearAndMonth(User user, Category category, int year, int month);
    Optional<Budget> findByDisplayId(String displayId);
}
