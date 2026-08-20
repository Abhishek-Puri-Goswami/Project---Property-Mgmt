# PropertyHub — Controlled AI Development Rules

## 1. Purpose

You are the coding agent responsible for implementing the PropertyHub project.

Your priority is:

> Correct, maintainable, production-oriented code with minimal unnecessary changes.

Do not optimize for speed at the cost of architecture, correctness, security, maintainability, or testability.

The complete project requirements are in:

```text
docs/vibe-coding/00-project-requirements.md
```

The approved implementation sequence is in:

```text
docs/vibe-coding/01-development-plan.md
```

Do not duplicate the entire requirements document in this file.

This file defines HOW you must work.

---

# 2. Documentation Structure

```text
PropertyHub/
│
├── CLAUDE.md
│
├── docs/
│   └── vibe-coding/
│       ├── 00-project-requirements.md
│       ├── 01-development-plan.md
│       ├── 02-validation-contract.md
│       ├── 03-frontend-standards.md
│       ├── prompts/
│       │   ├── STEP-00-ROADMAP.md
│       │   ├── STEP-01.md
│       │   └── ...
│       └── results/
│           ├── STEP-01-result.md
│           └── ...
│
├── eureka-server/
├── api-gateway/
├── auth-service/
├── property-service/
├── ai-service/
├── admin-server/
├── frontend/
└── admin-panel/
```

Use these documents as follows:

- `CLAUDE.md` = permanent execution rules
- `00-project-requirements.md` = complete project requirements
- `01-development-plan.md` = approved implementation roadmap
- `02-validation-contract.md` = shared backend/frontend/database validation rules
- `03-frontend-standards.md` = React/Vite/Axios/Claymorphism/toast/testing standards
- `prompts/STEP-XX.md` = exact prompt used for that STEP
- `results/STEP-XX-result.md` = actual implementation and validation result

Do not invent historical results.

---

# 3. Source-of-Truth Hierarchy

Use this order when information overlaps:

1. Explicit user instruction in the current conversation
2. `00-project-requirements.md`
3. Approved `01-development-plan.md`
4. Existing project files and established conventions
5. `CLAUDE.md`
6. General framework conventions

If two requirements conflict and the conflict materially affects architecture, API behavior, database design, security, or compatibility:

STOP and ask for clarification.

Do not silently choose a convenient interpretation.

---

# 4. Non-Negotiable Constraints

## Architecture

- Use independent Spring Boot microservices.
- Do not convert the system into a monolith.
- Keep the core architecture intentionally small.
- Core backend services:
  - Auth Service
  - Property Service
  - AI Service
  - API Gateway
  - Eureka Server
- Spring Boot Admin is a separate technical monitoring application.

## Containerization

- Do not use Docker.
- Do not use containerization.
- Do not create Dockerfiles.
- Do not create Docker Compose configuration.
- Do not introduce Kubernetes or other container-dependent infrastructure.
- Services must run directly in the approved development environment.

## Database

- PostgreSQL is the only database technology.
- Use PostgreSQL + pgvector for vector storage.
- Do not introduce MySQL.
- Do not introduce MongoDB.
- Do not introduce ChromaDB.
- Do not make Redis a required dependency.
- Do not introduce another database merely because it is common in production.

## Corporate Environment

- Development occurs on a company laptop with Privacy & Data Protection restrictions.
- Use only approved tools, libraries, services, and features.
- Do not unnecessarily upload project or company-sensitive information to external services.
- Never expose secrets in source code or configuration committed to shared project files.
- Do not introduce prohibited infrastructure.

---

# 5. Development Lifecycle

Follow this exact lifecycle for every logical feature:

```text
PLAN
  ↓
USER APPROVAL [Y/N]
  ↓
BUILD
  ↓
USER APPROVAL [Y/N]
  ↓
TEST
  ↓
PASS → COMPLETE / REVIEW
FAIL → STOP
  ↓
NEXT STEP
```

Never skip approval gates.

Never automatically continue to another STEP.

The intended working pattern is:

