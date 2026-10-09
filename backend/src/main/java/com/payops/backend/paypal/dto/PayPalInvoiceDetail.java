package com.payops.backend.paypal.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;

public record PayPalInvoiceDetail(

        @JsonProperty("currency_code")
        String currencyCode,

        @JsonProperty("invoice_number")
        String invoiceNumber,

        @JsonProperty("invoice_date")
        LocalDate invoiceDate,

        @JsonProperty("payment_term")
        PayPalPaymentTerm paymentTerm

) {
}