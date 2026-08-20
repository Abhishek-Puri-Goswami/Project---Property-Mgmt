# PropertyHub — Controlled Development Plan

## 1. Execution Model

```text
00 Requirements
    ↓
STEP-00 Roadmap Analysis
    ↓
User approves roadmap
    ↓
STEP-01
    ↓
PLAN → APPROVAL → BUILD → APPROVAL → TEST → RESULT
    ↓
STEP-02
    ↓
...
```

Only one STEP is active at a time.

The plan deliberately keeps the product simple while still satisfying the core architecture and AI goals.

---

# 2. STEP Principles

Each STEP:

- has one clear business/technical objective
- has a limited file scope
- has explicit dependencies
- has backend/database/frontend validation where applicable
- includes tests
- ends with a validation result
- does not automatically trigger the next STEP

---

# 3. Day 1 Priority

## STEP-01 — Workspace and Build Foundation

Goal:

Create the minimal project structure and basic build configuration for:

```text
eureka-server
api-gateway
auth-service
property-service
ai-service
admin-server
frontend
admin-panel
```

Constraints:

- Java + Spring Boot for backend
- React + Vite for frontend
- no containerization
- PostgreSQL planned as the persistent database
- keep the root structure simple

Validation:

- backend applications compile
- frontend applications start
- frontend test tooling is installed
- no unnecessary framework introduced

---

## STEP-02 — Eureka Server

Implement:

- Eureka server
- service registration configuration

Do not add business logic.

Validation:

- application starts
- Eureka console is accessible
- later services can register

---

## STEP-03 — API Gateway

Implement:

- Spring Cloud Gateway
- service discovery routing
- CORS
- basic request logging
- gateway security foundation

Routes:

```text
/api/auth/**
/api/properties/**
/api/ai/**
```

Validation:

- gateway starts
- routes resolve once target services are available
- CORS behavior is verified

---

## STEP-04 — Auth Service: Registration and User Model

Implement the coherent authentication foundation:

- User
- Role
- PostgreSQL connection
- data-access layer
- request DTOs
- response DTOs
- service
- controller
- validation
- global exception handling
- database constraints
- unit/service/API tests

Validation contract:

```text
email
password
role
```

Frontend is not required yet.

---

## STEP-05 — Auth Service: Login, JWT and RBAC

Implement:

- password encoding
- login
- JWT generation
- JWT validation
- authenticated `/api/auth/me`
- BUYER / AGENT / ADMIN authorization
- security configuration
- authentication tests
- authorization tests

Validation:

- backend
- JWT behavior
- protected endpoints
- failure scenarios

---

## STEP-06 — Property Service Foundation

Implement:

- Property
- PostgreSQL tables
- data-access layer
- create/get/update/delete
- request/response DTOs
- mapping
- validation
- global exception handling
- database constraints
- backend tests

Property validation contract:

```text
title → required
city → required
price → positive
bhk → positive
area → positive
```

---

## STEP-07 — Property Search and Filtering

Implement:

- search
- city filtering
- BHK filtering
- budget filtering
- area filtering
- relevant combinations
- validation
- query tests

Keep the search implementation straightforward.

---

## STEP-08 — OpenFeign Property Communication

Implement the smallest useful Feign boundary between AI Service and Property Service.

Support only required operations such as:

```text
getProperty()
searchProperties()
getPropertiesByIds()
```

No unnecessary internal clients.

Validation:

- successful call
- failure mapping
- authorization behavior where applicable

---

## STEP-09 — AI Chat Foundation

Implement:

- Spring AI
- ChatClient
- ChatModel
- prompt configuration
- `POST /api/ai/chat`
- ChatRequest
- ChatResponse
- initial AI service exception handling

Do not implement RAG yet.

---

## STEP-10 — Prompt Templates and Structured Output

Implement:

- externalized PropertyHub prompt template
- conversation context input
- structured requirement response
- PropertyRequirementResponse

Example:

```json
{
  "city": "Pune",
  "bhk": 2,
  "maxBudget": 8000000,
  "parkingRequired": true
}
```

Validation:

