package com.revolut.accountmanager.action;

import com.revolut.model.RequestContainer;
import com.revolut.model.entity.CurrencyType;
import com.revolut.model.requests.AccountDepositRequest;
import com.revolut.model.requests.AccountRegisterRequest;
import com.revolut.model.requests.AccountWithdrawRequest;
import com.revoult.model.dao.IAccountDao;
import com.revoult.model.dao.entity.Account;
import com.revoult.model.dao.impl.AccountRepository;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.math.BigDecimal;

import static org.junit.Assert.assertEquals;

public class AccountWithdrawalActionTest {

    IAccountDao accountDao;
    Account account;

    @BeforeClass
    public static void setupClass(){
        RequestContainer.setTestEnv();
    }

    @Before
    public void setup(){
        accountDao = new AccountRepository();
        account = accountDao.create(new AccountRegisterRequest("firstName1", "lastName1", "address1"));

        AccountDepositAction accountDepositAction = new AccountDepositAction(accountDao, account.getAccountId());
        accountDepositAction.execute(new AccountDepositRequest(CurrencyType.INR, new BigDecimal(1200)));
        accountDepositAction.execute(new AccountDepositRequest(CurrencyType.EURO, new BigDecimal(900)));
    }

    @Test
    public void testMultiThread() throws InterruptedException {
        AccountWithdrawalAction accountWithdrawalAction = new AccountWithdrawalAction(accountDao, account.getAccountId());

        for(int i = 0; i < 10; i++){
            Thread t = new Thread(new AccountWithdrawalThread(accountWithdrawalAction, "t"+i));
            t.start();
            t.join();
        }
        assertEquals(695, account.getBalance().get(CurrencyType.INR).intValue());
        assertEquals(395, account.getBalance().get(CurrencyType.EURO).intValue());
    }

    static class AccountWithdrawalThread implements Runnable{
        AccountWithdrawalAction accountWithdrawalAction;
        String threadName;

        AccountWithdrawalThread(AccountWithdrawalAction accountWithdrawalAction, String threadName) {
            this.accountWithdrawalAction = accountWithdrawalAction;
            this.threadName = threadName;
        }

        @Override
        public void run(){
            RequestContainer.setTestEnv();
            for(int i = 0; i < 10; i ++){
                if(i%2 == 0) {
                    accountWithdrawalAction.execute(new AccountWithdrawRequest(CurrencyType.INR, new BigDecimal(10.1)));
                } else {
                    accountWithdrawalAction.execute(new AccountWithdrawRequest(CurrencyType.EURO, new BigDecimal(10.1)));
                }

            }
        }
    }

}