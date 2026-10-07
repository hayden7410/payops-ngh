# PayOps AI — API Design

## 1. Purpose

This document defines the REST API design for the PayOps AI hackathon MVP.

The API connects:

- React frontend;
- Spring Boot backend;
- PayPal integration services;
- Operations Intelligence Engine;
- Gemini-powered AI Assistant.

The design should keep external-provider details hidden behind the backend.

React should never need to understand:

- PayPal OAuth;
- raw PayPal API responses;
- Gemini API structure;
- internal priority calculations.

---

# 2. API Design Principles

The API should follow several rules.

## 2.1 Backend as Gateway

React communicates only with Spring Boot.

```text
React
  ↓
Spring Boot
  ↓
PayPal / Gemini / PostgreSQL
```

The frontend never directly calls privileged PayPal or Gemini APIs.

---

## 2.2 Resource-Oriented APIs

Direct application data should use clear REST-style resources.

Examples:

```text
/api/invoices
/api/transactions
/api/operations
/api/actions
```

---

## 2.3 Separate AI Interaction

Natural-language AI Assistant interactions should use a dedicated API.

Example:

```text
POST /api/ai/messages
```

This keeps AI orchestration separate from normal CRUD/data endpoints.

---

## 2.4 Consistent Response Shape

Where practical, APIs should use predictable response structures.

Example success:

```json
{
  "success": true,
  "data": {}
}
```

Example failure:

```json
{
  "success": false,
  "error": {
    "code": "INVOICE_NOT_FOUND",
    "message": "The requested invoice could not be found."
  }
}
```

---

# 3. Authentication APIs

## 3.1 Login

```http
POST /api/auth/login
```

### Request

```json
{
  "email": "demo@payops.local",
  "password": "********"
}
```

### Response

```json
{
  "token": "jwt-token",
  "user": {
    "id": 1,
    "email": "demo@payops.local",
    "displayName": "Demo Merchant"
  }
}
```

---

## 3.2 Logout

```http
POST /api/auth/logout
```

If stateless JWT is used, logout may primarily clear frontend auth state.

If server-side token invalidation is implemented, the backend may also revoke the current session/token.

---

## 3.3 Current User

```http
GET /api/auth/me
```

### Response

```json
{
  "id": 1,
  "email": "demo@payops.local",
  "displayName": "Demo Merchant"
}
```

This endpoint helps React restore authenticated state.

---

# 4. Dashboard APIs

## 4.1 Dashboard Summary

```http
GET /api/dashboard/summary
```

### Response

```json
{
  "receivedAmount": 8420.00,
  "currency": "CAD",
  "outstandingAmount": 6850.00,
  "overdueAmount": 5200.00,
  "issuesRequiringAttention": 4,
  "highPriorityIssues": 2
}
```

The backend should calculate these values from trusted data.

Gemini is not required for this endpoint.

---

# 5. Transaction APIs

## 5.1 Get Transactions

```http
GET /api/transactions
```

Optional query parameters may include:

```text
?limit=20
?status=COMPLETED
?from=2026-10-01
?to=2026-10-31
```

### Response

```json
{
  "items": [
    {
      "transactionId": "TXN-001",
      "payer": "ABC Company",
      "date": "2026-10-07T14:30:00Z",
      "amount": 850.00,
      "currency": "CAD",
      "status": "COMPLETED"
    }
  ]
}
```

---

## 5.2 Get Transaction Detail

```http
GET /api/transactions/{transactionId}
```

### Response

```json
{
  "transactionId": "TXN-001",
  "payer": "ABC Company",
  "date": "2026-10-07T14:30:00Z",
  "amount": 850.00,
  "currency": "CAD",
  "status": "COMPLETED"
}
```

---

# 6. Invoice APIs

## 6.1 Get Invoices

```http
GET /api/invoices
```

Optional query parameters:

```text
?status=UNPAID
?overdue=true
?limit=20
```

### Response

```json
{
  "items": [
    {
      "invoiceId": "INV2-ABC123",
      "invoiceNumber": "INV-104",
      "customerName": "ABC Corp",
      "customerEmail": "billing@abc.example",
      "amount": 3000.00,
      "paidAmount": 0.00,
      "currency": "CAD",
      "dueDate": "2026-09-19",
      "status": "UNPAID"
    }
  ]
}
```

---

## 6.2 Get Invoice Detail

```http
GET /api/invoices/{invoiceId}
```

### Response

```json
{
  "invoiceId": "INV2-ABC123",
  "invoiceNumber": "INV-104",
  "customerName": "ABC Corp",
  "customerEmail": "billing@abc.example",
  "amount": 3000.00,
  "paidAmount": 0.00,
  "remainingAmount": 3000.00,
  "currency": "CAD",
  "dueDate": "2026-09-19",
  "status": "UNPAID"
}
```

