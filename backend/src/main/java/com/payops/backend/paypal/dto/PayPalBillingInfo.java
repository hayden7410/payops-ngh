package com.payops.backend.paypal.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record PayPalBillingInfo(

        @JsonProperty("email_address")
        String emailAddress,

        PayPalName name

) {
}