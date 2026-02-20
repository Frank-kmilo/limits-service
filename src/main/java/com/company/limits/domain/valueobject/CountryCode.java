package com.company.limits.domain.valueobject;

import java.util.Arrays;
import java.util.Locale;
import java.util.Objects;

public final class CountryCode {

    private final String value;

    public CountryCode(String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("CountryCode cannot be null or blank");
        }

        String normalized = value.trim().toUpperCase();

        boolean valid = Arrays.asList(Locale.getISOCountries())
                .contains(normalized);

        if (!valid) {
            throw new IllegalArgumentException("Invalid ISO 3166-1 alpha-2 country code: " + value);
        }

        this.value = normalized;
    }

    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CountryCode that)) return false;
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