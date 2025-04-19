package com.practice.demo.vaadin.components.forms;

import com.practice.demo.config.SecuritySessionHandler;
import com.practice.demo.dto.entity_dto.AccountDto;
import com.practice.demo.models.currency_enum.Currency;
import com.practice.demo.models.entities.Account;
import com.practice.demo.service.AccountService;
import com.practice.demo.service.ServiceContainer;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.dao.DataIntegrityViolationException;

import java.math.BigDecimal;
import java.util.List;

@Slf4j
public class AccountForm extends Dialog {

    private final TextField accountNameField = new TextField("Название счёта");
    private final ComboBox<Currency> currencyComboBox = new ComboBox<>("Валюта счёта");
    private final ComboBox<Account.AccountKind> kindComboBox = new ComboBox<>("Тип счёта");

    private final Button createButton = new Button("Создать счёт");
    private final Button cancelButton = new Button("Отмена");

    private final AccountService accountService = ServiceContainer.getInstance().getAccountService();

    @Setter
    private Runnable actionAfterAccountCreated = () -> { };

    public AccountForm() {
        setDraggable(true);
        setHeaderTitle("Создание нового счёта");

        accountNameField.setMaxLength(30);
        currencyComboBox.setItems(Currency.values());
        kindComboBox.setItems(Account.AccountKind.values());

        List.of(currencyComboBox, kindComboBox).forEach(comboBox -> {
            comboBox.setWidthFull();
            comboBox.setRequired(true);
            comboBox.addValueChangeListener(event -> {
                if (event.getValue() == null) {
                    comboBox.setInvalid(true);
                    comboBox.setErrorMessage("Поле обязательно для заполнения");
                }
            });
        });
        accountNameField.setWidthFull();
        accountNameField.setRequired(true);
        accountNameField.setValueChangeMode(ValueChangeMode.EAGER);
        accountNameField.addValueChangeListener(event -> {
            if (StringUtils.isBlank(event.getValue())) {
                accountNameField.setInvalid(true);
                accountNameField.setErrorMessage("Поле обязательно для заполнения");
            }
        });

        currencyComboBox.setItemLabelGenerator(Enum::name);
        kindComboBox.setItemLabelGenerator(Account.AccountKind::getName);

        VerticalLayout formLayout = new VerticalLayout(
                accountNameField,
                currencyComboBox,
                kindComboBox
        );
        formLayout.setPadding(false);
        formLayout.setSpacing(true);
        formLayout.setWidth("500px");

        add(formLayout);

        HorizontalLayout buttonLayout = new HorizontalLayout(cancelButton, createButton);
        buttonLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.END);
        buttonLayout.setAlignItems(FlexComponent.Alignment.CENTER);
        getFooter().add(buttonLayout);
        cancelButton.addClickListener(e -> close());
        createButton.addClickListener(e -> {
            if (isValidInput()) {
                try {
                    accountService.addAccount(
                            AccountDto.builder()
                                    .accountName(accountNameField.getValue())
                                    .accountKind(kindComboBox.getValue())
                                    .currency(currencyComboBox.getValue().getName())
                                    .balance(BigDecimal.ZERO)
                                    .build(),
                            SecuritySessionHandler.getClientId()
                    );
                    actionAfterAccountCreated.run();
                    Notification.show("Счёт успешно создан!");
                    close();
                } catch (DataIntegrityViolationException ex) {
                    log.error(ex.getMessage(), ex);
                    accountNameField.setInvalid(true);
                    accountNameField.setErrorMessage("Счёт с таким названием уже существует");
                } catch (Exception ex) {
                    log.error(ex.getMessage(), ex);
                    Notification.show("Ошибка при создании счета");
                }
            }
        });
    }

    private boolean isValidInput() {
        return StringUtils.isNotBlank(accountNameField.getValue())
                && currencyComboBox.getValue() != null
                && kindComboBox.getValue() != null;
    }

    public AccountForm withActionAfterAccountCreated(Runnable actionAfterAccountCreated) {
        this.actionAfterAccountCreated = actionAfterAccountCreated;
        return this;
    }
}
