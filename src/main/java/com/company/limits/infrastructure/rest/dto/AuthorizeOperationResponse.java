package com.company.limits.infrastructure.rest.dto;

import com.company.limits.domain.valueobject.Money;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.Instant;

@Data
@AllArgsConstructor
public class AuthorizeOperationResponse {

    private String status;
    private String reason;
    private long amount;
    private Instant processedAt;
}