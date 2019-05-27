package com.revolut.model.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.Value;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;

@Value
@Getter
@Builder
public class AccountView {
    private int accountId;
    private String firstName;
    private String lastName;
    private String address;
    private Map<CurrencyType, BigDecimal> balance;

    private Date createdAt;
    private Date updatedAt;
}
