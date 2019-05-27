package com.revolut.accountmanager.module;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import com.revolut.accountmanager.factory.ActionFactory;
import com.revoult.model.dao.IAccountDao;
import com.revoult.model.dao.impl.AccountRepository;

public class ServiceModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(IAccountDao.class).to(AccountRepository.class).in(Singleton.class);
        bind(ActionFactory.class).in(Singleton.class);
    }
}
