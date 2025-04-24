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
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.BigDecimalField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.dao.DataIntegrityViolationException;

import java.math.BigDecimal;
import java.util.List;
import java.util.function.Supplier;

@Slf4j
public class TransferMoneyForm extends Dialog {

    private final TextField toAccountField = new TextField("Счёт, на который переводятся деньги");
    private final BigDecimalField amountField = new BigDecimalField("Сумма перевода");
    private final ComboBox<Currency> currencyComboBox = new ComboBox<>("Валюта перевода");

    private final Button transferButton = new Button("Перевести");
    private final Button cancelButton = new Button("Отмена");
    private final ComboBox<String> fromAccountField = new ComboBox<>("Счёт, с которого переводятся деньги");

    private final AccountService accountService = ServiceContainer.getInstance().getAccountService();

    @Setter
    private Runnable actionAfterTransfer = () -> { };


    public TransferMoneyForm(Supplier<List<String>> myAccounts) {
        setDraggable(true);
        setHeaderTitle("Перевод денег");

        fromAccountField.setItems(myAccounts.get());
        fromAccountField.setRequired(true);
        toAccountField.setMaxLength(30);
        toAccountField.setValueChangeMode(ValueChangeMode.EAGER);
        toAccountField.setRequired(true);
        currencyComboBox.setItems(Currency.values());
        currencyComboBox.setRequired(true);

        // Валидация для текстовых полей
        List.of(fromAccountField, toAccountField).forEach(field -> {
            field.setWidthFull();
            ((HasValue<?, String>) field).addValueChangeListener(event -> {
                String fromAccount = fromAccountField.getValue();
                String toAccount = toAccountField.getValue();
                if (StringUtils.isNotBlank(fromAccount) && StringUtils.equals(fromAccount, toAccount)) {
                    fromAccountField.setInvalid(true);
                    toAccountField.setInvalid(true);
                    fromAccountField.setErrorMessage("Счета отправления и назначения совпадают");
                    toAccountField.setErrorMessage("Счета отправления и назначения совпадают");
                } else {
                    fromAccountField.setInvalid(false);
                    toAccountField.setInvalid(false);
                }
                if (StringUtils.isBlank(event.getValue())) {
                    field.setInvalid(true);
                    field.setErrorMessage("Поле обязательно для заполнения");
                }
            });
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
                fromAccountField,
                toAccountField,
                amountAndCurrencyLayout
        );
        formLayout.setPadding(false);
        formLayout.setSpacing(true);
        formLayout.setWidth("500px");

        add(formLayout);

        HorizontalLayout buttonLayout = new HorizontalLayout(cancelButton, transferButton);
        buttonLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.END);
        buttonLayout.setAlignItems(FlexComponent.Alignment.CENTER);
        getFooter().add(buttonLayout);

        cancelButton.addClickListener(e -> close());

        transferButton.addClickListener(e -> {
            if (isValidInput()) {
                try {
                    accountService.transferBetweenAccounts(
                            TransferBetweenAccountsDto.builder()
                                    .accountFromName(fromAccountField.getValue())
                                    .accountToName(toAccountField.getValue())
                                    .transactionSum(amountField.getValue())
                                    .currency(currencyComboBox.getValue().getName())
                                    .build(),
                            SecuritySessionHandler.getClientId()
                    );
                    actionAfterTransfer.run();
                    Notification.show("Перевод успешно выполнен!");
                    close();
                } catch (ResourceNotFoundException | NotEnoughMoneyException ex) {
                    log.info("Ошибка перевода средств со счёта {} на счёт {}: {}",
                            fromAccountField.getValue(), toAccountField.getValue(), ex.getMessage());
                    Notification.show(ex.getMessage());
                } catch (DataIntegrityViolationException ex) {
                    log.error(ex.getMessage(), ex);
                    Notification.show("Ошибка при выполнении перевода");
                } catch (Exception ex) {
                    log.error(ex.getMessage(), ex);
                    Notification.show("Ошибка при переводе средств");
                }
            }
        });
    }

    private boolean isValidInput() {
        ValidationUtils.invalidateEmptyFields(getChildren());
        return StringUtils.isNotBlank(fromAccountField.getValue())
                && StringUtils.isNotBlank(toAccountField.getValue())
                && !StringUtils.equals(fromAccountField.getValue(), toAccountField.getValue())
                && amountField.getValue() != null
                && amountField.getValue().compareTo(BigDecimal.ZERO) > 0
                && currencyComboBox.getValue() != null;
    }

    public TransferMoneyForm withActionAfterTransfer(Runnable actionAfterTransfer) {
        this.actionAfterTransfer = actionAfterTransfer;
        return this;
    }
}
