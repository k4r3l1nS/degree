package com.practice.demo.dto.entity_dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CurrencyRatesDto {

    private String currencyFrom;
    private String currencyTo;

    public boolean hasOneEmptyField() {
        return !isEmpty() &&
                (currencyFrom == null || currencyFrom.isEmpty() || currencyTo == null || currencyTo.isEmpty());
    }

    public boolean isEmpty() {

        return (currencyFrom == null || currencyFrom.isEmpty()) && (currencyTo == null || currencyTo.isEmpty());
    }

//    private Condition.OperationType operation = Condition.OperationType.EQUALS;


//    @Override
//    public List<Condition> toConditions() {
//
//        List<Condition> conditions = new ArrayList<>();
//
//        if (currencyFrom != null && !currencyFrom.isEmpty()) {
//
//            conditions.add(Condition.builder()
//                    .fieldName("currencyFrom").operation(operation)
//                    .value(Currency.resolveByName(currencyFrom)).logicalOperator(Condition.LogicalOperatorType.AND)
//                    .build());
//        }
//
//        if (currencyTo != null && !currencyTo.isEmpty()) {
//
//            conditions.add(Condition.builder()
//                    .fieldName("lastName").operation(operation)
//                    .value(Currency.resolveByName(currencyTo)).logicalOperator(Condition.LogicalOperatorType.AND)
//                    .build());
//        }
//
//        if (!conditions.isEmpty())
//            conditions.get(conditions.size() - 1).setLogicalOperator(Condition.LogicalOperatorType.END);
//
//        return conditions.isEmpty() ? null : conditions;
//    }
//
//    @Override
//    public boolean isEmpty() {
//        return (currencyFrom == null || currencyFrom.isEmpty()) && (currencyTo == null || currencyTo.isEmpty());
//    }
//
//    @Override
//    public void fillEmptyFields() {
//
//    }
}
