[README.md](https://github.com/user-attachments/files/34964164/README.md)
# Expense Tracker API (Spring Boot + MySQL)

## 📌 Project Overview

Expense Tracker API is a RESTful backend built with Spring Boot that lets users register, log in, and manage their daily expenses. It provides expense CRUD operations, search, spending analytics, budget tracking, monthly reports and CSV export, backed by a MySQL database.

This project is a migration of my earlier console-based application, [Expense-Tracker-Java-MySQL](https://github.com/Summu12322/Expense-Tracker-Java-MySQL), from plain Java + JDBC to a layered Spring Boot architecture using Spring Data JPA.

---

## 🔄 What Changed from the Console Version

| Console version | Spring Boot version |
|---|---|
| `Scanner` menu in `Main.java` | REST controllers (`@RestController`) |
| JDBC + hand-written SQL in DAO classes | Spring Data JPA repositories |
| `DBConnection` utility | `application.properties` + auto-configured DataSource |
| SHA-256 password hashing | BCrypt (salted) password hashing |
| `amount DOUBLE`, duplicate `date` / `expense_date` columns | `DECIMAL(10,2)` amounts, single `expense_date` column |
| Input validation with `Scanner` checks | Bean Validation (`@Valid`) on request DTOs |
| `printStackTrace()` | Global exception handling with JSON error responses |

---

## 🚀 Features

### User Management
* User registration
* User login
* BCrypt password hashing

### Expense Management
* Add, view, update and delete expenses
* Search by title or category
* Per-user data isolation (a user can only access their own expenses)

### Analytics & Reporting
* Total spending
* Category-wise spending
* Budget status (spent, remaining, % used, exceeded flag)
* Dashboard summary (count, total, highest, average)
* Monthly expense report

### Export
* Download all expenses as a CSV file

### API Quality
* DTO-based requests and responses (entities and passwords are never exposed)
* Request validation with clear error messages
* Centralised exception handling with consistent JSON errors

---

## 🛠 Technologies Used

* Java 21
* Spring Boot
* Spring Web (REST)
* Spring Data JPA / Hibernate
* MySQL
* Bean Validation
* Spring Security Crypto (BCrypt)
* Maven
* Eclipse IDE (Spring Tools)
* Git & GitHub

---

## 📂 Project Structure

```text
src/main/java/com/sumant/expensetracker
│
├── ExpensetrackerApplication.java
│
├── controller
│   ├── AuthController.java
│   └── ExpenseController.java
│
├── service
│   ├── ExpenseService.java
│   └── UserService.java
│
├── repository
│   ├── ExpenseRepository.java
│   └── UserRepository.java
│
├── entity
│   ├── Expense.java
│   └── User.java
│
├── dto
│   ├── ExpenseRequest / ExpenseResponse
│   ├── RegisterRequest / LoginRequest / UserResponse
│   ├── CategoryTotalResponse
│   ├── DashboardSummaryResponse
│   └── BudgetStatusResponse
│
├── exception
│   ├── GlobalExceptionHandler.java
│   ├── ErrorResponse.java
│   └── ResourceNotFound / DuplicateUsername / InvalidCredentials exceptions
│
└── config
    └── PasswordConfig.java
```

---

## 🗄 Database Schema

### users

| Column   | Type         | Constraints          |
| -------- | ------------ | -------------------- |
| id       | INT          | PK, AUTO_INCREMENT   |
| username | VARCHAR(100) | UNIQUE, NOT NULL     |
| password | VARCHAR(255) | NOT NULL (BCrypt)    |

### expenses

| Column       | Type          | Constraints              |
| ------------ | ------------- | ------------------------ |
| id           | INT           | PK, AUTO_INCREMENT       |
| title        | VARCHAR(255)  | NOT NULL                 |
| amount       | DECIMAL(10,2) | NOT NULL                 |
| category     | VARCHAR(100)  |                          |
| expense_date | DATE          |                          |
| user_id      | INT           | FK → users(id), NOT NULL |

---

## ▶ How to Run

1. Clone the repository:

```bash
git clone https://github.com/Summu12322/expense-tracker-springboot.git
cd expense-tracker-springboot
```

2. Make sure MySQL is running locally. The database `expense_tracker_v2` is created automatically (`createDatabaseIfNotExist=true`), and Hibernate creates the tables on startup (`spring.jpa.hibernate.ddl-auto=update`).

3. Create `src/main/resources/application-local.properties` with your real MySQL password. This file is listed in `.gitignore` and is never committed:

```properties
spring.datasource.password=YOUR_PASSWORD
```

If your MySQL username is not `root`, change `spring.datasource.username` in `application.properties`.

4. Run the application:

```bash
./mvnw spring-boot:run
```

Or run `ExpensetrackerApplication` from your IDE. The API starts on `http://localhost:8081`.

---

## 📡 API Endpoints

> **Note:** `userId` is currently passed as a query parameter. It will be replaced by JWT-based authentication (see Roadmap).

### Authentication

| Method | Endpoint             | Description        |
| ------ | -------------------- | ------------------ |
| POST   | `/api/auth/register` | Register a user    |
| POST   | `/api/auth/login`    | Log in             |

### Expenses

| Method | Endpoint                               | Description                  |
| ------ | -------------------------------------- | ---------------------------- |
| POST   | `/api/expenses`                        | Add an expense               |
| GET    | `/api/expenses`                        | List expenses (newest first) |
| PUT    | `/api/expenses/{id}`                   | Update an expense            |
| DELETE | `/api/expenses/{id}`                   | Delete an expense            |
| GET    | `/api/expenses/search?keyword=`        | Search by title or category  |
| GET    | `/api/expenses/summary/total`          | Total spending               |
| GET    | `/api/expenses/summary/category`       | Category-wise spending       |
| GET    | `/api/expenses/summary/budget?budget=` | Budget status                |
| GET    | `/api/expenses/summary/dashboard`      | Dashboard summary            |
| GET    | `/api/expenses/report/monthly`         | Monthly report (`year`, `month`) |
| GET    | `/api/expenses/export`                 | Download CSV report          |

### Example Requests

**Register**

```http
POST /api/auth/register
Content-Type: application/json

{ "username": "sumant", "password": "secret123" }
```

**Add expense**

```http
POST /api/expenses?userId=1
Content-Type: application/json

{
  "title": "Lunch",
  "amount": 250.50,
  "category": "Food",
  "expenseDate": "2026-11-14"
}
```

**Error response format**

```json
{
  "status": 404,
  "message": "Expense not found",
  "timestamp": "2026-11-14T18:30:00"
}
```

---

## 🔐 Security

* Passwords are hashed with BCrypt before being stored.
* Every expense query is scoped by `user_id`, so users cannot read, edit or delete each other's data.
* Responses use DTOs, so password hashes are never returned.

---

## 🗺 Roadmap

- [ ] Spring Security + JWT authentication (replace the `userId` parameter)
- [ ] Unit tests with JUnit and Mockito
- [ ] Swagger / OpenAPI documentation
- [ ] Pagination for expense listing
- [ ] Docker support and cloud deployment
- [ ] Flyway database migrations

---

## 👨‍💻 Author

**Sumant Singh Mankotia**

Computer Science Engineer

GitHub: https://github.com/Summu12322