```text
Complete Project Requirements
        ↓
Claude analyzes requirements
        ↓
Claude prepares STEP roadmap
        ↓
User approves roadmap
        ↓
One STEP at a time
        ↓
Claude plans STEP
        ↓
User approves
        ↓
Claude builds STEP
        ↓
Claude asks permission to test
        ↓
User approves
        ↓
Claude validates once
        ↓
PASS → stop
FAIL → stop
        ↓
User explicitly starts next STEP
```

---

# 6. STEP Definition

A STEP is one coherent logical feature.

Do not create one STEP per class.

A coherent authentication STEP may include:

```text
User model
Role
Data access layer
Request DTOs
Response DTOs
Service
Controller
JWT
Security configuration
Exception handling
Tests
```

This is one logical feature.

Do not mix unrelated features into the same STEP.

Prefer the simplest design that satisfies the requirements.

---

# 7. STAGE 1 — PLAN

Before modifying any file:

## Inspect the existing project

Understand:

- project structure
- service structure
- package structure
- existing entities
- DTO conventions
- controller patterns
- service patterns
- data-access patterns
- configuration
- security configuration
- database configuration
- tests
- build configuration
- dependencies
- implementation relevant to the requested STEP

Never assume the project is empty.

Never invent existing classes, endpoints, tables, configurations, or APIs.

## Read requirements

Read the relevant requirements from:

```text
docs/vibe-coding/00-project-requirements.md
docs/vibe-coding/01-development-plan.md
docs/vibe-coding/02-validation-contract.md
docs/vibe-coding/03-frontend-standards.md
```

Read only the sections relevant to the current STEP when possible.

## Produce the plan

Your response must contain only:

### Feature

- name
- short description
- why it is the next logical STEP

### Files to Create

- exact path
- purpose

### Files to Modify

- exact path
- exact logical change

### Architecture

Explain the feature's position within the existing architecture.

For backend business services, prefer meaningful separation:

```text
controller
service
data-access
entity
dto
├── request
└── response
exception
mapper
config
security
```

Do not create layers merely for decoration.

### Database Changes

List:

- tables
- columns
- relationships
- constraints
- indexes
- schema changes

If none:

```text
Database changes: None
```

### API Changes

List:

- HTTP method
- endpoint
- request DTO
- response DTO
- validation
- authorization
- expected status codes

### Frontend Changes

When applicable, list:

- pages/components
- state changes
- Axios calls
- validation rules
- loading states
- empty states
- error states
- success/error toasts
- role-specific UI

### Testing

List exact:

- backend tests
- frontend tests
- database/integration tests where applicable

### Validation Commands

Give exact commands.

Then STOP.

Do not write code.

Ask:

```text
Proceed with this feature? [Y/N]
```

Do not continue until the user explicitly answers `Y`.

---

# 8. STAGE 2 — BUILD

Only build after explicit approval.

Implement only the approved logical feature.

Rules:

- create required files
- modify required files
- keep changes scoped
- follow existing conventions
- use proper layering
- separate DTO request/response models
- keep controllers thin
- keep business logic in services
- keep persistence/data access in the data-access layer
- centralize suitable exception handling
- validate at the API boundary
- apply authorization correctly
- avoid speculative functionality
- avoid unrelated dependencies
- avoid unrelated refactoring
- avoid unrelated service changes
- avoid unrelated file changes

## Backend

Do not expose persistence entities directly through REST APIs unless explicitly justified.

## Frontend

Use:

- React
- Vite
- Axios
- Vitest
- `@vitest/ui`

Keep frontend architecture understandable.

Do not create unnecessary global state or abstractions.

Use the agreed Claymorphism design direction.

Provide logical loading, empty, success, and error states.

Use timeout-based toast notifications for meaningful operations.

## Batched implementation

Whenever possible, perform the approved STEP's file changes in one batched implementation operation.

Do not repeatedly stop after individual files belonging to the same approved feature.

## After BUILD

Do NOT run tests automatically.

Do NOT debug automatically.

Do NOT start the next STEP.

Report:

```text
BUILD COMPLETE: <feature name>
```

Then provide a concise list of created/modified files.

Then ask:

```text
Run the approved validation commands? [Y/N]
```

STOP.

---

# 9. STAGE 3 — TEST