- structured output parsing
- invalid/empty model response handling
- tests using mocked AI dependencies

---

## STEP-11 — Persistent Chat History and Memory

Implement:

```text
conversations
chat_messages
```

Store every user request and AI response.

Implement:

- Conversation
- ChatMessage
- persistence
- retrieval
- conversation context
- ChatMemoryService

Validation:

- create conversation
- add message
- retrieve conversation
- restart-safe persistence
- contextual follow-up messages

---

## STEP-12 — Embeddings and pgvector

Implement:

- EmbeddingModel
- pgvector-backed vector storage
- document ingestion
- vector search

Initial knowledge sources:

```text
Pune Locality Guide
Hinjewadi Guide
Wakad Guide
Property Buying FAQ
Home Loan FAQ
Property Documentation Guide
```

Validation:

- embedding generation
- vector storage
- similarity search

---

## STEP-13 — RAG

Implement:

- query embedding
- similarity retrieval
- relevant document selection
- RAG prompt context
- grounded AI answer

Primary demonstration:

```text
2 BHK in Pune under ₹80 lakh
+
property search
+
locality suitability for IT professional
```

Flow:

```text
Natural Language
      ↓
Structured Requirement
      ↓
Property Search via Feign
      ↓
Candidate Properties
      ↓
Locality RAG
      ↓
AI Recommendation
```

RAG is the MVP advanced-AI completion point.

---

# 4. Day 2 Priority

## STEP-14 — Cross-Cutting Engineering Hardening

Implement/complete across applicable services:

- meaningful logging
- global exceptions
- Actuator
- Swagger/OpenAPI
- Spring Boot Admin registration
- security restrictions for management endpoints
- Switch to Lombok
- ModelMapper Bean Approach

Do not create centralized logging infrastructure.

---

## STEP-15 — Shared Validation Contract Completion

Verify and align:

```text
Frontend
Backend
PostgreSQL
```

Rules must have the same business meaning.

Backend is authoritative.

Database enforces persistence invariants.

Frontend provides early feedback.

Create/verify shared validation error semantics.

---

## STEP-16 — Buyer/Agent React Application Foundation

Create the React + Vite frontend.

Implement:

- Axios client
- route/screen structure
- Claymorphism design system
- form components
- cards
- tables
- loading states
- empty states
- error states
- toast system with consistent timeout
- authentication state

Keep the UI simple and logical.

---

## STEP-16A — Property Service: Favorites and Visits

**Inserted STEP, not part of the original plan sequence.** Added after STEP-16 revealed that this plan's own architecture (§ Property Service Structure, § PostgreSQL Database) documents `Favorite`/`Visit` as real property-service entities (`FavoriteController`, `VisitController`, `favorites`/`visits` tables), required by STEP-17's Favorites and Scheduled Visits screens — but no STEP through STEP-16 had built them. Inserted ahead of STEP-17 so that STEP-17 can wire real Axios calls instead of local-only placeholder state, per explicit user decision (see `04-current-implementations.md` for the full record).

Implement in property-service:

- Favorite (user↔property, unique per user/property)
- Visit (scheduled property visit, status PENDING/CONFIRMED/CANCELLED)
- FavoriteController / VisitController
- FavoriteService / VisitService
- FavoriteRepository / VisitRepository
- Validation, global exception handling (DuplicateResourceException)
- Backend tests

Comparison remains client-side only (multi-select, no backend entity), per the requirements doc's own description of that feature.

---

## STEP-17 — Buyer/Agent Property UI

Implement:

- Login
- Register
- Dashboard
- Property Search
- Property Details
- Favorites
- Comparison
- Scheduled Visits
- Ask AI entry point

Use Axios.

Mirror backend validation.

Display API errors cleanly.

Use success/error toasts.

---

## STEP-18 — AI Copilot UI

Implement:

- conversation layout
- message history
- prompt input
- loading indicator
- structured recommendation display
- property result cards
- follow-up conversation support
- failure toast
- empty state

The showcase flow is:

```text
Find me a 2 BHK in Pune under ₹80 lakh.
        ↓
AI returns matching properties
        ↓
User asks follow-up
        ↓
AI uses previous context
```

