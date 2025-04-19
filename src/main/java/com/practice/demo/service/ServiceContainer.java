package com.practice.demo.service;

import com.practice.demo.components.units.CurrencyUnit;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Getter
public class ServiceContainer {

    @Getter
    private static ServiceContainer instance;

    @SuppressWarnings("unused")
    @Component
    @Order(Integer.MIN_VALUE)
    private static class Instance {
        @Autowired
        Instance(ServiceContainer services2) {
            ServiceContainer.instance = services2;
        }
    }

    @Autowired
    private AccountService accountService;

    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    private ClientService clientService;

    @Autowired
    private CurrencyRatesService currencyRatesService;

    @Autowired
    private OperationService operationService;

    @Autowired
    private ScheduledService scheduledService;

    @Autowired
    private CurrencyUnit currencyUnit;
}
