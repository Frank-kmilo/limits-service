package com.company.limits.domain.exception;

public class DuplicateOperationException extends DomainException {

    public DuplicateOperationException() {
        super("DUPLICATE_OPERATION",
                "Operation already processed for this clientOperationId");
    }
}