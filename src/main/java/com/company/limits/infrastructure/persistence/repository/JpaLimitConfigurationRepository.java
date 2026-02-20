package com.company.limits.infrastructure.persistence.repository;

import com.company.limits.infrastructure.persistence.entity.LimitConfigurationEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface JpaLimitConfigurationRepository
        extends JpaRepository<LimitConfigurationEntity, UUID> {

    Optional<LimitConfigurationEntity> findByCustomerIdAndCurrencyAndActiveTrue(
            String customerId,
            String currency
    );

    void deleteByCustomerIdAndCurrencyAndActiveTrue(
            String customerId,
            String currency
    );
}