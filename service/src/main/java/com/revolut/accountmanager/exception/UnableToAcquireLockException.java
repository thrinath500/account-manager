package com.revolut.accountmanager.exception;

public class UnableToAcquireLockException extends RuntimeException {

    public UnableToAcquireLockException(String message) {
        super(message);
    }
}
