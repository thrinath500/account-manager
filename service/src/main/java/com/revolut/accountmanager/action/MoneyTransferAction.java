package com.revolut.accountmanager.action;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.revolut.model.entity.CurrencyType;
import com.revolut.model.requests.AccountDepositRequest;
import com.revolut.model.requests.MoneyTransferRequest;
import com.revoult.model.dao.entity.Account;
import com.revolut.model.requests.AccountWithdrawRequest;
import com.revoult.model.dao.IAccountDao;
import lombok.extern.slf4j.Slf4j;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Slf4j
public class MoneyTransferAction extends BaseAction<MoneyTransferRequest, Void> {

    private IAccountDao accountDao;

    public MoneyTransferAction(IAccountDao accountDao) {
        this.accountDao = accountDao;
    }

    @Override
    void validate(MoneyTransferRequest transferRequest) {
        // Input validations already covered by dropwizard validations
        Preconditions.checkState(!transferRequest.getFromAccountId().equals(transferRequest.getToAccountId()),
                "Invalid state. Transfer cannot be done to the same account");

        Account fromAccount = accountDao.get(transferRequest.getFromAccountId());
        Preconditions.checkNotNull(fromAccount, "From account: " + transferRequest.getFromAccountId() + "doesn't exist");

        Account toAccount = accountDao.get(transferRequest.getToAccountId());
        Preconditions.checkNotNull(toAccount, "To account: " + transferRequest.getToAccountId() + "doesn't exist");

        // Checking valid amount type is present in From account
        CurrencyType type = transferRequest.getCurrencyType();
        Preconditions.checkState(fromAccount.getBalance().containsKey(type), "Requested currency of type : " + type +
                " is not present in from account");
        Preconditions.checkState(fromAccount.getBalance().get(type).compareTo(transferRequest.getValue()) >= 0,
                "Enough cash not present for withdrawal");
    }

    @Override
    Void execute(MoneyTransferRequest transferRequest) {
        Map<Integer, Account> accountMap = Maps.newHashMap();
        Account fromAccount = accountDao.get(transferRequest.getFromAccountId());
        Account toAccount = accountDao.get(transferRequest.getToAccountId());

        accountMap.put(fromAccount.getAccountId(), fromAccount);
        accountMap.put(toAccount.getAccountId(), toAccount);

        // Since the transaction involves locking across multiple accounts, there is a possibility of
        // deadlock if it involves same accounts across two different transactions such as cases of
        // transfer 1. Account1 -> Account2 and 2. Account2 -> Account1
        // To avoid the above possibility, take the locks in the lexicographic order irrespective of
        // "from" and "to" account sequences

        List<Integer> accountIds = Lists.newArrayList(accountMap.keySet());
        Collections.sort(accountIds);

        // Locking on first a/c
        accountMap.get(accountIds.get(0)).getLock().lock();
        try{
            // Locking on second a/c
            accountMap.get(accountIds.get(1)).getLock().lock();
            try {
                fromAccount.withdrawRequestConsumer.accept(new AccountWithdrawRequest(transferRequest.getCurrencyType(),
                        transferRequest.getValue()));
                try{
                    toAccount.depositRequestConsumer.accept(new AccountDepositRequest(transferRequest.getCurrencyType(),
                            transferRequest.getValue()));
                }catch (Exception e){
                    log.error("Withdrawal is success, where as deposit is failed.");
                    // Fixing the state. If some reason, this failed, we will have to reconcile by sidelining
                    fromAccount.depositRequestConsumer.accept(new AccountDepositRequest(transferRequest.getCurrencyType(),
                            transferRequest.getValue()));
                }
            }finally {
                // unlocking first on second a/c
                accountMap.get(accountIds.get(1)).getLock().unlock();
            }
        }finally {
            // unlocking last on first a/c
            accountMap.get(accountIds.get(0)).getLock().unlock();
        }
        return null;
    }
}