Only test after explicit user approval.

Run exactly the approved validation commands from PLAN.

Do not automatically:

- retry
- switch commands
- modify code
- debug
- start another STEP

Run the validation once.

---

# 10. Backend Testing

Backend testing uses:

- JUnit 5
- Mockito
- Spring Boot Test
- MockMvc where appropriate
- JaCoCo

Test important business and orchestration behavior.

Auth:

- registration
- duplicate email
- login
- invalid credentials
- JWT generation
- authorization behavior

Property:

- create
- get
- update
- delete
- search
- filtering
- validation
- not found

AI:

- chat
- conversation persistence
- conversation retrieval
- structured output
- embeddings
- RAG retrieval
- empty RAG
- AI exceptions
- tool invocation

Mock external AI dependencies.

The purpose is to test PropertyHub application logic, not the external model itself.

Target approximately 70%+ core business coverage.

---

# 11. Frontend Testing

Frontend testing is mandatory.

Use:

```text
Vitest
@vitest/ui
```

Test:

- form validation
- user interactions
- successful API flows
- API failure flows
- loading states
- empty states
- toast behavior
- critical UI business logic
- role-specific UI
- Axios error handling
- validation message mapping

The Vitest UI must be usable for visual inspection of tests and their results.

Recommended commands include:

```text
npm run test
npm run test:ui
```

where the project scripts map to the installed Vitest configuration.

Use the installed Vitest version's supported UI/HTML reporting configuration.

Do not introduce a separate test framework unless explicitly required.

---

# 12. Database Testing

Database-related validation should include, where applicable:

- entity mapping
- constraints
- unique rules
- not-null rules
- positive-value checks
- relationships
- persistence behavior
- query correctness

Use database/integration tests only where they add meaningful confidence.

Do not create unnecessarily complex infrastructure just for testing.

---

# 13. Shared Validation Rule

Validation must be logically consistent across:

```text
Frontend
    ↓
Backend
    ↓
PostgreSQL
```

The layers have different jobs:

```text
Frontend
→ immediate user feedback

Backend
→ authoritative business/API validation

Database
→ final persisted-data integrity
```

The same business rule must not be weakened or contradicted between layers.

Example:

```text
title  → required
city   → required
price  → positive
bhk    → positive
area   → positive
email  → required + valid format
password → required + minimum length
```

Frontend and backend should produce consistent field-level meanings and error semantics.

Database constraints should enforce the invariants that belong at persistence level:

```text
NOT NULL
UNIQUE
CHECK
FOREIGN KEY
```

Do not store plaintext passwords.

Password strength is validated before hashing.

The database persists only the encoded password.

Do not depend on frontend validation for security.

---

# 14. Test Failure Behavior

If validation fails:

1. Do NOT modify code.
2. Do NOT retry.
3. Do NOT enter a debugging loop.
4. Identify the relevant failure.
5. Preserve the actual error.
6. Show only relevant output.
7. Give exactly one proposed fix.

Use:

```text
TEST FAILED

Relevant error:
<actual error>

Proposed fix:
<one-sentence fix>

STOPPED — awaiting explicit permission to modify code.
```

Do not implement the fix until explicitly authorized.

---

# 15. Test Success Behavior

If validation succeeds:

```text
SUCCESS: Feature complete

<One-sentence summary of what was implemented and validated.>
```

Immediately after reporting SUCCESS, update `docs/vibe-coding/04-current-implementations.md`
with a new dated entry for the completed STEP, at the same level of detail as
the existing entries: objective, files created/modified, key design decisions,
every issue hit during validation with its root cause and fix (not just the
outcome), and the final validated result. This keeps that file a complete,
self-contained implementation log that a fresh session with no conversation
history can rely on.

STOP.

Do not automatically start the next STEP.

---

# 16. Review

Perform review only after successful validation and explicit request/approval.

Review:

### Architecture

- service boundaries
- package structure
- layering
- coupling
- leakage between layers

### API

- REST conventions
- request/response separation
- validation
- status codes
- error responses
- authorization

### Security

- authentication
- authorization
- JWT
- password handling
- sensitive configuration
- secret handling

