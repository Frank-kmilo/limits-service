package com.company.limits.domain.service;

import com.company.limits.domain.model.AuthorizationDecision;
import com.company.limits.domain.model.LimitConfiguration;
import com.company.limits.domain.valueobject.Money;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class AuthorizationEvaluator {

    public EvaluationResult evaluate(
            LimitConfiguration configuration,
            Money amount,
            Money dailyAccumulated,
            Money monthlyAccumulated
    ) {
        Objects.requireNonNull(configuration);
        Objects.requireNonNull(amount);
        Objects.requireNonNull(dailyAccumulated);
        Objects.requireNonNull(monthlyAccumulated);

        if (amount.isGreaterThan(configuration.getTransactionLimit())) {
            return EvaluationResult.rejected(
                    "Transaction amount exceeds transaction limit"
            );
        }

        Money newDailyTotal = dailyAccumulated.add(amount);
        if (newDailyTotal.isGreaterThan(configuration.getDailyLimit())) {
            return EvaluationResult.rejected(
                    "Daily limit exceeded"
            );
        }

        Money newMonthlyTotal = monthlyAccumulated.add(amount);
        if (newMonthlyTotal.isGreaterThan(configuration.getMonthlyLimit())) {
            return EvaluationResult.rejected(
                    "Monthly limit exceeded"
            );
        }

        return EvaluationResult.approved();
    }

    public static class EvaluationResult {

        private final AuthorizationDecision decision;
        private final String reason;

        private EvaluationResult(AuthorizationDecision decision, String reason) {
            this.decision = decision;
            this.reason = reason;
        }

        public static EvaluationResult approved() {
            return new EvaluationResult(
                    AuthorizationDecision.APPROVED,
                    "Operation approved"
            );
        }

        public static EvaluationResult rejected(String reason) {
            return new EvaluationResult(
                    AuthorizationDecision.REJECTED,
                    reason
            );
        }

        public AuthorizationDecision getDecision() {
            return decision;
        }

        public String getReason() {
            return reason;
        }
    }
}