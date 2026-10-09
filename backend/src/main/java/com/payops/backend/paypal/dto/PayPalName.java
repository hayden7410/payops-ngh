package com.payops.backend.paypal.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record PayPalName(

        @JsonProperty("given_name")
        String givenName,

        String surname

) {
}