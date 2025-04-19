package com.practice.demo.vaadin.components;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;

public class MenuLayout extends HorizontalLayout {

    public MenuLayout() {
        super();
        setPadding(false);
    }

    public void addMenuButton(
            Icon icon,
            String title,
            ComponentEventListener<ClickEvent<Button>> eventListener,
            ButtonVariant... themeVariants
    ) {
        HorizontalLayout horizontalLayout = new HorizontalLayout(icon, new Span(title));
        Button button = new Button(horizontalLayout, eventListener);
        button.addThemeVariants(themeVariants);
        button.getStyle().set("cursor", "pointer");
        add(button);
    }
}
