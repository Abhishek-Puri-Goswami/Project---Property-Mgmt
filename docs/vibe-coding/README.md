# PropertyHub — Controlled AI Vibe Coding

## Purpose

This documentation structure makes the development process traceable and easy to explain.

```text
Requirements
    ↓
Development Plan
    ↓
STEP Prompt
    ↓
Approval
    ↓
Implementation
    ↓
Approval
    ↓
Validation
    ↓
Result
```

## Files

```text
CLAUDE.md

docs/vibe-coding/
├── 00-project-requirements.md
├── 01-development-plan.md
├── 02-validation-contract.md
├── 03-frontend-standards.md
├── prompts/
└── results/
```

## Workflow

### First

Give Claude the STEP-00 roadmap prompt.

Claude should:

- inspect the existing project
- read the requirements
- compare the actual project state with the requirements
- propose or validate the implementation sequence
- make no code changes

### Then

For each STEP:

1. use the documented STEP prompt
2. Claude plans the STEP
3. review the plan
4. approve
5. Claude implements only that STEP
6. review the build summary
7. approve testing
8. Claude runs the approved validation exactly once
9. document the result
10. explicitly start the next STEP

## Validation Philosophy

PropertyHub uses layered validation:

```text
Frontend
    ↓
Backend
    ↓
PostgreSQL
```

The same business meaning must be preserved.

Frontend = early feedback.

Backend = authoritative API/business validation.

Database = persistence integrity.

## Testing Philosophy

Backend:

```text
JUnit 5
Mockito
Spring Boot Test
MockMvc
JaCoCo
```

Frontend:

```text
Vitest
@vitest/ui
```

Testing covers both backend and frontend.

## UI Philosophy

Frontend applications:

```text
React + Vite + Axios
```

Design direction:

```text
Claymorphism
```

UX must include:

```text
loading states
empty states
error states
success states
timeout toasts
```

## Simplicity Principle

The project should remain:

- easy to understand
- easy to explain
- easy to demo
- easy to test

Do not add complexity unless the requirement genuinely needs it.
