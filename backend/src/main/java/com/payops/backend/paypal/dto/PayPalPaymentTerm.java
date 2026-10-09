package com.payops.backend.paypal.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;

public record PayPalPaymentTerm(

        @JsonProperty("due_date")
        LocalDate dueDate

) {
}