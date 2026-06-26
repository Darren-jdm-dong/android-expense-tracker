# Research: Android Smart Expense Tracker

**Date**: 2026-06-24
**Feature**: Android Smart Expense Tracker
**Spec**: [spec.md](./spec.md)

## Technical Decisions

### Decision 1: UI Framework — Jetpack Compose

**Decision**: Use Jetpack Compose for all UI development.

**Rationale**:
- Modern declarative UI paradigm reduces boilerplate
- First-class Android support with active development
- Excellent Material 3 integration
- Reactive state management aligns with MVVM pattern
- Better testability with Compose Testing framework

**Alternatives considered**:
- XML Views: Legacy approach, more verbose, harder to test
- Flutter: Cross-platform but adds Dart dependency; overkill for single-platform app
- React Native: Similar cross-platform overhead; native is preferred for this scope

---

### Decision 2: Database — Room

**Decision**: Use Room (SQLite abstraction) for local data persistence.

**Rationale**:
- Official Android Jetpack library with first-class Kotlin support
- Compile-time SQL verification prevents runtime errors
- Built-in migration support for schema evolution
- LiveData/Flow integration for reactive queries
- Well-documented and widely adopted

**Alternatives considered**:
- SQLDelight: Good but less ecosystem support than Room
- Realm: Heavier footprint, licensing concerns
- Raw SQLite: Too much boilerplate, no compile-time checks
- DataStore: Only suitable for key-value, not relational data

---

### Decision 3: Dependency Injection — Hilt

**Decision**: Use Hilt for dependency injection.

**Rationale**:
- Built on Dagger, official Android DI solution
- Compile-time validation of dependency graph
- Reduces boilerplate compared to manual DI or Koin
- Excellent ViewModel integration
- Standard in modern Android development

**Alternatives considered**:
- Koin: Runtime resolution (no compile-time checks), simpler API
- Manual DI: Too much boilerplate for this project size
- Dagger: Same power but more boilerplate than Hilt

---

### Decision 4: Smart Categorization Approach

**Decision**: Use a keyword-matching table with confidence scoring (not ML).

**Rationale**:
- Simple, deterministic behavior users can understand
- Works fully offline with no model download
- Learns from user corrections via MerchantMapping table
- Zero inference latency
- Easy to debug and explain

**Alternatives considered**:
- On-device ML (TensorFlow Lite): Overkill for text categorization, larger APK
- Cloud-based NLP: Violates offline-first requirement
- Rule-based only (no learning): Too rigid, doesn't adapt to user patterns

**Approach**:
- Maintain a `MerchantMapping` table: keyword → category + match_count
- When user types a note, fuzzy-match against known keywords
- Auto-select category when confidence > 80% (3+ consistent matches)
- Show suggestion (not auto-select) when confidence 50-80%
- No suggestion when confidence < 50%

---

### Decision 5: Recurring Expense Scheduling

**Decision**: Use WorkManager for scheduling recurring expenses.

**Rationale**:
- Official Android solution for deferrable, guaranteed work
- Survives app restarts and device reboots
- Battery-efficient (respects Doze mode)
- Simple API for periodic tasks

**Alternatives considered**:
- AlarmManager: Less reliable, battery-unfriendly
- JobScheduler: Lower-level, more boilerplate
- Manual check on app open: Unreliable, misses scheduled dates

---

### Decision 6: Charting Library

**Decision**: Use MPAndroidChart for pie charts and line charts.

**Rationale**:
- Most widely used Android charting library
- Supports pie charts and line charts (required by spec)
- Good performance with large datasets
- Extensive customization options
- Open source (Apache 2.0)

**Alternatives considered**:
- Jetpack Compose Canvas (custom drawing): Too much effort for polished charts
- Vico: Newer, Compose-native, but less mature
- AAChartCore: Good but less community support

---

### Decision 7: Architecture Pattern — MVVM + Clean Architecture

**Decision**: Use MVVM with Clean Architecture layers (data/domain/ui).

**Rationale**:
- Separation of concerns: data access, business logic, UI are independent
- Testable: use cases can be unit tested without Android framework
- Scalable: easy to add features without modifying existing code
- Standard Android recommended architecture

**Layer responsibilities**:
- **data**: Room entities, DAOs, repository implementations
- **domain**: Pure Kotlin models, use cases (no Android dependencies)
- **ui**: Compose screens, ViewModels, navigation

---

### Decision 8: Export Format

**Decision**: Support CSV export for expense data.

**Rationale**:
- Universally compatible with Excel, Google Sheets, Numbers
- Simple to generate, small file size
- Users can do custom analysis in their preferred tool
- Easy to implement with standard Kotlin I/O

**Alternatives considered**:
- JSON: Less user-friendly for non-technical users
- Excel (.xlsx): Requires heavy library, overkill for simple data
- PDF: Difficult to generate, not analyzable

---

## Best Practices Applied

### Android 10+ (API 29) Considerations
- Scoped storage: Use MediaStore for exports, app-specific storage for database
- Background restrictions: WorkManager handles recurring tasks within limits
- BiometricPrompt API for device authentication (FR-014)

### Offline-First Design
- All data stored locally in Room database
- No network calls for any core feature
- Export to Downloads folder via MediaStore
- No sync conflicts to handle

### Chinese Localization
- All strings in `strings.xml` with Chinese values
- Date format: YYYY年MM月DD日
- Currency symbol: ¥ (CNY default)
- Number format: 1,234.56 (comma thousands, period decimal)

## Unresolved Items

None — all technical decisions have been made with reasonable defaults.
