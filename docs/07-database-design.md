# PayOps AI — Database Design

## 1. Purpose

This document defines the database structure for the PayOps AI hackathon MVP.

The database is responsible for storing application-owned data such as:

- users;
- AI conversations;
- AI messages;
- operational issues where persistence is useful;
- action requests;
- audit records;
- processed webhook events.

PayPal remains the authoritative source for PayPal-owned financial records such as transactions and invoices.

---

# 2. Database Technology

The application will use:

**PostgreSQL**

Accessed through:

- Spring Data JPA;
- Hibernate;
- repository abstractions.

The frontend will never communicate directly with PostgreSQL.

Expected flow:

```text
React
  ↓
Spring Boot
  ↓
PostgreSQL
```

---

# 3. Data Ownership Principle

The system must clearly separate three types of data.

## 3.1 PayPal-Owned Data

Examples:

- PayPal invoices;
- PayPal transaction IDs;
- PayPal payment status;
- PayPal payment amounts;
- PayPal payer/payment metadata.

PayPal remains authoritative.

---

## 3.2 PayOps-Owned Data

Examples:

- users;
- authentication information;
- AI conversations;
- action requests;
- audit records;
- processed webhook IDs;
- user-specific application state.

PostgreSQL is authoritative for these records.

---

## 3.3 Derived Operational Data

Examples:

- days overdue;
- reconciliation status;
- remaining balance;
- priority score;
- issue type;
- operational recommendation context.

These values are derived from trusted financial data.

They may be:

- calculated dynamically; or
- temporarily persisted for performance/history.

For the MVP, persistence should be used only where it provides clear value.

---

# 4. Core Entities

The initial MVP database should contain approximately these entities:

```text
User

AIConversation

AIMessage

OperationalIssue

ActionRequest

AuditLog

ProcessedWebhook
```

---

# 5. User Entity

## Purpose

Stores PayOps AI application users.

For the hackathon MVP, a seeded demo merchant account is sufficient.

## Suggested Fields

```text
User

id
email
password_hash
display_name
account_status
created_at
updated_at
```

## Example

```text
id: 1
email: demo@payops.local
display_name: Demo Merchant
account_status: ACTIVE
```

The password must be stored using BCrypt or another secure hashing algorithm.

---

# 6. User Table

Possible SQL-style structure:

```text
users

id                  BIGINT PRIMARY KEY
email               VARCHAR UNIQUE NOT NULL
password_hash       VARCHAR NOT NULL
display_name        VARCHAR
account_status      VARCHAR NOT NULL
created_at          TIMESTAMP NOT NULL
updated_at          TIMESTAMP
```

Possible account statuses:

```text
ACTIVE
DISABLED
```

No complex enterprise account model is required for the MVP.

---

# 7. AIConversation Entity

## Purpose

Represents an AI Assistant conversation.

A user may have one or more conversations.

## Suggested Fields

```text
AIConversation

id
user_id
title
created_at
updated_at
```

---

# 8. AIConversation Relationship

```text
User
  │
  └────────< AIConversation
```

One user may have many AI conversations.

Each conversation belongs to one user.

---

# 9. AIMessage Entity

## Purpose

Stores individual messages within an AI conversation.

Messages may represent:

- user messages;
- assistant messages;
- tool requests;
- tool results.

## Suggested Fields

```text
AIMessage

id
conversation_id
role
message_type
content
created_at
```

Possible roles:

```text
USER
ASSISTANT
SYSTEM
TOOL
```

Possible message types:

```text
TEXT
TOOL_CALL
TOOL_RESULT
ISSUE_LIST
RECOMMENDATION
ACTION_PREVIEW
ERROR
```

---

# 10. AI Message Relationship

```text
User
 │
 └──────< AIConversation
                │
                └──────< AIMessage
```

One conversation contains many messages.

---

# 11. OperationalIssue Entity

## Purpose

Represents a detected payment-operations issue.

Examples include:

- overdue invoice;
- partial payment;
- unmatched payment;
- possible duplicate;
- repeated payment failure.

## Important Design Decision

Operational issues do not necessarily need to be permanently stored.

