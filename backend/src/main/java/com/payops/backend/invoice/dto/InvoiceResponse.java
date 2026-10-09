package com.payops.backend.invoice.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record InvoiceResponse(

        String invoiceId,
        String invoiceNumber,

        String customerName,
        String customerEmail,

        BigDecimal amount,
        BigDecimal paidAmount,
        BigDecimal remainingAmount,

        String currency,

        LocalDate invoiceDate,
        LocalDate dueDate,

        String status

) {
}