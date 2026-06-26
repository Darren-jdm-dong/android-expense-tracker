# Tasks: Android Smart Expense Tracker

**Input**: Design documents from `/specs/001-android-expense-tracker/`

**Prerequisites**: plan.md (required), spec.md (required), data-model.md, contracts/, research.md, quickstart.md

**Tests**: Test tasks are included per constitution requirement (TDD - NON-NEGOTIABLE)

**Organization**: Tasks are grouped by user story to enable independent implementation and testing of each story.

## Format: `[ID] [P?] [Story] Description`

- **[P]**: Can run in parallel (different files, no dependencies)
- **[Story]**: Which user story this task belongs to (e.g., US1, US2, US3)
- Include exact file paths in descriptions

## Path Conventions

- **Single Android project**: `app/src/main/java/com/example/expense/`, `app/src/test/java/com/example/expense/`

---

## Phase 1: Setup (Shared Infrastructure)

**Purpose**: Project initialization and basic structure

- [x] T001 Create Android project structure with Gradle Kotlin DSL at `settings.gradle.kts` and `build.gradle.kts`
- [x] T002 Configure app-level dependencies in `app/build.gradle.kts` (Compose, Room, Hilt, Navigation, Material 3, DataStore, WorkManager, MPAndroidChart)
- [x] T003 [P] Configure Material 3 theme in `app/src/main/java/com/example/expense/ui/theme/Theme.kt`, `Color.kt`, `Type.kt`
- [x] T004 [P] Create Application class with Hilt setup in `app/src/main/java/com/example/expense/App.kt`
- [x] T005 [P] Create MainActivity entry point in `app/src/main/java/com/example/expense/MainActivity.kt`
- [x] T006 [P] Add Chinese string resources in `app/src/main/res/values/strings.xml`

---

## Phase 2: Foundational (Blocking Prerequisites)

**Purpose**: Core data layer that MUST be complete before ANY user story can be implemented

**⚠️ CRITICAL**: No user story work can begin until this phase is complete

- [x] T007 [P] Create ExpenseEntity Room entity in `app/src/main/java/com/example/expense/data/db/entity/ExpenseEntity.kt`
- [x] T008 [P] Create CategoryEntity Room entity in `app/src/main/java/com/example/expense/data/db/entity/CategoryEntity.kt`
- [x] T009 [P] Create BudgetEntity Room entity in `app/src/main/java/com/example/expense/data/db/entity/BudgetEntity.kt`
- [x] T010 [P] Create RecurringExpenseEntity Room entity in `app/src/main/java/com/example/expense/data/db/entity/RecurringExpenseEntity.kt`
- [x] T011 [P] Create MerchantMappingEntity Room entity in `app/src/main/java/com/example/expense/data/db/entity/MerchantMappingEntity.kt`
- [x] T012 Create AppDatabase with all entities and migrations in `app/src/main/java/com/example/expense/data/db/AppDatabase.kt`
- [x] T013 [P] Create ExpenseDao in `app/src/main/java/com/example/expense/data/db/dao/ExpenseDao.kt`
- [x] T014 [P] Create CategoryDao in `app/src/main/java/com/example/expense/data/db/dao/CategoryDao.kt`
- [x] T015 [P] Create BudgetDao in `app/src/main/java/com/example/expense/data/db/dao/BudgetDao.kt`
- [x] T016 [P] Create RecurringExpenseDao in `app/src/main/java/com/example/expense/data/db/dao/RecurringExpenseDao.kt`
- [x] T017 [P] Create MerchantMappingDao in `app/src/main/java/com/example/expense/data/db/dao/MerchantMappingDao.kt`
- [x] T018 [P] Create Expense domain model in `app/src/main/java/com/example/expense/domain/model/Expense.kt`
- [x] T019 [P] Create Category domain model in `app/src/main/java/com/example/expense/domain/model/Category.kt`
- [x] T020 [P] Create Budget domain model in `app/src/main/java/com/example/expense/domain/model/Budget.kt`
- [x] T021 [P] Create RecurringExpense domain model in `app/src/main/java/com/example/expense/domain/model/RecurringExpense.kt`
- [x] T022 Create ExpenseRepository in `app/src/main/java/com/example/expense/data/repository/ExpenseRepository.kt`
- [x] T023 Create CategoryRepository in `app/src/main/java/com/example/expense/data/repository/CategoryRepository.kt`
- [x] T024 Create default category seed data with 8 categories (餐饮, 交通, 购物, 住房, 娱乐, 医疗, 教育, 其他)
- [x] T025 Create Hilt DatabaseModule in `app/src/main/java/com/example/expense/di/DatabaseModule.kt`
- [x] T026 Create Hilt RepositoryModule in `app/src/main/java/com/example/expense/di/RepositoryModule.kt`
- [x] T027 Create Hilt AppModule in `app/src/main/java/com/example/expense/di/AppModule.kt`

