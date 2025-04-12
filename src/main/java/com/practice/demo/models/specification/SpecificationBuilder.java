package com.practice.demo.models.specification;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
public class SpecificationBuilder<T> {

    private List<Condition> conditions = new ArrayList<>();

    public SpecificationBuilder<T> with(Condition condition) {
        if (condition != null) {
            conditions.add(condition);
        }
        return this;
    }

    public SpecificationBuilder<T> with(List<Condition> conditions) {
        if (conditions != null) {
            this.conditions.addAll(conditions);
        }
        return this;
    }

    public Specification<T> build() {

        if (conditions.isEmpty()) {
            return null;
        }

        List<Specification<T>> specifications = new ArrayList<>();

        for (var condition : conditions) {
            specifications.add(new SpecificationImpl<>(condition));
        }

        Specification<T> finalSpecification = specifications.getFirst();
        for (int i = 0; i < conditions.size() - 1; ++i) {

            if (!conditions.get(i).getLogicalOperator().equals(Condition.LogicalOperatorType.END)) {

                finalSpecification = conditions.get(i).getLogicalOperator().equals(Condition.LogicalOperatorType.OR) ?
                        Specification.where(finalSpecification).or(specifications.get(i + 1)) :
                        Specification.where(finalSpecification).and(specifications.get(i + 1));
            }
        }

        return finalSpecification;
    }
}
