package com.zorvyn.finance.repository;

import com.zorvyn.finance.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;
import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category, UUID> {
    // Used to check if a category already exists before the Admin creates it
    Optional<Category> findByNameIgnoreCase(String name);

    // Used to show the user only "Active" categories in the dropdown
    List<Category> findAllByActiveTrue();
}