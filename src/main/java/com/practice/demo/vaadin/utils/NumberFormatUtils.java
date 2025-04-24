package com.practice.demo.vaadin.utils;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

@UtilityClass
@Slf4j
public class NumberFormatUtils {

    private static final DecimalFormat MONEY_FORMAT;

    static {
        DecimalFormatSymbols decimalSymbols = new DecimalFormatSymbols();
        decimalSymbols.setGroupingSeparator(' ');
        decimalSymbols.setDecimalSeparator(',');
        MONEY_FORMAT = new DecimalFormat("###,##0.00", decimalSymbols);
    }

    public String toMoney(Object rawSum) {
        if (rawSum instanceof BigDecimal bigDecimal) {
            return MONEY_FORMAT.format(bigDecimal.setScale(2, RoundingMode.HALF_UP));
        }
        if (rawSum instanceof Double doubleValue) {
            return MONEY_FORMAT.format(new BigDecimal(doubleValue).setScale(2, RoundingMode.HALF_UP));
        }
        try {
            BigDecimal sum = BigDecimal.valueOf(Double.parseDouble(rawSum.toString())).setScale(2, RoundingMode.HALF_UP);
            return MONEY_FORMAT.format(sum);
        } catch (Exception e) {
            log.error("Could not convert object {} to BigDecimal with error: {}", rawSum,  e.getMessage());
            throw new UnsupportedOperationException("Форматирование не поддерживается для " + rawSum.getClass().getName());
        }
    }
}
