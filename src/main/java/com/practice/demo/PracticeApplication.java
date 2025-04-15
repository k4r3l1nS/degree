package com.practice.demo;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.practice.demo.service.ScheduledService;
import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.component.page.LoadingIndicatorConfiguration;
import com.vaadin.flow.component.page.Meta;
import com.vaadin.flow.component.page.Push;
import com.vaadin.flow.internal.nodefeature.LoadingIndicatorConfigurationMap;
import com.vaadin.flow.server.CustomizedSystemMessages;
import com.vaadin.flow.server.ServiceInitEvent;
import com.vaadin.flow.server.VaadinServiceInitListener;
import com.vaadin.flow.shared.communication.PushMode;
import com.vaadin.flow.shared.ui.Transport;
import com.vaadin.flow.theme.Theme;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@Push(value = PushMode.AUTOMATIC, transport = Transport.WEBSOCKET_XHR)
@Theme("v1")
@Meta(name = "google", content = "notranslate")
public class PracticeApplication implements AppShellConfigurator, VaadinServiceInitListener {

	private static final Logger log = LoggerFactory.getLogger(PracticeApplication.class);

	public static void main(String[] args) {
		try {
			ConfigurableApplicationContext context = SpringApplication.run(PracticeApplication.class, args);
			context.getBean("scheduledService", ScheduledService.class).renewCurrencyRates();
		} catch (JsonProcessingException e) {
			log.warn("Не удалось обновить курс валют при запуске приложения: {}", e.getMessage());
		}
	}

	@Override
	public void serviceInit(ServiceInitEvent event) {
		event.getSource().setSystemMessagesProvider(systemMessagesInfo -> {
			CustomizedSystemMessages messages = new CustomizedSystemMessages();
			messages.setInternalErrorMessage("Обратитесь к администратору!");
			messages.setInternalErrorCaption("Серверная ошибка."); //внутренняя != ошибка соединения
			return messages;
		});
		event.getSource().addUIInitListener(uiInitEvent -> {
			LoadingIndicatorConfiguration conf = uiInitEvent.getUI().getLoadingIndicatorConfiguration();
			conf.setFirstDelay(LoadingIndicatorConfigurationMap.FIRST_DELAY_DEFAULT);
			conf.setSecondDelay(LoadingIndicatorConfigurationMap.SECOND_DELAY_DEFAULT);
			conf.setThirdDelay(LoadingIndicatorConfigurationMap.THIRD_DELAY_DEFAULT);
		});
	}
}
