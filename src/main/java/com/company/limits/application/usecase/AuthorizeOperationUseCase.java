package com.company.limits.application.usecase;

import com.company.limits.domain.model.Authorization;
import com.company.limits.domain.model.AuthorizationDecision;
import com.company.limits.domain.port.out.AuthorizationRepositoryPort;
import com.company.limits.domain.port.out.LimitConfigurationRepositoryPort;
import com.company.limits.domain.service.AuthorizationEvaluator;
import com.company.limits.domain.valueobject.CountryCode;
import com.company.limits.domain.valueobject.CurrencyCode;
import com.company.limits.domain.valueobject.CustomerId;
import com.company.limits.domain.valueobject.Money;
import com.company.limits.domain.exception.LimitConfigurationNotFoundException;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.Optional;

@Service
public class AuthorizeOperationUseCase {

    private final AuthorizationRepositoryPort authorizationRepository;
    private final LimitConfigurationRepositoryPort limitRepository;
    private final AuthorizationEvaluator evaluator;

    public AuthorizeOperationUseCase(
            AuthorizationRepositoryPort authorizationRepository,
            LimitConfigurationRepositoryPort limitRepository,
            AuthorizationEvaluator evaluator
    ) {
        this.authorizationRepository = authorizationRepository;
        this.limitRepository = limitRepository;
        this.evaluator = evaluator;
    }

    public Authorization execute(
            String customerId,
            String clientOperationId,
            long amount,
            String currencyCode,
            String countryCode,
            Instant operationTimestamp
    ) {

        CustomerId customer = new CustomerId(customerId);

        Optional<Authorization> existing =
                authorizationRepository.findByCustomerAndClientOperationId(
                        customer, clientOperationId
                );

        if (existing.isPresent()) {
            return existing.get();
        }

        CurrencyCode currency = new CurrencyCode(currencyCode);
        CountryCode country = new CountryCode(countryCode);
        Money money = new Money(amount);

        var configuration = limitRepository
                .findActiveByCustomerAndCurrency(customer, currency)
                .orElseThrow(LimitConfigurationNotFoundException::new);

        LocalDate date = operationTimestamp.atZone(ZoneOffset.UTC).toLocalDate();

        Instant startOfDay = date.atStartOfDay().toInstant(ZoneOffset.UTC);
        Instant endOfDay = date.plusDays(1).atStartOfDay().toInstant(ZoneOffset.UTC);

        Instant startOfMonth = date.withDayOfMonth(1)
                .atStartOfDay().toInstant(ZoneOffset.UTC);

        Money dailyAccumulated =
                authorizationRepository.sumApprovedDaily(
                        customer, currency, startOfDay, endOfDay
                );

        Money monthlyAccumulated =
                authorizationRepository.sumApprovedMonthly(
                        customer, currency, startOfMonth, endOfDay
                );

        var evaluation = evaluator.evaluate(
                configuration,
                money,
                dailyAccumulated,
                monthlyAccumulated
        );

        Authorization authorization;

        if (evaluation.getDecision() == AuthorizationDecision.APPROVED)  {
            authorization = Authorization.approve(
                    customer,
                    clientOperationId,
                    money,
                    currency,
                    country,
                    operationTimestamp
            );
        } else {
            authorization = Authorization.reject(
                    customer,
                    clientOperationId,
                    money,
                    currency,
                    country,
                    operationTimestamp,
                    evaluation.getReason()
            );
        }

        return authorizationRepository.save(authorization);
    }
}