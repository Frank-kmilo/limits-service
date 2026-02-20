package com.company.limits.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "authorizations",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_customer_operation",
                        columnNames = {"customer_id", "client_operation_id"})
        },
        indexes = {
                @Index(name = "idx_customer_currency_date",
                        columnList = "customer_id,currency,created_at")
        })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthorizationEntity {

    @Id
    private UUID id;

    @Column(name = "customer_id", nullable = false)
    private String customerId;

    @Column(name = "client_operation_id", nullable = false)
    private String clientOperationId;

    @Column(nullable = false)
    private Long amount;

    @Column(nullable = false)
    private String country;

    @Column(nullable = false)
    private String currency;

    @Column(nullable = false)
    private String status;

    @Column
    private String rejectionReason;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;
}