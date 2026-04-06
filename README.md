# Zorvyn - Financial Data Processing & Access Control System

**Zorvyn** is a robust, production-grade financial analytics backend built with **Spring Boot 3**. It bridges the gap between simple transaction logging and actionable business intelligence, featuring **automated anomaly detection**, **role-based security (RBAC)**, and **high-performance data aggregation**.

---

## 🌟 Key Technical Features

### 🛡️ Enterprise-Grade Security (RBAC)
Implemented **Spring Security 6** with **JWT (JSON Web Tokens)** to enforce strict data boundaries:
* **ADMIN:** Full system authority (User management, record CRUD).
* **ANALYST:** Access to platform-wide trends and automated risk alerts.
* **VIEWER:** Personal dashboard access with filtered private data.

### 🧠 Intelligent Anomaly Engine
A custom-built risk detection layer that monitors user spending patterns:
* **Behavioral Analysis:** Compares current transactions against historical averages for each specific user.
* **Automated Flagging:** Identifies "Spikes" exceeding a **500% variance**, providing Analysts with detailed hike percentages and risk justifications.

### 📊 Financial Insights & Aggregation
* **Category Breakdown:** Real-time spending maps (e.g., FOOD, RENT, BILLS) generated via optimized **JPQL `GROUP BY`** queries.
* **Growth Analytics:** High-level metrics for Analysts calculating Month-over-Month (MoM) transaction volume and platform growth.

---
## 👥 Implemented User Features & Permissions
The system enforces strict **Method-Level Security** using `@PreAuthorize`. Below are the features implemented for each role:

### 🛠️ Administrator (ADMIN)
* **User Lifecycle Management:** Register, promote/demote roles, and activate/deactivate user accounts.
* **Master Data Control:** Full CRUD access to all financial records and categories across the entire platform.
* **System Auditing:** View all transactions globally to ensure data integrity.

### 🔍 Financial Analyst (ANALYST)
* **Anomaly Detection:** Access to the "Risk Dashboard" to view transactions flagged by the Anomaly Engine.
* **Platform Trends:** View Month-over-Month (MoM) growth metrics and total platform transaction volume.
* **Market Insights:** Identify "High Spenders" based on custom-defined financial thresholds.

### 👤 Standard User (VIEWER)
* **Personal Dashboard:** View a private history of their own financial transactions.
* **Spending Map:** Real-time Category Breakdown (e.g., Food vs. Rent) generated specifically for their account.
* **Profile Management:** Securely access and update personal identity details.
---
## 🏗️ Architecture Design

Zorvyn follows a **Clean Layered Architecture**:

1.  **Controller Layer:** REST API management and DTO mapping.
2.  **Service Layer:** Core business logic (Anomaly math, Date windowing).
3.  **Repository Layer:** High-performance DB interaction with custom JPQL.
4.  **Security Layer:** JWT filtering and Method-Level `@PreAuthorize` guards.

---

## 🛠️ Technology Stack

* **Core:** Java 21, Spring Boot 3.4.0
* **Security:** Spring Security (JWT), Password Hashing (BCrypt)
* **Data:** Spring Data JPA, H2/PostgreSQL
* **DevOps:** Docker (Multi-stage builds)
* **Documentation:** Swagger UI / OpenAPI 3.0

---

## 🚀 Getting Started

### Prerequisites
* **Docker Desktop** (Recommended)
* **Java 21** & **Maven** (For local development)

### Running with Docker
1.  **Clone the repository:**
    ```bash
    git clone [https://github.com/MahanteshPatil1214/Finance-Data-Processing-and-Access-Control-.git](https://github.com/MahanteshPatil1214/Finance-Data-Processing-and-Access-Control-.git)
    cd Finance-Data-Processing-and-Access-Control-
    ```
2.  **Build the portable image:**
    ```bash
    docker build -t zorvyn-backend .
    ```
3.  **Run the container:**
    ```bash
    docker run -p 8080:8080 zorvyn-backend
    ```
### 🔑 Default Credentials (Testing)
The system automatically initializes the following users if the database is empty:

| Email | Password | Role |
| :--- | :--- | :--- |
| `admin@zorvyn.com` | `admin@123` | **ADMIN** |
| `analyst@zorvyn.com` | `analyst123` | **ANALYST** |
| `viewer@zorvyn.com` | `viewer123` | **VIEWER** |

### API Documentation
Once running, explore and test all endpoints via Swagger UI:
`http://localhost:8080/swagger-ui/index.html`

---

## 🧪 Testing & Demo (SQL Seed Data)

Since the H2 database starts empty, use these SQL commands in the **H2 Console** (`http://localhost:8080/h2-console`) to populate data and verify the analytics logic.

### 1. Add Monthly Spending (Verify Category Breakdown)
```sql
INSERT INTO financial_records (id, display_id, amount, type, transaction_date, category_id, user_id, is_deleted, created_at, updated_at) 
VALUES 
(RANDOM_UUID(), 'TXN-001', 300.00, 'EXPENSE', CURRENT_TIMESTAMP, (SELECT id FROM categories WHERE name = 'FOOD' LIMIT 1), (SELECT id FROM users WHERE email = 'admin@zorvyn.com' LIMIT 1), FALSE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(RANDOM_UUID(), 'TXN-002', 1200.00, 'EXPENSE', CURRENT_TIMESTAMP, (SELECT id FROM categories WHERE name = 'RENT' LIMIT 1), (SELECT id FROM users WHERE email = 'admin@zorvyn.com' LIMIT 1), FALSE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
```

### 2. Test Anomaly Engine (Spending Spike Alert)
```sql
-- Add a normal 'Baseline' transaction
INSERT INTO financial_records (id, display_id, amount, type, transaction_date, category_id, user_id, is_deleted, created_at, updated_at) 
VALUES (RANDOM_UUID(), 'BASE-02', 10000.00, 'EXPENSE', CURRENT_TIMESTAMP, (SELECT id FROM categories WHERE name = 'FOOD' LIMIT 1), (SELECT id FROM users WHERE email = 'admin@zorvyn.com' LIMIT 1), FALSE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Add the 'Spike' (> 500% variance)
INSERT INTO financial_records (id, display_id, amount, type, transaction_date, category_id, user_id, is_deleted, created_at, updated_at) 
VALUES (RANDOM_UUID(), 'SPIKE-02', 15000.00, 'EXPENSE', CURRENT_TIMESTAMP, (SELECT id FROM categories WHERE name = 'RENT' LIMIT 1), (SELECT id FROM users WHERE email = 'admin@zorvyn.com' LIMIT 1), FALSE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
```
---
* Developed by Mahantesh Patil