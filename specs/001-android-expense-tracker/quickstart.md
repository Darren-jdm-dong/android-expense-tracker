# Quickstart Validation: Android Smart Expense Tracker

**Date**: 2026-06-24
**Feature**: Android Smart Expense Tracker
**Spec**: [spec.md](./spec.md)
**Plan**: [plan.md](./plan.md)

## Prerequisites

- Android Studio Hedgehog (2023.1.1) or later
- JDK 17
- Android SDK with API level 35
- Android device or emulator running Android 10+ (API 29+)

## Setup

1. Clone the repository
2. Open the project in Android Studio
3. Sync Gradle dependencies
4. Run on device or emulator

## Validation Scenarios

### Scenario 1: Record Daily Expense (US1 - P1)

**Purpose**: Verify core expense recording functionality.

**Steps**:
1. Launch the app
2. Tap the "+" button on the home screen
3. Enter amount: `50.00`
4. Select category: `餐饮`
5. Enter note: `午餐`
6. Tap "保存" (Save)

**Expected Result**:
- Expense appears in today's expense list on home screen
- Total for today updates to show ¥50.00
- Entry shows: ¥50.00 | 餐饮 | 午餐

**Validation Command**:
```bash
# Run UI test
./gradlew connectedAndroidTest -Pandroid.testInstrumentationRunnerArguments.class=com.example.expense.ui.AddExpenseTest
```

---

### Scenario 2: Edit and Delete Expense (US1 - P1)

**Purpose**: Verify expense modification capabilities.

**Steps**:
1. From Scenario 1, swipe left on the ¥50.00 expense
2. Tap "编辑" (Edit)
3. Change amount to `55.00`
4. Tap "保存" (Save)
5. Verify amount updated to ¥55.00
6. Swipe left again
7. Tap "删除" (Delete)
8. Confirm deletion

**Expected Result**:
- After edit: expense shows ¥55.00
- After delete: expense removed from list
- Today's total updates accordingly

---

### Scenario 3: View Spending Report (US2 - P2)

**Purpose**: Verify report generation with charts.

**Steps**:
1. Add expenses across multiple categories:
   - ¥100 for 餐饮
   - ¥50 for 交通
   - ¥200 for 购物
2. Navigate to "报表" (Reports) tab
3. View pie chart

**Expected Result**:
- Pie chart shows 3 segments:
  - 购物: ¥200 (57%)
  - 餐饮: ¥100 (29%)
  - 交通: ¥50 (14%)
- Tap on 购物 segment → shows ¥200 expense detail

**Validation Command**:
```bash
# Run report unit tests
./gradlew test --tests "com.example.expense.domain.usecase.GetReportUseCaseTest"
```

---

### Scenario 4: Set and Track Budget (US3 - P3)

**Purpose**: Verify budget warnings and tracking.

**Steps**:
1. Navigate to "预算" (Budget) tab
2. Set budget for 餐饮: ¥2000/month
3. Add expenses for 餐饮 totaling ¥1600
4. Check budget screen

**Expected Result**:
- Budget shows ¥1600/¥2000 (80%)
- Warning notification appears: "餐饮预算已达80%"
- Budget bar turns yellow/orange

**Validation Command**:
```bash
# Run budget unit tests
./gradlew test --tests "com.example.expense.domain.usecase.CheckBudgetUseCaseTest"
```

---

### Scenario 5: Smart Auto-Categorization (US4 - P4)

**Purpose**: Verify category suggestion from learned patterns.

**Steps**:
1. Add expense with note "星巴克", category 餐饮 → Save
2. Repeat 2 more times (total 3 entries for 星巴克 → 餐饮)
3. Add new expense, type "星巴克" in note field

**Expected Result**:
- After 1st entry: no auto-suggestion
- After 2nd entry: suggestion appears (非自动选择)
- After 3rd entry: 餐饮 auto-selected as category

**Validation Command**:
```bash
# Run categorization unit tests
./gradlew test --tests "com.example.expense.domain.usecase.CategorizeExpenseUseCaseTest"
```

---

### Scenario 6: Recurring Expense (US5 - P5)

**Purpose**: Verify recurring expense scheduling.

**Steps**:
1. Navigate to "设置" (Settings) → "定期支出" (Recurring Expenses)
2. Create recurring expense:
   - Amount: ¥3000
   - Category: 住房
   - Note: 房租
   - Frequency: 每月 (Monthly)
   - Day: 1
3. Wait for next month (or manually trigger WorkManager)

**Expected Result**:
- Recurring expense appears in list
- On 1st of month: ¥3000 expense auto-created
- Notification: "已自动记录: 房租 ¥3000"

**Validation Command**:
```bash
# Run recurring expense unit tests
./gradlew test --tests "com.example.expense.domain.usecase.ManageRecurringUseCaseTest"
```

---

### Scenario 7: Export Data (FR-012)

**Purpose**: Verify CSV export functionality.

**Steps**:
1. Add several expenses
2. Navigate to "设置" (Settings) → "导出数据" (Export Data)
3. Select date range: 全部 (All)
4. Tap "导出" (Export)

**Expected Result**:
- CSV file created in Downloads folder
- File contains all expenses with headers: 日期,金额,分类,备注,货币
- File opens correctly in Excel/Google Sheets

---

### Scenario 8: Offline Functionality (FR-011)

**Purpose**: Verify all features work without internet.

**Steps**:
1. Enable airplane mode on device
2. Perform Scenarios 1-6

**Expected Result**:
- All features work identically
- No error messages about connectivity
- Data persists after app restart

---

### Scenario 9: Device Authentication (FR-014)

**Purpose**: Verify app protection with device lock.

**Steps**:
1. Enable app lock in Settings
2. Close app
3. Reopen app

**Expected Result**:
- BiometricPrompt or device PIN dialog appears
- App content hidden until authentication succeeds
- Authentication failure keeps app locked

---

## Performance Validation

### Cold Start Time

**Target**: <2 seconds

**Measurement**:
```bash
adb shell am start -W com.example.expense/.MainActivity
```

**Expected Output**:
```
TotalTime: <2000
```

### Report Rendering

**Target**: <1 second for 1 year of data (~365 expenses)

**Steps**:
1. Seed database with 365 test expenses
2. Open report view
3. Measure time from tap to chart display

**Validation Command**:
```bash
# Run performance benchmark
./gradlew benchmark
```

## Post-Validation Checklist

- [ ] All 9 scenarios pass
- [ ] Performance targets met
- [ ] No crashes or ANRs
- [ ] Chinese strings display correctly
- [ ] Dark mode works (if implemented)
- [ ] Data persists across app restarts
