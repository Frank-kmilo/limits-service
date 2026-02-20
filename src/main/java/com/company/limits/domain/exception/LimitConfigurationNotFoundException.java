package com.company.limits.domain.exception;

public class LimitConfigurationNotFoundException extends DomainException {

    public LimitConfigurationNotFoundException() {
        super("LIMIT_CONFIG_NOT_FOUND",
                "No active limit configuration found for customer and currency");
    }
}