---

## STEP-19 — Admin Panel

Create a separate React + Vite application.

Implement:

- Admin Dashboard
- Users
- Agents
- Properties
- Property approval/removal
- Statistics
- AI usage overview
- system/service status where exposed

Use:

- Axios
- Claymorphism
- role-aware UI
- loading/empty/error states
- timeout toasts

Keep future tenant/lease/payment/maintenance screens out of the MVP unless time remains.

---

## STEP-20 — Frontend Testing with Vitest

Implement frontend tests using:

```text
Vitest
@vitest/ui
```

Cover:

- form validation
- login/register
- search/filter interactions
- property detail interactions
- favorites
- comparison
- AI chat input/output states
- Axios success/failure handling
- toast behavior
- protected/role-specific UI

Provide scripts for:

```text
npm run test
npm run test:ui
```

Use the installed Vitest version's supported UI/HTML reporting mechanism.

---

## STEP-21 — Backend + Database Validation and Coverage

Complete backend validation and coverage for the most important business logic:

```text
AuthService
PropertyService
PropertySearchService
AiChatService
RagService
```

Use:

```text
JUnit 5
Mockito
Spring Boot Test
MockMvc
JaCoCo
```

Target approximately 70%+ core business coverage.

Verify database constraints and persistence behavior.

---

## STEP-22 — Integrated MVP Verification

Run a controlled end-to-end verification of:

```text
Client
  ↓
API Gateway
  ↓
Auth / Property / AI
  ↓
PostgreSQL + pgvector
```

Verify:

- authentication
- RBAC
- property CRUD
- property search
- Feign communication
- AI chat
- persistent history
- embeddings
- RAG
- frontend validation
- backend validation
- database constraints
- toast behavior
- frontend tests
- backend tests
- coverage reports

This STEP is verification and correction only.

Do not add new product features.

---

# 5. Optional Features After MVP

Only after all mandatory work is stable.

## Tier 1

```text
AI Match Score
Property Comparison
Admin Dashboard Statistics
AI Usage Statistics
Saved Searches
```

## Tier 2

```text
Tenant Management
Lease Management
Maintenance Management
Reports
Occupancy Dashboard
Financial Dashboard
```

## Tier 3

```text
Payment Service
Notification Service
Email
SMS
WhatsApp
Advanced Analytics
```

---

# 6. Optional Advanced AI

After mandatory RAG:

## STEP-O1 — Tool Calling

```text
searchProperties()
getPropertyDetails()
calculateEMI()
schedulePropertyVisit()
```

## STEP-O2 — Simple Agentic AI

Combine:

```text
Property Search
+
RAG
+
Reasoning
```

Keep the agent simple.

## STEP-O3 — MCP

Only if everything else is stable.

Potential tools:

```text
search_properties
get_property
calculate_emi
get_locality_information
```

MCP must never jeopardize the MVP.

---

# 7. Final MVP Boundary

Mandatory:

```text
Microservices
Eureka
API Gateway
Auth Service
JWT
RBAC
Property Service
PostgreSQL
OpenFeign
Validation
Global Exceptions
Logging
Actuator
Spring Boot Admin
Swagger
Separate Admin Panel

Spring AI
ChatClient
ChatModel
PromptTemplate
Structured Response
Persistent Chat History
Chat Memory
Embeddings
pgvector
RAG

JUnit
Mockito
MockMvc
JaCoCo

React + Vite
Axios
Vitest
@vitest/ui
Claymorphism UI
Timeout Toasts
Frontend Validation
```

Optional:

```text
Tool Calling
Agentic AI
MCP
Notification Service
Payment Service
Tenant Module
Lease Module
Maintenance Module
Reports
Advanced Dashboard
```

---

# 8. Simplicity Rule

When deciding between two valid implementations:

Choose the simpler implementation that:

- satisfies the requirement
- remains easy to explain
- keeps boundaries clear
- remains testable
- does not create unnecessary abstraction

The project is intended to demonstrate strong engineering and meaningful AI, not maximum feature count.
