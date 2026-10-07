# PayOps AI — System Architecture

## 1. Purpose

This document defines the technical architecture for the PayOps AI hackathon MVP.

The architecture is designed to support:

- secure user authentication;
- PayPal Sandbox integration;
- transaction and invoice retrieval;
- payment reconciliation;
- operational issue detection;
- deterministic priority analysis;
- AI explanation and recommendation;
- human-reviewed financial actions;
- auditability;
- secure deployment.

The architecture deliberately separates:

- financial data;
- deterministic financial analysis;
- AI reasoning;
- user authorization;
- PayPal action execution.

---

# 2. Core Architectural Principle

PayOps AI follows this model:

```text
PayPal
   ↓
Trusted Financial Data
   ↓
Operations Intelligence Engine
   ↓
Verified Operational Findings
   ↓
Gemini
   ↓
Explanation / Recommendation
   ↓
User Decision
   ↓
Validated Backend Action
   ↓
PayPal API
```

The responsibilities are deliberately separated.

### PayPal

Provides trusted payment infrastructure and financial records.

### Backend Intelligence Engine

Determines financial facts using deterministic logic.

### Gemini

Explains findings, understands user intent, recommends actions, and prepares workflows.

### Merchant

Remains responsible for approving sensitive financial operations.

---

# 3. Technology Stack

## Frontend

**React**

Responsibilities:

- login/logout interface;
- payment dashboard;
- operations queue;
- AI Copilot;
- transaction views;
- invoice views;
- action previews;
- confirmation dialogs;
- API communication.

---

## Backend

**Java 21 + Spring Boot**

Responsibilities:

- REST API;
- authentication;
- authorization;
- business logic;
- PayPal integration;
- Operations Intelligence Engine;
- Gemini orchestration;
- validation;
- action-state management;
- audit logging;
- webhook processing;
- database access.

---

## Database

**PostgreSQL**

Used for application-owned data including:

- users;
- conversations;
- messages;
- operational issues where persisted;
- action requests;
- audit events;
- webhook event tracking.

---

## Payment Provider

**PayPal Developer Platform**

Environment:

**PayPal Sandbox**

Used for:

- invoice information;
- payment information;
- transaction information;
- invoice creation;
- relevant PayPal events.

---

## Artificial Intelligence

**Google Gemini API**

Model:

```text
gemini-2.5-flash-lite
```

Used for:

- natural-language understanding;
- approved tool selection;
- explanation of findings;
- recommendations;
- summarization;
- conversational follow-up;
- structured extraction;
- action preparation.

---

# 4. High-Level Architecture

```text
                         ┌─────────────────────┐
                         │        USER         │
                         └──────────┬──────────┘
                                    │
                                    ▼
                         ┌─────────────────────┐
                         │   React Frontend    │
                         │                     │
                         │ Dashboard           │
                         │ Operations Queue    │
                         │ AI Copilot          │
                         │ Transactions        │
                         │ Invoices            │
                         │ Action Review       │
                         └──────────┬──────────┘
                                    │
                                HTTPS / REST
                                    │
                                    ▼
                    ┌───────────────────────────────┐
                    │      Spring Boot Backend      │
                    │                               │
                    │ Authentication                │
                    │ Authorization                 │
                    │ Validation                    │
                    │ Business Services             │
                    │ AI Orchestration              │
                    │ Action Management             │
                    │ Audit Logging                 │
                    └──────┬───────────┬────────────┘
                           │           │
                           │           │
                ┌──────────▼───┐   ┌──▼─────────────┐
                │ Operations   │   │ Gemini API     │
                │ Intelligence │   │ 2.5 Flash-Lite │
                │ Engine       │   └────────────────┘
                └──────┬───────┘
                       │
                 ┌─────▼─────┐
                 │ PayPal API│
                 │ Sandbox   │
                 └─────┬─────┘
                       │
                  ┌────▼────┐
                  │PostgreSQL│
                  └──────────┘
```

PostgreSQL is connected primarily to Spring Boot, not directly to PayPal.

---

# 5. Main Application Modules

The backend will be organized into several major modules.

```text
Authentication
Payment Integration
Operations Intelligence
AI Copilot
Financial Actions
Audit
Persistence
```

---

# 6. Frontend Architecture

Suggested React structure:

