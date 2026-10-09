package com.payops.backend.paypal.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record PayPalRecipient(

        @JsonProperty("billing_info")
        PayPalBillingInfo billingInfo

) {
}