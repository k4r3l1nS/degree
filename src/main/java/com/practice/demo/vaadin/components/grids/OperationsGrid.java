package com.practice.demo.vaadin.components.grids;

import com.practice.demo.components.units.CurrencyUnit;
import com.practice.demo.config.SecuritySessionHandler;
import com.practice.demo.models.currency_enum.Currency;
import com.practice.demo.models.db_views.OperationView;
import com.practice.demo.models.entities.Client;
import com.practice.demo.models.entities.Operation;
import com.practice.demo.service.ServiceContainer;
import com.practice.demo.vaadin.components.dialogs.OperationDialog;
import com.practice.demo.vaadin.components.templates.AbstractCustomGrid;
import com.practice.demo.vaadin.utils.NumberFormatUtils;
import com.vaadin.flow.function.ValueProvider;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Map;

@Getter
public class OperationsGrid extends AbstractCustomGrid<OperationView> {

    private final CurrencyUnit currencyUnit = ServiceContainer.getInstance().getCurrencyUnit();

    public static final Map<String, ValueProvider<OperationView, ?>> VALUE_PROVIDER_MAP;

    static {
        VALUE_PROVIDER_MAP = new LinkedHashMap<>();
        VALUE_PROVIDER_MAP.put("ФИО владельца", OperationView::getOwnerFullName);
        VALUE_PROVIDER_MAP.put("Название счёта", OperationView::getAccountName);
        VALUE_PROVIDER_MAP.put("Валюта счёта", OperationView::getAccountCurrency);
        VALUE_PROVIDER_MAP.put("Тип операции", operationView -> {
            if (operationView.getTransactionSum() == 0) {
                return Operation.OperationKind.DEPOSIT.equals(operationView.getOperationKind())
                        ? "Заведение счёта"
                        : "Закрытие счёта";
            }
            Operation.OperationKind operationKind = operationView.getOperationKind();
            return operationKind == null ? "" : operationKind.getName();
        });
        VALUE_PROVIDER_MAP.put("Сумма перевода", operationView -> {
            double transactionSum = operationView.getTransactionSum();
            return transactionSum == 0 ? "" : NumberFormatUtils.toMoney(transactionSum);
        });
        VALUE_PROVIDER_MAP.put("Валюта перевода", OperationView::getCurrencyFrom);
        VALUE_PROVIDER_MAP.put("Время операции", operationView -> {
            LocalDateTime operationDateTime = operationView.getOperationDateTime();
            return DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss").format(operationDateTime);
        });
    }

    @Override
    protected void initColumns() {
        Client.Role currentRole = SecuritySessionHandler.getRole();
        for (Map.Entry<String, ValueProvider<OperationView, ?>> entry : VALUE_PROVIDER_MAP.entrySet()) {
            if (Client.Role.USER.equals(currentRole) && "ФИО владельца".equals(entry.getKey())) {
                continue;
            }
            addColumn(entry.getValue()).setHeader(entry.getKey());
        }
        for (Column<OperationView> column : getColumns()) {
            column.setKey(column.getHeaderText());
            column.setResizable(true)
                    .setSortable(true)
                    .setTooltipGenerator((OperationView::getAccountName));
        }
        getColumnByKey("Сумма перевода").setComparator((operationView1, operationView2) -> {
            BigDecimal valueRub1 = currencyUnit.convert(operationView1.getCurrencyFrom(), Currency.RUB, BigDecimal.valueOf(operationView1.getTransactionSum()));
            BigDecimal valueRub2 = currencyUnit.convert(operationView2.getCurrencyFrom(), Currency.RUB, BigDecimal.valueOf(operationView2.getTransactionSum()));
            return valueRub1.compareTo(valueRub2);
        });
    }

    @Override
    protected void initListeners() {
        addItemDoubleClickListener(dblClick -> new OperationDialog(dblClick.getItem()).open());
    }

    @Override
    protected void initContextMenu() {

    }
}
