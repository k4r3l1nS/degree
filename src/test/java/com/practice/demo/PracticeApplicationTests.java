package com.practice.demo;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.practice.demo.dto.entity_dto.OperationDto;
import com.practice.demo.models.currency_enum.Currency;
import com.practice.demo.models.entities.Operation;
import com.practice.demo.service.OperationService;
import com.practice.demo.service.ScheduledService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;

@SpringBootTest
class PracticeApplicationTests {

	@Autowired
	private ScheduledService scheduledService;

	@Autowired
	private OperationService operationService;

	@Test
	void contextLoads() {
	}

	@Test
	void testGettingCbrRates() throws JsonProcessingException {

		scheduledService.renewCurrencyRates();
	}

	@Test
	void testOperationEvent() {

		operationService.addOperation(OperationDto.builder()
						.operationKind(Operation.OperationKind.DEPOSIT).currencyFrom(Currency.RUB)
						.transactionSum(BigDecimal.valueOf(1000))
				.build(),
				3052L);
	}
}
