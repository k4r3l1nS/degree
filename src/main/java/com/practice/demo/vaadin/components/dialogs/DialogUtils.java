package com.practice.demo.vaadin.components.dialogs;

import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import lombok.experimental.UtilityClass;

@UtilityClass
public class DialogUtils {

    public HorizontalLayout createInfoRow(String label, String value) {
        Span labelSpan = new Span(label + ":");
        labelSpan.getStyle().set("font-weight", "bold");
        Span valueSpan = new Span(value);
        HorizontalLayout row = new HorizontalLayout(labelSpan, valueSpan);
        row.setSpacing(true);
        row.setPadding(true);
        return row;
    }
}
