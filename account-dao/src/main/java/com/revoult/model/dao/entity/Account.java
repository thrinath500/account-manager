package com.revoult.model.dao.entity;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import com.revolut.model.RequestContainer;
import com.revolut.model.entity.AccountView;
import com.revolut.model.entity.CurrencyType;
import com.revolut.model.entity.AuditEntry;
import com.revolut.model.requests.AccountDepositRequest;
import com.revolut.model.requests.AccountWithdrawRequest;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;

@Slf4j
public class Account {
    @Getter
    private ReentrantLock lock;

    @Builder
    public Account(int accountId, String firstName, String lastName, String address, Date createdAt, Date updatedAt) {
        this.accountId = accountId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.address = address;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.balance = Maps.newHashMap();
        this.lock = new ReentrantLock();
        this.requestIdToAuditEntryMap = Maps.newHashMap();
    }

    @Getter
    private int accountId;
    @Getter
    private String firstName;
    @Getter
    private String lastName;
    @Getter
    private String address;

    // To support multiple currency
    // Should not be accessible to others
    private Map<CurrencyType, BigDecimal> balance;

    // Audit
    @Getter
    Map<String, AuditEntry> requestIdToAuditEntryMap;

    @Getter
    private Date createdAt;
    @Getter
    private Date updatedAt;

    public Consumer<AccountDepositRequest> depositRequestConsumer = new Consumer<AccountDepositRequest>() {
        @Override
        public void accept(AccountDepositRequest accountDepositRequest) {
            if(!RequestContainer.isTestEnv() && requestIdToAuditEntryMap.containsKey(RequestContainer.get())){
                log.warn("RequestId is already processed " + RequestContainer.get());
                return;
            }

            CurrencyType type = accountDepositRequest.getCurrencyType();
            balance.putIfAbsent(type, new BigDecimal(0).setScale(2, BigDecimal.ROUND_UP));
            balance.put(type, balance.get(type).add(accountDepositRequest.getValue()).setScale(2, BigDecimal.ROUND_UP));
            updatedAt = new Date();
            requestIdToAuditEntryMap.put(RequestContainer.get(), new AuditEntry(type, accountDepositRequest.getValue(),
                    AuditEntry.Type.CREDIT, balance.get(type)));
        }
    };

    public Consumer<AccountWithdrawRequest> withdrawRequestConsumer = new Consumer<AccountWithdrawRequest>() {
        @Override
        public void accept(AccountWithdrawRequest withdrawRequest) {
            if(!RequestContainer.isTestEnv() && requestIdToAuditEntryMap.containsKey(RequestContainer.get())){
                log.warn("RequestId is already processed " + RequestContainer.get());
                return;
            }

            CurrencyType type = withdrawRequest.getCurrencyType();
            BigDecimal value = withdrawRequest.getValue();

            Preconditions.checkState(balance.containsKey(type), "Requested currency of type : " + type + " is not present in account");
            Preconditions.checkState(balance.get(type).compareTo(value) >= 0, "Enough cash not present for withdrawal");
            balance.put(type, balance.get(type).subtract(value).setScale(2, BigDecimal.ROUND_UP));
            updatedAt = new Date();
            requestIdToAuditEntryMap.put(RequestContainer.get(), new AuditEntry(type, withdrawRequest.getValue(),
                    AuditEntry.Type.DEBIT, balance.get(type)));
        }
    };

    public AccountView view() {
        return AccountView.builder().accountId(accountId).
                firstName(firstName).
                lastName(lastName).
                address(address).
                // Cloning the map for de referencing
                balance(getBalance()).
                createdAt(createdAt).
                updatedAt(updatedAt).
                build();
    }

    public Map<CurrencyType, BigDecimal> getBalance() {
        return Maps.newHashMap(balance);
    }
}
