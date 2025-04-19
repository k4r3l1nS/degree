package com.practice.demo.dto.entity_dto;

import com.practice.demo.exceptions.models.EmptyFieldException;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
public class TransferBetweenAccountsDto {

    private String accountFromName;
    private String accountToName;
    private BigDecimal transactionSum;
    private String currency;

    private boolean hasEmptyFields(boolean isRefillFromOtherSource) {

        return StringUtils.isBlank(accountFromName) && !isRefillFromOtherSource ||
                StringUtils.isAnyBlank(accountToName, currency) || transactionSum == null;
    }

    public void throwIfNotFilled(boolean isRefillFromOtherSource) {

        if (hasEmptyFields(isRefillFromOtherSource)) {

            throw new EmptyFieldException("All fields and radio buttons must be filled in");
        }
    }
}
