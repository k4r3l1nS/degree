package com.practice.demo.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.practice.demo.components.event.publishers.OperationProceededPublisher;
import com.practice.demo.dto.json_obtaining.CurrencyRatesJsonDto;
import com.practice.demo.models.currency_enum.Currency;
import com.practice.demo.models.entities.Account;
import com.practice.demo.models.entities.CurrencyRates;
import com.practice.demo.models.entities.LastCurrencyRatesUpdate;
import com.practice.demo.models.entities.Operation;
import com.practice.demo.repos.entity_repos.AccountRepository;
import com.practice.demo.repos.entity_repos.CurrencyRatesRepository;
import com.practice.demo.repos.entity_repos.LastCurrencyRatesUpdateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ScheduledService {

    private final AccountRepository accountRepository;
    private final CurrencyRatesRepository currencyRatesRepository;
    private final LastCurrencyRatesUpdateRepository lastCurrencyRatesUpdateRepository;

    private final OperationProceededPublisher operationProceededPublisher;

    /**
     * Capitalizes active accumulation accounts at everyday midnight
     */
    @Scheduled(cron = "0 0 0 * * *")
//    @Scheduled(fixedRate = 10000)
    public void capitalize() {

        List<Account> accountList = accountRepository.findActiveAccumulationAccounts();

        for (var account : accountList) {

            account.capitalize();

            var finalSum = account.getPercentageCoefficient().multiply(account.getBalance());

            var operation = Operation.getOperation(Operation.OperationKind.DEPOSIT,
                    finalSum, account.getCurrency());


            account.performOperation(operation, finalSum);

            operationProceededPublisher.publishEvent(operation);
        }
    }

    /**
     * Gets currency rates from cbr at 3:00:01 AM everyday
     */
    @Scheduled(cron = "1 0 3 * * *")
    public void renewCurrencyRates()
            throws JsonProcessingException {

        WebClient webClient = WebClient.builder()
                .baseUrl("https://www.cbr-xml-daily.ru/daily_json.js")
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();

        String json = webClient.get().retrieve().bodyToMono(String.class).block();
        json = json.substring(0, json.indexOf("\"AUD\":")) +
                "\"RUB\": {\n" +
                "\t\t\t\"CharCode\": " + "\"RUB\",\n" +
                "\t\t\t\"Nominal\": " + "1,\n" +
                "\t\t\t\"Value\": " + "1.0\n" +
                "\t\t},\n" +
                "\t\t" +
                json.substring(json.indexOf("\"AUD\":"));

        CurrencyRatesJsonDto currencyRatesJsonDto = new ObjectMapper().readValue(json, CurrencyRatesJsonDto.class);

        var currencyMap = currencyRatesJsonDto.getValuteMap();

        currencyMap.forEach((name, valute) -> {

            var currencyEntity = currencyRatesRepository.findByCharCode(name);

            if (currencyEntity != null) {

                currencyEntity.setValue(valute.getValue()
                        .divide(BigDecimal.valueOf(valute.getNominal()), 10, RoundingMode.FLOOR));
                if (currencyEntity.getCurrency() == null) {

                    currencyEntity.setCurrency(Currency.resolveByName(name));
                }
            } else {
                currencyRatesRepository.save(new CurrencyRates(name, Currency.resolveByName(name),
                        (valute.getValue())
                                .divide(BigDecimal.valueOf(valute.getNominal()), 10, RoundingMode.FLOOR)));
            }
        });

        var lastUbdateList = lastCurrencyRatesUpdateRepository.findAll();

        if (lastUbdateList.isEmpty()) {

            LastCurrencyRatesUpdate lastUpdate = new LastCurrencyRatesUpdate();
            lastUpdate.setLastUpdate(currencyRatesJsonDto.getTimestamp());

            lastCurrencyRatesUpdateRepository.save(lastUpdate);
        } else {

            lastUbdateList.get(0).setLastUpdate(currencyRatesJsonDto.getTimestamp());
        }
    }
}
