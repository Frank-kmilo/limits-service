package com.company.limits.domain.valueobject;

import java.util.Currency;
import java.util.Objects;

public final class CurrencyCode {

    private final String value;

    public CurrencyCode(String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("CurrencyCode cannot be null or blank");
        }

        String normalized = value.trim().toUpperCase();

        try {
            Currency.getInstance(normalized);
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException("Invalid ISO 4217 currency code: " + value);
        }

        this.value = normalized;
    }

    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CurrencyCode that)) return false;
        return value.equals(that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return value;
    }
}