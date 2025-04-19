package com.practice.demo.vaadin.components.dialogs;

import com.practice.demo.models.db_views.AccountView;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Span;

public class AccountDialog extends Dialog {

    public AccountDialog(AccountView accountView) {
        add(new Span("Диалог в разработке. Закрыть окно можно по клавише Esc"));
        setCloseOnEsc(true);
    }
}
