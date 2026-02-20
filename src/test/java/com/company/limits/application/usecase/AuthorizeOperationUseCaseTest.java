package com.company.limits.application.usecase;

import com.company.limits.domain.model.Authorization;
import com.company.limits.domain.model.AuthorizationDecision;
import com.company.limits.domain.model.LimitConfiguration;
import com.company.limits.domain.port.out.AuthorizationRepositoryPort;
import com.company.limits.domain.port.out.LimitConfigurationRepositoryPort;
import com.company.limits.domain.service.AuthorizationEvaluator;
import com.company.limits.domain.valueobject.CountryCode;
import com.company.limits.domain.valueobject.CurrencyCode;
import com.company.limits.domain.valueobject.CustomerId;
import com.company.limits.domain.valueobject.Money;
import com.company.limits.domain.exception.LimitConfigurationNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthorizeOperationUseCaseTest {

    private AuthorizationRepositoryPort authorizationRepository;
    private LimitConfigurationRepositoryPort limitRepository;
    private AuthorizationEvaluator evaluator;
    private AuthorizeOperationUseCase useCase;

    @BeforeEach
    void setUp() {
        authorizationRepository = mock(AuthorizationRepositoryPort.class);
        limitRepository = mock(LimitConfigurationRepositoryPort.class);
        evaluator = new AuthorizationEvaluator();
        useCase = new AuthorizeOperationUseCase(authorizationRepository, limitRepository, evaluator);
    }

    @Test
    void approveTransaction_whenWithinLimits() {
        CustomerId customer = new CustomerId("CUST1");
        CurrencyCode currency = new CurrencyCode("USD");
        CountryCode country = new CountryCode("PE");

        LimitConfiguration config = new LimitConfiguration(
                UUID.randomUUID(),
                customer,
                currency,
                country,
                new Money(2000),
                new Money(10000),
                new Money(1000),
                1
        );

        when(limitRepository.findActiveByCustomerAndCurrency(customer, currency))
                .thenReturn(Optional.of(config));
        when(authorizationRepository.findByCustomerAndClientOperationId(any(), any()))
                .thenReturn(Optional.empty());
        when(authorizationRepository.sumApprovedDaily(any(), any(), any(), any()))
                .thenReturn(new Money(0, true));
        when(authorizationRepository.sumApprovedMonthly(any(), any(), any(), any()))
                .thenReturn(new Money(0, true));
        when(authorizationRepository.save(any(Authorization.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        Authorization result = useCase.execute(
                customer.getValue(),
                "op-1",
                500,
                currency.getValue(),
                country.getValue(),
                Instant.now()
        );

        assertNotNull(result);
        assertEquals(AuthorizationDecision.APPROVED, result.getDecision());
        // reason obtenido del evaluator debería coincidir
        var expected = evaluator.evaluate(config, new Money(500), new Money(0, true), new Money(0, true));
        assertEquals(expected.getReason(), result.getReason());
    }

    @Test
    void rejectTransaction_whenExceedsTransactionLimit() {
        CustomerId customer = new CustomerId("CUST1");
        CurrencyCode currency = new CurrencyCode("USD");
        CountryCode country = new CountryCode("PE");

        LimitConfiguration config = new LimitConfiguration(
                UUID.randomUUID(),
                customer,
                currency,
                country,
                new Money(2000),
                new Money(10000),
                new Money(400), // transaction limit 400
                1
        );

        when(limitRepository.findActiveByCustomerAndCurrency(customer, currency))
                .thenReturn(Optional.of(config));
        when(authorizationRepository.findByCustomerAndClientOperationId(any(), any()))
                .thenReturn(Optional.empty());
        when(authorizationRepository.sumApprovedDaily(any(), any(), any(), any()))
                .thenReturn(new Money(0, true));
        when(authorizationRepository.sumApprovedMonthly(any(), any(), any(), any()))
                .thenReturn(new Money(0, true));
        when(authorizationRepository.save(any(Authorization.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        // la operación es 500 > transaction limit 400
        Authorization result = useCase.execute(
                customer.getValue(),
                "op-2",
                500,
                currency.getValue(),
                country.getValue(),
                Instant.now()
        );

        assertNotNull(result);
        assertEquals(AuthorizationDecision.REJECTED, result.getDecision());

        // compara con el mensaje real del evaluator
        var expected = evaluator.evaluate(config, new Money(500), new Money(0, true), new Money(0, true));
        assertEquals(expected.getReason(), result.getReason());
    }

    @Test
    void rejectTransaction_whenExceedsDailyLimit() {
        CustomerId customer = new CustomerId("CUST1");
        CurrencyCode currency = new CurrencyCode("USD");
        CountryCode country = new CountryCode("PE");

        LimitConfiguration config = new LimitConfiguration(
                UUID.randomUUID(),
                customer,
                currency,
                country,
                new Money(10000), // daily
                new Money(50000), // monthly
                new Money(5000),  // per transaction
                1
        );

        when(limitRepository.findActiveByCustomerAndCurrency(customer, currency))
                .thenReturn(Optional.of(config));
        when(authorizationRepository.findByCustomerAndClientOperationId(any(), any()))
                .thenReturn(Optional.empty());

        // ya acumula 8000 en el día
        when(authorizationRepository.sumApprovedDaily(any(), any(), any(), any()))
                .thenReturn(new Money(8000, true));
        when(authorizationRepository.sumApprovedMonthly(any(), any(), any(), any()))
                .thenReturn(new Money(0, true));
        when(authorizationRepository.save(any(Authorization.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        // intento 3000: 8000 + 3000 > daily 10000 -> reject
        Authorization result = useCase.execute(
                customer.getValue(),
                "op-3",
                3000,
                currency.getValue(),
                country.getValue(),
                Instant.now()
        );

        assertNotNull(result);
        assertEquals(AuthorizationDecision.REJECTED, result.getDecision());
        var expected = evaluator.evaluate(config, new Money(3000), new Money(8000, true), new Money(0, true));
        assertEquals(expected.getReason(), result.getReason());
    }

    @Test
    void rejectTransaction_whenExceedsMonthlyLimit() {
        CustomerId customer = new CustomerId("CUST1");
        CurrencyCode currency = new CurrencyCode("USD");
        CountryCode country = new CountryCode("PE");

        LimitConfiguration config = new LimitConfiguration(
                UUID.randomUUID(),
                customer,
                currency,
                country,
                new Money(10000), // daily
                new Money(50000), // monthly
                new Money(5000),  // per transaction
                1
        );

        when(limitRepository.findActiveByCustomerAndCurrency(customer, currency))
                .thenReturn(Optional.of(config));
        when(authorizationRepository.findByCustomerAndClientOperationId(any(), any()))
                .thenReturn(Optional.empty());

        when(authorizationRepository.sumApprovedDaily(any(), any(), any(), any()))
                .thenReturn(new Money(0, true));
        // ya acumulado 48000 mensual
        when(authorizationRepository.sumApprovedMonthly(any(), any(), any(), any()))
                .thenReturn(new Money(48000, true));
        when(authorizationRepository.save(any(Authorization.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        // intento 3000: 48000 + 3000 > 50000 monthly -> reject
        Authorization result = useCase.execute(
                customer.getValue(),
                "op-4",
                3000,
                currency.getValue(),
                country.getValue(),
                Instant.now()
        );

        assertNotNull(result);
        assertEquals(AuthorizationDecision.REJECTED, result.getDecision());
        var expected = evaluator.evaluate(config, new Money(3000), new Money(0, true), new Money(48000, true));
        assertEquals(expected.getReason(), result.getReason());
    }

    @Test
    void returnExistingAuthorization_whenAlreadyExists() {
        CustomerId customer = new CustomerId("CUST1");
        CurrencyCode currency = new CurrencyCode("USD");
        CountryCode country = new CountryCode("PE");
        Money amount = new Money(2000);

        Authorization existing = Authorization.approve(
                customer,
                "op-exist",
                amount,
                currency,
                country,
                Instant.now()
        );

        when(authorizationRepository.findByCustomerAndClientOperationId(customer, "op-exist"))
                .thenReturn(Optional.of(existing));

        Authorization result = useCase.execute(
                customer.getValue(),
                "op-exist",
                2000,
                currency.getValue(),
                country.getValue(),
                Instant.now()
        );

        assertSame(existing, result);
        verify(authorizationRepository, never()).save(any());
    }

    @Test
    void throwWhenNoLimitConfiguration() {
        when(authorizationRepository.findByCustomerAndClientOperationId(any(), any()))
                .thenReturn(Optional.empty());
        when(limitRepository.findActiveByCustomerAndCurrency(any(), any()))
                .thenReturn(Optional.empty());

        assertThrows(LimitConfigurationNotFoundException.class, () ->
                useCase.execute("CUST1", "op-x", 100L, "USD", "PE", Instant.now())
        );
    }
}