```text
frontend/
│
├── src/
│
├── pages/
│   ├── LoginPage
│   ├── DashboardPage
│   ├── OperationsPage
│   ├── CopilotPage
│   ├── TransactionsPage
│   ├── InvoicesPage
│   └── InvoiceDetailPage
│
├── components/
│   ├── dashboard/
│   ├── operations/
│   ├── copilot/
│   ├── transactions/
│   ├── invoices/
│   ├── actions/
│   └── common/
│
├── services/
│   └── api/
│
├── hooks/
├── context/
├── types/
├── routes/
└── utils/
```

---

# 7. Frontend Routes

Initial routes:

```text
/login

/dashboard

/operations

/copilot

/transactions

/invoices

/invoices/:invoiceId
```

All routes except `/login` should require authentication.

---

# 8. Spring Boot Package Structure

Suggested package structure:

```text
com.payops
│
├── auth
├── user
├── paypal
├── intelligence
│   ├── reconciliation
│   ├── issue
│   ├── priority
│   ├── anomaly
│   └── summary
├── ai
│   ├── provider
│   ├── orchestration
│   ├── tools
│   └── prompt
├── copilot
├── transaction
├── invoice
├── action
├── audit
├── webhook
├── config
└── exception
```

---

# 9. Authentication Architecture

The MVP will use:

- Spring Security;
- BCrypt;
- JWT authentication;
- protected backend routes.

Authentication flow:

```text
User
 ↓
POST /api/auth/login
 ↓
Spring Security
 ↓
Validate credentials
 ↓
BCrypt verification
 ↓
JWT issued
 ↓
React stores authentication state
 ↓
JWT included in protected requests
```

For the hackathon, one seeded demo merchant account is sufficient.

---

# 10. PayOps Authentication vs PayPal Authentication

These are separate.

## PayOps Authentication

Identifies the merchant using PayOps AI.

## PayPal Authentication

Allows the Spring Boot backend to communicate with PayPal Sandbox.

Flow:

```text
Merchant
 ↓
PayOps Login
 ↓
Spring Boot
 ↓
PayPal OAuth
 ↓
PayPal Sandbox
```

The merchant's PayOps password is never sent to PayPal.

---

# 11. PayPal Integration Layer

PayPal-specific logic should be isolated from the rest of the system.

Suggested components:

```text
PayPalAuthService

PayPalTransactionService

PayPalInvoiceService

PayPalWebhookService

PayPalClient
```

Business services should not depend directly on raw PayPal HTTP responses.

---

# 12. PayPal Data Normalization

Raw PayPal responses should be converted into internal DTOs.

Flow:

```text
PayPal Raw Response
      ↓
PayPal DTO
      ↓
Application DTO
      ↓
Operations Intelligence
      ↓
Frontend / Gemini
```

For example:

```json
{
  "invoiceId": "INV2-123",
  "invoiceNumber": "INV-104",
  "customerName": "ABC Corp",
  "amount": 3000,
  "paidAmount": 0,
  "currency": "CAD",
  "dueDate": "2026-09-19",
  "status": "UNPAID"
}
```

This prevents the rest of the application from being tightly coupled to PayPal's response structure.

---

# 13. Operations Intelligence Engine

The Operations Intelligence Engine is responsible for converting financial data into verified operational findings.

It should not depend on Gemini for critical calculations.

The engine may contain:

```text
ReconciliationService

OverdueDetectionService

PartialPaymentService

DuplicateDetectionService

PriorityScoringService

OperationsSummaryService
```

---

# 14. Reconciliation Service

The reconciliation service compares invoice expectations with payment information.

Conceptually:

```text
Invoice
Expected: $1,200
      +
Payments
Received: $800
      ↓
ReconciliationService
      ↓
PARTIALLY_PAID
Remaining: $400
```

Possible statuses:

```text
MATCHED

UNPAID

PARTIALLY_PAID

OVERPAID

UNMATCHED
```

---

# 15. Overdue Detection

The backend calculates overdue information.

Example:

```text
Invoice Due Date:
2026-09-19

Current Date:
2026-10-07

Days Overdue:
18
```

The backend produces:

```json
{
  "issueType": "OVERDUE_INVOICE",
  "daysOverdue": 18,
  "outstandingAmount": 3000
}
```

Gemini does not calculate this independently.

---

# 16. Priority Scoring

