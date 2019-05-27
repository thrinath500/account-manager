package com.revolut.accountmanager.factory;

import com.revolut.accountmanager.action.*;
import com.revoult.model.dao.IAccountDao;

import javax.inject.Inject;

public class ActionFactory {

    @Inject
    private IAccountDao accountDao;

    public RegisterAccountAction accountRegistrationAction(){
        return new RegisterAccountAction(accountDao);
    }

    public ViewAccountAction viewAccountAction(){
        return new ViewAccountAction(accountDao);
    }

    public AccountDepositAction accountDepositAction(Integer accountId){
        return new AccountDepositAction(accountDao, accountId);
    }

    public AccountWithdrawalAction accountWithdrawAction(Integer accountId){
        return new AccountWithdrawalAction(accountDao, accountId);
    }

    public MoneyTransferAction moneyTransferAction(){
        return new MoneyTransferAction(accountDao);
    }

}
