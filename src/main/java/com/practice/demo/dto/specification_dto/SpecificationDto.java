package com.practice.demo.dto.specification_dto;

import com.practice.demo.models.specification.Condition;

import java.util.List;

public interface SpecificationDto {

    List<Condition> toConditions();

    boolean isEmpty();

    void fillEmptyFields();
}
