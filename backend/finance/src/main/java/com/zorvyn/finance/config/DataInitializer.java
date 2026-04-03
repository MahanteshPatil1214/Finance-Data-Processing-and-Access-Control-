package com.zorvyn.finance.config;

import com.zorvyn.finance.model.Category;
import com.zorvyn.finance.model.FinancialRecord;
import com.zorvyn.finance.model.Role;
import com.zorvyn.finance.model.TransactionType;
import com.zorvyn.finance.model.User;
import com.zorvyn.finance.repository.FinancialRecordRepository;
import com.zorvyn.finance.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final FinancialRecordRepository financialRecordRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (userRepository.count() == 0) {
            User admin = createUser("admin@zorvyn.com", "admin@123", Role.ADMIN);
            User analyst = createUser("analyst@zorvyn.com", "analyst123", Role.ANALYST);
            User viewer = createUser("viewer@zorvyn.com", "viewer123", Role.VIEWER);

            if (financialRecordRepository.count() == 0) {
                createRecord(admin, BigDecimal.valueOf(5000), TransactionType.INCOME, Category.SALARY, "Monthly Salary", LocalDateTime.now().minusDays(5));
                createRecord(admin, BigDecimal.valueOf(1500), TransactionType.INCOME, Category.INVESTMENTS, "Stock Dividends", LocalDateTime.now().minusDays(3));
                createRecord(admin, BigDecimal.valueOf(200), TransactionType.INCOME, Category.OTHERS, "Gift", LocalDateTime.now().minusDays(2));
                
                createRecord(admin, BigDecimal.valueOf(1200), TransactionType.EXPENSE, Category.RENT_AND_BILLS, "Monthly Rent", LocalDateTime.now().minusDays(1));
                createRecord(admin, BigDecimal.valueOf(300), TransactionType.EXPENSE, Category.FOOD_AND_DINING, "Groceries", LocalDateTime.now());
            }
        }
    }

    private User createUser(String email, String password, Role role) {
        User user = new User();
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setRole(role);
        user.setActive(true);
        return userRepository.save(user);
    }

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
}
