# Data Model: Android Smart Expense Tracker

**Date**: 2026-06-24
**Feature**: Android Smart Expense Tracker
**Spec**: [spec.md](./spec.md)

## Entities

### Expense

A single spending record.

| Field | Type | Required | Description |
|-------|------|----------|-------------|
| id | Long | Auto | Primary key, auto-generated |
| amount | Decimal(12,2) | Yes | Expense amount (max 99,999,999.99) |
| categoryId | Long | Yes | Foreign key → Category.id |
| date | Date | Yes | Date of expense (YYYY-MM-DD) |
| note | String(200) | No | User note (e.g., "午餐") |
| merchantName | String(100) | No | Merchant/keyword for categorization |
| currency | String(3) | Yes | ISO 4217 currency code (default: "CNY") |
| createdAt | DateTime | Yes | Record creation timestamp |
| updatedAt | DateTime | Yes | Last modification timestamp |

**Validation Rules**:
- amount > 0 AND amount <= 99,999,999.99
- categoryId must reference an existing Category
- date must not be in the future (beyond today)
- note max 200 characters
- currency must be a valid ISO 4217 code

**Relationships**:
- Many-to-one with Category (via categoryId)
- May originate from RecurringExpense (via recurringExpenseId)

---

### Category

A classification for expenses.

| Field | Type | Required | Description |
|-------|------|----------|-------------|
| id | Long | Auto | Primary key, auto-generated |
| name | String(50) | Yes | Display name (e.g., "餐饮") |
| icon | String(50) | Yes | Icon identifier or emoji |
| color | String(7) | Yes | Hex color (e.g., "#FF5722") |
| isDefault | Boolean | Yes | true for system-provided categories |
| parentId | Long | No | Parent category for hierarchy |
| sortOrder | Int | Yes | Display order (lower = first) |
| createdAt | DateTime | Yes | Record creation timestamp |

**Default Categories** (seeded on first launch):

| name | icon | color | sortOrder |
|------|------|-------|-----------|
| 餐饮 | 🍽️ | #FF5722 | 1 |
| 交通 | 🚗 | #2196F3 | 2 |
| 购物 | 🛒 | #9C27B0 | 3 |
| 住房 | 🏠 | #4CAF50 | 4 |
| 娱乐 | 🎮 | #FF9800 | 5 |
| 医疗 | 🏥 | #F44336 | 6 |
| 教育 | 📚 | #00BCD4 | 7 |
| 其他 | 📦 | #607D8B | 8 |

