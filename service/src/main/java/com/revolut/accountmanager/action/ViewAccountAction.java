package com.revolut.accountmanager.action;

import com.google.common.base.Preconditions;
import com.revolut.model.entity.AccountView;
import com.revoult.model.dao.IAccountDao;
import com.revoult.model.dao.entity.Account;

public class ViewAccountAction extends BaseAction<Integer, AccountView> {

    private IAccountDao accountDao;

    public ViewAccountAction(IAccountDao accountDao) {
        this.accountDao = accountDao;
    }

    @Override
    void validate(Integer accountId) {
        // Input validations already covered by dropwizard validations
        Account account = accountDao.get(accountId);
        Preconditions.checkState(account != null, "Input accountId : "+ accountId + " is not valid");
    }

    @Override
    AccountView execute(Integer accountId) {
        return accountDao.get(accountId).view();
    }
}
