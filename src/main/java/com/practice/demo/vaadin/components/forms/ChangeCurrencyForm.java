package com.practice.demo.vaadin.components.forms;

import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Span;

public class ChangeCurrencyForm extends Dialog {
    public ChangeCurrencyForm() {
        add(new Span("Форма в разработке. Закрыть окно можно по клавише Esc"));
        setCloseOnEsc(true);
    }
}
