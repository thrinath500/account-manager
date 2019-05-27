package com.revolut.accountmanager.action;

import com.revolut.model.entity.AccountView;
import com.revolut.model.requests.AccountRegisterRequest;
import com.revoult.model.dao.IAccountDao;

public class RegisterAccountAction extends BaseAction<AccountRegisterRequest, AccountView> {

    private IAccountDao accountDao;

    public RegisterAccountAction(IAccountDao accountDao) {
        this.accountDao = accountDao;
    }

    @Override
    void validate(AccountRegisterRequest registerRequest) {
        // Input validations already covered by dropwizard validations
    }

    @Override
    AccountView execute(AccountRegisterRequest registerRequest) {
        return accountDao.create(registerRequest).view();
    }
}
