package com.payops.backend.paypal.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record PayPalInvoiceSummary(

        String id,

        String status,

        PayPalInvoiceDetail detail,

        @JsonProperty("primary_recipients")
        List<PayPalRecipient> primaryRecipients,

        PayPalMoney amount,

        @JsonProperty("due_amount")
        PayPalMoney dueAmount,

        PayPalPayments payments

) {
}