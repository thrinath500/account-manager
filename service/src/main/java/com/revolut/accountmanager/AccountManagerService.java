package com.revolut.accountmanager;

import com.hubspot.dropwizard.guice.GuiceBundle;
import com.revolut.accountmanager.config.ServiceConfiguration;
import com.revolut.accountmanager.module.ServiceModule;
import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import lombok.extern.slf4j.Slf4j;

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
    }
}
