package com.practice.demo.vaadin.components.dialogs;

import com.practice.demo.models.db_views.AccountView;
import com.practice.demo.models.entities.Account;
import com.practice.demo.service.AccountService;
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

public class AccountDialog extends Dialog {

    private final AccountService accountService = ServiceContainer.getInstance().getAccountService();

    public AccountDialog(AccountView accountView) {
        setHeaderTitle("Выписка о счёте");
        setWidth("700px");

        VerticalLayout content = new VerticalLayout();
        content.setSpacing(false);
        content.setPadding(false);
        content.setMargin(false);

        H3 title = new H3(accountView.getAccountName());

        Account account = accountService.findById(accountView.getAccountId());

        content.add(title);
        content.add(DialogUtils.createInfoRow("Владелец", accountView.getFullName()));
        content.add(DialogUtils.createInfoRow("Тип счёта", accountView.getAccountKind() != null ? accountView.getAccountKind().getName() : "—"));
        content.add(DialogUtils.createInfoRow("Баланс", NumberFormatUtils.toMoney(accountView.getBalance()) + " " + accountView.getCurrency()));
        content.add(DialogUtils.createInfoRow("Количество операций", String.valueOf(accountView.getNumberOfOperations())));
        content.add(DialogUtils.createInfoRow("Последняя операция",
                accountView.getLatestOperation() != null ?
                        accountView.getLatestOperation().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss")) :
                        "Нет операций"
        ));
        if (Account.AccountKind.ACCUMULATIVE.equals(accountView.getAccountKind())) {
            content.add(DialogUtils.createInfoRow("Последняя капитализация",
                    account.getLastCapitalization() != null ?
                            account.getLastCapitalization().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss")) :
                            "Отсутствует"
            ));
            content.add(DialogUtils.createInfoRow(
                    "Годовая ставка",
                    NumberFormatUtils.toMoney(
                            BigDecimal.valueOf(Account.AccountKind.ACCUMULATION_COEFFICIENT_PER_YEAR)
                                    .subtract(BigDecimal.ONE)
                                    .multiply(BigDecimal.valueOf(100))
                    ) + "%"
            ));
        }
        accountService.extractLatestOperation(account.getId()).ifPresent(operation -> {
            if (!BigDecimal.ZERO.equals(operation.getTransactionSum())) {
                content.add(DialogUtils.createInfoRow("Тип последней операции", operation.getOperationKind().getName()));
                content.add(DialogUtils.createInfoRow(
                        "Сумма последнего перевода",
                        NumberFormatUtils.toMoney(operation.getTransactionSum()) + " " + operation.getCurrencyFrom().getName()
                ));
            }
        });
        content.add(DialogUtils.createInfoRow("Активен", Boolean.TRUE.equals(accountView.getIsActive()) ? "Да" : "Нет"));

        Button close = new Button("Закрыть", e -> close());
        HorizontalLayout footer = new HorizontalLayout(close);
        footer.setJustifyContentMode(FlexComponent.JustifyContentMode.END);
        footer.setWidthFull();
        getFooter().add(footer);

        add(content);
    }
}
