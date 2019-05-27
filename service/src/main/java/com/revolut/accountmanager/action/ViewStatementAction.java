package com.revolut.accountmanager.action;

import com.google.common.base.Preconditions;
import com.revolut.model.responses.AccountStatementResponse;
import com.revoult.model.dao.IAccountDao;
import com.revoult.model.dao.entity.Account;

public class ViewStatementAction extends BaseAction<Integer, AccountStatementResponse> {

    private IAccountDao accountDao;

    public ViewStatementAction(IAccountDao accountDao) {
        this.accountDao = accountDao;
    }

    @Override
    void validate(Integer accountId) {
        // Input validations already covered by dropwizard validations
        Account account = accountDao.get(accountId);
        Preconditions.checkState(account != null, "Input accountId : "+ accountId + " is not valid");
    }

    @Override
    AccountStatementResponse execute(Integer accountId) {
        return new AccountStatementResponse(accountDao.get(accountId).getRequestIdToAuditEntryMap());
    }
}
