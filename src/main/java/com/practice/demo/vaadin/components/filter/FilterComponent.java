package com.practice.demo.vaadin.components.filter;

import com.practice.demo.models.specification.Condition;
import com.practice.demo.vaadin.components.templates.AbstractCustomGrid;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import lombok.Getter;

import java.util.*;
import java.util.stream.Collectors;

@Getter
public class FilterComponent<T> extends Dialog {

    private final AbstractCustomGrid<T> customGrid;

    private final List<FilterRow> filterRows = new ArrayList<>();

    private Runnable actionAfterApply = () -> { };

    public FilterComponent(AbstractCustomGrid<T> customGrid) {
        VerticalLayout contents = new VerticalLayout();
        this.customGrid = customGrid;
        for (Grid.Column<T> tColumn : customGrid.getColumns()) {
            String columnKey = tColumn.getKey();
            addFilterRow(columnKey, Arrays.stream(Condition.OperationType.values()).collect(Collectors.toSet()));
        }
        filterRows.forEach(contents::add);
        add(contents);


        Button close = new Button("Отмена", e -> close());
        Button reset = new Button("Сбросить", e -> clearFilters());
        Button apply = new Button("Применить", e -> {
            actionAfterApply.run();
            close();
        });
        apply.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        HorizontalLayout footer = new HorizontalLayout(close, reset, apply);
        footer.setJustifyContentMode(FlexComponent.JustifyContentMode.END);
        footer.setWidthFull();
        getFooter().add(footer);
    }

    public FilterComponent<T> withActionAfterApply(Runnable actionAfterApply) {
        this.actionAfterApply = actionAfterApply;
        return this;
    }

    public void clearFilters() {
        filterRows.forEach(filterRow -> filterRow.selectCheckbox.setValue(false));
        actionAfterApply.run();
    }

    @Getter
    public static class FilterRow extends HorizontalLayout {
        private final Checkbox selectCheckbox = new Checkbox();
        private final TextField gridField = new TextField();
        private final ComboBox<Condition.OperationType> operationType = new ComboBox<>();
        private final TextField valueField = new TextField();

        public FilterRow() {
            gridField.setReadOnly(true);
            add(selectCheckbox, gridField, operationType, valueField);
            operationType.setWidth("150px");
            setWidthFull();
        }
    }

    private void addFilterRow(String fieldName, Set<Condition.OperationType> supportedOperations) {
        FilterRow filterRow = new FilterRow();
        filterRow.getGridField().setValue(fieldName);
        filterRow.getOperationType().setItems(supportedOperations);
        filterRow.getOperationType().setItemLabelGenerator(Condition.OperationType::getName);
        filterRows.add(filterRow);
    }
}
