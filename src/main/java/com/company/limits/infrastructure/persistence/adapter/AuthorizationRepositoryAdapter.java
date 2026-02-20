package com.company.limits.infrastructure.persistence.adapter;

import com.company.limits.domain.model.Authorization;
import com.company.limits.domain.port.out.AuthorizationRepositoryPort;
import com.company.limits.domain.valueobject.CurrencyCode;
import com.company.limits.domain.valueobject.CustomerId;
import com.company.limits.domain.valueobject.Money;
import com.company.limits.infrastructure.persistence.entity.AuthorizationEntity;
import com.company.limits.infrastructure.persistence.repository.JpaAuthorizationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class AuthorizationRepositoryAdapter
        implements AuthorizationRepositoryPort {

    private final JpaAuthorizationRepository repository;

    @Override
    public Optional<Authorization> findByCustomerAndClientOperationId(
            CustomerId customerId,
            String clientOperationId
    ) {
        return repository
                .findByCustomerIdAndClientOperationId(
                        customerId.getValue(),
                        clientOperationId
                )
                .map(this::toDomain);
    }

    @Override
    public Authorization save(Authorization authorization) {
        AuthorizationEntity entity = toEntity(authorization);
        return toDomain(repository.save(entity));
    }

    @Override
    public Money sumApprovedDaily(
            CustomerId customerId,
            CurrencyCode currencyCode,
            Instant start,
            Instant end
    ) {
        Long sum = repository.sumApprovedBetween(
                customerId.getValue(),
                currencyCode.getValue(),
                start,
                end
        );

        return new Money(sum != null ? sum : 0L, true);
    }

    @Override
    public Money sumApprovedMonthly(
            CustomerId customerId,
            CurrencyCode currencyCode,
            Instant start,
            Instant end
    ) {
        Long sum = repository.sumApprovedBetween(
                customerId.getValue(),
                currencyCode.getValue(),
                start,
                end
        );

        return new Money(sum != null ? sum : 0L, true);
    }

    @Override
    public Optional<Authorization> findById(UUID id) {
        return repository.findById(id)
                .map(this::toDomain);
    }

    private Authorization toDomain(AuthorizationEntity entity) {
        return Authorization.restore(
                entity.getId(),
                new CustomerId(entity.getCustomerId()),
                entity.getClientOperationId(),
                new Money(entity.getAmount()),
                new CurrencyCode(entity.getCurrency()),
                new com.company.limits.domain.valueobject.CountryCode(entity.getCountry()),
                entity.getStatus(),
                entity.getRejectionReason(),
                entity.getCreatedAt()
        );
    }

    private AuthorizationEntity toEntity(Authorization authorization) {
        return AuthorizationEntity.builder()
                .id(authorization.getId() != null
                        ? authorization.getId()
                        : UUID.randomUUID())
                .customerId(authorization.getCustomerId().getValue())
                .clientOperationId(authorization.getClientOperationId())
                .amount(authorization.getAmount().getAmountInCents())
                .currency(authorization.getCurrencyCode().getValue())
                .country(authorization.getCountryCode().getValue())
                .status(authorization.getDecision().name())
                .rejectionReason(authorization.getReason())
                .createdAt(authorization.getProcessedAt())
                .build();
    }
}