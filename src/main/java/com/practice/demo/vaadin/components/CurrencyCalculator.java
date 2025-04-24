package com.practice.demo.vaadin.components;

import com.practice.demo.components.units.CurrencyUnit;
import com.practice.demo.models.currency_enum.Currency;
import com.practice.demo.service.ServiceContainer;
import com.practice.demo.vaadin.utils.NumberFormatUtils;
import com.practice.demo.vaadin.utils.ValidationUtils;
import com.vaadin.flow.component.HasSize;
import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.BigDecimalField;
import com.vaadin.flow.component.html.Span;

import java.math.BigDecimal;
import java.util.List;

public class CurrencyCalculator extends VerticalLayout {

    private final CurrencyUnit currencyUnit = ServiceContainer.getInstance().getCurrencyUnit();

    private final ComboBox<Currency> fromCurrency = new ComboBox<>("Из валюты");
    private final ComboBox<Currency> toCurrency = new ComboBox<>("В валюту");
    private final BigDecimalField inputAmount = new BigDecimalField("Сумма");
    private final Span balanceSpan = new Span();
    private final Span balanceLabel = new Span("Здесь появится результат!");

    public CurrencyCalculator() {

        fromCurrency.setRequired(true);
        fromCurrency.setItems(Currency.values());
        fromCurrency.setItemLabelGenerator(Enum::name);

        toCurrency.setRequired(true);
        toCurrency.setItems(Currency.values());
        toCurrency.setItemLabelGenerator(Enum::name);

        inputAmount.setRequiredIndicatorVisible(true);
        inputAmount.setPlaceholder("Введите сумму");

        int fieldsWidth = 500;
        List.of(fromCurrency, toCurrency).forEach(field -> {
            if (field instanceof HasSize hasSize) {
                hasSize.setWidth(fieldsWidth, Unit.PIXELS);
            }
        });
        inputAmount.setWidth(fieldsWidth, Unit.PIXELS);
        setWidth(fieldsWidth + 32, Unit.PIXELS);

        balanceSpan.getStyle()
                .set("font-size", "20px")
                .set("padding", "2px 4px")
                .set("border", "1px dashed gray")
                .set("font-weight", "bold");
        balanceSpan.setVisible(false);
        HorizontalLayout balanceLayout = new HorizontalLayout(balanceLabel, balanceSpan);
        balanceLayout.setJustifyContentMode(JustifyContentMode.END);
        balanceLayout.setWidth(fieldsWidth, Unit.PIXELS);
        balanceLayout.setAlignItems(FlexComponent.Alignment.CENTER);

        Button convertButton = new Button("Конвертировать");
        convertButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        convertButton.addClickListener(e -> {
            if (isValidInput()) {
                convert();
            }
        });
        HorizontalLayout buttonLayout = new HorizontalLayout(convertButton);
        buttonLayout.setWidth(fieldsWidth, Unit.PIXELS);
        buttonLayout.setJustifyContentMode(JustifyContentMode.END);

        add(fromCurrency, toCurrency, inputAmount, balanceLayout, buttonLayout);
    }

    private boolean isValidInput() {
        ValidationUtils.invalidateEmptyFields(getChildren());
        return fromCurrency.getValue() != null
                && toCurrency.getValue() != null
                && inputAmount.getValue() != null;
    }

    private void convert() {
        Currency from = fromCurrency.getValue();
        Currency to = toCurrency.getValue();
        BigDecimal amount = inputAmount.getValue();

        if (from == null || to == null || amount == null) {
            Notification.show("Пожалуйста, заполните все поля", 3000, Notification.Position.MIDDLE);
            return;
        }

        BigDecimal result = currencyUnit.convert(from, to, amount);
        if (result != null) {
            balanceSpan.setText(
                    NumberFormatUtils.toMoney(amount) + " " + from.name() +
                    " = " +
                    NumberFormatUtils.toMoney(result) + " " + to.name()
            );
            setBalanceVisible(true);
        } else {
            setBalanceVisible(false);
            balanceLabel.setText("Ошибка при конвертации");
        }
    }

    private void setBalanceVisible(boolean visible) {
        if (visible) {
            balanceLabel.setText("Результат:");
            balanceLabel.getStyle().set("color", "black");
            balanceSpan.setVisible(true);
        } else {
            balanceLabel.setText("Здесь появится результат!");
            balanceLabel.getStyle().set("color", "gray");
            balanceSpan.setVisible(false);
        }
    }
}
