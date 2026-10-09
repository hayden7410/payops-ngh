package com.payops.backend.paypal.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record PayPalPayments(

        @JsonProperty("paid_amount")
        PayPalMoney paidAmount

) {
}