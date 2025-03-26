package com.example.personnel_management.exception;

/**
 * Custom exception to represent business rule violations in the application.
 * Thrown when a business logic or validation rule is not met.
 */
public class BusinessRuleException extends RuntimeException {
    /**
     * Constructs a new BusinessRuleException with the specified detail message.
     *
     * @param message the detail message explaining the business rule violation
     */
    public BusinessRuleException(String message) {
        super(message);
    }

    /**
     * Constructs a new BusinessRuleException with the specified detail message
     * and cause.
     *
     * @param message the detail message explaining the business rule violation
     * @param cause the underlying cause of the exception
     */
    public BusinessRuleException(String message, Throwable cause) {
        super(message, cause);
    }
}