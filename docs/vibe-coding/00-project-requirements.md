# PropertyHub
## AI-Powered Real Estate Management System

**Project Type:** AI-Oriented Real Estate Management Platform  
**Development Approach:** AI-Assisted Vibe Coding  
**Deadline:** 2 Days  
**Architecture:** Microservices  
**Backend:** Java + Spring Boot  
**Database:** PostgreSQL  
**Vector Database:** PostgreSQL + pgvector  
**Service Discovery:** Eureka Server  
**API Gateway:** Spring Cloud Gateway  
**Inter-Service Communication:** OpenFeign / FeignClient  
**Authentication:** Dedicated Auth Service + JWT  
**Authorization:** Role-Based Access Control  
**AI Framework:** Spring AI  
**Monitoring:** Spring Boot Actuator + Spring Boot Admin  
**API Documentation:** Swagger / OpenAPI  
**Logging:** SLF4J + Logback  
**Testing:** JUnit 5 + Mockito + Spring Boot Test  
**Coverage:** JaCoCo  
**Containerization:** None  
**Docker:** Not Used  

---

# 1. Executive Summary

PropertyHub is an AI-powered real-estate management platform designed to help users discover, evaluate, compare, and manage property information through both conventional web interfaces and natural-language AI interaction.

The system follows a **microservices architecture** and uses PostgreSQL as its persistent database. PostgreSQL with pgvector is also used for storing and searching vector embeddings required for Retrieval-Augmented Generation (RAG).

The primary differentiator is the **PropertyHub AI Assistant**, which progressively supports:

- Conversational AI
- ChatClient
- ChatModel
- Prompt Templates
- Structured AI Responses
- Persistent Chat History
- Chat Memory
- Embeddings
- pgvector
- RAG
- Tool Calling
- Agentic AI
- MCP Server

The AI development strategy is deliberately incremental:

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
MCP Server
```

RAG is the minimum mandatory advanced AI milestone.

Agentic AI is implemented after RAG.

MCP is implemented only if sufficient time remains.

---

# 2. Critical Project Constraints

The following requirements are fixed.

## 2.1 No Monolithic Architecture

The system must be implemented as independent Spring Boot microservices.

## 2.2 No Docker

Docker must not be used.

## 2.3 No Containerization

No Dockerfiles, Docker Compose, Kubernetes containers, or container-dependent infrastructure.

All services must run directly in the approved development environment.

## 2.4 PostgreSQL Only

PostgreSQL is the only database technology used.

No:

- MySQL
- MongoDB
- ChromaDB
- Redis as a required dependency

## 2.5 pgvector

pgvector is used for:

- Embeddings
- Semantic similarity search
- RAG retrieval

## 2.6 Corporate Development Environment

The project is developed on a company laptop with Privacy & Data Protection restrictions.

Therefore:

- Only approved tools and applications should be used.
- External AI services must comply with company policy.
- Secrets must never be committed to source-control system.
- Sensitive company/project information must not be unnecessarily uploaded to external services.
- Development tools must not depend on prohibited software.

---

# 3. Problem Statement

Traditional real-estate applications require users to manually search through property listings using multiple filters.

Users may need to specify:

- Location
- Budget
- BHK
- Area
- Parking
- Furnishing
- Property type
- Amenities

This makes property discovery time-consuming.

Additionally, users often need contextual information that cannot be obtained from simple property filters.

For example:

> "I need a 2 BHK in Pune under ₹80 lakh with parking, preferably in an area suitable for someone working in IT."

A conventional property search cannot easily understand the complete requirement.

PropertyHub addresses this problem by introducing an AI Property Assistant that can understand natural language, retrieve relevant information, interact with application services, maintain conversation context, and provide structured recommendations.

---

# 4. Project Vision

> **To build an intelligent real-estate platform where users can discover and evaluate properties through natural-language interaction while maintaining a scalable, secure, observable, and AI-oriented microservices architecture.**

PropertyHub should not simply provide a chatbot.

The AI should be connected to real application capabilities.

```text
User
 ↓
AI understands intent
 ↓
Structured requirement
 ↓
Retrieve knowledge when required
 ↓
Call application tools when required
 ↓
Maintain conversation context
 ↓
Generate useful response
```

---

# 5. Project Goals

## Core Goals

- Build a functional real-estate management platform.
- Implement microservices architecture.
- Implement service discovery using Eureka.
- Implement API Gateway.
- Implement dedicated authentication service.
- Implement JWT authentication.
- Implement role-based authorization.
- Implement PostgreSQL persistence.
- Implement OpenFeign communication.
- Implement validation.
- Implement global exception handling.
- Implement meaningful application logging.
- Implement Actuator.
- Implement Spring Boot Admin.
- Implement Swagger/OpenAPI.
- Implement automated testing.
- Generate JaCoCo coverage reports.

## AI Goals

- Implement Spring AI.
- Implement ChatClient.
- Implement ChatModel.
- Implement PromptTemplate.
- Implement structured responses.
- Implement permanent AI chat history.
- Implement chat memory.
- Implement embeddings.
- Implement pgvector.
- Implement RAG.
- Implement tool calling.
- Implement basic agentic AI.
- Implement MCP if time permits.

---

# 6. Scope

## 6.1 Core MVP

The mandatory business application consists of:

### Authentication

- Registration
- Login
- JWT
- Roles

### Properties

- Create property
- View property
- Update property
- Delete property
- Search properties
- Filter properties

### AI

- AI chat
- Persistent conversation
- Structured requirement extraction
- Embeddings
- pgvector
- RAG

### Engineering

- Microservices
- Eureka
- Gateway
- Feign
- Validation
- Exception handling
- Logging
- Actuator
- Admin Server
- Swagger
- JUnit
- Mockito
- JaCoCo

---

# 7. User Roles

## BUYER

A user looking for properties.

Capabilities:

- Search properties
- View property details
- Favorite properties
- Compare properties
- Schedule visits
- Use AI assistant

## AGENT

A real-estate agent managing listings.

Capabilities:

- Create properties
- Update properties
- Delete properties
- View managed properties
- View scheduled visits

## ADMIN

Platform administrator.

Capabilities:

- Manage users
- Manage properties
- View platform statistics
- Manage platform data

---

# 8. Separate Admin Panel

The **Admin Panel is a separate frontend application/interface**.

It is not the same as the normal Buyer/Agent frontend.

Recommended structure:

```text
PropertyHub
│
├── frontend/
│   └── Buyer / Agent Application
│
└── admin-panel/
    └── Admin Application
