package com.revolut.accountmanager.action;

import com.revolut.model.entity.CurrencyType;
import com.revolut.model.requests.AccountDepositRequest;
import com.revolut.model.requests.AccountRegisterRequest;
import com.revolut.model.requests.AccountWithdrawRequest;
import com.revoult.model.dao.IAccountDao;
import com.revoult.model.dao.entity.Account;
import com.revoult.model.dao.impl.AccountRepository;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;

import static org.junit.Assert.assertEquals;

public class AccountActionTest {

    IAccountDao accountDao;
    Account account;
    AccountDepositAction accountDepositAction;

    @Before
    public void setup(){
        accountDao = new AccountRepository();
        account = accountDao.create(new AccountRegisterRequest("firstName1", "lastName1", "address1"));

        accountDepositAction = new AccountDepositAction(accountDao, account.getAccountId());
        accountDepositAction.execute(new AccountDepositRequest(CurrencyType.INR, new BigDecimal(1200)));
        accountDepositAction.execute(new AccountDepositRequest(CurrencyType.EURO, new BigDecimal(900)));
    }

    @Test
    public void testMultiThread() throws InterruptedException {
        AccountWithdrawalAction accountWithdrawalAction = new AccountWithdrawalAction(accountDao, account.getAccountId());
        AccountDepositAction accountDepositAction = new AccountDepositAction(accountDao, account.getAccountId());

        for(int i = 0; i < 10; i++){
            Thread t = new Thread(new AccountActionThread(accountWithdrawalAction, accountDepositAction, "t"+i));
            t.start();
            t.join();
        }
        assertEquals(1099, account.getBalance().get(CurrencyType.INR).intValue());
        assertEquals(1001, account.getBalance().get(CurrencyType.EURO).intValue());
    }

    static class AccountActionThread implements Runnable{
        AccountWithdrawalAction accountWithdrawalAction;
        AccountDepositAction accountDepositAction;
        String threadName;

        public AccountActionThread(AccountWithdrawalAction accountWithdrawalAction,
                                   AccountDepositAction accountDepositAction, String threadName) {
            this.accountWithdrawalAction = accountWithdrawalAction;
            this.accountDepositAction = accountDepositAction;
            this.threadName = threadName;
        }

        @Override
        public void run(){
            for(int i = 0; i < 10; i ++){
                if(i%2 == 0) {
                    accountWithdrawalAction.execute(new AccountWithdrawRequest(CurrencyType.INR, new BigDecimal(10.1)));
                } else {
                    accountWithdrawalAction.execute(new AccountWithdrawRequest(CurrencyType.EURO, new BigDecimal(10.1)));
                }
                if(i%3 == 0){
                    accountDepositAction.execute(new AccountDepositRequest(CurrencyType.INR, new BigDecimal(10.1)));
                }else{
                    accountDepositAction.execute(new AccountDepositRequest(CurrencyType.EURO, new BigDecimal(10.1)));
                }

            }
        }
    }

}