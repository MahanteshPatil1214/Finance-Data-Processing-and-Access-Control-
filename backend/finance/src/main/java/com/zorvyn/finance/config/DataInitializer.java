package com.zorvyn.finance.config;

import com.zorvyn.finance.model.Category;
import com.zorvyn.finance.model.FinancialRecord;
import com.zorvyn.finance.model.Role;
import com.zorvyn.finance.model.TransactionType;
import com.zorvyn.finance.model.User;
import com.zorvyn.finance.repository.CategoryRepository;
import com.zorvyn.finance.repository.FinancialRecordRepository;
import com.zorvyn.finance.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Component responsible for seeding the database with initial data upon application startup.
 * It initializes default categories, system users, and sample financial records
 * if the database is empty.
 */
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;

    private final FinancialRecordRepository financialRecordRepository;

    private final PasswordEncoder passwordEncoder;


    private final CategoryRepository categoryRepository;

    /**
     * Executes the data seeding logic on startup.
     * @param args incoming main method arguments
     */
    @Override
    @Transactional
    public void run(String... args) {
        // 1. ALWAYS initialize categories first!
        initializeCategories();

        if (userRepository.count() == 0) {
            User admin = createUser("admin@zorvyn.com", "admin@123", Role.ADMIN);
            createUser("analyst@zorvyn.com", "analyst123", Role.ANALYST);
            createUser("viewer@zorvyn.com", "viewer123", Role.VIEWER);

            if (financialRecordRepository.count() == 0) {
                // 2. Fetch the newly created Category entities from the DB
                Category salary = categoryRepository.findByNameIgnoreCase("SALARY")
                        .orElseThrow(() -> new RuntimeException("Category SALARY not found"));
                Category investments = categoryRepository.findByNameIgnoreCase("INVESTMENTS")
                        .orElseThrow(() -> new RuntimeException("Category INVESTMENTS not found"));
                Category others = categoryRepository.findByNameIgnoreCase("OTHERS")
                        .orElseThrow(() -> new RuntimeException("Category OTHERS not found"));
                Category rent = categoryRepository.findByNameIgnoreCase("RENT")
                        .orElseThrow(() -> new RuntimeException("Category RENT not found"));
                Category food = categoryRepository.findByNameIgnoreCase("FOOD")
                        .orElseThrow(() -> new RuntimeException("Category FOOD not found"));

                // 3. Create records using the fetched entities
                createRecord(admin, BigDecimal.valueOf(5000), TransactionType.INCOME, salary, "Monthly Salary", LocalDateTime.now().minusDays(5));
                createRecord(admin, BigDecimal.valueOf(1500), TransactionType.INCOME, investments, "Stock Dividends", LocalDateTime.now().minusDays(3));
                createRecord(admin, BigDecimal.valueOf(200), TransactionType.INCOME, others, "Gift", LocalDateTime.now().minusDays(2));

                createRecord(admin, BigDecimal.valueOf(1200), TransactionType.EXPENSE, rent, "Monthly Rent", LocalDateTime.now().minusDays(1));
                createRecord(admin, BigDecimal.valueOf(300), TransactionType.EXPENSE, food, "Groceries", LocalDateTime.now());
            }
        }
    }

    /**
     * Helper to create and persist a new user.
     * @param email The user's unique email address
     * @param password The raw password to be encoded
     * @param role The authorization role assigned to the user
     * @return The saved User entity
     */
    private User createUser(String email, String password, Role role) {
        User user = new User();
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setRole(role);
        user.setActive(true);
        return userRepository.save(user);
    }

    /**
     * Creates and persists a new financial record in the system.
     * * @param creator     The user responsible for creating this entry
     * @param amount      The monetary value of the transaction
     * @param type        The type of transaction (e.g., INCOME or EXPENSE)
     * @param category    The specific category entity associated with this record
     * @param description A brief note describing the transaction
     * @param date        The timestamp when the transaction occurred
     */
    private void createRecord(User creator, BigDecimal amount, TransactionType type, Category category, String description, LocalDateTime date) {
        FinancialRecord record = new FinancialRecord();
        record.setAmount(amount);
        record.setType(type);
        record.setCategory(category);
        record.setDescription(description);
        record.setTransactionDate(date);
        record.setCreator(creator);
        // displayId is handled in PrePersist hook of AbstractMappedEntity
        financialRecordRepository.save(record);
    }

    /**
     * Seeds the database with default financial categories if none exist.
     * This method must be executed before creating financial records to ensure
     * foreign key constraints are satisfied.
     */
    private void initializeCategories() {
        if (categoryRepository.count() == 0) {
            // Make sure these names match exactly what you look up above
            java.util.List<String> defaultNames = java.util.List.of("FOOD", "SALARY", "RENT", "INVESTMENTS", "OTHERS");
            defaultNames.forEach(name -> {
                Category category = new Category();
                category.setName(name);
                category.setActive(true);
                categoryRepository.save(category);
            });
        }
    }
}
