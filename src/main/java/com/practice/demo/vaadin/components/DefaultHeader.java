package com.practice.demo.vaadin.components;

import com.practice.demo.config.Config;
import com.practice.demo.vaadin.IHasDefaultHeader;
import com.practice.demo.vaadin.pages.*;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.TabVariant;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.router.RouterLink;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

import java.util.LinkedHashMap;
import java.util.Map;

@Getter
@Setter
public class DefaultHeader extends HorizontalLayout {

    private final Tabs tabs = new Tabs();
    private static final Map<String, Class<? extends Component>> navigationMap = new LinkedHashMap<>();

    public DefaultHeader() {

        initNavigationMap();

        setWidthFull();

        addClassName("header");

        Div logoDiv = new Div();
        logoDiv.addClassName("logo");
        Image logo = new Image("images/logo.svg", Config.getInstance().getApplicationName());
        logo.setWidth("40px");
        logo.setHeight("32px");
        Span title = new Span("Cool Market");
        title.addClassName("fs-4");
        logoDiv.setWidth("150px");
        logoDiv.setHeight("40px");
        logoDiv.add(title);

        logoDiv.addClickListener(click -> UI.getCurrent().navigate(HomePage.class));

        HorizontalLayout navMenu = generateNavigationMenu();

        add(logoDiv, navMenu);

        tabs.addSelectedChangeListener(selectedChangeEvent -> {
            Tab selectedTab = selectedChangeEvent.getSelectedTab();
            if (selectedTab != null) {
                UI.getCurrent().navigate(
                        navigationMap.get(selectedTab.getLabel())
                ).ifPresent(pageComponent -> {
                    if (pageComponent instanceof IHasDefaultHeader hasDefaultHeader) {
                        hasDefaultHeader.switchTab(selectedTab);
                    }
                });
            }
        });
    }

    private void initNavigationMap() {
        navigationMap.put("Clients", ClientPage.class);
        navigationMap.put("Currency rates", CurrencyRatesPage.class);
        navigationMap.put("About us", AboutUsPage.class);
        navigationMap.put("Log out", LoginPage.class);
    }

    private HorizontalLayout generateNavigationMenu() {
        HorizontalLayout navMenu = new HorizontalLayout();
        navMenu.setSpacing(true);
        navMenu.setWidthFull();
        navMenu.setJustifyContentMode(JustifyContentMode.END);
        for (Map.Entry<String, Class<? extends Component>> entry : navigationMap.entrySet()) {
            tabs.add(generateMenuBarItem(entry.getKey(), entry.getValue()));
        }
        navMenu.add(tabs);
        return navMenu;
    }

    private Tab generateMenuBarItem(String menuItemName, Class<? extends Component> pageComponentClass) {
        Tab menuItem = new Tab(new RouterLink(menuItemName, pageComponentClass));
        menuItem.addClassName("nav-link");
        menuItem.setLabel(menuItemName);
        menuItem.addThemeVariants(TabVariant.LUMO_ICON_ON_TOP);
        return menuItem;
    }

    public void setSelectedTab(Tab activeTab) {
        if (activeTab == null) {
            tabs.setSelectedTab(null);
        } else {
            tabs.getChildren()
                    .toList()
                    .stream()
                    .filter(child -> child instanceof Tab tab && StringUtils.equals(activeTab.getLabel(), tab.getLabel()))
                    .map(child -> (Tab) child)
                    .findFirst()
                    .ifPresent(tabs::setSelectedTab);
        }
    }
}