```

The Admin Panel communicates with the same API Gateway but requires:

```text
ROLE_ADMIN
```

### Admin Panel Screens

- Admin Dashboard
- User Management
- Agent Management
- Property Management
- Property Approval/Removal
- System Statistics
- AI Usage Overview
- Service/System Status

---

# 9. Admin Panel vs Spring Boot Admin

These are two completely different components.

## Admin Panel

Business application.

Used by:

> Platform Administrator

Examples:

- Manage users
- Manage properties
- View statistics

## Spring Boot Admin Server

Technical monitoring application.

Used by:

> Developer / System Administrator

Examples:

- Service health
- Actuator endpoints
- Metrics
- Application status

Architecture:

```text
                 PropertyHub
                     |
          ┌──────────┴──────────┐
          |                     |
          ▼                     ▼
    Business Admin         Technical Admin
     Admin Panel          Spring Boot Admin
          |                     |
          ▼                     ▼
    API Gateway             Actuator
```

Both should exist.

---

# 10. Microservices Architecture

The finalized architecture contains the following core services.

```text
                         ┌──────────────────┐
                         │   Eureka Server  │
                         └────────┬─────────┘
                                  │
                         Service Discovery
                                  │
                                  ▼
                         ┌──────────────────┐
                         │   API Gateway    │
                         └────────┬─────────┘
                                  │
             ┌────────────────────┼────────────────────┐
             │                    │                    │
             ▼                    ▼                    ▼
      ┌─────────────┐      ┌─────────────┐      ┌─────────────┐
      │ Auth Service│      │  Property   │      │ AI Service  │
      │             │      │  Service    │      │             │
      │ JWT + RBAC  │      │ Real Estate │      │ Spring AI   │
      └──────┬──────┘      └──────┬──────┘      └──────┬──────┘
             │                    │                    │
             └────────────────────┼────────────────────┘
                                  │
                                  ▼
                         ┌──────────────────┐
                         │   PostgreSQL     │
                         │                  │
                         │ Business Data    │
                         │ AI Chat History  │
                         │ pgvector         │
                         └──────────────────┘
```

Infrastructure:

```text
                    ┌────────────────────┐
                    │ Spring Boot Admin  │
                    └─────────┬──────────┘
                              │
                    Actuator Monitoring
                              │
              ┌───────────────┼───────────────┐
              ▼               ▼               ▼
          Auth Service   Property Service   AI Service