Many issues can be calculated dynamically from PayPal data.

However, storing an issue may be useful when we need:

- issue history;
- consistent identifiers;
- user follow-up;
- action linkage;
- auditability.

For the MVP, we may persist only issues that appear in the operations queue or are involved in user actions.

---

# 12. OperationalIssue Fields

Suggested structure:

```text
OperationalIssue

id
user_id

issue_type
priority
status

external_resource_id
external_resource_type

amount_affected
currency

days_overdue
remaining_amount
priority_score

reason_codes

detected_at
resolved_at
updated_at
```

---

# 13. Operational Issue Types

Possible values:

```text
OVERDUE_INVOICE

UNPAID_INVOICE

PARTIAL_PAYMENT

OVERPAYMENT

UNMATCHED_PAYMENT

POSSIBLE_DUPLICATE

PAYMENT_FAILURE
```

Not every issue type must be implemented in the MVP.

---

# 14. Operational Issue Status

Possible values:

```text
OPEN

ACKNOWLEDGED

RESOLVED

DISMISSED
```

This allows future workflows such as:

```text
OPEN
 ↓
ACKNOWLEDGED
 ↓
RESOLVED
```

For the MVP, `OPEN` and `RESOLVED` may be sufficient.

---

# 15. Priority Fields

Operational issues may contain:

```text
priority_score
priority
```

Example:

```text
priority_score = 82

priority = HIGH
```

Possible priority values:

```text
LOW
MEDIUM
HIGH
```

The numeric score is produced by deterministic backend logic.

Gemini does not authoritatively assign the score.

---

# 16. Reason Codes

An issue should store structured reasons instead of only AI-generated explanation text.

Example:

```text
HIGH_AMOUNT

LONG_OVERDUE

PARTIAL_PAYMENT

LARGE_SHARE_OF_OUTSTANDING_BALANCE

REPEATED_FAILURE
```

Example issue:

```text
Issue:
INV-104

Priority:
HIGH

Reason Codes:
LONG_OVERDUE
HIGH_AMOUNT
LARGE_SHARE_OF_OUTSTANDING_BALANCE
```

Gemini can use these codes and the underlying metrics to generate a human-readable explanation.

---

# 17. ActionRequest Entity

## Purpose

Represents an AI-assisted or user-requested action that may eventually affect PayPal.

Examples:

- create invoice;
- send invoice;
- future refund preparation.

The entity is especially important because financial actions must not execute directly from AI output.

---

# 18. ActionRequest Fields

Suggested structure:

```text
ActionRequest

id
user_id
operational_issue_id

action_type
status

request_payload
validated_payload

idempotency_key

created_at
confirmed_at
processing_started_at
completed_at
cancelled_at

external_resource_id
error_code
```

---

# 19. Action Types

Initial MVP:

```text
CREATE_INVOICE
```

Possible future values:

```text
SEND_INVOICE
REFUND_PAYMENT
SEND_REMINDER
```

These future values should not imply that all are implemented.

---

# 20. Action Status

Use the state model:

```text
DRAFT

AWAITING_CONFIRMATION

CONFIRMED

PROCESSING

COMPLETED

FAILED

CANCELLED
```

Valid flow:

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

Alternative outcomes:

```text
FAILED
CANCELLED
```

---

# 21. Request vs Validated Payload

It may be useful to distinguish between:

```text
request_payload
```

and:

```text
validated_payload
```

Example:

AI extracts:

```json
{
  "customer": "ABC Company",
  "amount": "850"
}
```

Backend validates and normalizes:

```json
{
  "customer": "ABC Company",
  "amount": 850.00,
  "currency": "CAD",
  "dueDate": "2026-10-18"
}
```

Only validated data should be used for PayPal execution.

---

# 22. Idempotency Key

Each write action should have a unique idempotency identifier.

Example:

```text
0a6f44da-b221-4b1b-9827-...
```

This can help prevent accidental duplicate execution.

The database should enforce uniqueness where appropriate.

---

# 23. AuditLog Entity

## Purpose

Stores a security and operational history of important actions.

Audit records should remain separate from AI conversation history.

