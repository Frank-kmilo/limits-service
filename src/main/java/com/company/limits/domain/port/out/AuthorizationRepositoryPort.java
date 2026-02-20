package com.company.limits.domain.port.out;

import com.company.limits.domain.model.Authorization;
import com.company.limits.domain.valueobject.CurrencyCode;
import com.company.limits.domain.valueobject.CustomerId;
import com.company.limits.domain.valueobject.Money;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

public interface AuthorizationRepositoryPort {

    Authorization save(Authorization authorization);

    Optional<Authorization> findByCustomerAndClientOperationId(
            CustomerId customerId,
            String clientOperationId
    );

    Money sumApprovedDaily(
            CustomerId customerId,
            CurrencyCode currencyCode,
            Instant from,
            Instant to
    );

    Money sumApprovedMonthly(
            CustomerId customerId,
            CurrencyCode currencyCode,
            Instant from,
            Instant to
    );

    Optional<Authorization> findById(UUID id);
}