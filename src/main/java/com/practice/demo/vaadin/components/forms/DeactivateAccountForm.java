package com.practice.demo.vaadin.components.forms;

import com.practice.demo.config.SecuritySessionHandler;
import com.practice.demo.models.db_views.AccountView;
import com.practice.demo.service.AccountService;
import com.practice.demo.service.ServiceContainer;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.html.Span;

import java.util.List;

public class DeactivateAccountForm extends ConfirmDialog {

    private final AccountService accountService = ServiceContainer.getInstance().getAccountService();

    public DeactivateAccountForm(AccountView deletedAccount, List<AccountView> otherAccounts, Runnable actionAfterDeactivation) {
        setHeader("Деактивация счёта");
        setCancelable(true);
        setConfirmText("Деактивировать");
        setCancelText("Отмена");
        if (otherAccounts.isEmpty() || deletedAccount.getBalance() == 0) {
            Span warningSpan = new Span(deletedAccount.getBalance() != 0 ?
                        """
                        Вы уверены, что хотите деактивировать свой счёт?
                        У вас нет других счетов для миграции средств
                        """ : "Вы уверены, что хотите деактивировать свой счёт?"
            );
            add(warningSpan);
            addConfirmListener(confirmEvent -> {
                accountService.deactivateAccountById(deletedAccount.getAccountId());
                actionAfterDeactivation.run();
            });
        } else {
            ComboBox<AccountView> transferToComboBox = new ComboBox<>("Аккаунт для перевода средств");
            transferToComboBox.setWidthFull();
            transferToComboBox.setRequired(true);
            transferToComboBox.setItems(otherAccounts);
            transferToComboBox.setItemLabelGenerator(AccountView::getAccountName);
            add(transferToComboBox);
            addConfirmListener(confirmEvent -> {
                if (transferToComboBox.getValue() != null) {
                    accountService.deactivateAccountWithTransfer(
                            deletedAccount.getAccountId(),
                            transferToComboBox.getValue().getAccountId(),
                            SecuritySessionHandler.getClientId()
                    );
                    actionAfterDeactivation.run();
                } else {
                    transferToComboBox.setInvalid(true);
                    transferToComboBox.setErrorMessage("Поле обязательно к заполнению");
                }
            });
        }
        addCancelListener(cancelEvent -> close());
    }
}
