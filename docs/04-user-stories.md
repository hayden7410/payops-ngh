# PayOps AI — User Stories

## 1. Primary User

The primary user is a merchant, freelancer, or small-business owner using PayPal.

---

# 2. Authentication

## US-01 — Login

**As a merchant, I want to log in so that my payment operations information is protected.**

Priority: **P0**

## US-02 — Logout

**As a merchant, I want to securely end my session so that my workspace cannot be accessed afterward.**

Priority: **P0**

---

# 3. Payment Operations

## US-03 — Understand What Needs Attention

**As a merchant, I want PayOps AI to identify payment issues that require my attention so that I do not need to inspect every transaction manually.**

### Acceptance Criteria

The system identifies relevant issues and displays:

- issue type;
- affected amount;
- priority;
- explanation.

Priority: **P0**

---

## US-04 — Prioritize Payment Issues

**As a merchant, I want payment issues ranked by urgency and financial impact so that I know what to handle first.**

### Acceptance Criteria

- Issues have a defined priority.
- Priority is based on verified backend data.
- The AI can explain the priority.

Priority: **P0**

---

## US-05 — Understand Why an Issue Matters

**As a merchant, I want the AI to explain why an issue was flagged so that I can make an informed decision.**

Example:

> “This invoice is 18 days overdue and accounts for 44% of your total overdue balance.”

Priority: **P0**

---

# 4. Reconciliation

## US-06 — Match Payments to Invoices

**As a merchant, I want PayOps AI to reconcile payments against invoices so that I can identify incorrect or incomplete payments.**

Priority: **P0**

---

## US-07 — Detect Partial Payments

**As a merchant, I want to know when a customer has paid only part of an invoice so that I can follow up on the remaining balance.**

Priority: **P0**

---

## US-08 — Identify Unmatched Payments

**As a merchant, I want to know when payment activity cannot be matched to the expected invoice information so that I can investigate it.**

Priority: **P1**

---

## US-09 — Detect Potential Duplicates

**As a merchant, I want to know when payment records appear duplicated so that I can investigate before taking action.**

Priority: **P1**

---

# 5. AI Copilot

## US-10 — Ask What Requires Attention

**As a merchant, I want to ask “What needs my attention today?” so that AI can summarize my most important payment-operation issues.**

Priority: **P0**

---

## US-11 — Ask What to Handle First

**As a merchant, I want to ask what issue I should handle first so that AI can help prioritize my work.**

Priority: **P0**

---

## US-12 — Investigate an Issue

**As a merchant, I want to ask follow-up questions about an identified issue so that I can understand it before taking action.**

Example:

> “Why is ABC Corp high priority?”

Priority: **P0**

---

## US-13 — Understand Payment Changes

**As a merchant, I want AI to explain relevant changes in payment activity so that I can understand possible operational causes.**

Priority: **P1**

---

## US-14 — Receive Recommendations

**As a merchant, I want AI to recommend appropriate next actions so that I can respond to payment issues efficiently.**

Priority: **P0**

---

# 6. Dashboard

## US-15 — View Payment Health

**As a merchant, I want a dashboard summarizing my payment health so that I can understand the state of my operations quickly.**

Possible metrics:

- received payments;
- outstanding balance;
- overdue balance;
- issues requiring attention;
- high-priority issues.

Priority: **P0**

---

## US-16 — View Prioritized Operations Queue

**As a merchant, I want to see identified issues in priority order so that I can manage my payment workload efficiently.**

Priority: **P0**

---

# 7. Transactions and Invoices

## US-17 — View Transactions

**As a merchant, I want to view PayPal transactions so that I can inspect underlying payment records when necessary.**

Priority: **P0**

## US-18 — View Invoices

**As a merchant, I want to view PayPal invoices so that I can inspect their payment status.**

Priority: **P0**

## US-19 — View Overdue Invoices

**As a merchant, I want to see overdue invoices so that I can follow up on missing payments.**

Priority: **P0**

---

# 8. AI-Assisted Actions

## US-20 — Prepare an Invoice

**As a merchant, I want to request invoice preparation conversationally so that I can reduce repetitive data entry.**

Example:

> “Prepare an $850 invoice for ABC Company.”

Priority: **P0**

---

## US-21 — Provide Missing Information

**As a merchant, I want the AI to request missing information so that the financial action is complete and accurate before execution.**

Priority: **P0**

---

## US-22 — Review the Proposed Action

**As a merchant, I want to review an AI-prepared action so that I can verify the financial details.**

Priority: **P0**

---

## US-23 — Confirm the Action

**As a merchant, I want financial actions to execute only after I explicitly confirm them.**

Priority: **P0**

---

## US-24 — Cancel the Action

**As a merchant, I want to cancel a prepared action so that incorrect financial operations are not executed.**

Priority: **P0**

---

# 9. Security and Trust

## US-25 — Trust Financial Findings

**As a merchant, I want financial facts to come from PayPal or verified backend calculations so that AI cannot invent payment information.**

Priority: **P0**

---

## US-26 — Understand AI Recommendations

**As a merchant, I want recommendations to explain the supporting financial facts so that I can independently evaluate them.**

Priority: **P0**

---

## US-27 — Retain Financial Authority

**As a merchant, I want AI to recommend and prepare actions without independently authorizing them.**

Priority: **P0**

---

# 10. Main Demo Journey

The primary hackathon demo should follow:

```text
Merchant logs in
      ↓
Dashboard:
4 issues need attention
      ↓
Merchant:
"What should I handle first?"
      ↓
AI:
ABC Corp — HIGH PRIORITY
$3,000
18 days overdue
44% of total overdue balance
      ↓
Merchant:
"Do I have any payment mismatches?"
      ↓
AI:
INV-112 expected $1,200
Only $800 received
Remaining $400
      ↓
Merchant investigates
      ↓
AI recommends next action
      ↓
Merchant requests financial action
      ↓
AI prepares action
      ↓
Merchant reviews and confirms
      ↓
PayPal executes
      ↓
Application displays result
```

This journey demonstrates that PayOps AI performs:

**detection → reasoning → prioritization → recommendation → workflow preparation**

rather than simple record searching.