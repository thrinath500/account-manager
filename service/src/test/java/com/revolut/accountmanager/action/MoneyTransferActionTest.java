package com.revolut.accountmanager.action;

import com.revolut.model.entity.CurrencyType;
import com.revolut.model.requests.AccountDepositRequest;
import com.revolut.model.requests.AccountRegisterRequest;
import com.revolut.model.requests.MoneyTransferRequest;
import com.revoult.model.dao.IAccountDao;
import com.revoult.model.dao.entity.Account;
import com.revoult.model.dao.impl.AccountRepository;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.math.BigDecimal;

import static org.junit.Assert.*;

public class MoneyTransferActionTest {

    IAccountDao accountDao;
    Account fromAccount;
    Account toAccount;

    @Before
    public void setup(){
        accountDao = new AccountRepository();
        fromAccount = accountDao.create(new AccountRegisterRequest("firstName1", "lastName1", "address1"));
        toAccount = accountDao.create(new AccountRegisterRequest("firstName2", "lastName2", "address2"));
    }

    @Test(expected = IllegalStateException.class)
    public void negativeTestCase() {
        new MoneyTransferAction(accountDao).execute(new MoneyTransferRequest(CurrencyType.INR, new BigDecimal(10),
                fromAccount.getAccountId(), toAccount.getAccountId()));
    }

    @Test
    public void positiveCase(){
        new AccountDepositAction(accountDao, fromAccount.getAccountId()).
                execute(new AccountDepositRequest(CurrencyType.INR, new BigDecimal(1000)));
        new MoneyTransferAction(accountDao).execute(new MoneyTransferRequest(CurrencyType.INR, new BigDecimal(10),
                fromAccount.getAccountId(), toAccount.getAccountId()));
        assertEquals(10, toAccount.getBalance().get(CurrencyType.INR).intValue());
    }

    @Test
    public void testDeadlockCase1() throws InterruptedException {
        // Transaction flow
        // AccountA -> AccountB , AccountB -> AccountA

        Account accountA = accountDao.create(new AccountRegisterRequest("firstNameA", "lastNameA", "addressA"));
        Account accountB = accountDao.create(new AccountRegisterRequest("firstNameB", "lastNameB", "addressB"));

        new AccountDepositAction(accountDao, accountA.getAccountId()).
                execute(new AccountDepositRequest(CurrencyType.EURO, new BigDecimal(1000)));
        new AccountDepositAction(accountDao, accountB.getAccountId()).
                execute(new AccountDepositRequest(CurrencyType.EURO, new BigDecimal(100)));

        Thread thread1 = new Thread(() -> {
            new MoneyTransferAction(accountDao).execute(new MoneyTransferRequest(CurrencyType.EURO, new BigDecimal(10),
                    accountA.getAccountId(), accountB.getAccountId()));
        });
        Thread thread2 = new Thread(() -> {
            new MoneyTransferAction(accountDao).execute(new MoneyTransferRequest(CurrencyType.EURO, new BigDecimal(20),
                    accountB.getAccountId(), accountA.getAccountId()));
        });

        thread1.start();
        thread2.start();
        thread1.join();
        thread2.join();

        assertEquals(90, accountB.getBalance().get(CurrencyType.EURO).intValue());
        assertEquals(1010, accountA.getBalance().get(CurrencyType.EURO).intValue());
    }

    @Test
    public void testDeadlockCase2() throws InterruptedException {
        // Transaction flow
        // AccountA -> AccountB , AccountB -> AccountC

        Account accountA = accountDao.create(new AccountRegisterRequest("firstNameA", "lastNameA", "addressA"));
        Account accountB = accountDao.create(new AccountRegisterRequest("firstNameB", "lastNameB", "addressB"));
        Account accountC = accountDao.create(new AccountRegisterRequest("firstNameB", "lastNameB", "addressB"));

        new AccountDepositAction(accountDao, accountA.getAccountId()).
                execute(new AccountDepositRequest(CurrencyType.US_DOLLAR, new BigDecimal(1000)));
        new AccountDepositAction(accountDao, accountB.getAccountId()).
                execute(new AccountDepositRequest(CurrencyType.US_DOLLAR, new BigDecimal(100)));

        Thread thread1 = new Thread(() -> {
            new MoneyTransferAction(accountDao).execute(new MoneyTransferRequest(CurrencyType.US_DOLLAR, new BigDecimal(10),
                    accountA.getAccountId(), accountB.getAccountId()));
        });
        Thread thread2 = new Thread(() -> {
            new MoneyTransferAction(accountDao).execute(new MoneyTransferRequest(CurrencyType.US_DOLLAR, new BigDecimal(20),
                    accountB.getAccountId(), accountC.getAccountId()));
        });

        thread1.start();
        thread2.start();
        thread1.join();
        thread2.join();

        assertEquals(990, accountA.getBalance().get(CurrencyType.US_DOLLAR).intValue());
        assertEquals(90, accountB.getBalance().get(CurrencyType.US_DOLLAR).intValue());
        assertEquals(20, accountC.getBalance().get(CurrencyType.US_DOLLAR).intValue());
    }
}