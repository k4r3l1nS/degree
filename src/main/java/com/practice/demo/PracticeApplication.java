package com.practice.demo;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.practice.demo.service.ScheduledService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class PracticeApplication {

	private static final Logger log = LoggerFactory.getLogger(PracticeApplication.class);

	public static void main(String[] args) {
		try {
			ConfigurableApplicationContext context = SpringApplication.run(PracticeApplication.class, args);
			context.getBean("scheduledService", ScheduledService.class).renewCurrencyRates();
		} catch (JsonProcessingException e) {
			log.warn("Не удалось обновить курс валют при запуске приложения: {}", e.getMessage());
		}
	}

}
