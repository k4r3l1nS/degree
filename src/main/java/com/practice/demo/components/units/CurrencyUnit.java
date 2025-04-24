package com.practice.demo.components.units;

import com.practice.demo.exceptions.models.CurrencyNotSupportedException;
import com.practice.demo.models.currency_enum.Currency;
import com.practice.demo.service.CurrencyRatesService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Collection;

@Component
@RequiredArgsConstructor
@Slf4j
public class CurrencyUnit {

    private final CurrencyRatesService currencyRatesService;


    public BigDecimal convert(
            @NonNull Currency currencyFrom,
            @NonNull Currency currencyTo,
            @NonNull Object rawSum
    ) {
        if (rawSum instanceof BigDecimal bigDecimal) {
            return currencyRatesService.convert(currencyFrom, currencyTo, bigDecimal);
        }
        if (rawSum instanceof Double doubleValue) {
            return currencyRatesService.convert(currencyFrom, currencyTo, BigDecimal.valueOf(doubleValue));
        }
        try {
            BigDecimal sum = BigDecimal.valueOf(Double.parseDouble(rawSum.toString()));
            return currencyRatesService.convert(currencyFrom, currencyTo, sum);
        } catch (Exception e) {
            log.error("Could not convert object {} to BigDecimal with error: {}", rawSum,  e.getMessage());
            return null;
        }
    }

    public boolean isCorrect(String currencyName) {

        return currencyRatesService.existsByCharCode(currencyName);
    }

    public boolean isCorrect(Collection<String> currencyNames) {

        for (var currencyName : currencyNames) {

            if (!currencyRatesService.existsByCharCode(currencyName)) {

                return false;
            }
        }

        return true;
    }

    public void throwIfNotSupported(String currency) {

        if (!isCorrect(currency)) {

            throw new CurrencyNotSupportedException("Currency with name " +
                    currency + " is not supported");
        }
    }
}
