package com.company.limits.infrastructure.persistence.adapter;

import com.company.limits.domain.model.LimitConfiguration;
import com.company.limits.domain.port.out.LimitConfigurationRepositoryPort;
import com.company.limits.domain.valueobject.CurrencyCode;
import com.company.limits.domain.valueobject.CustomerId;
import com.company.limits.infrastructure.persistence.entity.LimitConfigurationEntity;
import com.company.limits.infrastructure.persistence.repository.JpaLimitConfigurationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class LimitConfigurationRepositoryAdapter
        implements LimitConfigurationRepositoryPort {

    private final JpaLimitConfigurationRepository repository;

    @Override
    public Optional<LimitConfiguration> findActiveByCustomerAndCurrency(
            CustomerId customerId,
            CurrencyCode currencyCode
    ) {
        return repository
                .findByCustomerIdAndCurrencyAndActiveTrue(
                        customerId.getValue(),
                        currencyCode.getValue()
                )
                .map(this::toDomain);
    }

    @Override
    public LimitConfiguration save(LimitConfiguration configuration) {

        LimitConfigurationEntity entity = toEntity(configuration);

        return toDomain(repository.save(entity));
    }

    @Override
    public void deactivateActiveConfiguration(
            CustomerId customerId,
            CurrencyCode currencyCode
    ) {

        repository.findByCustomerIdAndCurrencyAndActiveTrue(
                customerId.getValue(),
                currencyCode.getValue()
        ).ifPresent(entity -> {
            entity.setActive(false);
            repository.save(entity);
        });
    }

    private LimitConfiguration toDomain(LimitConfigurationEntity entity) {
        return new LimitConfiguration(
                entity.getId(),
                new CustomerId(entity.getCustomerId()),
                new CurrencyCode(entity.getCurrency()),
                new com.company.limits.domain.valueobject.CountryCode(entity.getCountry()),
                new com.company.limits.domain.valueobject.Money(entity.getDailyLimit()),
                new com.company.limits.domain.valueobject.Money(entity.getMonthlyLimit()),
                new com.company.limits.domain.valueobject.Money(entity.getTransactionLimit()),
                entity.getVersion()
        );
    }

    private LimitConfigurationEntity toEntity(LimitConfiguration configuration) {
        return LimitConfigurationEntity.builder()
                .id(configuration.getId() != null
                        ? configuration.getId()
                        : UUID.randomUUID())
                .customerId(configuration.getCustomerId().getValue())
                .currency(configuration.getCurrencyCode().getValue())
                .country(configuration.getCountryCode().getValue())
                .dailyLimit(configuration.getDailyLimit().getAmountInCents())
                .monthlyLimit(configuration.getMonthlyLimit().getAmountInCents())
                .transactionLimit(configuration.getTransactionLimit().getAmountInCents())
                .version(configuration.getVersion())
                .active(true)
                .createdAt(Instant.now())
                .build();
    }
}