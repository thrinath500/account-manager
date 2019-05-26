package com.revolut.accountmanager;

import com.revolut.accountmanager.config.ServiceConfiguration;
import com.revolut.accountmanager.controllers.AccountController;
import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AccountManagerService extends Application<ServiceConfiguration> {

    @Override
    public void initialize(Bootstrap<ServiceConfiguration> bootstrap) {
    }

    @Override
    public void run(ServiceConfiguration serviceConfiguration, Environment environment) throws Exception {
        log.info("Starting Account Manager Service...");
        environment.jersey().register(new AccountController());
    }

    public static void main(String[] args) throws Exception {
        new AccountManagerService().run(args);
    }
}