Operational issues should receive deterministic priority.

Example inputs:

- amount affected;
- days overdue;
- percentage of total outstanding balance;
- failure recurrence;
- issue type.

Conceptual formula:

```text
priorityScore =
    overdueWeight
  + financialImpactWeight
  + recurrenceWeight
```

Then:

```text
0–30   → LOW
31–60  → MEDIUM
61–100 → HIGH
```

The exact rules can be tuned later.

---

# 17. Operational Issue Model

Each identified issue should have a consistent structure.

Example:

```json
{
  "issueId": "ISS-101",
  "type": "OVERDUE_INVOICE",
  "priority": "HIGH",
  "amountAffected": 3000,
  "currency": "CAD",
  "resourceId": "INV-104",
  "daysOverdue": 18,
  "reasonCodes": [
    "HIGH_AMOUNT",
    "LONG_OVERDUE",
    "LARGE_SHARE_OF_OUTSTANDING_BALANCE"
  ]
}
```

Gemini can then turn that into:

> ABC Corp is high priority because the invoice is 18 days overdue and accounts for a large portion of your outstanding balance.

---

# 18. Operations Summary Service

The backend should be able to generate a structured operational summary.

Example:

```json
{
  "issuesRequiringAttention": 4,
  "highPriorityIssues": 2,
  "totalAmountAffected": 6850,
  "overdueAmount": 5200,
  "partialPaymentAmount": 400
}
```

This supports the flagship question:

> “What needs my attention today?”

---

# 19. Gemini Provider Abstraction

Gemini should be accessed through an internal abstraction.

Example:

```java
public interface AiProvider {

    AiResponse process(AiRequest request);

}
```

Implementation:

```java
@Service
public class GeminiAiProvider implements AiProvider {

}
```

Architecture:

```text
CopilotService
      ↓
AiProvider
      ↓
GeminiAiProvider
      ↓
Gemini 2.5 Flash-Lite
```

This avoids tightly coupling the entire product to one AI provider.

---

# 20. AI Tool Layer

Gemini receives access only to predefined tools.

Initial tools may include:

```text
getOperationsSummary()

getOperationalIssues()

getIssueDetails()

getReconciliationResults()

getRecentTransactions()

getInvoices()

getInvoice()

prepareInvoice()
```

Gemini cannot invent arbitrary executable methods.

---

# 21. AI Tool Registry

Conceptually:

```text
AiToolRegistry
│
├── GetOperationsSummaryTool
├── GetOperationalIssuesTool
├── GetIssueDetailsTool
├── GetReconciliationResultsTool
├── GetTransactionsTool
├── GetInvoicesTool
├── GetInvoiceTool
└── PrepareInvoiceTool
```

The registry acts as an explicit allowlist.

---

# 22. Flagship Copilot Flow

User asks:

> “What needs my attention today?”

Flow:

```text
1. React sends prompt.

2. CopilotController receives request.

3. Authentication validated.

4. CopilotService sends tool definitions to Gemini.

5. Gemini selects:
   getOperationsSummary()

6. Backend validates tool call.

7. Operations Intelligence Engine runs.

8. Engine retrieves normalized PayPal data.

9. Engine calculates:
   - overdue invoices
   - payment mismatches
   - partial payments
   - priority scores

10. Structured findings returned.

11. Gemini receives only required findings.

12. Gemini explains:
    - what matters
    - why it matters
    - what to consider doing next.

13. React renders:
    - explanation
    - priority cards
    - issue list
    - recommended next steps.
```

---

# 23. Investigation Flow

User then asks:

> “Why is ABC Corp high priority?”

Flow:

```text
Gemini
 ↓
getIssueDetails(issueId)
 ↓
Operations Intelligence Engine
 ↓
Verified issue facts
 ↓
Gemini explanation
```

Example response:

> ABC Corp is high priority because its $3,000 invoice is 18 days overdue and represents 44% of your total overdue balance.

The financial facts originate from backend logic.

---

# 24. Reconciliation Flow

User asks:

> “Do any payments not match their invoices?”

Flow:

```text
Gemini
 ↓
getReconciliationResults()
 ↓
ReconciliationService
 ↓
Invoices + Payments
 ↓
Comparison
 ↓
Structured Reconciliation Results
 ↓
Gemini Explanation
```

Possible result:

