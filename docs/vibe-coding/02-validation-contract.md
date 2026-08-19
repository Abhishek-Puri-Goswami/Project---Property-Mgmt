# PropertyHub — Shared Validation Contract

## 1. Purpose

This document defines the validation rules that must remain logically consistent across:

```text
React Frontend
      ↓
Spring Boot Backend
      ↓
PostgreSQL
```

The layers have different responsibilities, but they must not contradict each other.

---

# 2. Responsibility Model

## Frontend

Purpose:

- immediate user feedback
- prevent obviously invalid requests
- clear field-level messages
- reduce avoidable API calls

Frontend validation is NOT a security boundary.

## Backend

Purpose:

- authoritative API validation
- business-rule validation
- security validation
- normalized validation errors

Backend validation must always run even when the frontend validates the same field.

## PostgreSQL

Purpose:

- final data-integrity enforcement
- nullability
- uniqueness
- relationships
- safe numeric constraints
- other persistence invariants

The database does not replace backend business validation.

---

# 3. Authentication Validation

## Registration

```text
email
- required
- valid email format

password
- required
- minimum length

role
- allowed role
```

Allowed roles:

```text
BUYER
AGENT
ADMIN
```

The frontend validates user input.

The backend validates again.

The database enforces uniqueness and persistence integrity.

Password strength is not enforced against plaintext in PostgreSQL because only an encoded password is persisted.

---

# 4. Property Validation

| Field | Rule | Frontend | Backend | PostgreSQL |
|---|---|---:|---:|---:|
| title | required | ✓ | ✓ | NOT NULL |
| city | required | ✓ | ✓ | NOT NULL |
| price | positive | ✓ | ✓ | CHECK > 0 |
| bhk | positive | ✓ | ✓ | CHECK > 0 |
| area | positive | ✓ | ✓ | CHECK > 0 |
| property type | valid allowed value | ✓ | ✓ | appropriate constraint where useful |
| parking | valid boolean/value | ✓ | ✓ | appropriate constraint |
| furnishing | valid allowed value | ✓ | ✓ | appropriate constraint |

---

# 5. Search Validation

Search/filter parameters must have consistent meanings.

Examples:

```text
minPrice >= 0
maxPrice >= 0
minPrice <= maxPrice
bhk > 0
area > 0
```

The frontend should prevent obviously invalid combinations.

The backend must reject invalid combinations.

The database must never receive invalid persistence operations.

---

# 6. AI Request Validation

AI chat requests must validate:

```text
conversationId
message
```

Rules:

```text
message
- required
- not blank
```

Where a conversation identifier is required:

```text
conversationId
- valid identifier
- must belong to the requesting user
```

The backend remains authoritative.

---

# 7. Shared Error Semantics

Use consistent error categories.

Examples:

```text
VALIDATION_ERROR
RESOURCE_NOT_FOUND
DUPLICATE_RESOURCE
UNAUTHORIZED
FORBIDDEN
AI_SERVICE_ERROR
VECTOR_SEARCH_ERROR
TOOL_EXECUTION_ERROR
```

The frontend should map backend errors to:

- field-level messages when a field can be identified
- form-level messages when it cannot
- timeout toast for meaningful success/failure events

Never display raw stack traces.

---

# 8. Database Integrity Rules

Prefer database constraints for invariants that must never be violated.

Examples:

```text
NOT NULL
UNIQUE
CHECK
FOREIGN KEY
```

Examples:

```sql
price > 0
bhk > 0
area > 0
```

Use uniqueness for fields that must be unique, such as user email.

Use foreign keys for real relationships.

---

# 9. Validation Consistency Rule

If the backend says:

```text
price must be > 0
```

the frontend must not allow:

```text
price = -1
```

and the database should protect the same persisted invariant.

Do not create:

```text
Frontend: price >= 0
Backend:  price > 0
Database: price >= 0
```

unless there is an explicitly documented reason.

Rules should have one clear business meaning.

---

# 10. Testing the Validation Contract

For each important rule, test at least:

### Frontend

- invalid input
- valid input
- error message
- request blocked for clearly invalid input

### Backend

- invalid request rejected
- valid request accepted
- normalized error response

### Database

- invariant constraint enforced where applicable

This creates:

```text
Frontend Validation Test
        +
Backend Validation Test
        +
Database Integrity Test
```

---

# 11. Important Exception

Not every backend rule should be duplicated as a database rule.

Examples:

- password strength
- AI-specific semantic validation
- complex business logic
- authorization decisions

These belong at the appropriate application boundary.

The rule is consistency, not mechanical duplication.

---

# 12. Validation Acceptance Criteria

A feature is validation-complete only when:

```text
Frontend rules
      ↓
match
      ↓
Backend rules
      ↓
and persistence invariants
      ↓
are protected in PostgreSQL where applicable
```

No layer may silently weaken an important business rule.
