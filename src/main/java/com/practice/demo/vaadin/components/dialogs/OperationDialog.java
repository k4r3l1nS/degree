package com.practice.demo.vaadin.components.dialogs;

import com.practice.demo.models.db_views.OperationView;
import com.practice.demo.models.entities.Operation;
import com.practice.demo.service.OperationService;
import com.practice.demo.service.ServiceContainer;
import com.practice.demo.vaadin.utils.NumberFormatUtils;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;

public class OperationDialog extends Dialog {

    private final OperationService operationService = ServiceContainer.getInstance().getOperationService();

    public OperationDialog(OperationView operationView) {
        setHeaderTitle("Выписка об операции");
        setWidth("700px");

        VerticalLayout content = new VerticalLayout();
        content.setSpacing(false);
        content.setPadding(false);
        content.setMargin(false);

        H3 title = new H3(operationView.getAccountName());

        Operation operation = operationService.findById(operationView.getAccountId());

        content.add(title);
        NumberFormatUtils.toMoney(operationView.getTransactionSum());
        String operationKind;
        String transactionSum;
        String currencyFrom;
        if (operationView.getTransactionSum() == 0) {
            operationKind = "Заведение счёта";
            transactionSum = "—";
            currencyFrom = "—";
        } else {
            operationKind = operation.getOperationKind() != null ? operationView.getOperationKind().getName() : "—";
            transactionSum = NumberFormatUtils.toMoney(operationView.getTransactionSum());
            currencyFrom = operationView.getCurrencyFrom() != null ? operationView.getCurrencyFrom().getName() : "—";
        }
        content.add(DialogUtils.createInfoRow("Вид операции", operationKind));
        content.add(DialogUtils.createInfoRow("Сумма перевода", transactionSum));
        content.add(DialogUtils.createInfoRow("Валюта перевода", currencyFrom));
        content.add(DialogUtils.createInfoRow("Валюта счёта", operationView.getAccountCurrency() != null ? operationView.getAccountCurrency().getName() : "—"));
        content.add(DialogUtils.createInfoRow("Баланс счёта", NumberFormatUtils.toMoney(operationView.getBalance()) + " " + operationView.getAccountCurrency()));
        content.add(DialogUtils.createInfoRow("Время перевода", operationView.getOperationDateTime() != null ? operationView.getOperationDateTime().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss")) : "—"));
        content.add(DialogUtils.createInfoRow("Владелец счёта", operationView.getOwnerFullName()));
        content.add(DialogUtils.createInfoRow("E-mail владельца", operationView.getClientEmail()));

        Button close = new Button("Закрыть", e -> close());
        HorizontalLayout footer = new HorizontalLayout(close);
        footer.setJustifyContentMode(FlexComponent.JustifyContentMode.END);
        footer.setWidthFull();
        getFooter().add(footer);

        add(content);
    }
}
