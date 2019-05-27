package com.revolut.accountmanager.action;

import com.revolut.model.RequestContainer;
import com.revolut.model.entity.CurrencyType;
import com.revolut.model.requests.AccountDepositRequest;
import com.revolut.model.requests.AccountRegisterRequest;
import com.revoult.model.dao.IAccountDao;
import com.revoult.model.dao.entity.Account;
import com.revoult.model.dao.impl.AccountRepository;
import org.junit.BeforeClass;
import org.junit.Test;

import java.math.BigDecimal;

import static org.junit.Assert.*;

public class AccountDepositActionTest {

    @BeforeClass
    public static void setupClass(){
        RequestContainer.setTestEnv();
    }

    @Test
    public void testMultiThread() throws InterruptedException {
        IAccountDao accountDao = new AccountRepository();
        Account account = accountDao.create(new AccountRegisterRequest("firstName1", "lastName1", "address1"));

        AccountDepositAction accountDepositAction = new AccountDepositAction(accountDao, account.getAccountId());
        accountDepositAction.execute(new AccountDepositRequest(CurrencyType.INR, new BigDecimal(200)));

        for(int i = 0; i < 10; i++){
            Thread t = new Thread(new AccountDepositThread(accountDepositAction, "t"+i));
            t.start();
            t.join();
        }
        assertEquals(700, account.getBalance().get(CurrencyType.INR).intValue());
        assertEquals(500, account.getBalance().get(CurrencyType.EURO).intValue());
    }

    static class AccountDepositThread implements Runnable{
        AccountDepositAction accountDepositAction;
        String threadName;

        AccountDepositThread(AccountDepositAction accountDepositAction, String threadName) {
            this.accountDepositAction = accountDepositAction;
            this.threadName = threadName;
        }

        @Override
        public void run(){
            RequestContainer.setTestEnv();
            for(int i = 0; i < 10; i ++){
                if(i%2 == 0) {
                    accountDepositAction.execute(new AccountDepositRequest(CurrencyType.INR, new BigDecimal(10)));
                } else {
                    accountDepositAction.execute(new AccountDepositRequest(CurrencyType.EURO, new BigDecimal(10)));
                }

            }
        }
    }

}