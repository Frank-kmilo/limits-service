package com.company.limits.infrastructure.rest.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CreateLimitResponse {

    @JsonProperty("limit_id")
    private String limitId;

    @JsonProperty("status")
    private String status;

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