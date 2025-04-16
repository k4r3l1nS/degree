package com.practice.demo.vaadin.pages;

import com.practice.demo.vaadin.IHasDefaultHeader;
import com.practice.demo.vaadin.components.DefaultHeader;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.router.Route;

import javax.annotation.security.RolesAllowed;

@Route("")
@RolesAllowed({"ADMIN", "USER"})
public class HomePage extends VerticalLayout implements IHasDefaultHeader {

    private final DefaultHeader defaultHeader = new DefaultHeader();

    public HomePage() {
        add(defaultHeader);
        defaultHeader.setSelectedTab(null);
    }

    @Override
    public void switchTab(Tab activeTab) {
        defaultHeader.setSelectedTab(null);
    }
}
