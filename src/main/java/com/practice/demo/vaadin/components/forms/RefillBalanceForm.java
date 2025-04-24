package com.practice.demo.vaadin.components.forms;


import com.practice.demo.config.SecuritySessionHandler;
import com.practice.demo.dto.entity_dto.TransferBetweenAccountsDto;
import com.practice.demo.exceptions.models.NotEnoughMoneyException;
import com.practice.demo.exceptions.models.ResourceNotFoundException;
import com.practice.demo.models.currency_enum.Currency;
import com.practice.demo.service.AccountService;
import com.practice.demo.service.ServiceContainer;
import com.practice.demo.vaadin.utils.ValidationUtils;
import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.BigDecimalField;
import com.vaadin.flow.data.value.ValueChangeMode;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.util.List;
import java.util.function.Supplier;

@Slf4j
public class RefillBalanceForm extends Dialog {

    private final ComboBox<String> toAccountField = new ComboBox<>("Пополняемый счёт");
    private final ComboBox<Currency> currencyComboBox = new ComboBox<>("Валюта перевода");
    private final BigDecimalField amountField = new BigDecimalField("Сумма перевода");

    private final Button refillButton = new Button("Пополнить");
    private final Button cancelButton = new Button("Отмена");

    private final Span disclaimerSpan = new Span(
            """
                 Предположим, что в данной форме происходит зачисление денег из стороннего ресурса.
                 Так как платёжная система к приложению, само собой, не подключена, полагаем, что
                 деньги приходят без ошибок с некоторого сервиса, имитируемого этой формой.
                 """
    );

    private final AccountService accountService = ServiceContainer.getInstance().getAccountService();

    @Setter
    private Runnable actionAfterRefill = () -> { };

    public RefillBalanceForm(Supplier<List<String>> myAccounts) {
        setDraggable(true);
        setHeaderTitle("Пополнение счёта");

        toAccountField.setItems(myAccounts.get());
        toAccountField.setRequired(true);
        currencyComboBox.setItems(Currency.values());
        currencyComboBox.setRequired(true);

        toAccountField.setWidthFull();
        ((HasValue<?, String>) toAccountField).addValueChangeListener(event -> {
            if (StringUtils.isBlank(event.getValue())) {
                toAccountField.setInvalid(true);
                toAccountField.setErrorMessage("Поле обязательно для заполнения");
            }
        });

        // Валидация для суммы
        amountField.setWidthFull();
        amountField.getElement().setProperty("required", true);
        amountField.setValueChangeMode(ValueChangeMode.EAGER);
        amountField.addValueChangeListener(event -> {
            if (event.getValue() == null || event.getValue().compareTo(BigDecimal.ZERO) <= 0) {
                amountField.setInvalid(true);
                amountField.setErrorMessage("Сумма должна быть положительной");
            }
        });

        currencyComboBox.setItemLabelGenerator(Enum::name);

        // Контейнер для суммы и валюты
        HorizontalLayout amountAndCurrencyLayout = new HorizontalLayout(amountField, currencyComboBox);
        amountAndCurrencyLayout.setSpacing(true);
        amountAndCurrencyLayout.setWidthFull();

        VerticalLayout formLayout = new VerticalLayout(
                toAccountField,
                amountAndCurrencyLayout
        );
        formLayout.setPadding(false);
        formLayout.setSpacing(true);
        formLayout.setWidth("500px");

        disclaimerSpan.getStyle()
                .set("white-space", "normal")  // разрешаем перенос строк
                .set("overflow-wrap", "break-word")  // переносим слова при необходимости
                .set("width", "200px")  // фиксируем ширину
                .set("margin-top", "20px")
                .set("display", "block");
        disclaimerSpan.getStyle().set("width", "500px");
        add(formLayout, disclaimerSpan);

        HorizontalLayout buttonLayout = new HorizontalLayout(cancelButton, refillButton);
        buttonLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.END);
        buttonLayout.setAlignItems(FlexComponent.Alignment.CENTER);
        getFooter().add(buttonLayout);

        cancelButton.addClickListener(e -> close());

        refillButton.addClickListener(e -> {
            if (isValidInput()) {
                try {
                    accountService.addMoneyToAccount(
                            TransferBetweenAccountsDto.builder()
                                    .transactionSum(amountField.getValue())
                                    .accountToName(toAccountField.getValue())
                                    .currency(currencyComboBox.getValue().getName())
                                    .build(),
                            SecuritySessionHandler.getClientId()
                    );
                    actionAfterRefill.run();
                    Notification.show("Перевод успешно выполнен!");
                    close();
                } catch (ResourceNotFoundException | NotEnoughMoneyException ex) {
                    log.error("Ошибка пополнения средств на счёт {}: {}",
                            toAccountField.getValue(), ex.getMessage(), ex);
                    Notification.show(ex.getMessage());
                } catch (Exception ex) {
                    log.error(ex.getMessage(), ex);
                    Notification.show("Ошибка при переводе средств");
                }
            }
        });

    }

    private boolean isValidInput() {
        ValidationUtils.invalidateEmptyFields(getChildren());
        return StringUtils.isNotBlank(toAccountField.getValue())
                && amountField.getValue() != null
                && amountField.getValue().compareTo(BigDecimal.ZERO) > 0
                && currencyComboBox.getValue() != null;
    }

    public RefillBalanceForm withActionAfterRefill(Runnable actionAfterRefill) {
        this.actionAfterRefill = actionAfterRefill;
        return this;
    }
}
