package com.revolut.model.requests;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;

@AllArgsConstructor
@Getter
public class AccountRegisterRequest {

    @NotEmpty
    @Length(max = 20, min = 4)
    private String firstName;

    @NotEmpty
    @Length(max = 20, min = 4)
    private String lastName;

    @NotEmpty
    @Length(max = 50, min = 7)
    private String address;
}