**Checkpoint**: Foundation ready - user story implementation can now begin in parallel

---

## Phase 3: User Story 1 - Record Daily Expenses (Priority: P1) 🎯 MVP

**Goal**: Users can quickly record expenses with amount, category, date, and note. Expenses appear in a chronological list grouped by date.

**Independent Test**: Add an expense with amount 50.00, category "餐饮", note "午餐", verify it appears in today's expense list. Swipe left to edit/delete.

### Tests for User Story 1 ⚠️

> **NOTE: Write these tests FIRST, ensure they FAIL before implementation**

- [x] T028 [P] [US1] Unit test for AddExpenseUseCase in `app/src/test/java/com/example/expense/domain/usecase/AddExpenseUseCaseTest.kt`
- [x] T029 [P] [US1] Unit test for GetExpensesUseCase in `app/src/test/java/com/example/expense/domain/usecase/GetExpensesUseCaseTest.kt`
- [x] T030 [P] [US1] Unit test for ExpenseRepository in `app/src/test/java/com/example/expense/data/repository/ExpenseRepositoryTest.kt`

### Implementation for User Story 1

- [x] T031 [P] [US1] Create AddExpenseUseCase in `app/src/main/java/com/example/expense/domain/usecase/AddExpenseUseCase.kt`
- [x] T032 [P] [US1] Create GetExpensesUseCase in `app/src/main/java/com/example/expense/domain/usecase/GetExpensesUseCase.kt`
- [x] T033 [US1] Create HomeViewModel in `app/src/main/java/com/example/expense/ui/home/HomeViewModel.kt`
- [x] T034 [US1] Create HomeScreen with expense list grouped by date in `app/src/main/java/com/example/expense/ui/home/HomeScreen.kt`
- [x] T035 [US1] Create AddExpenseViewModel in `app/src/main/java/com/example/expense/ui/add/AddExpenseViewModel.kt`
- [x] T036 [US1] Create AddExpenseScreen with amount, category, date, note fields in `app/src/main/java/com/example/expense/ui/add/AddExpenseScreen.kt`
- [x] T037 [US1] Implement swipe-to-edit/delete on expense list items
- [x] T038 [US1] Create AppNavigation graph with Home and AddExpense routes in `app/src/main/java/com/example/expense/ui/navigation/AppNavigation.kt`
- [x] T039 [US1] Wire up Hilt injection for HomeViewModel and AddExpenseViewModel

**Checkpoint**: User Story 1 fully functional - can add, view, edit, delete expenses

---

## Phase 4: User Story 2 - View Spending Reports (Priority: P2)

**Goal**: Users can see visual summaries of spending patterns with pie chart (by category) and line chart (daily trend), with custom date range filtering.

**Independent Test**: Add expenses across 3+ categories, open report view, verify pie chart shows correct category breakdown. Switch to trend tab, verify line chart shows daily totals.

### Tests for User Story 2 ⚠️

> **NOTE: Write these tests FIRST, ensure they FAIL before implementation**

- [x] T040 [P] [US2] Unit test for GetReportUseCase in `app/src/test/java/com/example/expense/domain/usecase/GetReportUseCaseTest.kt`

### Implementation for User Story 2

- [x] T041 [US2] Create GetReportUseCase (category totals, daily totals) in `app/src/main/java/com/example/expense/domain/usecase/GetReportUseCase.kt`
- [x] T042 [US2] Create ReportViewModel in `app/src/main/java/com/example/expense/ui/report/ReportViewModel.kt`
- [x] T043 [US2] Create ReportScreen with pie chart and line chart tabs in `app/src/main/java/com/example/expense/ui/report/ReportScreen.kt`
- [x] T044 [US2] Integrate MPAndroidChart for pie chart (category breakdown) and line chart (daily trend)
- [x] T045 [US2] Implement custom date range picker for report filtering
- [x] T046 [US2] Add category detail drill-down (tap pie segment → list expenses)
- [x] T047 [US2] Add Reports tab to navigation

**Checkpoint**: User Story 2 fully functional - visual reports with charts and date filtering

---

## Phase 5: User Story 3 - Set and Track Budgets (Priority: P3)

**Goal**: Users can set monthly budgets per category, see spending progress, and receive warnings at 80% threshold.

**Independent Test**: Set budget of 2000 for "餐饮", add expenses totaling 1600, verify warning notification appears. Verify budget resets on new month.

### Tests for User Story 3 ⚠️

> **NOTE: Write these tests FIRST, ensure they FAIL before implementation**

- [x] T048 [P] [US3] Unit test for SetBudgetUseCase in `app/src/test/java/com/example/expense/domain/usecase/SetBudgetUseCaseTest.kt`
- [x] T049 [P] [US3] Unit test for CheckBudgetUseCase in `app/src/test/java/com/example/expense/domain/usecase/CheckBudgetUseCaseTest.kt`

