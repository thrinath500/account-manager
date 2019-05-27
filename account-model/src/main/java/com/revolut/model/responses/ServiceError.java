package com.revolut.model.responses;

import lombok.Getter;

@Getter
public class ServiceError {
    private String message;

    public ServiceError(String message) {
        this.message = message;
    }
}