---

# 7. Operations Intelligence APIs

These APIs expose deterministic backend analysis directly.

They do not require Gemini.

---

## 7.1 Get Operations Summary

```http
GET /api/operations/summary
```

### Response

```json
{
  "issuesRequiringAttention": 4,
  "highPriorityIssues": 2,
  "totalAmountAffected": 6850.00,
  "overdueAmount": 5200.00,
  "partialPaymentAmount": 400.00,
  "currency": "CAD"
}
```

---

## 7.2 Get Operational Issues

```http
GET /api/operations
```

Optional filters:

```text
?priority=HIGH
?type=OVERDUE_INVOICE
?status=OPEN
```

### Response

```json
{
  "items": [
    {
      "issueId": "ISS-101",
      "type": "OVERDUE_INVOICE",
      "priority": "HIGH",
      "priorityScore": 82,
      "amountAffected": 3000.00,
      "currency": "CAD",
      "resourceId": "INV2-ABC123",
      "resourceType": "PAYPAL_INVOICE",
      "reasonCodes": [
        "LONG_OVERDUE",
        "HIGH_AMOUNT",
        "LARGE_SHARE_OF_OUTSTANDING_BALANCE"
      ]
    }
  ]
}
```

---

## 7.3 Get Operational Issue Detail

```http
GET /api/operations/{issueId}
```

### Response

```json
{
  "issueId": "ISS-101",
  "type": "OVERDUE_INVOICE",
  "priority": "HIGH",
  "priorityScore": 82,
  "amountAffected": 3000.00,
  "currency": "CAD",
  "resourceId": "INV2-ABC123",
  "daysOverdue": 18,
  "reasonCodes": [
    "LONG_OVERDUE",
    "HIGH_AMOUNT",
    "LARGE_SHARE_OF_OUTSTANDING_BALANCE"
  ],
  "facts": {
    "shareOfOutstandingBalance": 44.0,
    "remainingAmount": 3000.00
  }
}
```

---

# 8. Reconciliation APIs

## 8.1 Get Reconciliation Results

```http
GET /api/operations/reconciliation
```

Optional query parameters:

```text
?status=PARTIALLY_PAID
```

### Response

```json
{
  "items": [
    {
      "invoiceId": "INV2-XYZ789",
      "invoiceNumber": "INV-112",
      "expectedAmount": 1200.00,
      "receivedAmount": 800.00,
      "remainingAmount": 400.00,
      "currency": "CAD",
      "reconciliationStatus": "PARTIALLY_PAID"
    }
  ]
}
```

---

# 9. AI Assistant API

The AI Assistant provides natural-language interaction over the approved backend tools.

---

## 9.1 Send AI Message

```http
POST /api/ai/messages
```

### Request

```json
{
  "conversationId": "CONV-001",
  "message": "What needs my attention today?",
  "context": {
    "page": "DASHBOARD",
    "selectedResourceId": null
  }
}
```

`conversationId` may be optional for a new conversation.

---

# 10. AI Assistant Response

The response should be structured so React can render more than text.

Example:

```json
{
  "conversationId": "CONV-001",
  "messageId": "MSG-010",
  "message": "You have four payment issues requiring attention. ABC Corp should be handled first.",
  "responseType": "ISSUE_LIST",
  "data": {
    "issues": [
      {
        "issueId": "ISS-101",
        "type": "OVERDUE_INVOICE",
        "priority": "HIGH",
        "amountAffected": 3000.00,
        "currency": "CAD",
        "summary": "18 days overdue and 44% of total overdue balance."
      }
    ]
  },
  "action": null
}
```

---

# 11. AI Response Types

Initial response types:

```text
TEXT

METRIC

TABLE

ISSUE_LIST

RECONCILIATION_RESULT

RECOMMENDATION

ACTION_PREVIEW

ERROR
```

React should render the appropriate component based on `responseType`.

---

# 12. AI Tool Flow

Example request:

```text
"What needs my attention today?"
```

Internal flow:

```text
POST /api/ai/messages
        ↓
AiAssistantController
        ↓
AiAssistantService
        ↓
Gemini
        ↓
Tool Call:
getOperationsSummary()
        ↓
OperationsSummaryService
        ↓
Structured result
        ↓
Gemini
        ↓
AI response DTO
```

The AI tool call itself is not exposed directly to the frontend.

---

# 13. AI Investigation Example

User:

> “Why is ABC Corp high priority?”

Request:

```json
{
  "conversationId": "CONV-001",
  "message": "Why is ABC Corp high priority?",
  "context": {
    "selectedIssueId": "ISS-101"
  }
}
```

The AI may internally call:

```text
getIssueDetails("ISS-101")
```

The backend returns trusted facts.

Gemini turns them into an explanation.

---

# 14. Action Preparation API Flow

A user may ask:

> “Prepare an invoice for ABC Company for $850.”

The request still goes through:

```http
POST /api/ai/messages
```

Gemini selects:

```text
prepareInvoice
```

The backend validates available fields.

If enough information exists, it creates an `ActionRequest`.

---

# 15. Action Preview Response

Example:

```json
{
  "conversationId": "CONV-001",
  "message": "I've prepared the invoice for your review.",
  "responseType": "ACTION_PREVIEW",
  "action": {
    "actionId": "7f9c3f18-1234-...",
    "actionType": "CREATE_INVOICE",
    "status": "AWAITING_CONFIRMATION",
    "details": {
      "customerName": "ABC Company",
      "customerEmail": "billing@abc.example",
      "amount": 850.00,
      "currency": "CAD",
      "dueDate": "2026-10-18",
      "description": "Consulting Services"
    }
  }
}
```

React displays a review card.

---

# 16. Missing Information Response

If information is incomplete:

User:

> “Prepare an $850 invoice for ABC Company.”

The backend may determine that customer email and due date are missing.

Response:

```json
{
  "message": "I need the customer email and due date before I can prepare this invoice.",
  "responseType": "TEXT",
  "data": {
    "missingFields": [
      "customerEmail",
      "dueDate"
    ]
  }
}
```

No PayPal write action occurs.

---

# 17. Confirm Action

```http
POST /api/actions/{actionId}/confirm
```

No financial payload should need to be trusted from the frontend at confirmation time.

The backend retrieves the previously validated action by `actionId`.

---

## 17.1 Confirmation Request

Possible request:

```json
{}
```

or no body.

The backend already has the validated payload.

This is safer than allowing React to resend:

```json
{
  "amount": 850
}
```

and trusting it again.

---

## 17.2 Confirmation Flow

```text
actionId
   ↓
Load ActionRequest
   ↓
Verify authenticated user owns action
   ↓
Verify status = AWAITING_CONFIRMATION
   ↓
Verify action not already executed
   ↓
Transition to CONFIRMED
   ↓
Validate stored payload
   ↓
Transition to PROCESSING
   ↓
Call PayPal
   ↓
COMPLETED / FAILED
```

---

# 18. Confirm Action Response

Successful example:

```json
{
  "actionId": "7f9c3f18-1234-...",
  "status": "COMPLETED",
  "actionType": "CREATE_INVOICE",
  "externalResourceId": "INV2-PAYPAL123",
  "message": "Invoice created successfully."
}
```

---

# 19. Cancel Action

```http
POST /api/actions/{actionId}/cancel
```

### Response

```json
{
  "actionId": "7f9c3f18-1234-...",
  "status": "CANCELLED",
  "message": "The invoice creation request was cancelled."
}
```

Cancellation should only be permitted from appropriate states.

---

# 20. Get Action

```http
GET /api/actions/{actionId}
```

Useful for refreshing state after processing.

### Response

```json
{
  "actionId": "7f9c3f18-1234-...",
  "actionType": "CREATE_INVOICE",
  "status": "COMPLETED",
  "externalResourceId": "INV2-PAYPAL123",
  "createdAt": "2026-10-07T20:00:00Z",
  "confirmedAt": "2026-10-07T20:01:10Z",
  "completedAt": "2026-10-07T20:01:12Z"
}
```

---

# 21. Conversation APIs

## 21.1 Get Conversation

```http
GET /api/ai/conversations/{conversationId}
```

### Response

```json
{
  "conversationId": "CONV-001",
  "messages": [
    {
      "role": "USER",
      "content": "What needs my attention today?"
    },
    {
      "role": "ASSISTANT",
      "content": "You have four payment issues requiring attention."
    }
  ]
}
```

---

# 22. Audit API

An audit-history UI is P1, but the backend may expose:

```http
GET /api/audit
```

or:

```http
GET /api/actions/{actionId}/audit
```

This is not required for the first MVP implementation.

---

# 23. PayPal Webhook API

```http
POST /api/webhooks/paypal
```

This endpoint differs from normal user endpoints.

It should not require user JWT authentication.

Instead, it must use PayPal webhook verification.

Processing flow:

```text
Receive payload
    ↓
Verify PayPal authenticity
    ↓
Check event ID
    ↓
Reject duplicate / invalid event
    ↓
Process supported event
```

---

# 24. Standard HTTP Status Codes

Use conventional status codes.

```text
200 OK
```

Successful read or operation.

```text
201 Created
```

Resource successfully created where appropriate.

```text
400 Bad Request
```

Invalid request or validation failure.

```text
401 Unauthorized
```

Authentication required or invalid token.

```text
403 Forbidden
```

Authenticated but not authorized.

```text
404 Not Found
```

Requested resource does not exist.

```text
409 Conflict
```

Invalid state transition or duplicate operation.

```text
422 Unprocessable Entity
```

