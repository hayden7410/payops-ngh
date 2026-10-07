# PayOps AI — MVP Scope

## 1. Purpose

This document defines the minimum viable product for PayOps AI.

The MVP must demonstrate more than conversational search.

It must show that AI can help a merchant identify, understand, prioritize, and respond to payment-operation issues using PayPal data.

---

## 2. Core MVP Workflow

The primary product workflow is:

```text
PayPal Data
   ↓
Operations Analysis
   ↓
Issue Detection
   ↓
Prioritization
   ↓
AI Explanation
   ↓
Recommendation
   ↓
Action Preparation
   ↓
User Confirmation
   ↓
PayPal Execution
```

---

## 3. Must-Have Capabilities

### Authentication

The application must provide basic login/logout functionality.

A seeded demo merchant is sufficient.

### PayPal Integration

The system must retrieve relevant financial information from PayPal Sandbox.

### Operations Dashboard

The dashboard must summarize payment activity and highlight issues requiring attention.

### Operational Issue Detection

The system must identify at least:

- overdue invoices;
- unpaid invoices;
- partial payments;
- unmatched or inconsistent payment information where technically feasible.

### Payment Reconciliation

The system must compare payment information against invoice information.

Possible reconciliation states include:

```text
MATCHED
UNPAID
PARTIALLY_PAID
OVERPAID
UNMATCHED
```

### Priority Analysis

Detected issues should receive a priority level such as:

```text
LOW
MEDIUM
HIGH
```

Priority should be based primarily on deterministic backend rules.

### AI Explanation

Gemini must explain why an issue was detected and why it matters.

### AI Recommendations

Gemini must recommend reasonable next actions based on verified operational findings.

### AI Copilot

The merchant can investigate operations conversationally.

### Action Preparation

The AI can prepare at least one supported financial workflow.

The primary MVP action will be invoice preparation/creation.

### Human Confirmation

No financial write operation may execute without explicit user confirmation.

---

## 4. Flagship AI Scenario

The main AI interaction should be:

> “What needs my attention today?”

The result might be:

```text
HIGH PRIORITY

ABC Corp
Invoice INV-104
$3,000 outstanding
18 days overdue

Reason:
This invoice represents 44% of your total overdue balance.

Recommended action:
Follow up with ABC Corp before lower-value outstanding invoices.
```

Additional detected issues might include:

```text
PARTIAL PAYMENT

Invoice INV-112
Expected: $1,200
Received: $800
Remaining: $400
```

and:

```text
PAYMENT FAILURE

2 failed payments today
Total affected: $310
```

---

## 5. MVP Intelligence Services

The backend should include services for:

```text
OverdueInvoiceDetection

PaymentReconciliation

PartialPaymentDetection

PriorityScoring

OperationsSummary
```

These services produce verified structured results.

Gemini consumes those results.

---

## 6. Copilot Capabilities

The MVP should support questions such as:

> “What needs my attention today?”

> “Why is this invoice high priority?”

> “Which payments do not match their invoices?”

> “Do I have any partial payments?”

> “What is my largest outstanding risk?”

> “Summarize my payment operations.”

> “What should I handle first?”

> “Prepare an invoice for ABC Company.”

Basic queries such as:

> “Show my recent transactions.”

remain supported but are no longer the central AI use case.

---

## 7. Financial Action Workflow

For supported financial actions:

```text
User Request
   ↓
AI extracts information
   ↓
Backend validates
   ↓
Action draft created
   ↓
User reviews
   ↓
User confirms
   ↓
PayPal executes
```

The MVP will initially support:

**Invoice creation**

Other financial actions are stretch goals.

---

## 8. Required Demo Scenarios

### Scenario 1 — Operations Intelligence

User asks:

> “What needs my attention today?”

System identifies and prioritizes operational issues.

---

### Scenario 2 — Reconciliation

User asks:

> “Do any payments not match their invoices?”

System compares payment and invoice data and identifies discrepancies.

---

### Scenario 3 — Explanation

User asks:

> “Why is ABC Corp high priority?”

AI explains using verified financial facts.

---

### Scenario 4 — Action

User asks:

> “Prepare an invoice for ABC Company for $850.”

AI prepares the action.

User reviews and confirms.

PayPal Sandbox creates the invoice.

---

## 9. Should-Have Features

After the core MVP is working:

- automatic operations brief;
- contextual AI follow-up;
- webhooks;
- audit-history screen;
- possible duplicate-payment detection;
- payment-failure analysis;
- charts;
- prompt suggestions.

---

## 10. Stretch Features

If time remains:

- refunds;
- dispute assistance;
- subscription failure analysis;
- advanced anomaly detection;
- automated customer follow-up drafts;
- deeper cash-flow analysis;
- payment trend analysis.

---

## 11. Out-of-Scope Features

The MVP excludes:

- autonomous refunds;
- autonomous transfers;
- autonomous invoice sending without review;
- accounting software;
- tax functionality;
- ERP capabilities;
- advanced machine-learning fraud systems;
- production PayPal transactions.

---

## 12. MVP Completion Criteria

The MVP is complete when this end-to-end workflow works:

```text
Merchant logs in
   ↓
System retrieves PayPal Sandbox data
   ↓
Backend identifies payment issues
   ↓
Merchant asks:
"What needs my attention?"
   ↓
Gemini explains prioritized issues
   ↓
Merchant investigates one issue
   ↓
AI recommends a next action
   ↓
Merchant requests an operation
   ↓
AI prepares it
   ↓
Merchant confirms
   ↓
PayPal executes
   ↓
Result is displayed
```

---

## 13. Scope Rule

Development priority is:

```text
Payment data
   ↓
Reliable backend analysis
   ↓
Issue detection
   ↓
AI explanation
   ↓
Prioritization
   ↓
Action preparation
   ↓
PayPal execution
   ↓
Polish
```

A smaller intelligent system is preferable to a large dashboard with shallow AI functionality.