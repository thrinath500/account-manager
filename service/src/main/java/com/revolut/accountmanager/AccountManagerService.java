package com.revolut.accountmanager;

import com.hubspot.dropwizard.guice.GuiceBundle;
import com.revolut.accountmanager.config.ServiceConfiguration;
import com.revolut.accountmanager.factory.ActionFactory;
import com.revolut.accountmanager.module.ServiceModule;
import com.revolut.model.entity.CurrencyType;
import com.revolut.model.requests.AccountDepositRequest;
import com.revolut.model.requests.AccountRegisterRequest;
import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;

@Slf4j
public class AccountManagerService extends Application<ServiceConfiguration> {

    private static GuiceBundle<ServiceConfiguration> guiceBundle;

    @Override
    public void initialize(Bootstrap<ServiceConfiguration> bootstrap) {
        guiceBundle = GuiceBundle.<ServiceConfiguration>newBuilder().
                addModule(new ServiceModule()).
                setConfigClass(ServiceConfiguration.class).
                enableAutoConfig(getClass().getPackage().getName()).
                build();
        bootstrap.addBundle(guiceBundle);
    }

    @Override
    public void run(ServiceConfiguration serviceConfiguration, Environment environment) throws Exception {
        log.info("Starting initialization of  Account Manager Service...");
    }

    public static void main(String[] args) throws Exception {
        new AccountManagerService().run(args);

        // Loading test data
        ActionFactory actionFactory = guiceBundle.getInjector().getInstance(ActionFactory.class);
        actionFactory.accountRegistrationAction().process(new AccountRegisterRequest("User1", "LastName1", "Address1"));
        actionFactory.accountRegistrationAction().process(new AccountRegisterRequest("User2", "LastName2", "Address2"));

        actionFactory.accountDepositAction(1).process(new AccountDepositRequest(CurrencyType.INR, new BigDecimal(100.0)));
        actionFactory.accountDepositAction(2).process(new AccountDepositRequest(CurrencyType.INR, new BigDecimal(1250.0)));
    }
}
