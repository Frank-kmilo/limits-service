package com.company.limits.application.usecase;

import com.company.limits.domain.model.LimitConfiguration;
import com.company.limits.domain.port.out.LimitConfigurationRepositoryPort;
import com.company.limits.domain.valueobject.CountryCode;
import com.company.limits.domain.valueobject.CurrencyCode;
import com.company.limits.domain.valueobject.CustomerId;
import com.company.limits.domain.valueobject.Money;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CreateLimitUseCaseTest {

    private LimitConfigurationRepositoryPort repository;
    private CreateLimitUseCase useCase;

    @BeforeEach
    void setUp() {
        repository = mock(LimitConfigurationRepositoryPort.class);
        useCase = new CreateLimitUseCase(repository);
    }

    @Test
    void createLimitSuccess() {
        CustomerId customerId = new CustomerId("123");
        CurrencyCode currency = new CurrencyCode("USD");
        CountryCode country = new CountryCode("PE");

        Money daily = new Money(1000);
        Money monthly = new Money(5000);
        Money transaction = new Money(500);

        LimitConfiguration configuration = new LimitConfiguration(
                UUID.randomUUID(),
                customerId,
                currency,
                country,
                daily,
                monthly,
                transaction,
                1
        );

        // mock save
        when(repository.findActiveByCustomerAndCurrency(customerId, currency))
                .thenReturn(Optional.empty());

        when(repository.save(any(LimitConfiguration.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        LimitConfiguration result = useCase.execute(
                customerId.getValue(),
                currency.getValue(),
                country.getValue(),
                daily.getAmountInCents(),
                monthly.getAmountInCents(),
                transaction.getAmountInCents()
        );

        assertNotNull(result.getId());
        assertEquals(customerId.getValue(), result.getCustomerId().getValue());
        assertEquals(currency.getValue(), result.getCurrencyCode().getValue());
        assertEquals(country.getValue(), result.getCountryCode().getValue());
        assertEquals(daily.getAmountInCents(), result.getDailyLimit().getAmountInCents());
        assertEquals(monthly.getAmountInCents(), result.getMonthlyLimit().getAmountInCents());
        assertEquals(transaction.getAmountInCents(), result.getTransactionLimit().getAmountInCents());
        assertTrue(result.getVersion() == 1);

        // verify save called
        ArgumentCaptor<LimitConfiguration> captor = ArgumentCaptor.forClass(LimitConfiguration.class);
        verify(repository).save(captor.capture());
        assertEquals(result, captor.getValue());
    }
}