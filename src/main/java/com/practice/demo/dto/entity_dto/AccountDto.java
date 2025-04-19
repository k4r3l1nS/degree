package com.practice.demo.dto.entity_dto;

import com.practice.demo.exceptions.models.EmptyFieldException;
import com.practice.demo.exceptions.models.InvalidSumInputException;
import com.practice.demo.models.entities.Account;
import com.practice.demo.models.currency_enum.Currency;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class AccountDto {

    private String accountName;

    private BigDecimal balance;

    private String currency;

    private Account.AccountKind accountKind;

    public Account toEntity() {

        var account = new Account();

        account.setName(accountName);
        account.setCurrency(Currency.resolveByName(currency));
        account.setAccountKind(accountKind);

        account.setLastCapitalization(accountKind.equals(Account.AccountKind.ACCUMULATIVE) ? LocalDateTime.now() : null);

        return account;
    }

    @Transactional
    public void mapTo(Account accountEntity) {

        if (accountName != null && !accountName.isEmpty()) {

            accountEntity.setName(accountName);
        }

        if (currency != null) {

            accountEntity.setBalance(balance);
            accountEntity.setCurrency(Currency.resolveByName(currency));
        }

        if (accountKind != null && accountEntity.getAccountKind() != accountKind) {

            accountEntity.setAccountKind(accountKind);
            accountEntity.setLastCapitalization(accountKind.equals(Account.AccountKind.ACCUMULATIVE) ?
                    LocalDateTime.now() : null);
        }
    }

    private boolean hasEmptyFields() {

        return accountName == null || accountName.isEmpty() || balance == null ||
                currency == null || accountKind == null;
    }

    public void throwIfNotFilled() {

        if (hasEmptyFields()) {

            throw new EmptyFieldException("All fields and radio buttons must be filled in");
        }


        if (balance.compareTo(BigDecimal.ZERO) < 0) {

            throw new InvalidSumInputException("Account can not be created with negative balance");
        }
    }


}