Conversation history explains what was discussed.

Audit history explains what the system actually did.

---

# 24. AuditLog Fields

Suggested structure:

```text
AuditLog

id
user_id
action_request_id

event_type
resource_type
resource_id

outcome
details

created_at
```

---

# 25. Audit Event Types

Examples:

```text
USER_LOGIN_SUCCESS

USER_LOGIN_FAILURE

ACTION_PREPARED

ACTION_CONFIRMED

ACTION_CANCELLED

PAYPAL_REQUEST_STARTED

ACTION_COMPLETED

ACTION_FAILED

WEBHOOK_RECEIVED

WEBHOOK_REJECTED
```

---

# 26. Audit Data Restrictions

Audit logs must not contain:

- passwords;
- PayPal Client Secret;
- Gemini API key;
- JWT secret;
- database password;
- PayPal OAuth access token.

Sensitive personal data should also be minimized.

---

# 27. ProcessedWebhook Entity

## Purpose

Prevents duplicate webhook events from being processed repeatedly.

PayPal may retry webhook delivery.

The application must therefore track processed event identifiers.

---

# 28. ProcessedWebhook Fields

Suggested structure:

```text
ProcessedWebhook

id
paypal_event_id
event_type
processing_status
received_at
processed_at
```

The `paypal_event_id` should be unique.

---

# 29. Webhook Processing Flow

```text
PayPal Webhook
      ↓
Extract Event ID
      ↓
Does ProcessedWebhook exist?
      │
      ├── YES
      │     ↓
      │   Ignore duplicate
      │
      └── NO
            ↓
      Verify event
            ↓
      Process event
            ↓
      Save ProcessedWebhook
```

---

# 30. Entity Relationships

Conceptual ER model:

```text
User
 │
 ├────────< AIConversation
 │              │
 │              └────────< AIMessage
 │
 ├────────< OperationalIssue
 │
 ├────────< ActionRequest
 │              │
 │              └────────< AuditLog
 │
 └────────< AuditLog


OperationalIssue
      │
      └────────< ActionRequest


ProcessedWebhook
```

---

# 31. Relationship Details

## User → AIConversation

```text
1 : Many
```

## AIConversation → AIMessage

```text
1 : Many
```

## User → OperationalIssue

```text
1 : Many
```

## User → ActionRequest

```text
1 : Many
```

## OperationalIssue → ActionRequest

Potential:

```text
1 : Many
```

An issue may result in multiple prepared actions over time.

## ActionRequest → AuditLog

```text
1 : Many
```

---

# 32. What We Should NOT Store

Avoid storing complete copies of all PayPal data unless necessary.

Do not create giant tables duplicating:

```text
all PayPal transactions

all PayPal invoice fields

complete raw PayPal API payloads
```

This introduces:

- synchronization complexity;
- stale data;
- unnecessary sensitive information;
- larger security surface.

Prefer retrieving current PayPal data through the integration layer.

---

# 33. External Resource References

Where an application record refers to a PayPal resource, store the external identifier.

Example:

```text
external_resource_type = PAYPAL_INVOICE

external_resource_id = INV2-XXXX
```

This allows PayOps AI to retrieve fresh information when required.

---

# 34. JSON Fields

Some flexible data may be stored using PostgreSQL `JSONB`.

Potential examples:

```text
reason_codes

request_payload

validated_payload

audit_details
```

However, important searchable fields should remain normal database columns.

For example:

```text
priority
status
action_type
created_at
```

should not be buried inside JSON.

---

# 35. Timestamp Standard

All important entities should include appropriate timestamps.

Recommended backend storage:

**UTC**

Examples:

```text
created_at
updated_at
confirmed_at
completed_at
detected_at
resolved_at
```

The frontend may convert timestamps to the user's local timezone.

---

# 36. Monetary Data

Never store financial amounts using floating-point database types.

Use:

```text
DECIMAL / NUMERIC
```

Example:

```text
NUMERIC(19,2)
```

In Java, use:

```text
BigDecimal
```

Not:

```text
double
float
```

This prevents common financial precision problems.

---

# 37. Currency

