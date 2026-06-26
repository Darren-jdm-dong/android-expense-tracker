# Implementation Plan: Android Smart Expense Tracker

**Branch**: `001-android-expense-tracker` | **Date**: 2026-06-24 | **Spec**: [spec.md](./spec.md)

**Input**: Feature specification from `/specs/001-android-expense-tracker/spec.md`

## Summary

Build an Android expense tracking app with manual entry, visual reports,
budget management, smart auto-categorization, and recurring expenses.
The app follows a local-first architecture with all data stored on-device
using SQLite. The UI is built with Jetpack Compose for a modern, reactive
experience. Smart categorization uses a simple keyword-matching approach
with a merchant mapping table that learns from user behavior.

## Technical Context

**Language/Version**: Kotlin 2.0+, targeting JVM 17

**Primary Dependencies**:
- Jetpack Compose (UI framework)
- Room (local database abstraction)
- Hilt (dependency injection)
- MPAndroidChart (charting for reports)
- WorkManager (recurring expense scheduling)
- Navigation Compose (screen navigation)
- Material 3 (design system)
- DataStore (key-value storage for preferences)

**Storage**: SQLite via Room (local-only, no cloud sync for v1)

**Testing**:
- JUnit 5 (unit tests)
- MockK (mocking)
- Compose Testing (UI tests)
- Robolectric (Android unit tests without emulator)
- Espresso (integration/UI tests on device)

**Target Platform**: Android 10+ (API level 29 minimum, API 35 target)

**Project Type**: mobile-app (Android native)

**Performance Goals**:
- Expense entry completes in <10 seconds (user-facing)
- Report rendering in <1 second for 1 year of data
- App cold start in <2 seconds

**Constraints**:
- <100MB memory footprint
- Fully offline-capable (no network required for any core feature)
- APK size <15MB
- Support for devices with 2GB+ RAM

**Scale/Scope**:
- Single user, personal device
- Expected data volume: ~10,000 expenses over 5 years of use
- 5 user stories, ~14 functional requirements
- Simplified Chinese UI with CNY default currency

## Constitution Check

*GATE: Must pass before Phase 0 research. Re-check after Phase 1 design.*

| Principle | Status | Evidence |
|-----------|--------|----------|
| I. User-Centric Design | ✅ PASS | Spec contains 5 user stories with acceptance scenarios in Given/When/Then format, all prioritized P1-P5 |
| II. Test-Driven Development | ✅ PASS | Plan includes TDD workflow: unit tests first, then implementation. Testing stack defined (JUnit 5, MockK, Compose Testing) |
| III. API-First Architecture | N/A | Mobile app with no external API surface. No backend service in scope. |
| IV. Security by Default | ✅ PASS | FR-014 requires device-level authentication. Local storage means no network attack surface. Data at rest protected by Android's file-based encryption. |
| V. Simplicity (YAGNI) | ✅ PASS | v1 scope is deliberately constrained: no cloud sync, no bank integration, no receipt scanning. Simple keyword-matching for categorization rather than ML. |

**Gate Result**: PASS — all applicable principles satisfied.

## Project Structure

### Documentation (this feature)

```text
specs/001-android-expense-tracker/
├── plan.md              # This file (/speckit-plan command output)
├── research.md          # Phase 0 output (/speckit-plan command)
├── data-model.md        # Phase 1 output (/speckit-plan command)
├── quickstart.md        # Phase 1 output (/speckit-plan command)
├── contracts/           # Phase 1 output (/speckit-plan command)
└── tasks.md             # Phase 2 output (/speckit-tasks command - NOT created by /speckit-plan)
```

### Source Code (repository root)

