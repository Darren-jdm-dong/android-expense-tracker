<!-- Sync Impact Report
Version change: (uninitialized) → 1.0.0
Modified principles: N/A (initial creation)
Added sections:
  - Core Principles (5 principles)
  - Technology Stack & Constraints
  - Development Workflow
  - Governance
Removed sections: None
Templates requiring updates:
  - .specify/templates/plan-template.md ✅ (Constitution Check section references this file)
  - .specify/templates/spec-template.md ✅ (no changes needed — already generic)
  - .specify/templates/tasks-template.md ✅ (no changes needed — already generic)
Follow-up TODOs: None
-->

# My Project Constitution

## Core Principles

### I. User-Centric Design

Every feature MUST serve a clearly identified user need. Requirements MUST
be expressed as user stories with acceptance scenarios before implementation
begins. Features without defined user value MUST NOT be built.

- User stories MUST be prioritized (P1, P2, P3) and independently testable.
- Acceptance scenarios MUST use Given/When/Then format.
- Edge cases MUST be identified during specification, not discovered in
  production.

**Rationale**: Prevents building features that nobody needs. Ensures every
line of code traces back to a concrete user outcome.

### II. Test-Driven Development (NON-NEGOTIABLE)

TDD is mandatory for all feature work. The cycle MUST be:

1. Write tests that express the specification.
2. Confirm tests fail (red).
3. Implement the minimum code to pass (green).
4. Refactor while keeping tests green.

- Contract tests MUST define API boundaries before implementation.
- Integration tests MUST cover critical user journeys.
- Tests MUST NOT be written after implementation as an afterthought.

**Rationale**: Tests-first ensures the specification drives the code, not
the other way around. Prevents specification drift and regression.

### III. API-First Architecture

Backend APIs MUST be defined (contracts, schemas, endpoints) before
frontend implementation begins. The API contract is the source of truth.

- OpenAPI/GraphQL schemas MUST be authored before endpoint implementation.
- Frontend MUST develop against mocked or stubbed API contracts initially.
- Breaking API changes MUST be versioned and communicated.

**Rationale**: Decouples frontend and backend development. Enables parallel
work streams. Prevents tight coupling and integration surprises.

### IV. Security by Default

Security is not a feature to add later — it MUST be built into the
foundation. Every endpoint, input, and data flow MUST be secured by default.

- Authentication and authorization MUST be implemented in the foundational
  phase, not per-feature.
- All user input MUST be validated and sanitized at the boundary.
- Sensitive data MUST NOT be logged or exposed in error responses.
- Dependencies MUST be audited for known vulnerabilities before adoption.

**Rationale**: Retrofitting security is expensive and error-prone. Building
it in from the start costs little and prevents entire classes of
vulnerabilities.

### V. Simplicity (YAGNI)

Start with the simplest solution that meets the requirement. Do NOT build
for hypothetical future needs. Complexity MUST be justified in writing
before adoption.

- Prefer standard library solutions over third-party dependencies.
- Prefer flat structures over deep hierarchies until proven necessary.
- Every abstraction MUST earn its place by reducing, not adding, complexity.
- "It might be useful someday" is NOT a valid justification.

**Rationale**: Unnecessary complexity is the primary source of maintenance
burden. Simple code is easier to understand, test, debug, and modify.

## Technology Stack & Constraints

The following technology decisions apply to this project:

- **Language/Version**: To be determined per feature plan.
- **Primary Framework**: To be determined per feature plan.
- **Testing Framework**: Must support contract, integration, and unit tests.
- **Database**: To be determined per feature plan.
- **Deployment**: To be determined per feature plan.

Specific technology choices are made during the planning phase (`/speckit-plan`)
and documented in each feature's `plan.md`. The constitution does not mandate
specific tools — it mandates the process by which tools are chosen.

## Development Workflow

All feature work MUST follow the spec-driven workflow:

1. **Specify** (`/speckit`): Define user stories, requirements, and success
   criteria in a feature spec.
2. **Plan** (`/speckit-plan`): Research, design data models, define contracts,
   and produce an implementation plan.
3. **Task** (`/speckit-tasks`): Break the plan into ordered, trackable tasks
   organized by user story.
4. **Implement**: Execute tasks in dependency order. Tests first, then code.
5. **Validate**: Run quickstart validation. Confirm acceptance scenarios pass.

Quality gates:

- Spec MUST be approved before planning begins.
- Plan MUST pass constitution check before task breakdown.
- Tests MUST fail before implementation (red phase confirmed).
- Each user story MUST be independently verifiable at its checkpoint.

## Governance

This constitution is the highest-authority document for the My Project
codebase. All plans, specs, and implementation decisions MUST comply with
these principles.

**Amendment procedure**:

1. Proposed changes are documented with rationale.
2. Version is incremented per semantic versioning:
   - MAJOR: Principle removal or incompatible redefinition.
   - MINOR: New principle or materially expanded guidance.
   - PATCH: Clarifications, wording fixes, non-semantic refinements.
3. `LAST_AMENDED_DATE` is updated to the amendment date.
4. Dependent templates are reviewed and updated for consistency.

**Compliance review**: Every plan's Constitution Check section MUST verify
adherence to all five principles before implementation begins. Violations
MUST be documented in the Complexity Tracking table with justification.

**Version**: 1.0.0 | **Ratified**: 2026-06-24 | **Last Amended**: 2026-06-24
