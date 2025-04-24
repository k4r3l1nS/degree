package com.practice.demo.vaadin.components.forms;

import com.practice.demo.components.units.CurrencyUnit;
import com.practice.demo.exceptions.models.NotEnoughMoneyException;
import com.practice.demo.exceptions.models.ResourceNotFoundException;
import com.practice.demo.models.currency_enum.Currency;
import com.practice.demo.models.db_views.AccountView;
import com.practice.demo.service.AccountService;
import com.practice.demo.service.ServiceContainer;
import com.practice.demo.vaadin.utils.NumberFormatUtils;
import com.practice.demo.vaadin.utils.ValidationUtils;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;

@Slf4j
public class ChangeCurrencyForm extends Dialog {

    private final ComboBox<Currency> currencyComboBox = new ComboBox<>("Валюта перевода");

    private final Span balanceSpan = new Span();
    private final Button applyButton = new Button("Изменить валюту");
    private final Button cancelButton = new Button("Отмена");

    private final AccountService accountService = ServiceContainer.getInstance().getAccountService();
    private final CurrencyUnit currencyUnit = ServiceContainer.getInstance().getCurrencyUnit();

    public ChangeCurrencyForm(AccountView accountView, Runnable actionAfterCurrencyChange) {
        setDraggable(true);
        setHeaderTitle("Смена валюты");
        setWidth("500px");

        currencyComboBox.setItems(
                Arrays.stream(Currency.values())
                        .filter(currency -> !currency.equals(accountView.getCurrency()))
                        .toList()
        );
        currencyComboBox.setItemLabelGenerator(Enum::name);
        currencyComboBox.setRequired(true);
        currencyComboBox.setWidthFull();

        balanceSpan.getStyle()
                .set("font-size", "20px")
                .set("padding", "2px 4px")
                .set("border", "1px dashed gray")
                .set("font-weight", "bold");
        balanceSpan.setVisible(false);
        Span balanceLabel = new Span("Баланс после перевода:");
        HorizontalLayout balanceLayout = new HorizontalLayout(balanceLabel, balanceSpan);
        balanceLayout.setAlignItems(FlexComponent.Alignment.CENTER);

        VerticalLayout mainLayout = new VerticalLayout(currencyComboBox, balanceLayout);
        mainLayout.setPadding(false);
        add(mainLayout);

        HorizontalLayout buttonLayout = new HorizontalLayout(cancelButton, applyButton);
        buttonLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.END);
        buttonLayout.setAlignItems(FlexComponent.Alignment.CENTER);
        getFooter().add(buttonLayout);

        currencyComboBox.addValueChangeListener(valueChangeEvent -> {
            Currency newCurrency = valueChangeEvent.getValue();
            if (newCurrency == null) {
                currencyComboBox.setInvalid(true);
                currencyComboBox.setErrorMessage("Поле обязательно для заполнения");
                balanceSpan.setVisible(false);
            } else {
                balanceSpan.setText(NumberFormatUtils.toMoney(
                        currencyUnit.convert(accountView.getCurrency(), newCurrency, accountView.getBalance())
                ) + " " + newCurrency.getName());
                balanceSpan.setVisible(true);
            }
        });

        cancelButton.addClickListener(e -> close());
        applyButton.addClickListener(buttonClickEvent -> {
            if (isValidInput()) {
                try {
                    accountService.changeAccountCurrency(accountView.getAccountId(), currencyComboBox.getValue());
                    actionAfterCurrencyChange.run();
                    close();
                    Notification.show("Смена валюты " + accountView.getAccountName() + " прошла успешно!");
                } catch (ResourceNotFoundException | NotEnoughMoneyException ex) {
                    log.error("Ошибка смены валюты счёта {}: {}",
                            accountView.getAccountName(), ex.getMessage(), ex);
                    Notification.show(ex.getMessage());
                } catch (Exception ex) {
                    log.error(ex.getMessage(), ex);
                    Notification.show("Ошибка при смене валюты");
                }
            }
        });
    }

    private boolean isValidInput() {
        ValidationUtils.invalidateEmptyFields(getChildren());
        return currencyComboBox.getValue() != null;
    }
}