Optional for semantically invalid financial data.

```text
500 Internal Server Error
```

Unexpected server failure.

```text
502 Bad Gateway
```

Potentially appropriate for upstream provider failures.

---

# 25. Error Response Format

Use a consistent structure.

Example:

```json
{
  "success": false,
  "error": {
    "code": "ACTION_ALREADY_COMPLETED",
    "message": "This financial action has already been completed.",
    "timestamp": "2026-10-07T20:00:00Z"
  }
}
```

---

# 26. Validation Error Example

```json
{
  "success": false,
  "error": {
    "code": "VALIDATION_ERROR",
    "message": "The request contains invalid fields.",
    "fields": {
      "amount": "Amount must be greater than zero.",
      "currency": "Currency must be a supported ISO currency code."
    }
  }
}
```

---

# 27. API Security

All protected endpoints must require authentication.

Protected:

```text
/api/dashboard/**
/api/transactions/**
/api/invoices/**
/api/operations/**
/api/ai/**
/api/actions/**
```

Public or separately secured:

```text
/api/auth/login

/api/webhooks/paypal
```

---

# 28. Authorization

Backend endpoints must verify ownership.

Example:

```http
POST /api/actions/{actionId}/confirm
```

must verify:

```text
Authenticated User
      ↓
ActionRequest.user_id
      ↓
Match?
```

If not:

```text
403 Forbidden
```

---

# 29. Input Limits

Useful initial limits should include:

### AI messages

For example:

```text
Maximum prompt length: 4,000 characters
```

### String fields

Apply reasonable maximum lengths to:

- customer name;
- description;
- email;
- titles.

### Monetary values

Validate:

```text
amount > 0
```

and reasonable maximum constraints where appropriate.

---

# 30. Pagination

Transaction and invoice lists should support simple pagination if required.

Possible request:

```http
GET /api/transactions?page=0&size=20
```

Response:

```json
{
  "items": [],
  "page": 0,
  "size": 20,
  "totalItems": 85,
  "totalPages": 5
}
```

For the hackathon, simple pagination is sufficient.

---

# 31. API DTO Separation

Do not expose JPA entities directly through controllers.

Use DTOs.

Example:

```text
User Entity
   ↓
UserResponse

OperationalIssue Entity/Model
   ↓
OperationalIssueResponse

ActionRequest Entity
   ↓
ActionResponse
```

This prevents database structure from becoming the public API contract.

---

# 32. Money Representation

Java DTOs should use:

```text
BigDecimal
```

for monetary values.

JSON example:

```json
{
  "amount": 850.00,
  "currency": "CAD"
}
```

Never calculate money using Java `double`.

---

# 33. Date and Time Formats

Use ISO formats.

Date:

```text
2026-10-18
```

Timestamp:

```text
2026-10-07T20:00:00Z
```

Backend timestamps should be handled consistently, preferably in UTC.

---

# 34. Versioning

For the hackathon MVP, explicit API versioning such as:

```text
/api/v1/
```

is optional.

We may keep:

```text
/api/
```

to reduce unnecessary complexity.

If the project grows after the hackathon, versioning can be introduced later.

---

# 35. Direct API vs AI Assistant API

This distinction is important.

A normal page might call:

```text
GET /api/operations
```

because it already knows what data it needs.

The AI Assistant receives:

```text
POST /api/ai/messages
```

because the user's intent must first be interpreted.

Example:

```text
Operations Page
     ↓
GET /api/operations
```

versus:

```text
User:
"What should I handle first?"
        ↓
POST /api/ai/messages
        ↓
Gemini determines correct backend tool
```

We should not unnecessarily send every normal dashboard request through Gemini.

---

# 36. Final API Flow

The application supports two primary interaction paths.

## Direct UI Flow

```text
React Page
   ↓
REST API
   ↓
Business Service
   ↓
PayPal / Intelligence Engine
   ↓
Structured Response
```

## AI Assistant Flow

```text
User Prompt
   ↓
POST /api/ai/messages
   ↓
Gemini
   ↓
Approved Tool
   ↓
Business Service
   ↓
PayPal / Intelligence Engine
   ↓
Verified Result
   ↓
Gemini Explanation
   ↓
Structured AI Response
   ↓
React
```

## Financial Action Flow

```text
AI/User prepares action
        ↓
ActionRequest stored
        ↓
AWAITING_CONFIRMATION
        ↓
POST /api/actions/{id}/confirm
        ↓
Backend checks ownership + state + validation
        ↓
PayPal API
        ↓
COMPLETED / FAILED
```

---

# 37. API Design Principle

The API should maintain clear separation between:

**data retrieval**

**operational analysis**

**AI interpretation**

and

**financial execution**

The frontend should never be responsible for enforcing critical financial business rules.

The AI should never bypass the backend APIs that protect those rules.