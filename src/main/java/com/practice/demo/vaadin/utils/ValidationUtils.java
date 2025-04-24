package com.practice.demo.vaadin.utils;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasValidation;
import com.vaadin.flow.component.HasValue;
import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.StringUtils;

import java.util.stream.Stream;

@UtilityClass
public class ValidationUtils {

    /**
     * Рекурсивно инвалидировать незаполненные поля ниже по dom-дереву
     *
     * @param componentStream Поток с дочерними компонентами данного
     */
    public void invalidateEmptyFields(Stream<? extends Component> componentStream) {
        componentStream
                .forEach(child -> {
                    if (child instanceof HasValidation && child instanceof HasValue<?, ?>) {
                        Object value = ((HasValue<?, ?>) child).getValue();
                        if (value == null || StringUtils.isBlank(value.toString())) {
                            ((HasValidation) child).setInvalid(true);
                            ((HasValidation) child).setErrorMessage("Поле обязательно для заполнения");
                        }
                    }
                    invalidateEmptyFields(child.getChildren());
                });
    }
}
