package com.practice.demo.repos.entity_repos;

import com.practice.demo.models.entities.LastCurrencyRatesUpdate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LastCurrencyRatesUpdateRepository extends JpaRepository<LastCurrencyRatesUpdate, Long> {
}
