# Design Splitwise

A **Spring Boot backend implementation of a Splitwise-like expense-sharing system**.

The application allows users to create expenses, split expenses among multiple users using different splitting strategies, track individual shares, and record settlements when users pay each other.

The project demonstrates **layered architecture**, **dependency injection**, and the **Strategy Pattern** with a strategy factory for handling different expense-splitting algorithms.

---

## Table of Contents

* [Overview](#overview)
* [Features](#features)
* [Tech Stack](#tech-stack)
* [Architecture](#architecture)
* [Project Structure](#project-structure)
* [Domain Model](#domain-model)
* [Expense Splitting](#expense-splitting)
* [Strategy Pattern](#strategy-pattern)
* [Strategy Factory](#strategy-factory)
* [Settlement](#settlement)
* [Settlement API](#settlement-api)
* [Complete Example](#complete-example)
* [Layer Responsibilities](#layer-responsibilities)
* [Dependency Injection](#dependency-injection)
* [Design Decisions](#design-decisions)
* [Production Improvements](#production-improvements)
* [Future Enhancements](#future-enhancements)

---

## Overview

In a group expense-sharing application, one user may pay the entire amount for an expense while multiple users are responsible for different portions of that expense.

For example, Rahul, Amit, and Priya have dinner.

Rahul pays:

```text
₹900
```

With an equal split:

```text
Rahul → ₹300
Amit  → ₹300
Priya → ₹300
```

Since Rahul paid the restaurant, Amit and Priya owe Rahul their respective shares.

If Amit later pays Rahul ₹200, the application records that payment as a **settlement** and updates Amit's outstanding amount.

The core flow is:

```text
Expense
   ↓
Split
   ↓
Outstanding Amount
   ↓
Settlement
```

---

## Features

* User management
* Expense management
* Multiple expense participants
* Equal expense splitting
* Unequal expense splitting
* Percentage-based expense splitting
* Individual user share tracking
* Settled amount tracking
* Settlement between users
* Settlement history
* REST API using Spring Boot
* Layered application architecture
* Strategy-based split calculation

---

## Tech Stack

* **Java**
* **Spring Boot**
* **Spring Data JPA**
* **REST API**
* **Maven**
* **Relational Database**
* **JPA/Hibernate**

---

# Architecture

The project follows a standard layered architecture.

```text
                    Client
                      |
                      | HTTP Request
                      ↓
              +----------------+
              |   Controller   |
              +-------+--------+
                      |
                      ↓
              +----------------+
              |    Service     |
              +-------+--------+
                      |
                      ↓
              +----------------+
              |   Repository   |
              +-------+--------+
                      |
                      ↓
                   Database
```

For expense splitting, the service layer uses different strategies:

```text
                  ExpenseService
                        |
                        ↓
              SplitStrategyFactory
                        |
          +-------------+-------------+
          |             |             |
          ↓             ↓             ↓
       Equal         Unequal      Percentage
      Strategy       Strategy       Strategy
          |             |             |
          +-------------+-------------+
                        |
                        ↓
                      Splits
```

---

# Project Structure

The project is organized into separate packages based on responsibility.

```text
com.example.designsplitwise
│
├── controller
│   └── SettlementController
│
├── dto
│   └── SettlementRequest
│
├── model
│   ├── User
│   ├── Expense
│   ├── Split
│   ├── Settlement
│   └── SplitType
│
├── repository
│   ├── UserRepository
│   ├── ExpenseRepository
│   ├── SplitRepository
│   └── SettlementRepository
│
├── service
│   ├── UserService
│   ├── ExpenseService
│   ├── SplitService
│   └── SettlementService
│
└── strategy
    ├── SplitStrategy
    ├── SplitStrategyFactory
    ├── EqualSplitStrategy
    ├── UnequalSplitStrategy
    └── PercentageSplitStrategy
```

---

# Domain Model

The main domain entities are:

```text
User
Expense
Split
Settlement
```

The relationship between them can be represented as:

```text
User
  |
  | participates in
  ↓
Expense
  |
  | divided into
  ↓
Split
  |
  | creates outstanding balance
  ↓
Debt
  |
  | reduced through
  ↓
Settlement
```

---

## User

A `User` represents a person using the application.

Users can:

* Create expenses
* Participate in expenses
* Pay for expenses
* Owe money to other users
* Settle outstanding balances

---

## Expense

An `Expense` represents the original spending.

For example:

```text
Expense
--------------------
Description = Dinner
Amount      = ₹900
Paid By     = Rahul
Split Type  = EQUAL
```

The expense represents the original transaction where money was spent.

---

## Split

A `Split` represents one user's share of an expense.

For example:

```text
Expense = ₹900

Rahul → ₹300
Amit  → ₹300
Priya → ₹300
```

Each user can have an individual `Split`.

The split also tracks the amount already settled.

```text
amount        = ₹300
settledAmount = ₹200
```

The remaining amount is:

```text
remainingAmount = amount - settledAmount

remainingAmount = ₹300 - ₹200
                = ₹100
```

Therefore:

> `Split` represents the current state of a user's obligation for an expense.

---

## Settlement

A `Settlement` represents an actual payment between two users.

For example:

```text
Amit → ₹200 → Rahul
```

A settlement stores information such as:

```text
amount
expense
paidBy
paidTo
```

Therefore:

> `Settlement` represents the history of payments made to reduce outstanding debt.

---

# Expense Splitting

The application supports three split types.

```text
EQUAL
UNEQUAL
PERCENTAGE
```

Each type requires a different calculation algorithm.

---

## Equal Split

The expense is divided equally among all participants.

Example:

```text
Total Expense = ₹900
Participants  = 3
```

Calculation:

```text
₹900 / 3 = ₹300
```

Result:

```text
Rahul → ₹300
Amit  → ₹300
Priya → ₹300
```

Implemented using:

```text
EqualSplitStrategy
```

---

## Unequal Split

Each participant is assigned a specific amount.

Example:

```text
Total Expense = ₹1,000

Rahul → ₹500
Amit  → ₹300
Priya → ₹200
```

The total split should equal the expense:

```text
₹500 + ₹300 + ₹200 = ₹1,000
```

Implemented using:

```text
UnequalSplitStrategy
```

---

## Percentage Split

Each participant is assigned a percentage of the total expense.

Example:

```text
Total Expense = ₹1,000

Rahul → 50%
Amit  → 30%
Priya → 20%
```

The strategy calculates:

```text
Rahul → ₹500
Amit  → ₹300
Priya → ₹200
```

The percentages should total:

```text
50% + 30% + 20% = 100%
```

Implemented using:

```text
PercentageSplitStrategy
```

---

# Strategy Pattern

The project uses the **Strategy Pattern** to represent different ways of calculating expense splits.

The common abstraction is:

```text
SplitStrategy
```

with multiple implementations:

```text
                  SplitStrategy
                       |
          +------------+------------+
          |            |            |
          ↓            ↓            ↓
       Equal        Unequal     Percentage
      Strategy      Strategy      Strategy
```

Each strategy contains its own splitting algorithm.

### Why use Strategy Pattern?

Without the Strategy Pattern, the expense service could become filled with conditional logic:

```text
if splitType == EQUAL
    calculate equal split

else if splitType == UNEQUAL
    calculate unequal split

else if splitType == PERCENTAGE
    calculate percentage split
```

The Strategy Pattern moves these algorithms into separate classes.

This provides:

* Better separation of responsibilities
* Easier maintenance
* Easier testing
* Easier addition of new split types
* Reduced conditional logic

---

# Strategy Factory

The project also contains:

```text
SplitStrategyFactory
```

The factory selects and creates the appropriate strategy based on `SplitType`.

Conceptually:

```text
SplitType
    |
    ↓
SplitStrategyFactory
    |
    +── EQUAL      → EqualSplitStrategy
    |
    +── UNEQUAL    → UnequalSplitStrategy
    |
    +── PERCENTAGE → PercentageSplitStrategy
```

The implementation is based on:

```java
public static SplitStrategy getSplitStrategy(SplitType splitType) {
    return switch (splitType) {
        case EQUAL -> new EqualSplitStrategy();
        case UNEQUAL -> new UnequalSplitStrategy();
        case PERCENTAGE -> new PercentageSplitStrategy();
        default -> throw new IllegalArgumentException(
            "Unknown split type: " + splitType
        );
    };
}
```

The responsibilities are therefore:

```text
Factory
→ Decides which strategy to create.

Strategy
→ Performs the actual split calculation.
```

> The factory is part of the application's runtime flow when `SplitStrategyFactory.getSplitStrategy()` is invoked by the expense/split processing code.

---

# Settlement

Settlement is the process of recording a payment that reduces an existing debt.

Suppose:

```text
Amit owes Rahul ₹500
```

Amit pays:

```text
₹300
```

After settlement:

```text
Original Amount = ₹500
Settled Amount  = ₹300
Remaining       = ₹200
```

The application performs two important operations:

1. Updates the corresponding `Split`.
2. Creates a `Settlement` record.

---

## Settlement Flow

```text
Settlement Request
        |
        ↓
SettlementController
        |
        ↓
SettlementService
        |
        ↓
Find User's Split
        |
        ↓
Update settledAmount
        |
        ↓
Save Split
        |
        ↓
Create Settlement
        |
        ↓
Save Settlement
```

---

# Settlement API

## Settle an Expense

### Endpoint

```http
POST /api/v1/splitwise/settle
```

### Request

Example:

```json
{
  "expenseId": 10,
  "paidByUserId": 2,
  "paidToUserId": 1,
  "amount": 300
}
```

This represents:

```text
User 2 pays ₹300 to User 1
for Expense 10
```

---

## Controller

The settlement endpoint is exposed through:

```java
@RestController
@RequestMapping("/api/v1/splitwise")
public class SettlementController
```

The endpoint:

```java
@PostMapping("/settle")
public ResponseEntity<?> settle(
        @RequestBody SettlementRequest settlementRequest) {

    String message =
        settlementService.settle(settlementRequest);

    return new ResponseEntity<>(message, HttpStatus.OK);
}
```

The controller is intentionally kept thin.

It:

1. Receives the HTTP request.
2. Converts the request body into `SettlementRequest`.
3. Delegates the operation to `SettlementService`.
4. Returns the response.

---

# Settlement Service

`SettlementService` coordinates the settlement operation.

It uses:

```text
SettlementRepository
UserService
ExpenseService
SplitService
```

The settlement process is:

```text
1. Find the user's Split
2. Calculate the new settled amount
3. Save the updated Split
4. Retrieve the Expense
5. Retrieve the payer
6. Retrieve the receiver
7. Create Settlement
8. Save Settlement
```

The current implementation updates the settled amount using:

```java
double newSettledAmount =
    split.getSettledAmount()
        + settlementRequest.getAmount();

split.setSettledAmount(newSettledAmount);
splitService.saveSplit(split);
```

It then creates the settlement record:

```java
Settlement settlement = new Settlement();

settlement.setAmount(settlementRequest.getAmount());
settlement.setExpense(
    expenseService.getExpenseById(
        settlementRequest.getExpenseId()
    )
);
settlement.setPaidBy(
    userService.getUser(
        settlementRequest.getPaidByUserId()
    )
);
settlement.setPaidTo(
    userService.getUser(
        settlementRequest.getPaidToUserId()
    )
);

settlementRepository.save(settlement);
```

---

# Split vs Settlement

This is an important distinction in the system.

### Split

Represents:

> How much the user owes and how much has already been settled.

Example:

```text
Original Amount = ₹500
Settled Amount  = ₹300
Remaining       = ₹200
```

### Settlement

Represents:

> An individual payment transaction.

Example:

```text
Amit paid Rahul ₹300
```

Multiple settlements can exist for one split:

```text
Original Debt = ₹500

Settlement 1 = ₹100
Settlement 2 = ₹200
Settlement 3 = ₹200

Total Settled = ₹500
Remaining     = ₹0
```

---

# Complete Example

Consider three users:

```text
Rahul
Amit
Priya
```

Rahul pays:

```text
₹900
```

for dinner.

## Step 1 — Expense

```text
Expense
----------------
Amount  = ₹900
PaidBy  = Rahul
Type    = EQUAL
```

## Step 2 — Split

The equal strategy calculates:

```text
₹900 / 3 = ₹300
```

Splits:

```text
Rahul → ₹300
Amit  → ₹300
Priya → ₹300
```

Since Rahul paid the entire bill:

```text
Amit  owes Rahul ₹300
Priya owes Rahul ₹300
```

## Step 3 — Settlement

Amit pays Rahul:

```text
₹200
```

The split becomes:

```text
Original Amount = ₹300
Settled Amount  = ₹200
Remaining       = ₹100
```

A settlement record is created:

```text
PaidBy = Amit
PaidTo = Rahul
Amount = ₹200
```

## Step 4 — Final Settlement

Amit later pays:

```text
₹100
```

Now:

```text
Original Amount = ₹300
Settled Amount  = ₹300
Remaining       = ₹0
```

Amit's debt is completely settled.

---

# Layer Responsibilities

## Controller

Responsible for:

* HTTP endpoints
* Request handling
* Response handling

Example:

```text
SettlementController
```

---

## DTO

Responsible for transferring data between the client and application.

Example:

```text
SettlementRequest
```

---

## Service

Responsible for:

* Business logic
* Coordinating operations
* Calling repositories
* Applying business rules

Examples:

```text
UserService
ExpenseService
SplitService
SettlementService
```

---

## Repository

Responsible for persistence and database operations.

Examples:

```text
UserRepository
ExpenseRepository
SplitRepository
SettlementRepository
```

---

## Model

Contains the application's domain objects.

Examples:

```text
User
Expense
Split
Settlement
SplitType
```

---

## Strategy

Contains the algorithms used for calculating expense splits.

Examples:

```text
EqualSplitStrategy
UnequalSplitStrategy
PercentageSplitStrategy
```

---

# Dependency Injection

The application uses Spring's dependency injection.

For example, `SettlementService` receives its dependencies through its constructor:

```java
@Autowired
public SettlementService(
    SettlementRepository settlementRepository,
    UserService userService,
    ExpenseService expenseService,
    SplitService splitService
) {
    this.settlementRepository = settlementRepository;
    this.userService = userService;
    this.expenseService = expenseService;
    this.splitService = splitService;
}
```

This is **constructor-based dependency injection**.

Spring creates the required objects and injects them into the service.

Benefits include:

* Loose coupling
* Easier testing
* Clear dependencies
* Better maintainability

---

# Design Principles

The project demonstrates several important software design principles.

## Single Responsibility Principle

Different components have different responsibilities:

```text
Controller  → HTTP handling
Service     → Business logic
Repository  → Persistence
Strategy    → Split calculation
Factory     → Strategy selection
```

---

## Open/Closed Principle

The Strategy Pattern makes the split calculation extensible.

For example, a future split type could introduce:

```text
ExactAmountSplitStrategy
```

without rewriting the existing strategy implementations.

---

## Separation of Concerns

The project separates:

```text
API
Business Logic
Persistence
Domain Models
Algorithms
```

This keeps individual components easier to understand and maintain.

---

# Production Improvements

The current implementation demonstrates the core functionality. A production-ready system could improve it further.

## Use BigDecimal for Money

The current implementation uses `double` for monetary amounts.

For financial applications, `BigDecimal` should generally be preferred to avoid floating-point precision issues.

```text
BigDecimal amount
```

---

## Validate Settlements

A user should not be able to settle more than their outstanding balance.

The validation should ensure:

```text
settlementAmount
    <=
amount - settledAmount
```

Example:

```text
Original Debt = ₹500
Already Paid  = ₹400
Remaining     = ₹100
```

A settlement of ₹200 should be rejected.

---

## Validate Split Amounts

For unequal splits:

```text
Sum of split amounts = Expense amount
```

For percentage splits:

```text
Sum of percentages = 100%
```

---

## Use Transactions

Settlement updates multiple pieces of data:

```text
Update Split
     +
Create Settlement
```

These operations should ideally be atomic using a transaction.

For example:

```java
@Transactional
```

This prevents situations where the split is updated but the settlement record fails to save.

---

## Exception Handling

The application can introduce custom exceptions such as:

```text
UserNotFoundException
ExpenseNotFoundException
SplitNotFoundException
InvalidSettlementException
InvalidSplitException
```

Centralized exception handling can then return consistent API responses.

---

## Request Validation

DTOs can be validated for:

* Required IDs
* Positive amounts
* Valid percentages
* Valid split values

---

# Future Enhancements

Potential future features include:

* User registration and authentication
* User groups
* Group expenses
* Expense history
* Balance summary between users
* Simplified debt settlement
* Notifications
* Expense categories
* Multiple currencies
* Pagination
* Search and filtering
* Global exception handling
* Unit and integration tests
* API documentation using Swagger/OpenAPI

---

# Key Design Patterns

### Strategy Pattern

Used for different expense-splitting algorithms.

```text
SplitStrategy
     |
     +── EqualSplitStrategy
     +── UnequalSplitStrategy
     +── PercentageSplitStrategy
```

### Factory

`SplitStrategyFactory` selects/creates the required split strategy based on `SplitType`.

```text
SplitType
   ↓
SplitStrategyFactory
   ↓
SplitStrategy
```

### Dependency Injection

Spring injects services and repositories through constructors.

### Layered Architecture

The application separates:

```text
Controller
    ↓
Service
    ↓
Repository
    ↓
Database
```

---

# Core System Flow

The complete business flow can be summarized as:

```text
                User
                  |
                  ↓
               Expense
                  |
                  ↓
             Split Type
                  |
                  ↓
        SplitStrategyFactory
                  |
       +----------+----------+
       |          |          |
       ↓          ↓          ↓
     Equal     Unequal   Percentage
       |          |          |
       +----------+----------+
                  |
                  ↓
                Split
                  |
                  ↓
          Outstanding Debt
                  |
                  ↓
             Settlement
                  |
                  ↓
          Settlement History
```

---

# Conclusion

This project demonstrates the core backend design of a Splitwise-like application using **Spring Boot**.

The key concepts are:

```text
Expense
   ↓
Split
   ↓
Debt
   ↓
Settlement
```

and:

```text
SplitType
   ↓
Strategy Factory
   ↓
Split Strategy
   ↓
Split Calculation
```

The project focuses on keeping responsibilities separated, making split calculations extensible, and maintaining a clear distinction between **current debt state (`Split`)** and **payment history (`Settlement`)**.
