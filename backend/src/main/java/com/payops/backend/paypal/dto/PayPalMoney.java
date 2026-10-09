package com.payops.backend.paypal.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

public record PayPalMoney(

        @JsonProperty("currency_code")
        String currencyCode,

        BigDecimal value

) {
}