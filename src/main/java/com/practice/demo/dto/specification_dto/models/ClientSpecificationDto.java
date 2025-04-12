package com.practice.demo.dto.specification_dto.models;

import com.practice.demo.dto.specification_dto.SpecificationDto;
import com.practice.demo.models.specification.Condition;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
public class ClientSpecificationDto implements SpecificationDto {

    public static final String DEFAULT_FIRST_NAME_OPERATION_TYPE = "begins with";
    public static final String DEFAULT_LAST_NAME_OPERATION_TYPE = "begins with";
    public static final String DEFAULT_NUMBER_OF_ACCOUNTS_OPERATION_TYPE = "=";
    public static final String DEFAULT_REGISTRATION_DATE_OPERATION_TYPE = ">";
    public static final String DEFAULT_BIRTH_DATE_OPERATION_TYPE = ">";

    private String firstName;
    private String lastName;
    private Integer numberOfAccounts;
    private LocalDateTime registrationDate;
    private LocalDate birthDate;

    private String firstNameOT;
    private String lastNameOT;
    private String numberOfAccountsOT;
    private String registrationDateOT;
    private String birthDateOT;

    @Override
    public boolean isEmpty() {

        return (firstName == null || firstName.isEmpty()) && (lastName == null || lastName.isEmpty()) &&
                numberOfAccounts == null && registrationDate == null && birthDate == null;
    }

    @Override
    public List<Condition> toConditions() {

        List<Condition> conditions = new ArrayList<>();

        if (firstName != null && !firstName.isEmpty()) {

            conditions.add(Condition.builder()
                    .fieldName("firstName").operation(Condition.OperationType.resolveByName(firstNameOT))
                    .value(firstName).logicalOperator(Condition.LogicalOperatorType.AND)
                    .build());
        }

        if (lastName != null && !lastName.isEmpty()) {

            conditions.add(Condition.builder()
                    .fieldName("lastName").operation(Condition.OperationType.resolveByName(lastNameOT))
                    .value(lastName).logicalOperator(Condition.LogicalOperatorType.AND)
                    .build());
        }

        if (numberOfAccounts != null) {

            conditions.add(Condition.builder()
                    .fieldName("numberOfAccounts").operation(Condition.OperationType.resolveByName(numberOfAccountsOT))
                    .value(numberOfAccounts).logicalOperator(Condition.LogicalOperatorType.AND)
                    .build());
        }

        if (registrationDate != null) {

            conditions.add(Condition.builder()
                    .fieldName("registrationDate").operation(Condition.OperationType.resolveByName(registrationDateOT))
                    .value(registrationDate).logicalOperator(Condition.LogicalOperatorType.AND)
                    .build());
        }

        if (birthDate != null) {

            conditions.add(Condition.builder()
                    .fieldName("birthDate").operation(Condition.OperationType.resolveByName(birthDateOT))
                    .value(birthDate).logicalOperator(Condition.LogicalOperatorType.AND)
                    .build());
        }

        if (!conditions.isEmpty()) {
            conditions.getLast().setLogicalOperator(Condition.LogicalOperatorType.END);
        }

        return conditions.isEmpty() ? null : conditions;
    }

    @Override
    public void fillEmptyFields() {

        if (firstNameOT == null) {
            firstNameOT = DEFAULT_FIRST_NAME_OPERATION_TYPE;
        }

        if (lastNameOT == null) {
            lastNameOT = DEFAULT_LAST_NAME_OPERATION_TYPE;
        }

        if (numberOfAccountsOT == null) {
            numberOfAccountsOT = DEFAULT_NUMBER_OF_ACCOUNTS_OPERATION_TYPE;
        }

        if (registrationDateOT == null) {
            registrationDateOT = DEFAULT_REGISTRATION_DATE_OPERATION_TYPE;
        }

        if (birthDateOT == null) {
            birthDateOT = DEFAULT_BIRTH_DATE_OPERATION_TYPE;
        }
    }
}
