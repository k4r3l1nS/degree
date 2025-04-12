package com.practice.demo.dto.entity_dto;

import com.practice.demo.exceptions.models.EmptyFieldException;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
public class TransferBetweenAccountsDto {

    private String accountFromName;
    private String accountToName;
    private BigDecimal transactionSum;
    private String currency;

    private boolean hasEmptyFields() {

        return accountFromName == null || accountFromName.isEmpty() ||
                accountToName == null || accountToName.isEmpty() || transactionSum == null ||
                currency == null || currency.isEmpty();
    }

    public void throwIfNotFilled() {

        if (hasEmptyFields()) {

            throw new EmptyFieldException("All fields and radio buttons must be filled in");
        }
    }
}
