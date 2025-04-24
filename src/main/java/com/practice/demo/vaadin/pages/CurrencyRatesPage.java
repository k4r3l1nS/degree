package com.practice.demo.vaadin.pages;

import com.practice.demo.vaadin.IHasDefaultHeader;
import com.practice.demo.vaadin.components.CurrencyCalculator;
import com.practice.demo.vaadin.components.DefaultHeader;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.router.Route;

import javax.annotation.security.RolesAllowed;

@Route("currency-rates")
@RolesAllowed({"ADMIN", "USER", "SU"})
public class CurrencyRatesPage extends VerticalLayout implements IHasDefaultHeader {

    private final DefaultHeader defaultHeader = new DefaultHeader();

    public CurrencyRatesPage() {
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);

        CurrencyCalculator currencyCalculator = new CurrencyCalculator();
        VerticalLayout contents = new VerticalLayout(currencyCalculator);
        contents.setAlignItems(Alignment.CENTER);
        contents.setJustifyContentMode(JustifyContentMode.CENTER);
        add(defaultHeader, contents);
    }

    @Override
    public void switchTab(Tab activeTab) {
        defaultHeader.setSelectedTab(activeTab);
    }
}
