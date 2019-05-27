package com.revolut.model.entity;

import lombok.Getter;
import lombok.Value;

import java.math.BigDecimal;

@Value
@Getter
public class AuditEntry {

    private CurrencyType currencyType;
    private BigDecimal value;

    private Type type;
    private BigDecimal balance;

    public enum Type{
        CREDIT,
        DEBIT
    }
}
