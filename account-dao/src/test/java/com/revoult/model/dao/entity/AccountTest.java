package com.revoult.model.dao.entity;

import com.revolut.model.RequestContainer;
import com.revolut.model.entity.CurrencyType;
import com.revolut.model.requests.AccountDepositRequest;
import com.revolut.model.requests.AccountWithdrawRequest;
import org.junit.BeforeClass;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.Date;

import static org.junit.Assert.*;

public class AccountTest {

    @BeforeClass
    public static void setupClass(){
        RequestContainer.setTestEnv();
    }

    @Test
    public void creationTest() {
        Account account = createTestAccount();
        assertNotNull(account.getLock());
        assertTrue(account.getBalance() != null && account.getBalance().size() == 0);
    }

    @Test
    public void depositTest(){
        Account account = createTestAccount();
        account.depositRequestConsumer.accept(new AccountDepositRequest(CurrencyType.INR, new BigDecimal(100.1)));
        assertEquals(account.getBalance().size(),1);
        assertEquals("Price should match", 100.1, account.getBalance().get(CurrencyType.INR).doubleValue(), 0.0);
        account.depositRequestConsumer.accept(new AccountDepositRequest(CurrencyType.EURO, new BigDecimal(50d)));
        assertEquals(account.getBalance().size(),2);
        assertEquals("Price should match", 50d, account.getBalance().get(CurrencyType.EURO).doubleValue(), 0.0);
        account.depositRequestConsumer.accept(new AccountDepositRequest(CurrencyType.INR, new BigDecimal(200.1)));
        assertEquals(account.getBalance().size(),2);
        assertEquals("Price should match", 300.2, account.getBalance().get(CurrencyType.INR).doubleValue(), 0.0);
    }

    @Test
    public void withdrawTest(){
        Account account = createTestAccount();
        account.depositRequestConsumer.accept(new AccountDepositRequest(CurrencyType.INR, new BigDecimal(100.1)));
        account.depositRequestConsumer.accept(new AccountDepositRequest(CurrencyType.EURO, new BigDecimal(50)));

        account.withdrawRequestConsumer.accept(new AccountWithdrawRequest(CurrencyType.EURO, new BigDecimal(20)));
        assertEquals(account.getBalance().size(),2);
        assertEquals("Price should match", 30d, account.getBalance().get(CurrencyType.EURO).doubleValue(), 0.0);
        account.withdrawRequestConsumer.accept(new AccountWithdrawRequest(CurrencyType.INR, new BigDecimal(50)));
        assertEquals(account.getBalance().size(),2);
        assertEquals("Price should match", 50.1, account.getBalance().get(CurrencyType.INR).doubleValue(), 0.0);
    }

    @Test(expected = IllegalStateException.class)
    public void negativeWithDrawNoCurrency(){
        Account account = createTestAccount();
        account.withdrawRequestConsumer.accept(new AccountWithdrawRequest(CurrencyType.EURO, new BigDecimal(20d)));
    }

    @Test(expected = IllegalStateException.class)
    public void negativeWithDrawHigherValue(){
        Account account = createTestAccount();
        account.depositRequestConsumer.accept(new AccountDepositRequest(CurrencyType.INR, new BigDecimal(100.1)));
        account.withdrawRequestConsumer.accept(new AccountWithdrawRequest(CurrencyType.INR, new BigDecimal(200d)));
    }


    static Account createTestAccount(){
        return new Account(1, "firstName", "lastName", "address1", new Date(), new Date());
    }

}