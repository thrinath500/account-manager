package com.revoult.model.dao.impl;

import com.revolut.model.requests.AccountRegisterRequest;
import com.revoult.model.dao.entity.Account;
import org.junit.Test;

import static org.junit.Assert.*;

public class AccountRepositoryTest {

    AccountRepository accountRepository = new AccountRepository();

    @Test
    public void createAndGet() {
        // resetting the counter, since the order of execution tests may change the outcome
        accountRepository.idGenerator.set(1);
        assertEquals(0, accountRepository.idToAccountMap.size());

        Account account = accountRepository.create(new AccountRegisterRequest("firstName1", "lastName1", "address1"));

        assertEquals(1, accountRepository.idToAccountMap.size());
        assertEquals(1, account.getAccountId());
        assertNotNull(account.getCreatedAt());
        assertNotNull(account.getUpdatedAt());

        assertEquals(1, accountRepository.get(1).getAccountId());
    }

    @Test
    public void testMultiCreate() throws InterruptedException {
        // resetting the counter, since the order of execution tests may change the outcome
        accountRepository.idGenerator.set(1);
        for(int i = 2; i < 102; i++){
            Thread t = new Thread(new AccountCreationThread(accountRepository, "t"+i));
            t.start();
            t.join();
        }
        assertEquals(1000, accountRepository.idToAccountMap.size());
        // No miss in the sequence, verifying the thread safe
        assertEquals(1000, accountRepository.idToAccountMap.keySet().stream().mapToInt(e -> e).max().getAsInt());
    }

    static class AccountCreationThread implements Runnable{
        AccountRepository accountRepository;
        String threadName;

        AccountCreationThread(AccountRepository accountRepository, String threadName) {
            this.accountRepository = accountRepository;
            this.threadName = threadName;
        }

        @Override
        public void run(){
            for(int i = 1; i <= 10; i ++){
                accountRepository.create(new AccountRegisterRequest("firstName" + threadName + i,
                        "lastName1" + threadName + i,
                        "address1" + threadName + i));
            }
        }
    }
}