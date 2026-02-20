package com.company.limits.infrastructure.rest.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class CreateLimitRequest {

    @JsonProperty("customer_id")
    private String customerId;

    @JsonProperty("currency_code")
    private String currencyCode;

    @JsonProperty("country_code")
    private String countryCode;

    @JsonProperty("daily_limit_amount")
    private Long dailyLimitAmount;

    @JsonProperty("monthly_limit_amount")
    private Long monthlyLimitAmount;

    @JsonProperty("transaction_limit_amount")
    private Long transactionLimitAmount;
}