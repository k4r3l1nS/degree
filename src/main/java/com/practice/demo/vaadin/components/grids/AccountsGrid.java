package com.practice.demo.vaadin.components.grids;

import com.practice.demo.components.units.CurrencyUnit;
import com.practice.demo.config.SecuritySessionHandler;
import com.practice.demo.models.currency_enum.Currency;
import com.practice.demo.models.db_views.AccountView;
import com.practice.demo.models.entities.Account;
import com.practice.demo.models.entities.Client;
import com.practice.demo.service.ServiceContainer;
import com.practice.demo.vaadin.components.dialogs.AccountDialog;
import com.practice.demo.vaadin.components.templates.AbstractCustomGrid;
import com.vaadin.flow.component.grid.dataview.GridListDataView;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;

public class AccountsGrid extends AbstractCustomGrid<AccountView> {

    private static final DecimalFormatSymbols DECIMAL_SYMBOLS;

    private final CurrencyUnit currencyUnit = ServiceContainer.getInstance().getCurrencyUnit();

    static {
        DECIMAL_SYMBOLS = new DecimalFormatSymbols();
        DECIMAL_SYMBOLS.setGroupingSeparator(' ');
        DECIMAL_SYMBOLS.setDecimalSeparator(',');
    }

    @Override
    protected void setupColumns() {
        Client.Role currentRole = SecuritySessionHandler.getRole();
        if (!Client.Role.USER.equals(currentRole)) {
            addColumn(AccountView::getFullName).setHeader("ФИО владельца");
        }
        addColumn(AccountView::getAccountName).setHeader("Название счёта");
        addColumn(accountView -> new DecimalFormat("###,##0.00", DECIMAL_SYMBOLS).format(
                BigDecimal.valueOf(accountView.getBalance()).setScale(2, RoundingMode.HALF_UP)
        )).setHeader("Баланс");
        addColumn(AccountView::getCurrency).setHeader("Валюта счёта");
        addColumn(accountView -> {
            Account.AccountKind accountKind = accountView.getAccountKind();
            return accountKind == null ? "" : accountKind.getName();
        }).setHeader("Тип счёта");
        addColumn(accountView -> {
            Integer operationNumber = accountView.getNumberOfOperations();
            return operationNumber == null ? 0 : operationNumber;
        }).setHeader("Количество операций");
        addColumn(accountView -> {
            LocalDateTime lastOperation = accountView.getLatestOperation();
            return DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss").format(lastOperation);
        }).setHeader("Последняя операция");
        for (Column<AccountView> column : getColumns()) {
            column.setKey(column.getHeaderText());
            column.setResizable(true)
                    .setSortable(true)
                    .setTooltipGenerator((AccountView::getAccountName));
        }
        getColumnByKey("Баланс").setComparator((accountView1, accountView2) -> {
            BigDecimal valueRub1 = currencyUnit.convert(accountView1.getCurrency(), Currency.RUB, BigDecimal.valueOf(accountView1.getBalance()));
            BigDecimal valueRub2 = currencyUnit.convert(accountView2.getCurrency(), Currency.RUB, BigDecimal.valueOf(accountView2.getBalance()));
            return valueRub1.compareTo(valueRub2);
        });
    }

    @Override
    protected void initListeners() {
        addItemDoubleClickListener(dblClick -> new AccountDialog(dblClick.getItem()).open());
    }

    @Override
    public GridListDataView<AccountView> setItems(Collection<AccountView> items) {
        GridListDataView<AccountView> gridListDataView = super.setItems(items);
        gridListDataView.refreshAll();
        return gridListDataView;
    }
}
