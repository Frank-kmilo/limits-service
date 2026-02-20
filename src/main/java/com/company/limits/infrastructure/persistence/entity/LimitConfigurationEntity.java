package com.company.limits.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "limit_configurations",
        indexes = {
                @Index(name = "idx_customer_currency_active",
                        columnList = "customer_id,currency,active")
        })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LimitConfigurationEntity {

    @Id
    private UUID id;

    @Column(name = "customer_id", nullable = false)
    private String customerId;

    @Column(nullable = false)
    private String currency;

    @Column(nullable = false)
    private String country;

    @Column(name = "daily_limit", nullable = false)
    private Long dailyLimit;

    @Column(name = "monthly_limit", nullable = false)
    private Long monthlyLimit;

    @Column(name = "transaction_limit", nullable = false)
    private Long transactionLimit;

    @Column(nullable = false)
    private Integer version;

    @Column(nullable = false)
    private Boolean active;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;
}