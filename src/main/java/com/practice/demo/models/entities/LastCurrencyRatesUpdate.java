package com.practice.demo.models.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Getter
@Setter
@Table(name = "last_currency_rates_update")
@NoArgsConstructor
@AllArgsConstructor
public class LastCurrencyRatesUpdate {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "last_currency_rates_update_seq")
    @SequenceGenerator(name = "last_currency_rates_update_seq", sequenceName = "last_currency_rates_update_seq")
    private Long id;

    @Column(name = "value")
    private Timestamp lastUpdate;
}
