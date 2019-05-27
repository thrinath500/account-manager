package com.revolut.model.requests;

import com.revolut.model.entity.CurrencyType;
import lombok.AllArgsConstructor;
import lombok.Getter;

import javax.validation.constraints.Digits;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@AllArgsConstructor
@Getter
public class MoneyTransferRequest {

    @NotNull
    private CurrencyType currencyType;

    @NotNull
    @Max(message = "Cannot transfer more than 10 million per txn" , value = 10000000)
    @Min(message = "input, cannot be less than 1.0 per currency type" , value =  1)
    @Digits(integer = 8, fraction = 2)
    private BigDecimal value;

    @NotNull
    @Min(message = "fromAccountId, should be a valid number" , value = 1)
    private Integer fromAccountId;

    @NotNull
    @Min(message = "toAccountId, should be a valid number" , value = 1)
    private Integer toAccountId;
}
