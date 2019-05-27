package com.revoult.model.dao;

import com.revoult.model.dao.entity.Account;
import com.revolut.model.requests.AccountRegisterRequest;

public interface IAccountDao {
    Account create(AccountRegisterRequest registerRequest);

    Account get(int accountId);
}