```

---

# 11. Service Responsibilities

## 11.1 Eureka Server

Responsibilities:

- Service registration
- Service discovery

No business logic.

---

# 12. API Gateway

Technology:

- Spring Cloud Gateway
- Eureka Discovery Client

Responsibilities:

- External entry point
- Request routing
- CORS
- Authentication filtering where appropriate
- Basic request logging

Routes:

```text
/api/auth/**        → auth-service
/api/properties/**  → property-service
/api/ai/**          → ai-service
```

---

# 13. Auth Service

Responsibilities:

- Registration
- Login
- Password encoding
- JWT generation
- User role management

APIs:

```http
POST /api/auth/register
POST /api/auth/login
GET  /api/auth/me
```

Roles:

```text
BUYER
AGENT
ADMIN
```

---

# 14. Property Service

Responsibilities:

- Property CRUD
- Property search
- Property filtering
- Favorites
- Visits
- Property comparison

The service owns property-related business logic.

It must not directly access AI service tables.

---

# 15. AI Service

The AI Service is the main technical showcase.

Responsibilities:

```text
ChatClient
ChatModel
PromptTemplate
Structured Output
Chat History
Chat Memory
Embeddings
pgvector
RAG
Tool Calling
Agentic AI
MCP
```

The service also communicates with Property Service using OpenFeign.

---

# 16. OpenFeign Communication

Synchronous internal communication will use:

> **Spring Cloud OpenFeign / FeignClient**

No direct cross-service database access should be used.

Example:

```text
AI Service
    |
    | FeignClient
    v
Property Service
    |
    v
PostgreSQL
```

For example, when the AI needs live property information:

```text
User
 ↓
AI Service
 ↓
PropertyFeignClient
 ↓
Property Service
 ↓
PropertyRepository
 ↓
PostgreSQL
```

This keeps service boundaries clear.

---

# 17. Example Feign Clients

## PropertyFeignClient

```text
getProperty()
searchProperties()
getPropertiesByIds()
```

## AuthFeignClient

Only introduce this if a real business requirement requires user information from Auth Service.

Avoid unnecessary Feign clients.

---

# 18. Recommended Layered Architecture

Every business microservice should follow a consistent layered architecture.

```text
Controller
    ↓
Service
    ↓
data-access layer
    ↓
PostgreSQL
```

Supporting layers:

```text
DTO
Mapper
Entity
Exception
Configuration
Security
```

---

# 19. Recommended Package Structure

## Auth Service

```text
com.propertyhub.auth
│
├── controller
│   └── AuthController
│
├── service
│   ├── AuthService
│   └── JwtService
│
├── data-access layer
│   └── UserRepository
│
├── entity
│   └── User
│
├── dto
│   ├── request
│   │   ├── LoginRequest
│   │   └── RegisterRequest
│   │
│   └── response
│       ├── LoginResponse
│       └── UserResponse
│
├── security
│   ├── SecurityConfig
│   ├── JwtAuthenticationFilter
│   └── CustomUserDetailsService
│
├── exception
│   ├── GlobalExceptionHandler
│   ├── UserAlreadyExistsException
│   ├── InvalidCredentialsException
│   └── ErrorResponse
│
└── config
```

---

# 20. Property Service Structure

```text
com.propertyhub.property
│
├── controller
│   ├── PropertyController
│   ├── FavoriteController
│   └── VisitController
│
├── service
│   ├── PropertyService
│   ├── FavoriteService
│   └── VisitService
│
├── data-access layer
│   ├── PropertyRepository
│   ├── FavoriteRepository
│   └── VisitRepository
│
├── entity
│   ├── Property
│   ├── Favorite
│   └── Visit
│
├── dto
│   ├── request
│   │   ├── CreatePropertyRequest
│   │   ├── UpdatePropertyRequest
│   │   └── VisitRequest
│   │
│   └── response
│       ├── PropertyResponse
│       ├── PropertySummaryResponse
│       └── VisitResponse
│
├── mapper
│   └── PropertyMapper
│
├── exception
│   ├── GlobalExceptionHandler
│   ├── ResourceNotFoundException
│   ├── DuplicateResourceException
│   └── ErrorResponse
│
├── security
│   └── SecurityConfig
│
└── config
```

---

# 21. AI Service Structure

```text
com.propertyhub.ai
│
├── controller
│   ├── AiChatController
│   └── ConversationController
│
├── service
│   ├── AiChatService
│   ├── ConversationService
│   ├── StructuredResponseService
│   ├── RagService
│   └── AgentService
│
├── data-access layer
│   ├── ConversationRepository
│   └── ChatMessageRepository
│
├── entity
│   ├── Conversation
│   └── ChatMessage
│
├── dto
│   ├── request
│   │   ├── ChatRequest
│   │   └── CreateConversationRequest
│   │
│   └── response
│       ├── ChatResponse
│       ├── ConversationResponse
│       ├── ChatMessageResponse
│       ├── PropertyRequirementResponse
│       └── PropertyRecommendationResponse
│
├── prompt
│   ├── PropertyPromptTemplate
│   └── PromptConfig
│
├── memory
│   └── ChatMemoryService
│
├── rag
│   ├── DocumentIngestionService
│   ├── EmbeddingService
│   └── VectorSearchService
│
├── tools
│   ├── PropertyTools
│   ├── MortgageTools
│   └── VisitTools
│
├── agent
│   └── PropertyAgent
│
├── client
│   └── PropertyFeignClient
│
├── exception
│   ├── GlobalExceptionHandler
│   ├── AiServiceException
│   ├── VectorSearchException
│   ├── ToolExecutionException
│   └── ErrorResponse
│
└── config
    ├── AiConfig
    ├── VectorStoreConfig
    └── SecurityConfig
```

---

# 22. DTO Design Standard

DTOs must be separated explicitly.

```text
dto/
├── request/
└── response/
```

## Request DTO

Represents data coming into the API.

Example:

```text
CreatePropertyRequest
```

## Response DTO

Represents data returned from the API.

Example:

```text
PropertyResponse
```

Entities should not be exposed directly through REST APIs.

Correct:

```text
Request DTO
    ↓
Controller
    ↓
Service
    ↓
Entity
    ↓
data-access layer
```

And:

```text
data-access layer
    ↓
Entity
    ↓
Mapper
    ↓
Response DTO
    ↓
Controller
```

---

# 23. PostgreSQL Database

PostgreSQL is the only database used.

Business tables:

```text
users
properties
favorites
visits
```

AI tables:

```text
conversations
chat_messages
```

Vector data:

```text
pgvector-backed vector store
```

---

# 24. AI Chat Persistence

Every request and response must be permanently stored.

Example:

```text
conversations
----------------------
id
user_id
title
created_at
updated_at
```

```text
chat_messages
----------------------
id
conversation_id
role
content
created_at
```

Example:

```text
Conversation 101

USER:
Find a 2 BHK in Pune under 80 lakh.

ASSISTANT:
I found 3 matching properties...
```

After restarting the application, the conversation must still exist.

---

# 25. Chat Memory

Persistent chat history will also provide the foundation for conversation memory.

Flow:

```text
New Message
     ↓
Load Previous Messages
     ↓
PostgreSQL
     ↓
Build Conversation Context
     ↓
ChatClient
     ↓
ChatModel
     ↓
Response
     ↓
Persist Response
```

Example:

```text
User:
Find me a 2 BHK under 80 lakh.

AI:
Here are three properties.

User:
Which one has parking?

AI:
Property P102 has covered parking.
```

The phrase:

> "Which one"

depends on previous context.

---

# 26. Spring AI

Spring AI will be used as the primary AI framework.

The implementation should demonstrate:

```text
Spring AI
│
├── ChatClient
├── ChatModel
├── PromptTemplate
├── Structured Output
├── Chat Memory
├── EmbeddingModel
├── PgVectorStore
├── RAG
├── Tool Calling
└── MCP
```

---

# 27. ChatClient + ChatModel

Basic AI API:

```http
POST /api/ai/chat
```

Request:

```json
{
  "conversationId": 101,
  "message": "Find me a 2 BHK in Pune under 80 lakh."
}
```

Response:

```json
{
  "conversationId": 101,
  "messageId": 501,
  "response": "I found three properties that match your requirement."
}
```

---

# 28. Prompt Templates

Prompts should not be scattered throughout Java service methods.

Example:

```text
prompts/
└── property-assistant.st
```

Conceptual template:

```text
You are PropertyHub AI.

User Request:
{userMessage}

Conversation Context:
{conversationHistory}

Property Context:
{propertyContext}

Knowledge Context:
{ragContext}

Rules:
- Do not invent property information.
- Use provided information when available.
- Ask for clarification when required.
- Give concise and useful answers.
```

---

# 29. Structured AI Response

Natural-language input:

> Find me a 2 BHK in Pune under 80 lakh with parking.

Structured output:

```json
{
  "city": "Pune",
  "bhk": 2,
  "maxBudget": 8000000,
  "parkingRequired": true
}
```

Java representation:

```text
PropertyRequirementResponse
```

This structured object can then be passed to the property search process.

---

# 30. Embeddings

Knowledge documents will be converted into embeddings.

```text
Document
   ↓
EmbeddingModel
   ↓
Vector
   ↓
pgvector
```

Example knowledge:

```text
Pune Locality Guide
Hinjewadi Guide
Wakad Guide
Property Buying FAQ
Home Loan FAQ
Property Documentation Guide
```

---

# 31. pgvector

pgvector stores the generated embeddings in PostgreSQL.

The architecture is:

```text
PostgreSQL
│
├── Business Data
│
├── Chat History
│
└── Vector Store
       │
       ├── Embedding
       ├── Content
       └── Metadata
```

No separate vector database is required.

---

# 32. RAG

RAG is the primary advanced AI milestone.

Flow:

```text
User Question
      ↓
EmbeddingModel
      ↓
Query Vector
      ↓
pgvector similarity search
      ↓
Relevant Documents
      ↓
PromptTemplate
      ↓
ChatModel
      ↓
Grounded Answer
```

Example:

> Is Hinjewadi a suitable locality for IT professionals?

The system retrieves relevant locality information from pgvector before generating the response.

---

# 33. AI + Property Data

The strongest RAG scenario combines:

```text
Structured Requirements
        +
Property Search
        +
RAG
```

Example:

> "Find me a 2 BHK under ₹80 lakh in Pune and tell me whether the locality is suitable for an IT professional."

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

This should be the main AI demonstration.

---

# 34. Tool Calling

Tool calling comes after RAG.

Initial tools:

```text
searchProperties()
getPropertyDetails()
calculateEMI()
schedulePropertyVisit()
```

Example:

> Calculate EMI for ₹70 lakh at 8.5% for 20 years.

The model can invoke:

```text
calculateEMI()
```

The application executes the tool and provides the result back to the model.

---

# 35. Agentic AI

Agentic AI is implemented after the RAG workflow is stable.

Example:

> Find the best property for me and tell me whether its locality is suitable for an IT professional.

The agent can combine:

```text
Property Search
      +
RAG
      +
Reasoning
      ↓
Recommendation
```

Keep the agent simple.

A complex multi-agent architecture is unnecessary for this project.

---

# 36. MCP Server

MCP is the final AI enhancement.

Only implement it if all mandatory features are stable.

Potential MCP capabilities:

```text
search_properties
get_property
calculate_emi
get_locality_information
```

Conceptually:

```text
AI Service
     ↓
MCP Client
     ↓
MCP Server
     ↓
Property Capabilities
```

MCP should never be allowed to jeopardize RAG or testing.

---

# 37. Security Architecture

Authentication is handled by Auth Service.

```text
Client
   ↓
API Gateway
   ↓
JWT
   ↓
Spring Security
   ↓
Role Authorization
```

Roles:

```text
BUYER
AGENT
ADMIN
```

Passwords must be encoded.

JWT secrets/keys must be externalized and never committed to source control.

---

# 38. Service-to-Service Security

OpenFeign is used for internal synchronous calls.

Example:

```text
AI Service
   |
   | PropertyFeignClient
   ↓
Property Service
```

The services should not bypass the Property Service and query its database directly.

---

# 39. Validation

Use Jakarta Bean Validation.

Examples:

```text
email → valid email
password → minimum length
price → positive
bhk → positive
area → positive
title → required
city → required
```

Validation failures are handled centrally.

---

# 40. Global Exception Handling

Every business service must implement:

```java
@RestControllerAdvice
```

Recommended structure:

```text
exception/
├── GlobalExceptionHandler
├── ResourceNotFoundException
├── DuplicateResourceException
├── InvalidRequestException
├── UnauthorizedException
├── ForbiddenException
├── AiServiceException
├── VectorSearchException
├── ToolExecutionException
└── ErrorResponse
```

Standard response:

```json
{
  "timestamp": "...",
  "status": 404,
  "error": "PROPERTY_NOT_FOUND",
  "message": "Property with id 101 was not found",
  "path": "/api/properties/101"
}
```

Validation errors should also have a consistent response structure.

---

# 41. Logging

Logging is intended to demonstrate and trace application behavior.

Use:

```text
SLF4J
+
Logback
```

Do not build centralized logging infrastructure.

## Authentication logs

```text
INFO  Registration request received
INFO  User registered successfully
INFO  Login successful
WARN  Authentication failed
```

## Property logs

```text
INFO  Property creation requested
INFO  Property created successfully
INFO  Property search started
INFO  Property search completed: resultCount=5
WARN  Property not found
```

## AI logs

```text
INFO  AI chat request received
INFO  Conversation history loaded
INFO  Prompt prepared
INFO  ChatModel request started
INFO  ChatModel response received
INFO  AI response persisted
```

## RAG logs

```text
INFO  RAG processing started
INFO  Query embedding generated
INFO  pgvector search started
INFO  Relevant documents retrieved
INFO  RAG context added
INFO  RAG response generated
```

## Tool logs

```text
INFO  Tool execution started: searchProperties
INFO  Tool execution completed
```

## Errors

```text
ERROR AI model invocation failed
ERROR Database operation failed
ERROR Tool execution failed
```

Do not log:

- Passwords
- JWT tokens
- API keys
- Authorization headers
- Sensitive personal information

---

# 42. No Correlation ID Requirement

Correlation IDs and distributed tracing are intentionally out of scope.

The objective is not to build an enterprise observability platform.

The objective is:

> **Make the application flow clearly visible through meaningful logs.**

This is sufficient for the two-day evaluation.

---

# 43. Actuator

Every major Spring Boot service should expose Actuator.

Important endpoints:

```text
/actuator/health
/actuator/info
/actuator/metrics
```

Use appropriate security restrictions for management endpoints.

---

# 44. Spring Boot Admin

A separate technical monitoring application will monitor:

```text
Auth Service
Property Service
AI Service
Gateway
```

The evaluator can see:

- Service status
- Health
- Metrics
- Runtime information

---

# 45. Swagger / OpenAPI

Each REST service should provide OpenAPI documentation.

At minimum:

```text
Auth APIs
Property APIs
AI APIs
```

Document:

- Request DTOs
- Response DTOs
- Validation
- HTTP status codes
- Security requirements
- API descriptions

---

# 46. Testing Strategy

Testing focuses on important business and AI orchestration logic.

## Auth

- Registration
- Duplicate email
- Login
- Invalid credentials
- JWT generation

## Property

- Create
- Get
- Update
- Delete
- Search
- Validation
- Not found

## AI

- Chat request
- Chat persistence
- Conversation retrieval
- Structured response
- Embedding flow
- RAG retrieval
- Empty RAG result
- AI exception handling
- Tool invocation

---

# 47. JUnit + Mockito

Use Mockito to isolate business logic.

For example:

```text
AiChatService
     |
     ├── ChatClient
     ├── ConversationRepository
     ├── ChatMessageRepository
     ├── VectorStore
     └── PropertyFeignClient
```

Mock these dependencies when testing `AiChatService`.

The objective is to test **our application's orchestration**, not the external LLM itself.

---

# 48. JaCoCo

JaCoCo will generate test coverage reports.

Prioritize coverage for:

```text
AuthService
PropertyService
PropertySearchService
AiChatService
RagService
```

Target:

> Approximately 70%+ coverage for core business logic.

Do not waste the two-day deadline attempting 100% coverage.

---

# 49. Frontend Architecture

The frontend consists of two separate applications/interfaces.

```text
frontend/
    Buyer + Agent Application

admin-panel/
    Admin Application
```

Both communicate through:

```text
API Gateway
```

---

# 50. Buyer / Agent Application

Recommended screens:

## Login/Register

```text
PropertyHub
Welcome to intelligent property discovery

Email
Password

[ Login ]
```

## Dashboard

```text
Welcome back!

Search Properties

[ Pune ] [ 2 BHK ] [ ₹80L ]

Property Cards
```

## Property Details

Display:

- Images
- Price
- BHK
- Area
- Location
- Amenities
- AI Match Score

Actions:

```text
[ Favorite ]
[ Compare ]
[ Schedule Visit ]
[ Ask AI ]
```

## AI Copilot

The primary showcase screen.

```text
PropertyHub AI

You:
Find me a 2 BHK in Pune under ₹80 lakh.

AI:
I found 3 matching properties.

Property A — 91%
Property B — 87%
Property C — 83%

You:
Which one has parking?

AI:
Property A has covered parking...
```

---

# 51. Admin Panel

The Admin Panel should visually resemble a professional management dashboard.

Inspired by the provided dashboard reference, the layout should contain:

```text
┌─────────────────────────────────────────────┐
│ PropertyHub Admin                           │
├──────────────┬──────────────────────────────┤
│ Dashboard    │ Welcome, Admin               │
│ Properties   │                              │
│ Users        │ ┌──────┐ ┌──────┐ ┌──────┐ │
│ Agents       │ │Users │ │Props │ │Visits│ │
│ Tenants*     │ └──────┘ └──────┘ └──────┘ │
│ Leases*      │                              │
│ Payments*    │ ┌─────────────────────────┐ │
│ Maintenance*│ │ Property Activity       │ │
│ Reports      │ │                         │ │
│ AI Analytics │ │        Chart            │ │
│ Settings     │ └─────────────────────────┘ │
└──────────────┴──────────────────────────────┘
```

The starred items can remain future modules if not implemented.

---

# 52. Core Admin Dashboard

The MVP Admin Panel should display:

### Total Properties

```text
125
```

### Total Users

```text
340
```

### Total Agents

```text
42
```

### AI Conversations

```text
1,248
```

### Recent Properties

A table showing:

```text
Property
Agent
Location
Price
Status
Created Date
```

### Recent Users

```text
Name
Email
Role
Status
Created Date
```

---

# 53. AI Analytics in Admin Panel

This is a good AI-oriented extra feature that is relatively cheap to implement.

Display:

```text
Total AI Conversations
Total AI Messages
RAG Queries
Tool Calls
Most Asked Property Locations
```

Example:

```text
AI Activity

Total Conversations       1,248
Total AI Messages         5,823
RAG Queries               1,240
Tool Calls                864
```

This connects the Admin Panel directly to the AI aspect of the project.

---

# 54. Property Management UI

Admin should have:

```text
Properties
-----------------------------------------
Property   Location   Price   Agent Status
P101       Pune       ₹72L    Rahul Active
P102       Pune       ₹80L    Amit  Active
P103       Indore     ₹55L    Priya Pending
```

Actions:

```text
View
Edit
Delete
Approve
```

---

# 55. Extra Application Features

The following features are intentionally **not required for the 2-day MVP**, but they can make PropertyHub look like a much more complete real-estate management platform if time permits.

They should remain at the end of the project plan so they never interfere with the mandatory AI implementation.

---

# 56. Extra Feature 1 — Tenant Management

Add:

```text
Tenant Service / Tenant Module
```

Capabilities:

- Tenant profiles
- Tenant-property association
- Tenant status
- Contact information

Admin Panel:

```text
Tenants
---------------------------
Name
Property
Lease Status
Contact
```

---

# 57. Extra Feature 2 — Lease Management

Manage:

- Lease agreements
- Start date
- End date
- Monthly rent
- Security deposit
- Lease status

Example:

```text
Lease #L102
Property: Apartment 301
Tenant: Rahul Sharma
Rent: ₹25,000
Start: 01-08-2026
End: 31-07-2027
Status: Active
```

---

# 58. Extra Feature 3 — Payment Service

A dedicated:

```text
payment-service
```

could manage:

- Rent payments
- Security deposits
- Payment status
- Payment history
- Pending payments

Dashboard:

```text
Rental Income
₹10,00,000

Pending Payments
₹2,00,000
```

Actual payment gateway integration is not required.

A simulated payment workflow can be used for demonstration.

---

# 59. Extra Feature 4 — Notification Service

A dedicated:

```text
notification-service
```

could support:

```text
Email
SMS
WhatsApp
In-App Notifications
```

Possible events:

```text
Property Visit Scheduled
Payment Reminder
Lease Expiring
New Property Match
Property Approved
Maintenance Update
```

Example:

```text
User schedules visit
        ↓
Notification Service
        ↓
Email
SMS
WhatsApp
```

For the two-day project, this should be considered optional.

---

# 60. Extra Feature 5 — Maintenance Management

Inspired by the dashboard reference.

Users/tenants can submit:

```text
Maintenance Request
```

Example:

```text
Property: Sunset Apartments
Unit: 301
Issue: Water leak in bathroom
Priority: Critical
Status: Open
```

Admin/agent can:

- View requests
- Assign request
- Change priority
- Update status
- Close request

---

# 61. Extra Feature 6 — Lease & Occupancy Dashboard

Admin dashboard can display:

```text
Total Properties
Total Units
Occupied Units
Vacant Units
Occupancy Rate
```

Example:

```text
Total Units       192
Occupied          178
Vacant             14
Occupancy         92.7%
```

This directly provides the dashboard-style experience shown in the reference image.

---

# 62. Extra Feature 7 — Financial Dashboard

Display:

```text
Monthly Revenue
Collected Amount
Pending Payments
Expenses
```

Charts:

- Monthly revenue
- Collected vs pending
- Property-wise income

This is an excellent visual enhancement for the Admin Panel.

---

# 63. Extra Feature 8 — Reports

Generate reports such as:

```text
Property Report
User Report
Agent Report
Revenue Report
Occupancy Report
AI Usage Report
```

Reports could initially be displayed as tables/charts rather than generated PDFs.

---

# 64. Extra Feature 9 — Property Recommendation History

Store previous AI recommendations.

Example:

```text
Recommendation History

User: Rahul
Request:
2 BHK Pune under ₹80L

Recommended:
P102
P108
P115

Date:
19 Aug 2026
```

This can be built using the existing AI chat infrastructure.

---

# 65. Extra Feature 10 — Saved Searches

A user can save:

```text
2 BHK
Pune
₹80L maximum
Parking required
```

Then revisit the saved search later.

Potential future extension:

```text
Saved Search
      ↓
New Matching Property
      ↓
Notification Service
```

---

# 66. Extra Feature 11 — Property Comparison

Allow users to select multiple properties.

Example:

| Feature | Property A | Property B |
|---|---:|---:|
| Price | ₹72L | ₹76L |
| BHK | 2 | 2 |
| Area | 1150 sqft | 1280 sqft |
| Parking | Yes | Yes |
| Location | Hinjewadi | Wakad |
| AI Match | 91% | 87% |

The AI can explain which property better matches the user's requirements.

---

# 67. Extra Feature 12 — AI Property Match Score

For each property:

```text
AI Match Score
91%
```

Criteria:

```text
✓ Budget
✓ Location
✓ BHK
✓ Parking
✓ Area
```

This is one of the highest-value AI enhancements because it combines structured output with the actual business domain.

---

# 68. Extra Feature 13 — AI Property Comparison

User:

> Compare these two properties and tell me which is better for my requirements.

AI can generate:

```text
Property A
91% match

Property B
84% match

Recommendation:
Property A better satisfies the stated budget
and parking requirement.
```

---

# 69. Extra Feature 14 — AI-Powered Admin Insights

Admin can ask:

> "Which city has the most active property listings?"

Or:

> "What are the most common property requirements in AI conversations?"

This could eventually combine:

```text
PostgreSQL analytics
+
AI
```

---

# 70. Extra Feature 15 — Notification Service Architecture

If implemented:

```text
                  PropertyHub
                       |
                       v
              Notification Service
                       |
          ┌────────────┼────────────┐
          ▼            ▼            ▼
        Email          SMS       WhatsApp
```

Possible technologies can be selected based on what is approved and available in the corporate environment.

---

# 71. Extra Feature 16 — Payment Service Architecture

If implemented:

```text
Property Service
      |
      v
Payment Service
      |
      v
PostgreSQL
```

Responsibilities:

- Payment records
- Payment status
- Transaction history
- Payment reminders

A real payment gateway should only be introduced if explicitly required.

---

# 72. Extra Feature Priority

## Tier 1 — Very High Value / Low Effort

Implement only after the mandatory requirements:

```text
AI Match Score
Property Comparison
Admin Dashboard Statistics
AI Usage Statistics
Saved Searches
```

## Tier 2 — Medium Effort

```text
Tenant Management
Lease Management
Maintenance Management
Reports
Occupancy Dashboard
Financial Dashboard
```

## Tier 3 — High Effort

```text
Payment Service
Notification Service
Email
SMS
WhatsApp
Advanced Analytics
```

---

# 73. Final MVP Boundary

The project is considered complete without the optional features when the following are working:

```text
✓ Microservices
✓ Eureka
✓ API Gateway
✓ Auth Service
✓ JWT
✓ RBAC
✓ Property Service
✓ PostgreSQL
✓ OpenFeign
✓ Validation
✓ Global Exceptions
✓ Logging
✓ Actuator
✓ Spring Boot Admin
✓ Swagger
✓ Separate Admin Panel

AI:

✓ Spring AI
✓ ChatClient
✓ ChatModel
✓ PromptTemplate
✓ Structured Response
✓ Persistent Chat History
✓ Chat Memory
✓ Embeddings
✓ pgvector
✓ RAG

Testing:

✓ JUnit
✓ Mockito
✓ MockMvc
✓ JaCoCo
```

Then:

```text
OPTIONAL

○ Tool Calling
○ Agentic AI
○ MCP
○ Notification Service
○ Payment Service
○ Tenant Module
○ Lease Module
○ Maintenance Module
○ Reports
○ Advanced Dashboard
```

---

# 74. Final Two-Day Development Strategy

## Day 1

### Infrastructure

```text
Eureka
Gateway
Auth Service
Property Service
AI Service
Admin Server
```

### Database

```text
PostgreSQL
Business Tables
AI Chat Tables
pgvector
```

### Security

```text
JWT
RBAC
```

### Property MVP

```text
CRUD
Search
```

### AI Foundation

```text
ChatClient
ChatModel
PromptTemplate
Structured Output
```

### Persistent AI

```text
Conversation
Chat Messages
Chat Memory
```

### RAG

```text
Embeddings
pgvector
Retrieval
Grounded Response
```

### Day 1 Success Condition

```text
Microservices
+
Security
+
PostgreSQL
+
AI Chat
+
Permanent History
+
Embeddings
+
pgvector
+
RAG
```

---

# 75. Day 2

## Morning

```text
Structured AI refinement
RAG refinement
Property AI recommendations
Tool Calling
```

## Midday

```text
Agentic AI
```

## Afternoon

```text
Logging
Global Exceptions
Actuator
Spring Boot Admin
Swagger
```

## Testing

```text
JUnit
Mockito
MockMvc
JaCoCo
```

## Frontend

```text
Buyer/Agent UI
AI Chat UI
Admin Panel
```

## Final Time

```text
MCP
```

Only if everything else is stable.

---

# 76. Final Architecture Philosophy

PropertyHub should follow this principle:

> **Keep the business application intentionally small, but make the AI architecture meaningful.**

The project does not need 15 microservices to demonstrate microservices.

It needs:

```text
Auth
Property
AI
Gateway
Eureka
```

with:

```text
PostgreSQL
pgvector
OpenFeign
JWT
RAG
```

and strong engineering practices around them.

The AI should not be a disconnected chatbot.

It should interact with the actual PropertyHub domain:

```text
                PropertyHub AI
                     |
        ┌────────────┼────────────┐
        ▼            ▼            ▼
   PostgreSQL      pgvector    Property
   Chat History    RAG         Service
                                  |
                               Feign
```

---

# 77. Final Project Positioning

The project should be presented as:

> **PropertyHub is an AI-oriented, microservices-based real-estate management platform that combines secure property management with persistent conversational AI, semantic search using PostgreSQL pgvector, Retrieval-Augmented Generation, and intelligent business-tool integration.**

The key technical story is:

```text
Natural Language
      ↓
Spring AI ChatClient
      ↓
Structured Requirement
      ↓
Conversation Memory
      ↓
Property Service via Feign
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
MCP*
      ↓
Useful Property Recommendation
```

`* MCP is implemented only if time permits.`

---

# 78. Final Non-Negotiable Rules

The coding agent must follow these rules throughout development:

1. No monolithic application.
2. No Docker.
3. No containerization.
4. PostgreSQL only.
5. pgvector for vector storage.
6. No direct cross-service database access.
7. Use OpenFeign for synchronous service communication.
8. Use DTOs with separate `request` and `response` packages.
9. Do not expose JPA entities directly through REST APIs.
10. Use service and data-access layer layers.
11. Use centralized global exception handling.
12. Use meaningful SLF4J/Logback logs.
13. Do not implement unnecessary distributed tracing.
14. Do not log passwords, tokens, API keys, or sensitive data.
15. Persist every AI request and response.
16. Persist conversation history permanently in PostgreSQL.
17. RAG must be completed before Agentic AI.
18. Agentic AI must be completed before MCP.
19. MCP must never jeopardize the mandatory requirements.
20. Every AI-generated implementation must be compiled and tested before moving to the next feature.
21. Do not introduce unnecessary dependencies.
22. Do not create unnecessary microservices.
23. Prefer simple, maintainable implementations suitable for the two-day deadline.
24. All external AI/tool usage must comply with the company's Privacy & Data Protection restrictions.

---

# 79. Final Technology Stack

| Category | Technology |
|---|---|
| Language | Java |
| Backend | Spring Boot |
| Architecture | Microservices |
| Service Discovery | Eureka |
| Gateway | Spring Cloud Gateway |
| Communication | Spring Cloud OpenFeign |
| Database | PostgreSQL |
| Vector Store | pgvector |
| ORM | Spring Data JPA / Hibernate |
| Security | Spring Security |
| Authentication | JWT |
| Authorization | RBAC |
| AI Framework | Spring AI |
| Chat | ChatClient + ChatModel |
| Prompting | PromptTemplate |
| Structured Output | Spring AI Structured Output |
| Memory | Persistent PostgreSQL-backed history |
| Embeddings | Spring AI EmbeddingModel |
| RAG | Spring AI + pgvector |
| Tools | Spring AI Tool Calling |
| Agentic AI | Spring AI-based orchestration |
| MCP | Spring AI MCP |
| Validation | Jakarta Bean Validation |
| Exception Handling | `@RestControllerAdvice` |
| Logging | SLF4J + Logback |
| Monitoring | Spring Boot Actuator |
| Technical Admin | Spring Boot Admin |
| Business Admin | Separate Admin Panel |
| API Documentation | Swagger / OpenAPI |
| Testing | JUnit 5 |
| Mocking | Mockito |
| Integration Testing | Spring Boot Test / MockMvc |
| Coverage | JaCoCo |
| Containerization | **None** |
| Docker | **Not Used** |

---

# 80. Final Priority

The entire project can ultimately be reduced to this priority stack:

```text
                 PROPERTYHUB
                      │
                      ▼
             ┌─────────────────┐
             │ Microservices   │
             │ Eureka + Gateway│
             └────────┬────────┘
                      │
          ┌───────────┼───────────┐
          ▼           ▼           ▼
        Auth       Property       AI
        JWT        Service       Service
        RBAC          │             │
                      │             │
                      └──────┬──────┘
                             ▼
                       PostgreSQL
                             │
                  ┌──────────┴─────────┐
                  ▼                    ▼
             Chat History          pgvector
                                       │
                                       ▼
                                      RAG
                                       │
                                       ▼
                                Tool Calling
                                       │
                                       ▼
                                  Agentic AI
                                       │
                                       ▼
                                     MCP*
```

### **MVP = everything through RAG.**

### **Strong final project = RAG + Tool Calling + Agentic AI.**

### **Standout bonus = MCP.**

### **Extra product features = Admin analytics, comparison, match score, tenants, leases, payments, notifications, maintenance, reports.**

This gives us a **very clear 2-day boundary**: we are not trying to build every feature in the reference dashboard. We build the core PropertyHub platform and make the **AI Property Copilot the centerpiece**, while keeping the additional real-estate-management modules available as documented extension points.

# PropertyHub Frontend and Shared Validation Addendum

This addendum extends the original PropertyHub requirements with the agreed frontend and validation standards.

## A. Frontend Applications

PropertyHub has two separate React applications:

```text
frontend/
    Buyer + Agent Application

admin-panel/
    Admin Application
```

Both applications must be created with Vite using React.

Preferred setup:

```text
React + Vite
Axios
Vitest
@vitest/ui
```

Do not introduce a frontend framework or build system that is not necessary for the MVP.

## B. React Project Creation

Create React applications using Vite.

The exact scaffolding command should use the team's approved local Node.js/npm setup and the current Vite React template.

The implementation should stay simple enough to explain in a technical presentation.

## C. HTTP Client

Use Axios for frontend-to-backend HTTP communication.

Keep Axios usage centralized through a small API/client layer so that:

- base URL configuration is centralized
- JWT handling is centralized
- common error handling is centralized
- request/response behavior remains easy to explain
- UI components do not contain repeated HTTP configuration

Do not create a large client abstraction.

## D. UI Design Standard

The visual direction is:

> Claymorphism

Use Claymorphism consistently but keep the design professional and readable rather than decorative.

The UI should use a logical visual hierarchy:

```text
Application Shell
    ↓
Navigation
    ↓
Page Header
    ↓
Primary Action / Search
    ↓
Content Cards / Tables
    ↓
Secondary Actions
    ↓
Feedback / Toast Area
```

Avoid excessive shadows, oversized cards, random gradients, or decorative elements that reduce usability.

### Buyer / Agent UI

Recommended logical screens:

```text
Login / Register
Dashboard
Property Search
Property Details
Favorites
Comparison
Scheduled Visits
AI Copilot
```

### Admin UI

Recommended logical screens:

```text
Admin Dashboard
Users
Agents
Properties
Property Approval
Statistics
AI Analytics
System / Service Status
```

## E. Toast Feedback

The frontend must provide short-lived, user-friendly toast notifications for meaningful activities.

Examples:

```text
Property created successfully
Property updated successfully
Property deleted successfully
Added to favorites
Removed from favorites
Visit scheduled successfully
Login successful
Registration successful
Unable to load properties
Invalid credentials
Validation failed
AI request failed
Server unavailable
```

Toasts must:

- appear near the active interaction area without obscuring core content
- have clear success/error/information semantics
- automatically disappear after a timeout
- remain dismissible where appropriate
- not replace inline validation for form fields

The timeout should be consistent throughout the application.

## F. Frontend Validation

Frontend validation must mirror the same business validation rules used by the backend.

The frontend should provide immediate feedback before an API request is sent.

Examples:

```text
email    → required + valid email format
password → required + minimum length
price    → required + positive
bhk      → required + positive
area     → required + positive
title    → required
city     → required
```

Do not duplicate unrelated or conflicting rules in the frontend.

## G. Backend Validation

Backend validation remains authoritative for API requests.

Use Jakarta Bean Validation at the API boundary.

The backend must validate the same business rules represented in the frontend.

Backend validation must never trust the frontend.

## H. Database Validation / Integrity

Database constraints must enforce the persistent data invariants that can be enforced safely at the database level.

Examples:

```text
NOT NULL
UNIQUE
CHECK (price > 0)
CHECK (bhk > 0)
CHECK (area > 0)
FOREIGN KEY
```

Database validation is the final persistence-integrity boundary.

Do not attempt to store plaintext passwords or use database constraints to validate plaintext password strength.

Password strength is validated before hashing at the frontend/backend boundary; only the encoded password is persisted.

## I. Shared Validation Contract

The same logical validation contract must be maintained across:

```text
Frontend
   ↓
Backend
   ↓
Database
```

The rule meanings must remain consistent.

Example:

| Field | Frontend | Backend | Database |
|---|---|---|---|
| title | required | required | NOT NULL |
| city | required | required | NOT NULL |
| price | positive | positive | CHECK > 0 |
| bhk | positive | positive | CHECK > 0 |
| area | positive | positive | CHECK > 0 |
| email | required + valid format | required + valid format | uniqueness / integrity as appropriate |
| password | required + minimum length | required + minimum length | only encoded value persisted |

The layers have different responsibilities:

```text
Frontend
→ fast user feedback

Backend
→ authoritative business/API validation

Database
→ final persistent data integrity
```

Do not weaken backend validation because frontend validation exists.

Do not rely on the database to replace business validation that belongs in the backend.

## J. Consistent Validation Errors

Use a consistent validation error structure between backend and frontend.

The frontend should be able to map backend validation errors to the correct form field or display a clear general message.

Do not expose raw stack traces or internal exceptions to users.

## K. Frontend Testing

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
- critical business UI logic
- protected/role-specific UI behavior where implemented

Vitest UI must be available for visually reviewing the test suite and its results.

Use the Vitest UI or HTML reporting facilities supported by the installed Vitest version.

Official Vitest documentation confirms `@vitest/ui` and the `--ui` mode for browser-based test inspection. The HTML reporter is also available for report output.
