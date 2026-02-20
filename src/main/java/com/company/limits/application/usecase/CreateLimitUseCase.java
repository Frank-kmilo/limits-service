package com.company.limits.application.usecase;

import com.company.limits.domain.model.LimitConfiguration;
import com.company.limits.domain.port.out.LimitConfigurationRepositoryPort;
import com.company.limits.domain.valueobject.CountryCode;
import com.company.limits.domain.valueobject.CurrencyCode;
import com.company.limits.domain.valueobject.CustomerId;
import com.company.limits.domain.valueobject.Money;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class CreateLimitUseCase {

    private final LimitConfigurationRepositoryPort repository;

    public CreateLimitUseCase(LimitConfigurationRepositoryPort repository) {
        this.repository = repository;
    }

    public LimitConfiguration execute(
            String customerId,
            String currencyCode,
            String countryCode,
            long dailyLimit,
            long monthlyLimit,
            long transactionLimit
    ) {

        CustomerId customer = new CustomerId(customerId);
        CurrencyCode currency = new CurrencyCode(currencyCode);
        CountryCode country = new CountryCode(countryCode);

        Money daily = new Money(dailyLimit);
        Money monthly = new Money(monthlyLimit);
        Money transaction = new Money(transactionLimit);

        Optional<LimitConfiguration> existing =
                repository.findActiveByCustomerAndCurrency(customer, currency);

        int newVersion = 1;

        if (existing.isPresent()) {
            repository.deactivateActiveConfiguration(customer, currency);
            newVersion = existing.get().getVersion() + 1;
        }

        LimitConfiguration configuration = new LimitConfiguration(
                UUID.randomUUID(),
                customer,
                currency,
                country,
                daily,
                monthly,
                transaction,
                newVersion
        );

        return repository.save(configuration);
    }
}