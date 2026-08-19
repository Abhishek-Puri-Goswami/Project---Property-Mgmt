# PropertyHub — Frontend Standards

## 1. Applications

Create two separate React applications with Vite:

```text
frontend/
admin-panel/
```

---

# 2. Stack

```text
React
Vite
Axios
Vitest
@vitest/ui
```

Keep the frontend stack intentionally small.

---

# 3. Axios

Use a small centralized HTTP client layer.

Responsibilities:

- base URL
- JWT attachment
- common response handling
- common error normalization

Do not duplicate Axios configuration inside every component.

---

# 4. UI Direction

Use:

> Claymorphism

The style should remain:

- professional
- readable
- consistent
- accessible
- easy to explain

Do not overdecorate.

---

# 5. Common UI Patterns

Create reusable patterns for:

```text
Button
Input
Select
Form
Card
Table
Modal/Dialog
Loading State
Empty State
Error State
Toast
```

Do not build a large design system.

---

# 6. Buyer / Agent Screens

```text
Login
Register
Dashboard
Property Search
Property Details
Favorites
Comparison
Visits
AI Copilot
```

---

# 7. Admin Screens

```text
Dashboard
Users
Agents
Properties
Property Approval
Statistics
AI Analytics
System Status
```

---

# 8. UX States

Every API-driven screen should consider:

```text
Idle
Loading
Success
Empty
Error
```

Do not leave the user staring at a blank screen while an operation is running.

---

# 9. Toasts

Use short-lived timeout toasts for:

```text
success
error
information
important completed activity
```

Examples:

```text
Login successful
Property created successfully
Property updated successfully
Property deleted successfully
Added to favorites
Visit scheduled successfully
Unable to load properties
AI request failed
Server unavailable
```

Use a consistent timeout.

Toasts must not replace field-level validation.

---

# 10. Validation

Frontend validation must mirror backend business rules.

Validate before API calls when practical.

At minimum:

```text
email
password
title
city
price
bhk
area
```

Display clear messages.

Do not create frontend-only rules that contradict backend rules.

---

# 11. Testing

Use:

```text
Vitest
@vitest/ui
```

Test:

- rendering
- user interactions
- form validation
- Axios success
- Axios failure
- loading
- empty
- error states
- toast success
- toast failure
- role-aware UI
- AI chat behavior

Recommended scripts:

```json
{
  "scripts": {
    "dev": "vite",
    "build": "vite build",
    "test": "vitest run",
    "test:ui": "vitest --ui"
  }
}
```

Use the current installed Vitest configuration to enable the UI package and reporting.

---

# 12. Test Reporting

Vitest UI should be available for reviewing the suite visually.

Where reporting is needed, use the Vitest-supported UI/HTML reporting mechanism rather than introducing a second reporting stack.

The goal is:

```text
Test execution
      ↓
Vitest
      ↓
Vitest UI / report
```

---

# 13. Simplicity

Prefer:

```text
pages
components
services
validation
hooks
```

only where useful.

Do not introduce unnecessary:

- state libraries
- UI frameworks
- routing abstractions
- data-fetching libraries
- design systems

unless required.