### Implementation for User Story 3

- [x] T050 [P] [US3] Create BudgetRepository in `app/src/main/java/com/example/expense/data/repository/BudgetRepository.kt`
- [x] T051 [P] [US3] Create SetBudgetUseCase in `app/src/main/java/com/example/expense/domain/usecase/SetBudgetUseCase.kt`
- [x] T052 [P] [US3] Create CheckBudgetUseCase (80% warning logic) in `app/src/main/java/com/example/expense/domain/usecase/CheckBudgetUseCase.kt`
- [x] T053 [US3] Create BudgetViewModel in `app/src/main/java/com/example/expense/ui/budget/BudgetViewModel.kt`
- [x] T054 [US3] Create BudgetScreen with category budget list and progress bars in `app/src/main/java/com/example/expense/ui/budget/BudgetScreen.kt`
- [x] T055 [US3] Implement budget warning notification using Android NotificationManager
- [x] T056 [US3] Add budget progress indicator to HomeScreen (current month spending vs budget)
- [x] T057 [US3] Add Budget tab to navigation

**Checkpoint**: User Story 3 fully functional - budget tracking with warnings

---

## Phase 6: User Story 4 - Smart Auto-Categorization (Priority: P4)

**Goal**: App auto-suggests categories based on learned merchant/keyword patterns. After 3 consistent mappings, auto-selects category.

**Independent Test**: Type "星巴克" 3 times with category "餐饮", verify 4th time auto-selects "餐饮".

### Tests for User Story 4 ⚠️

> **NOTE: Write these tests FIRST, ensure they FAIL before implementation**

- [x] T058 [P] [US4] Unit test for CategorizeExpenseUseCase in `app/src/test/java/com/example/expense/domain/usecase/CategorizeExpenseUseCaseTest.kt`

### Implementation for User Story 4

- [x] T059 [US4] Create MerchantMappingRepository in `app/src/main/java/com/example/expense/data/repository/MerchantMappingRepository.kt`
- [x] T060 [US4] Create CategorizeExpenseUseCase (keyword matching + confidence) in `app/src/main/java/com/example/expense/domain/usecase/CategorizeExpenseUseCase.kt`
- [x] T061 [US4] Integrate category suggestion into AddExpenseScreen (show suggestion chip, auto-select at 80% confidence)
- [x] T062 [US4] Update AddExpenseUseCase to save MerchantMapping when expense is saved

**Checkpoint**: User Story 4 fully functional - smart categorization learns from user behavior

---

## Phase 7: User Story 5 - Recurring Expenses (Priority: P5)

**Goal**: Users can set up recurring expenses (daily/weekly/monthly) that auto-create on schedule with notification.

**Independent Test**: Create recurring expense "房租" ¥3000/monthly, verify it appears in recurring list and auto-creates on scheduled date.

### Tests for User Story 5 ⚠️

> **NOTE: Write these tests FIRST, ensure they FAIL before implementation**

- [x] T063 [P] [US5] Unit test for ManageRecurringUseCase in `app/src/test/java/com/example/expense/domain/usecase/ManageRecurringUseCaseTest.kt`

### Implementation for User Story 5

- [x] T064 [US5] Create RecurringExpenseRepository in `app/src/main/java/com/example/expense/data/repository/RecurringExpenseRepository.kt`
- [x] T065 [US5] Create ManageRecurringUseCase (create, cancel, generate) in `app/src/main/java/com/example/expense/domain/usecase/ManageRecurringUseCase.kt`
- [x] T066 [US5] Create RecurringExpenseWorker for WorkManager scheduling in `app/src/main/java/com/example/expense/data/worker/RecurringExpenseWorker.kt`
- [x] T067 [US5] Create RecurringExpenseScreen (list + create form) in `app/src/main/java/com/example/expense/ui/settings/RecurringExpenseScreen.kt`
- [x] T068 [US5] Implement WorkManager periodic task for recurring expense generation
- [x] T069 [US5] Add notification for auto-generated recurring expenses

**Checkpoint**: User Story 5 fully functional - recurring expenses auto-create on schedule

---

## Phase 8: Polish & Cross-Cutting Concerns

**Purpose**: Features that span multiple user stories or add finishing touches

- [x] T070 [P] Create SettingsScreen in `app/src/main/java/com/example/expense/ui/settings/SettingsScreen.kt`
- [x] T071 [P] Create SettingsViewModel in `app/src/main/java/com/example/expense/ui/settings/SettingsViewModel.kt`
- [x] T072 Implement CSV export functionality (export all expenses to Downloads)
- [x] T073 Implement device authentication (BiometricPrompt / device PIN) for app lock
- [x] T074 Add DataStore for user preferences (default currency, app lock enabled)
- [x] T075 [P] Add Settings tab to navigation
- [ ] T076 Run quickstart.md validation scenarios
- [ ] T077 Performance optimization (cold start, report rendering)
- [ ] T078 Final APK size audit (<15MB target)