```text
INV-112

Expected:
$1,200

Received:
$800

Remaining:
$400

Status:
PARTIALLY_PAID
```

---

# 25. Financial Action Flow

Example:

> “Prepare an invoice for ABC Company for $850.”

Flow:

```text
User
 ↓
Gemini
 ↓
prepareInvoice()
 ↓
Backend validates fields
 ↓
Missing information?
 │
 ├── Yes → request information
 │
 └── No
      ↓
Create ActionRequest
      ↓
AWAITING_CONFIRMATION
      ↓
React displays Action Preview
      ↓
User clicks Confirm
      ↓
ActionService
      ↓
Authentication check
Authorization check
Validation
State check
Idempotency check
      ↓
PayPalInvoiceService
      ↓
PayPal Sandbox
      ↓
Invoice created
      ↓
Audit recorded
      ↓
Action marked COMPLETED
```

---

# 26. Financial Action States

```text
DRAFT
   ↓
AWAITING_CONFIRMATION
   ↓
CONFIRMED
   ↓
PROCESSING
   ↓
COMPLETED
```

Alternative states:

```text
CANCELLED

FAILED
```

The backend controls valid transitions.

---

# 27. Structured Copilot Responses

React should not receive only AI text.

Possible response types:

```text
TEXT

METRIC

ISSUE_LIST

TABLE

RECONCILIATION_RESULT

RECOMMENDATION

ACTION_PREVIEW

ERROR
```

Example:

```json
{
  "message": "You have four issues requiring attention.",
  "responseType": "ISSUE_LIST",
  "data": {
    "issues": []
  }
}
```

---

# 28. Conversation Architecture

Possible entities:

```text
AIConversation

AIMessage
```

Conversation structure:

```text
Conversation
│
├── USER message
├── ASSISTANT message
├── TOOL_CALL
├── TOOL_RESULT
├── ASSISTANT message
└── USER follow-up
```

Financial audit records should remain separate from AI conversation history.

---

# 29. Context Management

The application should send only relevant context to Gemini.

Possible context:

```text
System instructions

Recent messages

Current page

Selected issue ID

Relevant tool definitions
```

Example:

```json
{
  "page": "OPERATIONS",
  "selectedIssueId": "ISS-101"
}
```

This allows:

> “Why is this high priority?”

without requiring the merchant to repeat the identifier.

---

# 30. Data Minimization

Gemini should receive only the financial information necessary for its task.

Prefer:

```json
{
  "type": "OVERDUE_INVOICE",
  "amount": 3000,
  "daysOverdue": 18,
  "shareOfOutstandingBalance": 44,
  "priority": "HIGH"
}
```

Avoid sending:

- full raw PayPal responses;
- unnecessary personal information;
- API secrets;
- access tokens;
- unrelated metadata.

---

# 31. Database Responsibilities

PostgreSQL stores application-owned data.

Expected entities:

```text
User

AIConversation

AIMessage

OperationalIssue

ActionRequest

AuditLog

ProcessedWebhook
```

Some operational findings may be calculated dynamically rather than persisted.

That decision will be finalized in database design.

---

# 32. Financial Source of Truth

The system distinguishes:

## PayPal-Owned Financial Data

Examples:

- invoices;
- transaction/payment identifiers;
- PayPal statuses.

## Backend-Calculated Facts

Examples:

- days overdue;
- remaining amount;
- reconciliation status;
- priority score.

## AI-Generated Content

Examples:

- explanations;
- recommendations;
- summaries;
- natural-language responses.

These categories must not be confused.

---

# 33. REST API Groups

Proposed API groups:

```text
/api/auth/**

/api/dashboard/**

/api/operations/**

/api/transactions/**

/api/invoices/**

/api/copilot/**

/api/actions/**

/api/webhooks/paypal/**
```

---

# 34. Example Endpoints

## Authentication

```text
POST /api/auth/login

POST /api/auth/logout
```

## Dashboard

```text
GET /api/dashboard/summary
```

## Operations

```text
GET /api/operations

GET /api/operations/{issueId}

GET /api/operations/summary

GET /api/operations/reconciliation
```

## Transactions

```text
GET /api/transactions
```

## Invoices

```text
GET /api/invoices

GET /api/invoices/{id}
```

## Copilot

```text
POST /api/copilot/messages
```