### Database

- relationships
- constraints
- indexes
- data-access design
- transaction boundaries

### Frontend

- component boundaries
- UI consistency
- form validation
- loading/error/empty states
- Axios handling
- toast behavior
- accessibility basics
- unnecessary complexity

### Testing

- unit tests
- controller tests
- service tests
- frontend tests
- database/integration tests where appropriate
- edge cases
- JaCoCo
- Vitest UI/reporting

### Maintainability

- naming
- duplication
- complexity
- exception handling
- configuration
- unnecessary abstraction

If fixes are required, create a separate controlled fix STEP.

---

# 17. Scope Control

If an unrelated issue is discovered:

```text
OUT-OF-SCOPE FINDING:
<problem>

RECOMMENDATION:
<recommended action>
```

Do not fix it automatically.

Continue only with the approved STEP.

---

# 18. Ambiguity Rule

If ambiguity could materially affect:

- architecture
- API behavior
- database design
- security
- compatibility
- validation semantics

STOP and ask one concise clarification question.

For minor details already constrained by the existing project conventions, use the convention instead of asking unnecessarily.

---

# 19. AI Development Priority

The AI progression is:

```text
Basic AI Chat
      ↓
Prompt Templates
      ↓
Structured Output
      ↓
Persistent Chat History
      ↓
Embeddings
      ↓
pgvector
      ↓
RAG
      ↓
Tool Calling
      ↓
Agentic AI
      ↓
MCP
```

RAG is the minimum mandatory advanced AI milestone.

Agentic AI follows RAG.

MCP is optional and only implemented when mandatory features are stable.

Keep AI connected to actual PropertyHub business capabilities.

---

# 20. PropertyHub Architecture Guardrails

Core services:

```text
Eureka
API Gateway
Auth Service
Property Service
AI Service
Spring Boot Admin
```

Database:

```text
PostgreSQL
pgvector
```

Communication:

```text
OpenFeign
```

Security:

```text
Spring Security
JWT
BUYER
AGENT
ADMIN
```

API routes:

```text
/api/auth/**
/api/properties/**
/api/ai/**
```

Do not bypass service boundaries with cross-service direct database access.

---

# 21. Frontend Guardrails

The two frontend applications are:

```text
frontend/
admin-panel/
```

Both are React + Vite applications.

Use Axios for backend communication.

Use Claymorphism as the visual direction.

Keep the UI logical and easy to explain.

Use reusable UI patterns for:

```text
buttons
forms
cards
tables
dialogs
loading states
empty states
error states
toasts
```

Provide short-lived toast notifications for:

```text
success
failure
information
important activity completion
```

Toasts must disappear after a consistent timeout.

Inline form validation remains necessary.

---

# 22. Simplicity Principle

Prefer simple implementations.

The project should be:

- easy to explain
- easy to understand
- easy to demo
- easy to test
- easy to maintain

Do not simplify away a requirement that is necessary for correctness.

When multiple architectures satisfy the requirements, choose the simpler one.

Avoid:

- unnecessary abstractions
- unnecessary design patterns
- unnecessary services
- unnecessary infrastructure
- speculative scalability
- complex state management
- complex agent architectures

---

# 23. Communication Efficiency

Be concise.

Do not provide:

- generic tutorials
- repeated requirements
- long introductions
- obvious code explanations
- unrelated recommendations

Communicate only what is necessary for the current development stage.

---

# 24. Absolute Rules

```text
One logical feature per cycle.
One approval before implementation.
One approval before testing.
One test execution.
No automatic debugging.
No unrelated changes.
No Docker.
No containerization.
PostgreSQL only.
pgvector for vectors.
No direct cross-service database access.
Frontend validation must mirror backend business validation.
Database constraints must protect persistence integrity.
Frontend testing is mandatory.
Vitest + @vitest/ui are the frontend testing standard.
React applications use Vite.
Axios is the frontend HTTP client.
Claymorphism is the agreed UI direction.
Use timeout-based toasts.
AI must remain connected to the PropertyHub domain.
Never invent project state.
Never silently change requirements.
Never automatically continue to the next STEP.
STOP at approval gates.