Store currency separately.

Example:

```text
amount_affected = 850.00

currency = CAD
```

Do not assume every financial amount is CAD.

Possible format:

```text
CHAR(3)
```

following standard currency codes.

---

# 38. Example OperationalIssue

```text
id:
ISS-001

user:
1

issue_type:
OVERDUE_INVOICE

priority:
HIGH

priority_score:
82

external_resource_type:
PAYPAL_INVOICE

external_resource_id:
INV2-ABC123

amount_affected:
3000.00

currency:
CAD

days_overdue:
18

reason_codes:
[
  LONG_OVERDUE,
  HIGH_AMOUNT,
  LARGE_SHARE_OF_OUTSTANDING_BALANCE
]

status:
OPEN
```

---

# 39. Example ActionRequest

```text
id:
ACT-001

user:
1

action_type:
CREATE_INVOICE

status:
AWAITING_CONFIRMATION

validated_payload:
{
  customer: ABC Company,
  email: billing@abc.example,
  amount: 850.00,
  currency: CAD,
  dueDate: 2026-10-18
}

idempotency_key:
UUID

created_at:
...

confirmed_at:
null

external_resource_id:
null
```

After confirmation and successful PayPal execution:

```text
status:
COMPLETED

confirmed_at:
...

completed_at:
...

external_resource_id:
PAYPAL_INVOICE_ID
```

---

# 40. Indexing

Useful initial indexes may include:

```text
users.email

ai_conversations.user_id

ai_messages.conversation_id

operational_issues.user_id

operational_issues.status

operational_issues.priority

action_requests.user_id

action_requests.status

action_requests.idempotency_key

audit_logs.action_request_id

processed_webhooks.paypal_event_id
```

Do not over-optimize indexing for the MVP.

---

# 41. Referential Integrity

Use foreign keys where appropriate.

Examples:

```text
AIConversation.user_id
→ User.id

AIMessage.conversation_id
→ AIConversation.id

OperationalIssue.user_id
→ User.id

ActionRequest.user_id
→ User.id

ActionRequest.operational_issue_id
→ OperationalIssue.id
```

---

# 42. Deletion Strategy

For financially relevant audit information, avoid destructive cascading deletion without consideration.

For example, deleting a user should not silently destroy financial audit history in a real production system.

For the hackathon MVP, account deletion functionality is out of scope.

This simplifies the data-retention model.

---

# 43. Initial JPA Entities

Likely JPA entities:

```text
User

AIConversation

AIMessage

OperationalIssue

ActionRequest

AuditLog

ProcessedWebhook
```

Not every PayPal DTO should become a JPA entity.

PayPal integration DTOs should generally remain separate.

---

# 44. Database Migration Strategy

Use a repeatable schema-management approach.

Preferred options:

```text
Flyway
```

or:

```text
Liquibase
```

For this project, **Flyway** is sufficient and relatively lightweight.

Possible migration structure:

```text
backend/
└── src/main/resources/db/migration/

    V1__create_users.sql

    V2__create_ai_conversations.sql

    V3__create_operational_issues.sql

    V4__create_action_requests.sql

    V5__create_audit_logs.sql
```

---

# 45. Database Security

Database credentials must remain server-side.

Required controls include:

- environment-based credentials;
- strong database password;
- JPA / parameterized queries;
- no user-supplied SQL;
- authorization checks before retrieving user-owned records;
- minimum necessary exposed database access.

---

# 46. MVP Simplification

Although this document describes a scalable structure, the MVP should remain lightweight.

The minimum useful database could initially contain:

```text
User

AIConversation

AIMessage

ActionRequest

AuditLog
```

Then add:

```text
OperationalIssue

ProcessedWebhook
```

when their corresponding features are implemented.

This allows us to avoid creating tables before they are actually necessary.

---

# 47. Database Design Principle

The core rule is:

> **Do not duplicate PayPal unnecessarily.**

PayPal stores financial resources.

PostgreSQL stores PayOps AI application state.

The Operations Intelligence Engine derives operational facts.

Gemini explains those facts.

This separation reduces data duplication, synchronization complexity, and security risk.