package com.company.limits.domain.model;

import com.company.limits.domain.valueobject.Money;

public class AuthorizationEvaluator {

    public static class Evaluation {
        private final AuthorizationDecision decision;
        private final String reason;

        public Evaluation(AuthorizationDecision decision, String reason) {
            this.decision = decision;
            this.reason = reason;
        }

        public AuthorizationDecision getDecision() {
            return decision;
        }

        public String getReason() {
            return reason;
        }
    }

    public Evaluation evaluate(
            LimitConfiguration config,
            Money transactionAmount,
            Money dailyAccumulated,
            Money monthlyAccumulated
    ) {
        if (transactionAmount.isGreaterThan(config.getTransactionLimit())) {
            return new Evaluation(AuthorizationDecision.REJECTED, "Transaction exceeds limit");
        }

        if (dailyAccumulated.add(transactionAmount).isGreaterThan(config.getDailyLimit())) {
            return new Evaluation(AuthorizationDecision.REJECTED, "Daily limit exceeded");
        }

        if (monthlyAccumulated.add(transactionAmount).isGreaterThan(config.getMonthlyLimit())) {
            return new Evaluation(AuthorizationDecision.REJECTED, "Monthly limit exceeded");
        }

        return new Evaluation(AuthorizationDecision.APPROVED, "Operation approved");
    }
}