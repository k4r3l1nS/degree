package com.practice.demo.dto.specification_dto.models;

import com.practice.demo.dto.specification_dto.SpecificationDto;
import com.practice.demo.models.currency_enum.Currency;
import com.practice.demo.models.entities.Account;
import com.practice.demo.models.specification.Condition;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Getter
@Setter
@Builder
public class AccountSpecificationDto implements SpecificationDto {

    public static final String DEFAULT_ACCOUNT_NAME_OPERATION_TYPE = "begins with";
    public static final String DEFAULT_BALANCE_OPERATION_TYPE = ">";
    public static final String DEFAULT_CURRENCY_OPERATION_TYPE = "=";
    public static final String DEFAULT_NUMBER_OF_OPERATIONS_OPERATION_TYPE = "=";
    public static final String DEFAULT_LATEST_OPERATION_OPERATION_TYPE = ">";
    public static final String DEFAULT_ACCOUNT_KIND_OPERATION_TYPE = ">";

    private String accountName;
    private Double balance;
    private Currency currency;
    private Integer numberOfOperations;
    private LocalDateTime latestOperation;
    private Account.AccountKind accountKind;

    private String accountNameOT;
    private String balanceOT;
    private String currencyOT;
    private String numberOfOperationsOT;
    private String latestOperationOT;
    private String accountKindOT;

    @Override
    public boolean isEmpty() {

        return (accountName == null || accountName.isEmpty()) && balance == null && currency == null &&
                numberOfOperations == null && latestOperation == null && accountKind == null;
    }

    @Override
    public List<Condition> toConditions() {

        List<Condition> conditions = new ArrayList<>();

        conditions.add((Condition.builder()
                .fieldName("isActive").operation(Condition.OperationType.IN)
                .values(Arrays.asList(true, null)).logicalOperator(Condition.LogicalOperatorType.AND)
                .build()));

        if (accountName != null && !accountName.isEmpty()) {

            conditions.add(Condition.builder()
                    .fieldName("accountName").operation(Condition.OperationType.resolveByName(accountNameOT))
                    .value(accountName).logicalOperator(Condition.LogicalOperatorType.AND)
                    .build());
        }

        if (balance != null) {

            conditions.add(Condition.builder()
                    .fieldName("balance").operation(Condition.OperationType.resolveByName(balanceOT))
                    .value(balance).logicalOperator(Condition.LogicalOperatorType.AND)
                    .build());
        }

        if (currency != null) {

            conditions.add(Condition.builder()
                    .fieldName("currency").operation(Condition.OperationType.resolveByName(currencyOT))
                    .value(currency).logicalOperator(Condition.LogicalOperatorType.AND)
                    .build());
        }

        if (latestOperation != null) {

            conditions.add(Condition.builder()
                    .fieldName("latestOperation").operation(Condition.OperationType.resolveByName(latestOperationOT))
                    .value(latestOperation).logicalOperator(Condition.LogicalOperatorType.AND)
                    .build());
        }

        if (numberOfOperations != null) {

            conditions.add(Condition.builder()
                    .fieldName("numberOfOperations").operation(Condition.OperationType.resolveByName(numberOfOperationsOT))
                    .value(numberOfOperations).logicalOperator(Condition.LogicalOperatorType.AND)
                    .build());
        }

        if (accountKind != null) {

            conditions.add(Condition.builder()
                    .fieldName("accountKind").operation(Condition.OperationType.resolveByName(accountKindOT))
                    .value(accountKind).logicalOperator(Condition.LogicalOperatorType.AND)
                    .build());
        }

        conditions.get(conditions.size() - 1).setLogicalOperator(Condition.LogicalOperatorType.END);

        return conditions;
    }

    public List<Condition> toConditions(Long clientId) {

        List<Condition> conditions = new ArrayList<>();

        conditions.add(Condition.builder()
                .fieldName("clientId").operation(Condition.OperationType.EQUALS)
                .value(clientId).logicalOperator(Condition.LogicalOperatorType.AND)
                .build());

        var otherConditions = toConditions();

        conditions.addAll(otherConditions);

        return conditions;
    }

    @Override
    public void fillEmptyFields() {

        if (currencyOT == null) {
            currencyOT = DEFAULT_CURRENCY_OPERATION_TYPE;
        }

        if (accountNameOT == null) {
            accountNameOT = DEFAULT_ACCOUNT_NAME_OPERATION_TYPE;
        }

        if (numberOfOperationsOT == null) {
            numberOfOperationsOT = DEFAULT_NUMBER_OF_OPERATIONS_OPERATION_TYPE;
        }

        if (latestOperationOT == null) {
            latestOperationOT = DEFAULT_LATEST_OPERATION_OPERATION_TYPE;
        }

        if (balanceOT == null) {
            balanceOT = DEFAULT_BALANCE_OPERATION_TYPE;
        }

        if (accountKindOT == null) {
            accountKindOT = DEFAULT_ACCOUNT_KIND_OPERATION_TYPE;
        }
    }
}
