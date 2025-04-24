package com.practice.demo.repos.entity_repos;

import com.practice.demo.models.entities.CurrencyRates;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface CurrencyRatesRepository
        extends JpaRepository<CurrencyRates, String>, JpaSpecificationExecutor<CurrencyRates> {

    CurrencyRates findByCharCode(String charCode);

    boolean existsByCharCode(String currencyName);
}
