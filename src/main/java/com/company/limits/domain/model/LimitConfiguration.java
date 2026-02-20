package com.company.limits.domain.model;

import com.company.limits.domain.valueobject.CountryCode;
import com.company.limits.domain.valueobject.CurrencyCode;
import com.company.limits.domain.valueobject.CustomerId;
import com.company.limits.domain.valueobject.Money;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public class LimitConfiguration {

    private final UUID id;
    private final CustomerId customerId;
    private final CurrencyCode currencyCode;
    private final CountryCode countryCode;

    private final Money dailyLimit;
    private final Money monthlyLimit;
    private final Money transactionLimit;

    private boolean active;
    private final int version;
    private final Instant createdAt;
    private Instant deactivatedAt;

    public LimitConfiguration(
            UUID id,
            CustomerId customerId,
            CurrencyCode currencyCode,
            CountryCode countryCode,
            Money dailyLimit,
            Money monthlyLimit,
            Money transactionLimit,
            int version
    ) {
        this.id = Objects.requireNonNull(id);
        this.customerId = Objects.requireNonNull(customerId);
        this.currencyCode = Objects.requireNonNull(currencyCode);
        this.countryCode = Objects.requireNonNull(countryCode);
        this.dailyLimit = Objects.requireNonNull(dailyLimit);
        this.monthlyLimit = Objects.requireNonNull(monthlyLimit);
        this.transactionLimit = Objects.requireNonNull(transactionLimit);

        validateBusinessRules();

        this.version = version;
        this.active = true;
        this.createdAt = Instant.now();
    }

    private void validateBusinessRules() {
        if (transactionLimit.isGreaterThan(dailyLimit)) {
            throw new IllegalArgumentException("Transaction limit cannot exceed daily limit");
        }

        if (dailyLimit.isGreaterThan(monthlyLimit)) {
            throw new IllegalArgumentException("Daily limit cannot exceed monthly limit");
        }
    }

    public void deactivate() {
        if (!active) {
            throw new IllegalStateException("Configuration already inactive");
        }
        this.active = false;
        this.deactivatedAt = Instant.now();
    }

    public boolean isActive() {
        return active;
    }

    public UUID getId() {
        return id;
    }

    public CustomerId getCustomerId() {
        return customerId;
    }

    public CurrencyCode getCurrencyCode() {
        return currencyCode;
    }

    public CountryCode getCountryCode() {
        return countryCode;
    }

    public Money getDailyLimit() {
        return dailyLimit;
    }

    public Money getMonthlyLimit() {
        return monthlyLimit;
    }

    public Money getTransactionLimit() {
        return transactionLimit;
    }

    public int getVersion() {
        return version;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getDeactivatedAt() {
        return deactivatedAt;
    }

}