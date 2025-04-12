package com.practice.demo.dto.specification_dto.models;

import com.practice.demo.dto.specification_dto.SpecificationDto;
import com.practice.demo.models.entities.Operation;
import com.practice.demo.models.currency_enum.Currency;
import com.practice.demo.models.specification.Condition;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.*;

@Getter
@Setter
public class OperationSpecificationDto implements SpecificationDto {

    public static final String DEFAULT_OPERATION_DATE_TIME_OPERATION_TYPE = ">";
    public static final String DEFAULT_OPERATION_KIND_OPERATION_TYPE = "=";
    public static final String DEFAULT_TRANSACTION_SUM_OPERATION_TYPE = ">";
    public static final String DEFAULT_CURRENCY_FROM_OPERATION_TYPE = "=";


    private LocalDateTime operationDateTime;
    private Operation.OperationKind operationKind;
    private Double transactionSum;
    private Currency currencyFrom;

    private String operationDateTimeOT;
    private String operationKindOT;
    private String transactionSumOT;
    private String currencyFromOT;

    @Override
    public List<Condition> toConditions() {

        List<Condition> conditions = new ArrayList<>();

        if (operationDateTime != null) {

            conditions.add(Condition.builder()
                    .fieldName("operationDateTime").operation(Condition.OperationType.resolveByName(operationDateTimeOT))
                    .value(operationDateTime).logicalOperator(Condition.LogicalOperatorType.AND)
                    .build());
        }

        if (operationKind != null) {

            conditions.add(Condition.builder()
                    .fieldName("operationKind").operation(Condition.OperationType.resolveByName(operationKindOT))
                    .value(operationKind).logicalOperator(Condition.LogicalOperatorType.AND)
                    .build());
        }

        if (transactionSum != null) {

            conditions.add(Condition.builder()
                    .fieldName("transactionSum").operation(Condition.OperationType.resolveByName(transactionSumOT))
                    .value(transactionSum).logicalOperator(Condition.LogicalOperatorType.AND)
                    .build());
        }

        if (currencyFrom != null) {

            conditions.add(Condition.builder()
                    .fieldName("currencyFrom").operation(Condition.OperationType.resolveByName(currencyFromOT))
                    .value(currencyFrom).logicalOperator(Condition.LogicalOperatorType.AND)
                    .build());
        }

        if (!conditions.isEmpty()) {
            conditions.getLast().setLogicalOperator(Condition.LogicalOperatorType.END);
        }

        return conditions;
    }

    public List<Condition> toConditions(Long accountId) {

        List<Condition> conditions = new ArrayList<>();

        conditions.add(0, Condition.builder()
                .fieldName("accountId").operation(Condition.OperationType.EQUALS)
                .value(accountId).logicalOperator(Condition.LogicalOperatorType.AND)
                .build());

        var otherConditions = toConditions();

        if (toConditions().isEmpty()) {
            conditions.getLast().setLogicalOperator(Condition.LogicalOperatorType.END);
        } else {
            conditions.addAll(otherConditions);
        }

        return conditions;
    }

    @Override
    public boolean isEmpty() {

        return operationDateTime == null && operationKind == null && transactionSum == null && currencyFrom == null;
    }

    @Override
    public void fillEmptyFields() {

        if (operationDateTimeOT == null) {
            operationDateTimeOT = DEFAULT_OPERATION_DATE_TIME_OPERATION_TYPE;
        }

        if (operationKindOT == null) {
            operationKindOT = DEFAULT_OPERATION_KIND_OPERATION_TYPE;
        }

        if (transactionSumOT == null) {
            transactionSumOT = DEFAULT_TRANSACTION_SUM_OPERATION_TYPE;
        }

        if (currencyFromOT == null) {
            currencyFromOT = DEFAULT_CURRENCY_FROM_OPERATION_TYPE;
        }
    }
}
