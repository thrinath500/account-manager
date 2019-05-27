package com.revoult.model.dao.impl;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;
import com.revoult.model.dao.entity.Account;
import com.revolut.model.requests.AccountRegisterRequest;
import com.revoult.model.dao.IAccountDao;

import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class AccountRepository implements IAccountDao {

    @VisibleForTesting
    ConcurrentHashMap<Integer, Account> idToAccountMap = new ConcurrentHashMap<Integer, Account>();
    // AtomicInteger to be thread safe
    @VisibleForTesting
    protected static final AtomicInteger idGenerator = new AtomicInteger(1);

    public Account create(AccountRegisterRequest registerRequest) {
        Date currentDate = new Date();
        int id = idGenerator.getAndIncrement();
        idToAccountMap.put(id, Account.builder().accountId(id).
                firstName(registerRequest.getFirstName()).
                lastName(registerRequest.getLastName()).
                address(registerRequest.getAddress()).
                createdAt(currentDate).
                updatedAt(currentDate).build());
        return idToAccountMap.get(id);
    }

    public Account get(int accountId) {
        Account account = idToAccountMap.get(accountId);
        Preconditions.checkNotNull(account, "Request accountId : " + accountId + " is not present");
        return account;
    }
}
