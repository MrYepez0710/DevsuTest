package com.devsu.domain.exception;

/**
 * Exception for insufficient balance scenarios (F3)
 * Thrown when a withdrawal would result in negative balance
 */
public class InsufficientBalanceException extends RuntimeException {
    
    private static final long serialVersionUID = 1L;
    
    public InsufficientBalanceException(String message) {
        super(message);
    }
    
    public InsufficientBalanceException(String message, Throwable cause) {
        super(message, cause);
    }
}
