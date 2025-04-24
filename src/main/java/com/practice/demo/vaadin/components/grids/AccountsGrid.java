package com.practice.demo.vaadin.components.grids;

import com.practice.demo.components.units.CurrencyUnit;
import com.practice.demo.config.SecuritySessionHandler;
import com.practice.demo.models.currency_enum.Currency;
import com.practice.demo.models.db_views.AccountView;
import com.practice.demo.models.entities.Account;
import com.practice.demo.models.entities.Client;
import com.practice.demo.service.AccountService;
import com.practice.demo.service.ServiceContainer;
import com.practice.demo.vaadin.utils.NumberFormatUtils;
import com.practice.demo.vaadin.components.dialogs.AccountDialog;
import com.practice.demo.vaadin.components.forms.ChangeCurrencyForm;
import com.practice.demo.vaadin.components.forms.DeactivateAccountForm;
import com.practice.demo.vaadin.components.templates.AbstractCustomGrid;
import com.vaadin.flow.component.grid.contextmenu.GridContextMenu;
import com.vaadin.flow.component.grid.dataview.GridListDataView;
import com.vaadin.flow.data.provider.Query;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.List;

public class AccountsGrid extends AbstractCustomGrid<AccountView> {

    private final CurrencyUnit currencyUnit = ServiceContainer.getInstance().getCurrencyUnit();
    private final AccountService accountService = ServiceContainer.getInstance().getAccountService();

    @Override
    protected void initColumns() {
        Client.Role currentRole = SecuritySessionHandler.getRole();
        if (!Client.Role.USER.equals(currentRole)) {
            addColumn(AccountView::getFullName).setHeader("ФИО владельца");
        }
        addColumn(AccountView::getAccountName).setHeader("Название счёта");
        addColumn(accountView -> NumberFormatUtils.toMoney(accountView.getBalance())).setHeader("Баланс");
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
    protected void initContextMenu() {
        GridContextMenu<AccountView> contextMenu = this.addContextMenu();
        contextMenu.addItem("Перевести в другую валюту").addMenuItemClickListener(menuClick ->
                menuClick.getItem().ifPresent(accountView ->
                    new ChangeCurrencyForm(accountView, () ->
                        setItems(accountService.fetchAccountViewsByUsername(SecuritySessionHandler.getLogin()))
                    ).open()
                )
        );
        contextMenu.addItem("Деактивировать").addMenuItemClickListener(menuClick ->
                menuClick.getItem().ifPresent(accountView -> {
                    List<AccountView> otherAccounts = this.getDataProvider().fetch(new Query<>())
                            .filter(filteredView -> filteredView.getAccountId() != null && !filteredView.getAccountId().equals(accountView.getAccountId()))
                            .toList();
                    new DeactivateAccountForm(
                            accountView,
                            otherAccounts,
                            () -> setItems(accountService.fetchAccountViewsByUsername(SecuritySessionHandler.getLogin()))
                    ).open();
                })
        );
    }

    @Override
    public GridListDataView<AccountView> setItems(Collection<AccountView> items) {
        GridListDataView<AccountView> gridListDataView = super.setItems(items);
        gridListDataView.refreshAll();
        return gridListDataView;
    }
}
