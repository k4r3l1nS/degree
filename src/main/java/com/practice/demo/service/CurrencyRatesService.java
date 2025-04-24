package com.practice.demo.service;

import com.practice.demo.dto.entity_dto.CurrencyRatesDto;
import com.practice.demo.exceptions.models.CurrencyNotSupportedException;
import com.practice.demo.exceptions.models.EmptyFieldException;
import com.practice.demo.models.currency_enum.Currency;
import com.practice.demo.models.db_views.CurrencyRatesView;
import com.practice.demo.models.entities.LastCurrencyRatesUpdate;
import com.practice.demo.repos.db_view_repos.CurrencyRatesViewRepository;
import com.practice.demo.repos.entity_repos.CurrencyRatesRepository;
import com.practice.demo.repos.entity_repos.LastCurrencyRatesUpdateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CurrencyRatesService {

    private final CurrencyRatesRepository currencyRatesRepository;
    private final CurrencyRatesViewRepository currencyRatesViewRepository;
    private final LastCurrencyRatesUpdateRepository lastCurrencyRatesUpdateRepository;

    public BigDecimal convert(Currency currencyFrom, Currency currencyTo, BigDecimal sum) {

        var coefficient = currencyRatesRepository.findByCharCode(currencyFrom.getName()).getValue()
                .divide(currencyRatesRepository.findByCharCode(currencyTo.getName()).getValue(),
                        10, RoundingMode.FLOOR);

        return coefficient.multiply(sum);
    }

    public LastCurrencyRatesUpdate getLastUpdate() {

        return lastCurrencyRatesUpdateRepository.findAll().getFirst();
    }

    public boolean existsByCharCode(String currencyName) {

        return currencyRatesRepository.existsByCharCode(currencyName);
    }

    public BigDecimal findRate(CurrencyRatesDto currencyRatesDto) {

        if (currencyRatesDto.isEmpty()) {

            return null;
        }

        if (currencyRatesDto.hasOneEmptyField()) {

            throw new EmptyFieldException("Please fill all fields or leave them empty");
        }

        String currencyFrom = currencyRatesDto.getCurrencyFrom();
        String currencyTo = currencyRatesDto.getCurrencyTo();

        var currencyFromEntity = currencyRatesRepository.findByCharCode(currencyFrom.toUpperCase());
        var currencyToEntity = currencyRatesRepository.findByCharCode(currencyTo.toUpperCase());

        if (currencyFrom != null && currencyFromEntity == null ||
                currencyTo != null && currencyToEntity == null) {

            String name = currencyFrom != null && currencyFromEntity == null ?
                    currencyFrom : currencyTo;

            throw new CurrencyNotSupportedException("Currency with name \"" + name + "\" is not supported");
        }

        return convert(currencyFromEntity.getCurrency(), currencyToEntity.getCurrency(), BigDecimal.ONE);
    }

    public List<CurrencyRatesView> findAllViews() {

        return currencyRatesViewRepository.findAll();
    }
}