```text
app/
├── src/
│   ├── main/
│   │   ├── java/com/example/expense/
│   │   │   ├── App.kt                    # Application class
│   │   │   ├── MainActivity.kt           # Entry point
│   │   │   ├── data/
│   │   │   │   ├── db/
│   │   │   │   │   ├── AppDatabase.kt    # Room database definition
│   │   │   │   │   ├── dao/              # Data Access Objects
│   │   │   │   │   │   ├── ExpenseDao.kt
│   │   │   │   │   │   ├── CategoryDao.kt
│   │   │   │   │   │   ├── BudgetDao.kt
│   │   │   │   │   │   ├── RecurringExpenseDao.kt
│   │   │   │   │   │   └── MerchantMappingDao.kt
│   │   │   │   │   └── entity/           # Room entities
│   │   │   │   │       ├── ExpenseEntity.kt
│   │   │   │   │       ├── CategoryEntity.kt
│   │   │   │   │       ├── BudgetEntity.kt
│   │   │   │   │       ├── RecurringExpenseEntity.kt
│   │   │   │   │       └── MerchantMappingEntity.kt
│   │   │   │   └── repository/           # Repository implementations
│   │   │   │       ├── ExpenseRepository.kt
│   │   │   │       ├── CategoryRepository.kt
│   │   │   │       ├── BudgetRepository.kt
│   │   │   │       ├── RecurringExpenseRepository.kt
│   │   │   │       └── MerchantMappingRepository.kt
│   │   │   ├── domain/
│   │   │   │   ├── model/                # Domain models
│   │   │   │   │   ├── Expense.kt
│   │   │   │   │   ├── Category.kt
│   │   │   │   │   ├── Budget.kt
│   │   │   │   │   └── RecurringExpense.kt
│   │   │   │   └── usecase/              # Business logic
│   │   │   │       ├── AddExpenseUseCase.kt
│   │   │   │       ├── GetExpensesUseCase.kt
│   │   │   │       ├── GetReportUseCase.kt
│   │   │   │       ├── SetBudgetUseCase.kt
│   │   │   │       ├── CheckBudgetUseCase.kt
│   │   │   │       ├── CategorizeExpenseUseCase.kt
│   │   │   │       └── ManageRecurringUseCase.kt
│   │   │   ├── ui/
│   │   │   │   ├── theme/                # Material 3 theme
│   │   │   │   │   ├── Color.kt
│   │   │   │   │   ├── Type.kt
│   │   │   │   │   └── Theme.kt
│   │   │   │   ├── navigation/           # Navigation graph
│   │   │   │   │   └── AppNavigation.kt
│   │   │   │   ├── home/                 # Home screen
│   │   │   │   │   ├── HomeScreen.kt
│   │   │   │   │   └── HomeViewModel.kt
│   │   │   │   ├── add/                  # Add expense screen
│   │   │   │   │   ├── AddExpenseScreen.kt
│   │   │   │   │   └── AddExpenseViewModel.kt
│   │   │   │   ├── report/               # Report screen
│   │   │   │   │   ├── ReportScreen.kt
│   │   │   │   │   └── ReportViewModel.kt
│   │   │   │   ├── budget/               # Budget screen
│   │   │   │   │   ├── BudgetScreen.kt
│   │   │   │   │   └── BudgetViewModel.kt
│   │   │   │   └── settings/             # Settings screen
│   │   │   │       ├── SettingsScreen.kt
│   │   │   │       └── SettingsViewModel.kt
│   │   │   └── di/                       # Hilt modules
│   │   │       ├── AppModule.kt
│   │   │       ├── DatabaseModule.kt
│   │   │       └── RepositoryModule.kt
│   │   └── res/
│   │       ├── values/                   # Strings, colors, themes
│   │       │   ├── strings.xml           # Chinese strings
│   │       │   └── themes.xml
│   │       └── drawable/                 # Icons, vectors
│   └── test/                             # Unit tests
│       └── java/com/example/expense/
│           ├── domain/
│           │   └── usecase/
│           │       ├── AddExpenseUseCaseTest.kt
│           │       ├── GetReportUseCaseTest.kt
│           │       ├── CheckBudgetUseCaseTest.kt
│           │       └── CategorizeExpenseUseCaseTest.kt
│           └── data/
│               └── repository/
│                   ├── ExpenseRepositoryTest.kt
│                   └── BudgetRepositoryTest.kt
├── build.gradle.kts                      # App-level build config
└── proguard-rules.pro

build.gradle.kts                          # Project-level build config
settings.gradle.kts                       # Project settings
```

**Structure Decision**: Single Android app with clean architecture layers
(data → domain → ui). The data layer uses Room for persistence, the domain
layer contains use cases with business logic, and the UI layer uses
Jetpack Compose with ViewModels. This structure follows Android's
recommended app architecture guidelines.

## Complexity Tracking

> No constitution violations to justify. All principles satisfied.
