# Feature Specification: Android Smart Expense Tracker

**Feature Branch**: `001-android-expense-tracker`

**Created**: 2026-06-24

**Status**: Draft

**Input**: User description: "创建一个安卓智能记账app"

## User Scenarios & Testing *(mandatory)*

### User Story 1 - Record Daily Expenses (Priority: P1)

As a user, I want to quickly record my daily expenses so that I can track
where my money goes. The entry process must be fast enough to complete in
under 10 seconds so it doesn't interrupt my routine.

**Why this priority**: This is the core value proposition. Without the
ability to record expenses, no other feature matters.

**Independent Test**: Can be fully tested by manually adding an expense
with amount, category, and note, then verifying it appears in the expense
list.

**Acceptance Scenarios**:

1. **Given** the user is on the home screen, **When** they tap the "add
   expense" button and enter an amount of 50.00 with category "餐饮", **Then**
   the expense is saved and appears in today's expense list.
2. **Given** the user is adding an expense, **When** they select a date
   other than today, **Then** the expense is recorded under the selected
   date.
3. **Given** the user has entered an expense, **When** they add an optional
   note "午餐", **Then** the note is saved and visible alongside the expense.
4. **Given** the user makes a mistake, **When** they swipe left on an
   expense entry, **Then** they can edit or delete the entry.

---

### User Story 2 - View Spending Reports (Priority: P2)

As a user, I want to see visual summaries of my spending patterns so that
I can understand my financial habits and make informed decisions.

**Why this priority**: Reports transform raw data into actionable insights,
which is the key differentiator from a simple note-taking app.

**Independent Test**: Can be tested by adding several expenses across
different categories and dates, then verifying the reports show correct
totals and category breakdowns.

**Acceptance Scenarios**:

1. **Given** the user has recorded expenses this month, **When** they open
   the report view, **Then** they see a pie chart showing spending by
   category with percentages.
2. **Given** the user is viewing reports, **When** they switch to the
   "trend" tab, **Then** they see a line chart of daily spending over the
   past 30 days.
3. **Given** the user wants to compare periods, **When** they select a
   custom date range, **Then** the report updates to show only expenses
   within that range.
4. **Given** the user taps on a category in the pie chart, **When** the
   detail view opens, **Then** they see a list of all expenses in that
   category for the selected period.

---

### User Story 3 - Set and Track Budgets (Priority: P3)

As a user, I want to set monthly budgets for different categories so that
I can control my spending and receive warnings when I'm approaching limits.

**Why this priority**: Budgets add proactive financial management on top
of passive tracking, increasing the app's long-term value.

**Independent Test**: Can be tested by setting a budget of 2000 for "餐饮",
adding expenses totaling 1800, and verifying a warning notification appears.

**Acceptance Scenarios**:

1. **Given** the user opens budget settings, **When** they set a monthly
   budget of 5000 for "餐饮", **Then** the budget is saved and displayed
   on the home screen with current spending progress.
2. **Given** a category budget is set to 2000, **When** total spending in
   that category reaches 80% (1600), **Then** the user receives a warning
   notification.
3. **Given** a category budget is exceeded, **When** the user views the
   budget screen, **Then** the exceeded category is highlighted in red.
4. **Given** the month ends, **When** a new month begins, **Then** budget
   tracking resets automatically while preserving budget settings.

---

### User Story 4 - Smart Auto-Categorization (Priority: P4)

As a user, I want the app to automatically suggest categories based on
what I type so that I can record expenses even faster.

**Why this priority**: This is the "smart" differentiator. It reduces
friction and improves data quality over time.

**Independent Test**: Can be tested by typing "星巴克" and verifying the
app suggests "餐饮/咖啡" as the category.

**Acceptance Scenarios**:

1. **Given** the user types "星巴克" as a note, **When** the amount field
   is focused, **Then** the app pre-selects "餐饮" as the suggested
   category.
2. **Given** the user has categorized "星巴克" as "餐饮" three times
   before, **When** they type "星巴克" again, **Then** the app
   auto-selects "餐饮" without user action.
3. **Given** the user enters an unrecognized merchant, **When** they
   manually select a category, **Then** the app remembers this mapping
   for future entries.

---

### User Story 5 - Recurring Expenses (Priority: P5)

As a user, I want to set up recurring expenses (rent, subscriptions,
utilities) so that I don't have to manually enter them every month.

