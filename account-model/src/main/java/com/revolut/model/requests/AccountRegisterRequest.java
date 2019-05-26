package com.revolut.model.requests;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class AccountRegisterRequest {
    private String firstName;
    private String lastName;
    private String address;
    private String phoneNumber;
}
