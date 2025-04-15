package com.practice.demo.models.entities;

import com.practice.demo.models.currency_enum.Currency;
import lombok.*;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "currency_rates")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CurrencyRates {

    /**
     * Currency name
     */
    @Id
    @Column(name = "char_code")
    private String charCode;

    @Enumerated
    @Column(name = "currency")
    private Currency currency;

    /**
     * Coefficient {CharCode} to RUB
     */
    @Column(name = "value")
    private BigDecimal value;
}
