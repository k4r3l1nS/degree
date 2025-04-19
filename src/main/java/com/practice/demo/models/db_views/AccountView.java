package com.practice.demo.models.db_views;

import com.practice.demo.models.currency_enum.Currency;
import com.practice.demo.models.entities.Account;
import javax.persistence.*;
import lombok.Getter;
import org.hibernate.annotations.Immutable;
import org.hibernate.annotations.Subselect;

import java.time.LocalDateTime;

@Entity
@Immutable
@Getter
@Subselect("select * from ACCOUNT_VIEW")
public class AccountView {

    @Id
    @Column(name = "id")
    private Integer id;

    @Column(name = "account_id")
    private Long accountId;

    @Column(name = "full_name")
    private String fullName;

    @Column(name = "client_id")
    private Long clientId;

    @Column(name = "account_name")
    private String accountName;

    @Column(name = "balance")
    private Double balance;

    @Enumerated(EnumType.STRING)
    @Column(name = "account_kind")
    private Account.AccountKind accountKind;

    @Enumerated(EnumType.STRING)
    @Column(name = "currency")
    private Currency currency;

    @Column(name = "number_of_operations")
    private Integer numberOfOperations;

    @Column(name = "latest_operation")
    private LocalDateTime latestOperation;

    @Column(name = "is_active")
    private Boolean isActive;
}
