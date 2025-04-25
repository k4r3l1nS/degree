package com.practice.demo.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Getter
@Component
@Primary
public class Config {

    private static Config INSTANCE;

    public static Config getInstance() {
        return INSTANCE;
    }

    @SuppressWarnings("unused")
    @Configuration
    @Order(Integer.MIN_VALUE)
    private static class Instance {
        @Autowired
        Instance(Config config) {
            Config.INSTANCE = config;
        }
    }

    @Value("${metadata.application-name}")
    private String applicationName;

    @Value("${metadata.minimal-age}")
    private int minimalAge;

    @Value("${metadata.support-email}")
    private String supportEmail;
}