## Actions

```text
POST /api/actions/{id}/confirm

POST /api/actions/{id}/cancel
```

## PayPal Webhook

```text
POST /api/webhooks/paypal
```

---

# 35. Validation Architecture

Validation occurs in layers:

```text
Frontend
   ↓
DTO Validation
   ↓
Business Rule Validation
   ↓
Authorization
   ↓
Action State Validation
   ↓
External API Validation
```

Gemini-generated arguments go through the same validation path as normal user input.

---

# 36. Error Handling

Spring Boot should use centralized exception handling.

Example:

```text
GlobalExceptionHandler
```

Error response:

```json
{
  "code": "PAYMENT_RECONCILIATION_FAILED",
  "message": "We couldn't analyze the selected payment data."
}
```

Do not expose internal stack traces.

---

# 37. Audit Architecture

Important state-changing operations should generate audit records.

Example:

```text
ACTION_PREPARED
        ↓
ACTION_CONFIRMED
        ↓
PAYPAL_REQUEST_STARTED
        ↓
ACTION_COMPLETED
```

or:

```text
ACTION_FAILED
```

---

# 38. Webhook Architecture

If implemented:

```text
PayPal
 ↓
Webhook Endpoint
 ↓
Verify Authenticity
 ↓
Check Duplicate Event
 ↓
Process Supported Event
 ↓
Update Relevant State
 ↓
Store Event ID
```

---

# 39. Environment Configuration

Expected environment variables:

```text
DB_URL
DB_USERNAME
DB_PASSWORD

JWT_SECRET

PAYPAL_CLIENT_ID
PAYPAL_CLIENT_SECRET
PAYPAL_BASE_URL

GEMINI_API_KEY
GEMINI_MODEL
```

With:

```text
GEMINI_MODEL=gemini-2.5-flash-lite
```

---

# 40. Deployment Architecture

Conceptually:

```text
Internet
   │
   ▼
React Frontend
   │
   ▼
Spring Boot API
   │
   ├──────── PostgreSQL
   │
   ├──────── Gemini API
   │
   └──────── PayPal Sandbox
```

Exact hosting providers will be selected later.

---

# 41. Trust Boundaries

```text
USER
 │
 ▼
REACT
 │
 ▼
SPRING BOOT
 │
 ├──── PostgreSQL
 │
 ├──── Gemini
 │
 └──── PayPal
```

Spring Boot remains the central trust boundary.

External input from:

- browser;
- Gemini;
- PayPal webhooks;

must all be validated appropriately.

---

# 42. Architecture Priorities

Development should proceed in this order:

```text
Security Foundation
      ↓
Authentication
      ↓
PayPal Data Retrieval
      ↓
Data Normalization
      ↓
Operations Intelligence Engine
      ↓
Reconciliation
      ↓
Priority Scoring
      ↓
Dashboard / Operations Queue
      ↓
Gemini Integration
      ↓
AI Tools
      ↓
Financial Action Workflow
      ↓
Audit / Webhooks
      ↓
UI Polish
```

---

# 43. MVP Architecture Boundary

The project intentionally avoids unnecessary complexity.

The MVP will not require:

- microservices;
- Kubernetes;
- Kafka;
- multiple databases;
- distributed processing;
- complex enterprise RBAC;
- autonomous financial agents;
- custom machine-learning models.

A modular Spring Boot monolith is sufficient.

---

# 44. Final Architecture Summary

PayOps AI can be summarized as:

```text
                    PAYOPS AI

                       USER
                        │
                        ▼
                 REACT FRONTEND
                        │
                        ▼
                 SPRING BOOT
                        │
       ┌────────────────┼─────────────────┐
       │                │                 │
       ▼                ▼                 ▼
OPERATIONS          GEMINI            PAYPAL
INTELLIGENCE        2.5 FLASH-LITE    SANDBOX
ENGINE
       │                │                 │
       │                │                 │
Financial Facts     Explanation       Financial Data
Reconciliation      Recommendations   Financial Actions
Priority            Conversation
Issue Detection     Action Preparation
```

The architectural principle is:

> **PayPal provides the financial records and supported financial actions.**

> **The Operations Intelligence Engine determines verified payment-operation facts.**

> **Gemini explains, reasons, recommends, and prepares.**

> **The merchant remains the final financial authority.**