**Why this priority**: Automates repetitive entries, reducing the effort
needed to maintain accurate records.

**Independent Test**: Can be tested by creating a recurring expense for
"房租" of 3000 monthly, then verifying it auto-creates on the 1st of
each month.

**Acceptance Scenarios**:

1. **Given** the user creates a recurring expense, **When** they set
   "房租" to 3000/month starting from July 1, **Then** the expense is
   scheduled and will auto-create on the 1st of each month.
2. **Given** a recurring expense is due, **When** the scheduled date
   arrives, **Then** the expense is automatically added and the user
   receives a confirmation notification.
3. **Given** the user wants to stop a recurring expense, **When** they
   cancel it, **Then** future instances are not created but past entries
   remain unchanged.

---

### Edge Cases

- What happens when the user enters a negative amount? → The app MUST
  reject negative values and display a validation error.
- What happens when the user tries to add an expense without selecting a
  category? → The app MUST require a category selection before saving.
- How does the app handle very large amounts (e.g., 999,999,999)? → The
  app MUST accept amounts up to 99,999,999.99 and reject values above.
- What happens when the device is offline? → All core features MUST work
  fully offline with local data storage.
- What happens if the user clears app data? → The app MUST warn the user
  before clearing and offer an export option.

## Requirements *(mandatory)*

### Functional Requirements

- **FR-001**: System MUST allow users to record expenses with amount,
  category, date, and optional note.
- **FR-002**: System MUST provide a default set of expense categories
  (餐饮, 交通, 购物, 住房, 娱乐, 医疗, 教育, 其他) with ability to add
  custom categories.
- **FR-003**: System MUST display expenses in a chronological list grouped
  by date.
- **FR-004**: System MUST generate visual spending reports (pie chart by
  category, line chart by date trend).
- **FR-005**: System MUST support custom date range filtering for reports.
- **FR-006**: System MUST allow users to set monthly budgets per category.
- **FR-007**: System MUST notify users when category spending reaches 80%
  of the budget.
- **FR-008**: System MUST auto-suggest categories based on expense notes
  using learned patterns.
- **FR-009**: System MUST support recurring expense scheduling (daily,
  weekly, monthly).
- **FR-010**: System MUST allow editing and deleting existing expense
  entries.
- **FR-011**: System MUST store all data locally so core features work
  without internet connectivity.
- **FR-012**: System MUST support exporting expense data in a common
  format for backup or analysis.
- **FR-013**: System MUST support Chinese Yuan (CNY) as the default
  currency with ability to record expenses in other currencies.
- **FR-014**: System MUST protect user data with device-level
  authentication (PIN, fingerprint, or face unlock).

### Key Entities

- **Expense**: A single spending record — amount, category, date, note,
  optional merchant name, currency.
- **Category**: A classification for expenses — name, icon/color, budget
  limit (optional), parent category for hierarchy (optional).
- **Budget**: A spending limit — category, amount, period (monthly),
  current spending total.
- **RecurringExpense**: A template for repeated entries — amount,
  category, frequency, start date, end date (optional).
- **MerchantMapping**: A learned association — merchant/keyword text →
  preferred category, confidence score.

## Success Criteria *(mandatory)*

### Measurable Outcomes

- **SC-001**: Users can add a new expense in under 10 seconds from
  opening the app.
- **SC-002**: 90% of expenses are correctly auto-categorized after the
  user has recorded 50 entries.
- **SC-003**: Users can view a monthly spending report within 2 taps from
  the home screen.
- **SC-004**: Budget warnings are delivered within 1 minute of the
  spending threshold being crossed.
- **SC-005**: The app remains fully functional without internet
  connectivity for all core features.
- **SC-006**: 80% of users successfully record their first expense
  without needing a tutorial.
- **SC-007**: Users can export their complete expense history in under
  30 seconds.

## Assumptions

- Users are individuals managing personal finances, not businesses or
  teams.
- The primary market is Chinese-speaking users (UI in Simplified Chinese).
- The app targets Android 10 (API level 29) and above.
- Users have stable access to their own device (single-user, no account
  system needed for v1).
- Receipt photo scanning is out of scope for the initial version — it
  adds significant complexity and can be added later.
- Bank account integration is out of scope — manual entry provides
  better privacy and works offline.
- Cloud sync and multi-device support are out of scope for v1 —
  local-first storage ensures privacy and offline capability.
