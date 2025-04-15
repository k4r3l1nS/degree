package com.practice.demo.models.db_views;


import com.practice.demo.models.currency_enum.Currency;
import lombok.Getter;
import org.hibernate.annotations.Immutable;
import org.hibernate.annotations.Subselect;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Immutable
@Getter
@Subselect("select * from currency_rates_view")
public class CurrencyRatesView {

    @Id
    @Column(name = "char_code")
    private String charCode;

    @Column(name = "last_update")
    private LocalDateTime lastUpdate;

    @Column(name = "value")
    private BigDecimal value;

    @Enumerated
    @Column(name = "currency")
    private Currency currency;
}