**Validation Rules**:
- name must be unique (case-insensitive)
- color must be valid hex (#RRGGBB)
- parentId must reference an existing Category (if set)
- Cannot delete default categories; can hide them

**Relationships**:
- Self-referencing hierarchy (via parentId)
- One-to-many with Expense
- One-to-one with Budget (optional)

---

### Budget

A spending limit for a category in a given month.

| Field | Type | Required | Description |
|-------|------|----------|-------------|
| id | Long | Auto | Primary key, auto-generated |
| categoryId | Long | Yes | Foreign key → Category.id (unique per month) |
| amount | Decimal(12,2) | Yes | Budget limit amount |
| yearMonth | String(7) | Yes | Format: "YYYY-MM" |
| createdAt | DateTime | Yes | Record creation timestamp |
| updatedAt | DateTime | Yes | Last modification timestamp |

**Validation Rules**:
- amount > 0
- yearMonth must be valid format "YYYY-MM"
- Unique constraint on (categoryId, yearMonth)
- Only one budget per category per month

**Relationships**:
- Many-to-one with Category (via categoryId)
- Current spending is calculated (not stored) from Expense records

**Derived Fields** (calculated at query time):
- `spentAmount`: SUM of Expense.amount for this category in this month
- `remainingAmount`: budget.amount - spentAmount
- `usagePercent`: (spentAmount / budget.amount) * 100
- `isExceeded`: spentAmount > budget.amount
- `isWarning`: usagePercent >= 80

---

### RecurringExpense

A template for automatically creating expenses on a schedule.

| Field | Type | Required | Description |
|-------|------|----------|-------------|
| id | Long | Auto | Primary key, auto-generated |
| amount | Decimal(12,2) | Yes | Expense amount |
| categoryId | Long | Yes | Foreign key → Category.id |
| note | String(200) | No | Note for generated expenses |
| currency | String(3) | Yes | ISO 4217 currency code |
| frequency | Enum | Yes | DAILY, WEEKLY, MONTHLY |
| dayOfMonth | Int | No | Day of month (1-31) for MONTHLY |
| dayOfWeek | Int | No | Day of week (1-7) for WEEKLY |
| startDate | Date | Yes | First occurrence date |
| endDate | Date | No | Stop date (null = indefinite) |
| isActive | Boolean | Yes | true to continue generating |
| lastGeneratedDate | Date | No | Last date an expense was created |
| createdAt | DateTime | Yes | Record creation timestamp |
| updatedAt | DateTime | Yes | Last modification timestamp |

**Validation Rules**:
- amount > 0
- For MONTHLY: dayOfMonth required (1-28 to avoid month-end issues)
- For WEEKLY: dayOfWeek required (1=Monday, 7=Sunday)
- startDate must be in the past or today
- endDate must be after startDate (if set)

**Relationships**:
- Many-to-one with Category (via categoryId)
- One-to-many with Expense (generated expenses reference this)

---

### MerchantMapping

A learned association between a keyword/merchant and a category.

| Field | Type | Required | Description |
|-------|------|----------|-------------|
| id | Long | Auto | Primary key, auto-generated |
| keyword | String(100) | Yes | Merchant name or keyword (lowercased) |
| categoryId | Long | Yes | Foreign key → Category.id |
| matchCount | Int | Yes | Number of times user confirmed this mapping |
| lastUsedAt | DateTime | Yes | Last time this mapping was used |
| createdAt | DateTime | Yes | Record creation timestamp |

**Validation Rules**:
- keyword must be non-empty, stored lowercase
- Unique constraint on (keyword, categoryId)
- matchCount >= 1

**Confidence Calculation**:
- Total matches for keyword: SUM(matchCount) across all categories for this keyword
- Confidence for top category: topMatchCount / totalMatches
- Auto-select threshold: confidence >= 0.8 AND matchCount >= 3
- Suggest threshold: confidence >= 0.5

**Relationships**:
- Many-to-one with Category (via categoryId)

---

## Entity Relationship Diagram

```text
┌─────────────────┐       ┌─────────────────┐
│    Category      │       │     Budget      │
├─────────────────┤       ├─────────────────┤
│ id (PK)         │◄──┐   │ id (PK)         │
│ name            │   │   │ categoryId (FK) │──┐
│ icon            │   │   │ amount          │  │
│ color           │   │   │ yearMonth       │  │
│ isDefault       │   │   └─────────────────┘  │
│ parentId (FK)───┼───┤                        │
│ sortOrder       │   │   ┌─────────────────┐  │
└─────────────────┘   │   │    Expense      │  │
                      │   ├─────────────────┤  │
                      ├───│ categoryId (FK) │◄─┘
                      │   │ id (PK)         │
                      │   │ amount          │
                      │   │ date            │
                      │   │ note            │
                      │   │ merchantName    │
                      │   │ currency        │
                      │   └─────────────────┘
                      │
                      │   ┌─────────────────────┐
                      │   │  RecurringExpense    │
                      │   ├─────────────────────┤
                      └───│ categoryId (FK)      │
                          │ id (PK)              │
                          │ amount               │
                          │ frequency            │
                          │ startDate            │
                          │ endDate              │
                          └─────────────────────┘

┌─────────────────────┐
│  MerchantMapping     │
├─────────────────────┤
│ id (PK)             │
│ keyword             │
│ categoryId (FK)─────┼──→ Category.id
│ matchCount          │
│ lastUsedAt          │
└─────────────────────┘
```

## State Transitions

### Expense Lifecycle

```text
[Created] → [Edited] → [Deleted]
```

- Expenses are soft-deleted (marked as deleted, not physically removed)
- Deleted expenses are excluded from reports but preserved for data integrity

### RecurringExpense Lifecycle

```text
[Active] → [Paused] → [Active]
[Active] → [Completed] (endDate reached)
[Active] → [Cancelled] (user cancels)
```

- Active: Generates expenses on schedule
- Paused: Temporarily stopped, can resume
- Completed: endDate reached, no more generation
- Cancelled: User-initiated stop, past expenses preserved

### Budget Lifecycle

```text
[Within Budget] → [Warning (80%)] → [Exceeded (100%)]
```

- State is calculated dynamically from expense totals
- Resets at the start of each month (yearMonth changes)
