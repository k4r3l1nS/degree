package com.practice.demo.models.entities;


import com.practice.demo.models.currency_enum.Currency;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Getter
@Setter
@Entity
@Table(name = "operations")
public class Operation {

    /**
     * Unique operation id
     */
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "operation_seq")
    @SequenceGenerator(name = "operation_seq", sequenceName = "operation_seq")
    private Long id;

    /**
     * Account entity which operation is related to
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id")
    private Account account;

    /**
     * Operation kind: withdrawal, deposit or capitalization
     */
    @Column(name = "operation_kind")
    @Enumerated(EnumType.STRING)
    private OperationKind operationKind;

    /**
     * Sum to be transacted
     */
    @Column(name = "transaction_sum")
    private BigDecimal transactionSum;

    /**
     * Operation date & time
     */
    @Column(name = "operation_date")
    private LocalDateTime operationDateTime;

    /**
     * Currency of transaction sum
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "currency_from")
    private Currency currencyFrom;

    /**
     * Nested class representing operation kind
     */
    @Getter
    public enum OperationKind {

        DEPOSIT("DEPOSIT"),
        WITHDRAWAL("WITHDRAWAL");

        private final static Map<String, OperationKind> _map;

        static {

            _map = Stream.of(values()).collect(Collectors.toMap(OperationKind::getName, Function.identity()));
        }

        private final String name;

        OperationKind(String name) {
            this.name = name;
        }

        public static OperationKind resolveByName(String name) {

            return _map.getOrDefault(name, null);
        }
    }

    /**
     * No arguments constructor
     */
    public Operation() {

        this.operationDateTime = LocalDateTime.now();
    }

    /**
     * Configures operation entity by external data
     *
     * @param operationKind kind of operation: withdrawal or deposit
     * @param transactionSum sum to be transacted
     * @param currencyFrom currency of transaction sum
     * @return operation entity
     */
    public static Operation getOperation(OperationKind operationKind, BigDecimal transactionSum, Currency currencyFrom) {

        Operation operation = new Operation();

        operation.setOperationKind(operationKind);
        operation.setTransactionSum(transactionSum);
        operation.setCurrencyFrom(currencyFrom);

        return operation;
    }
}
