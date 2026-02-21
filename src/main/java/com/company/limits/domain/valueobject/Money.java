package com.company.limits.domain.valueobject;

import java.util.Objects;

public final class Money {

    private final long amountInCents;

    public Money(long amountInCents) {
        validateMoney(amountInCents, false);
        this.amountInCents = amountInCents;
    }

    public Money(long amountInCents, boolean allowZero) {
        validateMoney(amountInCents, allowZero);
        this.amountInCents = amountInCents;
    }

    private void validateMoney(long amountInCents, boolean allowZero) {
        if (amountInCents < 0) {
            throw new IllegalArgumentException("Amount cannot be negative");
        }

        if (!allowZero && amountInCents == 0) {
            throw new IllegalArgumentException("Amount must be greater than zero");
        }
    }

    public long getAmountInCents() {
        return amountInCents;
    }

    public Money add(Money other) {
        Objects.requireNonNull(other, "Money to add cannot be null");
        long result = Math.addExact(this.amountInCents, other.amountInCents);
        return new Money(result);
    }

    public boolean isGreaterThan(Money other) {
        Objects.requireNonNull(other);
        return this.amountInCents > other.amountInCents;
    }

    public boolean isLessThanOrEqual(Money other) {
        Objects.requireNonNull(other);
        return this.amountInCents <= other.amountInCents;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Money money)) return false;
        return amountInCents == money.amountInCents;
    }

    @Override
    public int hashCode() {
        return Objects.hash(amountInCents);
    }

    @Override
    public String toString() {
        return String.valueOf(amountInCents);
    }
}