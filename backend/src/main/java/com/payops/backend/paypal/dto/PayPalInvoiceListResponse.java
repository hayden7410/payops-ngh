package com.payops.backend.paypal.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record PayPalInvoiceListResponse(

        @JsonProperty("total_pages")
        int totalPages,

        @JsonProperty("total_items")
        int totalItems,

        List<PayPalInvoiceSummary> items

) {
}