---

## Phase 9: Convergence

**Purpose**: Address gaps found during convergence assessment

- [x] T079 Add edit expense functionality to HomeScreen and create EditExpenseScreen per FR-010 (partial)
- [x] T080 Add currency selector UI in AddExpenseScreen to support multiple currencies per FR-013 (partial)
- [x] T081 Implement BiometricPrompt authentication for app lock per FR-014 (partial)
- [x] T082 Implement swipe-to-edit gesture on expense list items per US1/AC4 (partial)
- [ ] T083 Run quickstart.md validation scenarios and fix any issues per T076 (partial)
- [ ] T084 Profile and optimize cold start time and report rendering performance per T077 (partial)
- [ ] T085 Build release APK and verify size is under 15MB per T078 (partial)

---

## Dependencies & Execution Order

### Phase Dependencies

- **Setup (Phase 1)**: No dependencies - can start immediately
- **Foundational (Phase 2)**: Depends on Setup completion - BLOCKS all user stories
- **User Stories (Phase 3-7)**: All depend on Foundational phase completion
  - User stories can then proceed in parallel (if staffed)
  - Or sequentially in priority order (P1 → P2 → P3 → P4 → P5)
- **Polish (Phase 8)**: Depends on all user stories being complete

### User Story Dependencies

- **User Story 1 (P1)**: Can start after Foundational (Phase 2) - No dependencies on other stories
- **User Story 2 (P2)**: Can start after Foundational (Phase 2) - Reads expenses created by US1
- **User Story 3 (P3)**: Can start after Foundational (Phase 2) - Uses categories from US1
- **User Story 4 (P4)**: Can start after Foundational (Phase 2) - Enhances US1's add expense flow
- **User Story 5 (P5)**: Can start after Foundational (Phase 2) - Creates expenses like US1

### Within Each User Story

- Tests MUST be written and FAIL before implementation
- Models/use cases before ViewModels
- ViewModels before Screens
- Core implementation before integration
- Story complete before moving to next priority

### Parallel Opportunities

- All Setup tasks marked [P] can run in parallel (Phase 1)
- All entity creation tasks marked [P] can run in parallel (T007-T011)
- All DAO creation tasks marked [P] can run in parallel (T013-T017)
- All domain model tasks marked [P] can run in parallel (T018-T021)
- All test tasks within a story marked [P] can run in parallel
- User stories can run in parallel after Foundational phase completes

---

## Parallel Example: User Story 1

```bash
# Launch all tests for User Story 1 together:
Task: "T028 Unit test for AddExpenseUseCase"
Task: "T029 Unit test for GetExpensesUseCase"
Task: "T030 Unit test for ExpenseRepository"

# Launch all use cases for User Story 1 together (after tests fail):
Task: "T031 Create AddExpenseUseCase"
Task: "T032 Create GetExpensesUseCase"
```

---

## Implementation Strategy

### MVP First (User Story 1 Only)

1. Complete Phase 1: Setup (T001-T006)
2. Complete Phase 2: Foundational (T007-T027) - CRITICAL
3. Complete Phase 3: User Story 1 (T028-T039)
4. **STOP and VALIDATE**: Test expense recording independently
5. Deploy/demo if ready

### Incremental Delivery

1. Complete Setup + Foundational → Foundation ready
2. Add User Story 1 → Test independently → Deploy/Demo (MVP!)
3. Add User Story 2 → Test independently → Deploy/Demo
4. Add User Story 3 → Test independently → Deploy/Demo
5. Add User Story 4 → Test independently → Deploy/Demo
6. Add User Story 5 → Test independently → Deploy/Demo
7. Each story adds value without breaking previous stories

### Parallel Team Strategy

With multiple developers:

1. Team completes Setup + Foundational together
2. Once Foundational is done:
   - Developer A: User Story 1 (P1 - core expense recording)
   - Developer B: User Story 2 (P2 - reports, can use mock data)
   - Developer C: User Story 3 (P3 - budgets, can use mock data)
3. After US1 complete:
   - Developer A: User Story 4 (P4 - smart categorization)
   - Developer B: User Story 5 (P5 - recurring expenses)
   - Developer C: Polish phase

---

## Notes

- [P] tasks = different files, no dependencies
- [Story] label maps task to specific user story for traceability
- Each user story should be independently completable and testable
- Verify tests fail before implementing (TDD - constitution requirement)
- Commit after each task or logical group
- Stop at any checkpoint to validate story independently
- Avoid: vague tasks, same file conflicts, cross-story dependencies that break independence
