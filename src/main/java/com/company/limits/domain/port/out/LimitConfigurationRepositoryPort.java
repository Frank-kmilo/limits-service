package com.company.limits.domain.port.out;

import com.company.limits.domain.model.LimitConfiguration;
import com.company.limits.domain.valueobject.CurrencyCode;
import com.company.limits.domain.valueobject.CustomerId;

import java.util.Optional;

public interface LimitConfigurationRepositoryPort {

    LimitConfiguration save(LimitConfiguration configuration);

    Optional<LimitConfiguration> findActiveByCustomerAndCurrency(
            CustomerId customerId,
            CurrencyCode currencyCode
    );

    void deactivateActiveConfiguration(
            CustomerId customerId,
            CurrencyCode currencyCode
    );
}