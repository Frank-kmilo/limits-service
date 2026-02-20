package com.company.limits.infrastructure.persistence.repository;

import com.company.limits.infrastructure.persistence.entity.AuthorizationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

public interface JpaAuthorizationRepository
        extends JpaRepository<AuthorizationEntity, UUID> {

    Optional<AuthorizationEntity>
    findByCustomerIdAndClientOperationId(
            String customerId,
            String clientOperationId
    );

    @Query("""
        SELECT COALESCE(SUM(a.amount), 0)
        FROM AuthorizationEntity a
        WHERE a.customerId = :customerId
          AND a.currency = :currency
          AND a.status = 'APPROVED'
          AND a.createdAt >= :start
          AND a.createdAt < :end
    """)
    Long sumApprovedBetween(
            String customerId,
            String currency,
            Instant start,
            Instant end
    );
}