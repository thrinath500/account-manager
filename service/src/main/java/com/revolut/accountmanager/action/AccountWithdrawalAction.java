package com.revolut.accountmanager.action;

import com.google.common.base.Preconditions;
import com.revolut.accountmanager.util.LockUtils;
import com.revolut.model.RequestContainer;
import com.revolut.model.entity.AccountView;
import com.revoult.model.dao.entity.Account;
import com.revolut.model.requests.AccountWithdrawRequest;
import com.revoult.model.dao.IAccountDao;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AccountWithdrawalAction extends BaseAction<AccountWithdrawRequest, AccountView> {

    private IAccountDao accountDao;
    private Integer accountId;

    public AccountWithdrawalAction(IAccountDao accountDao, Integer accountId) {
        this.accountDao = accountDao;
        this.accountId = accountId;
    }

    @Override
    void validate(AccountWithdrawRequest withdrawRequest) {
        // Input validations already covered by dropwizard validations
        Account account = accountDao.get(accountId);
        Preconditions.checkState(account != null, "Account doesn't exist with accountId : " + accountId);
    }

    @Override
    AccountView execute(AccountWithdrawRequest withdrawRequest) {
        Account account = accountDao.get(accountId);
        if(account.isAlreadyExecuted()){
            log.warn("RequestId is already processed " + RequestContainer.get());
            return account.view();
        }
        LockUtils.executeInLockingManner(account.getLock(), account.withdrawRequestConsumer, withdrawRequest);
        return account.view();
    }
}
