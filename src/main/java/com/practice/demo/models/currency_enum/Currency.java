package com.practice.demo.models.currency_enum;

import lombok.Getter;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Getter
public enum Currency {

    RUB("RUB"),
    USD("USD"),
    EUR("EUR"),
    CNY("CNY"),
    CHF("CHF"),
    AUD("AUD"),
    AZN("AZN"),
    GBP("GBP"),
    AMD("AMD"),
    BYN("BYN"),
    BGN("BGN"),
    BRL("BRL"),
    HUF("HUF"),
    VND("VND"),
    HKD("HKD"),
    GEL("GEL"),
    DKK("DKK"),
    AED("AED"),
    EGP("EGP"),
    INR("INR"),
    IDR("IDR"),
    KZT("KZT"),
    CAD("CAD"),
    QAR("QAR"),
    KGS("KGS"),
    MDL("MDL"),
    NZD("NZD"),
    NOK("NOK"),
    PLN("PLN"),
    RON("RON"),
    XDR("XDR"),
    SGD("SGD"),
    TJS("TJS"),
    THB("THB"),
    TRY("TRY"),
    TMT("TMT"),
    UZS("UZS"),
    UAH("UAH"),
    CZK("CZK"),
    SEK("SEK"),
    RSD("RSD"),
    ZAR("ZAR"),
    KRW("KRW"),
    JPY("JPY");


    private static final Map<String, Currency> _map;

    static {
        _map = Stream.of(values()).collect(Collectors.toMap(Currency::getName, Function.identity()));
    }

    private final String name;

    Currency(String name) {
        this.name = name;
    }

    public static Currency resolveByName(String name) {
        return _map.getOrDefault(name, null);
    }
}
