package com.company.limits.infrastructure.rest.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.Instant;

@Data
public class AuthorizeOperationRequest {
    @JsonProperty("customer_id")
    private String customerId;

    @JsonProperty("client_operation_id")
    private String clientOperationId;

    @JsonProperty("amount")
    private Long amount;

    @JsonProperty("currency_code")
    private String currency;

    @JsonProperty("country_code")
    private String country;

    @JsonProperty("operation_timestamp")
    private Instant operationTimestamp;
}