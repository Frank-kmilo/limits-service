package com.company.limits.domain.model;

import com.company.limits.domain.valueobject.CountryCode;
import com.company.limits.domain.valueobject.CurrencyCode;
import com.company.limits.domain.valueobject.CustomerId;
import com.company.limits.domain.valueobject.Money;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public class Authorization {

    private final UUID id;
    private final CustomerId customerId;
    private final String clientOperationId;
    private final Money amount;
    private final CurrencyCode currencyCode;
    private final CountryCode countryCode;
    private final Instant operationTimestamp;

    private final AuthorizationDecision decision;
    private final String reason;
    private final Instant processedAt;

    private Authorization(
            UUID id,
            CustomerId customerId,
            String clientOperationId,
            Money amount,
            CurrencyCode currencyCode,
            CountryCode countryCode,
            Instant operationTimestamp,
            AuthorizationDecision decision,
            String reason
    ) {
        this.id = Objects.requireNonNull(id);
        this.customerId = Objects.requireNonNull(customerId);

        if (clientOperationId == null || clientOperationId.trim().isEmpty()) {
            throw new IllegalArgumentException("clientOperationId cannot be null or blank");
        }

        this.clientOperationId = clientOperationId.trim();
        this.amount = Objects.requireNonNull(amount);
        this.currencyCode = Objects.requireNonNull(currencyCode);
        this.countryCode = Objects.requireNonNull(countryCode);
        this.operationTimestamp = Objects.requireNonNull(operationTimestamp);
        this.decision = Objects.requireNonNull(decision);

        if (reason == null || reason.trim().isEmpty()) {
            throw new IllegalArgumentException("Reason cannot be null or blank");
        }

        this.reason = reason.trim();
        this.processedAt = Instant.now();
    }

    public static Authorization approve(
            CustomerId customerId,
            String clientOperationId,
            Money amount,
            CurrencyCode currencyCode,
            CountryCode countryCode,
            Instant operationTimestamp
    ) {
        return new Authorization(
                UUID.randomUUID(),
                customerId,
                clientOperationId,
                amount,
                currencyCode,
                countryCode,
                operationTimestamp,
                AuthorizationDecision.APPROVED,
                "Operation approved"
        );
    }

    public static Authorization restore(
            UUID id,
            CustomerId customerId,
            String clientOperationId,
            Money amount,
            CurrencyCode currency,
            CountryCode country,
            String status,
            String rejectionReason,
            Instant createdAt
    ) {
        return new Authorization(
                id,
                customerId,
                clientOperationId,
                amount,
                currency,
                country,
                createdAt,
                AuthorizationDecision.valueOf(status),
                rejectionReason
        );
    }

    public static Authorization reject(
            CustomerId customerId,
            String clientOperationId,
            Money amount,
            CurrencyCode currencyCode,
            CountryCode countryCode,
            Instant operationTimestamp,
            String reason
    ) {
        return new Authorization(
                UUID.randomUUID(),
                customerId,
                clientOperationId,
                amount,
                currencyCode,
                countryCode,
                operationTimestamp,
                AuthorizationDecision.REJECTED,
                reason
        );
    }


    public UUID getId() {
        return id;
    }

    public CustomerId getCustomerId() {
        return customerId;
    }

    public String getClientOperationId() {
        return clientOperationId;
    }

    public Money getAmount() { return amount; }

    public CurrencyCode getCurrencyCode() {return currencyCode;}

    public CountryCode getCountryCode() {
        return countryCode;
    }

    public Instant getOperationTimestamp() {
        return operationTimestamp;
    }

    public AuthorizationDecision getDecision() {
        return decision;
    }

    public String getReason() { return reason; }

    public Instant getProcessedAt() {
        return processedAt;
    }
}