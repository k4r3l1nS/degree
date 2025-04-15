package com.practice.demo.models.db_views;

import com.practice.demo.models.entities.Operation;
import com.practice.demo.models.currency_enum.Currency;
import javax.persistence.*;
import lombok.Getter;
import org.hibernate.annotations.Immutable;
import org.hibernate.annotations.Subselect;

import java.time.LocalDateTime;

@Entity
@Immutable
@Getter
@Subselect("select * from operation_view")
public class OperationView {

    @Id
    @Column(name = "operation_id")
    private Long operationId;

    @Column(name = "account_id")
    private Long accountId;

    @Column(name = "client_email")
    private String clientEmail;

    @Column(name = "client_id")
    private Long clientId;

    @Column(name = "owner_full_name")
    private String ownerFullName;

    @Column(name = "balance")
    private double balance;

    @Column(name = "account_currency")
    private Currency accountCurrency;

    @Column(name = "operation_kind")
    private Operation.OperationKind operationKind;

    @Column(name = "transaction_sum")
    private Double transactionSum;

    @Column(name = "currency_from")
    private Currency currencyFrom;

    @Column(name = "operation_date")
    private LocalDateTime operationDateTime;

    @Column(name = "account_name")
    private String accountName;
}
