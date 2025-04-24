package com.practice.demo.vaadin.components;

import com.practice.demo.config.Config;
import com.practice.demo.config.SecuritySessionHandler;
import com.practice.demo.models.entities.Client;
import com.practice.demo.vaadin.IHasDefaultHeader;
import com.practice.demo.vaadin.pages.*;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.contextmenu.ContextMenu;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.TabVariant;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.router.RouterLink;
import com.vaadin.flow.shared.Registration;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

import java.util.LinkedHashMap;
import java.util.Map;

@Getter
@Setter
public class DefaultHeader extends HorizontalLayout {

    private final Tabs tabs = new Tabs();
    private final Map<String, Class<? extends Component>> navigationMap = new LinkedHashMap<>();
    private Registration tabsChangeRegistration;

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

        String username = SecuritySessionHandler.getLogin();
        Span usernameSpan = new Span(username);
        usernameSpan.setClassName("fs-4");
        usernameSpan.getStyle().set("cursor", "pointer");

        Avatar avatar = new Avatar(username);
        avatar.setThemeName("xsmall");

        Icon dropdownIcon = VaadinIcon.CHEVRON_DOWN.create();
        dropdownIcon.getStyle().set("width", "12px");

        HorizontalLayout userLayout = new HorizontalLayout(avatar, usernameSpan, dropdownIcon);
        userLayout.setAlignItems(FlexComponent.Alignment.CENTER);
        userLayout.getStyle()
                .set("cursor", "pointer")
                .set("padding", "0.25em 0.5em")
                .set("border-radius", "10px")
                .set("background-color", "#f0f2f5");

        ContextMenu contextMenu = new ContextMenu(userLayout);
        contextMenu.setOpenOnClick(true);
        contextMenu.addItem("Выйти", (itemClickEvent) -> SecuritySessionHandler.destroySession());

        add(logoDiv, navMenu, userLayout);

        tabs.setSelectedTab(null);
        tabsChangeRegistration = initListener();
    }

    private void initNavigationMap() {
        Client.Role currentRole = SecuritySessionHandler.getRole();
        switch (currentRole) {
            case USER:
                navigationMap.clear();
                navigationMap.put("Мои счета", AccountsPage.class);
                navigationMap.put("Операции по счетам", OperationsPage.class);
                navigationMap.put("Соотношения валют", CurrencyRatesPage.class);
                navigationMap.put("О нас", AboutUsPage.class);
                break;
            case ADMIN, SU:
                navigationMap.clear();
                navigationMap.put("Клиентская база", ClientsPage.class);
                navigationMap.put("Соотношения валют", CurrencyRatesPage.class);
                navigationMap.put("О нас", AboutUsPage.class);
                break;
            default:
                throw new IllegalStateException("Роль не может быть не определена");
        }
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
        tabsChangeRegistration.remove();
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
        tabsChangeRegistration = initListener();
    }

    private Registration initListener() {
        return tabs.addSelectedChangeListener(selectedChangeEvent ->
                changeTabs(selectedChangeEvent.getSelectedTab(), selectedChangeEvent.getPreviousTab())
        );
    }

    private void changeTabs(Tab selectedTab, Tab previousTab) {
        if (selectedTab != null && (previousTab == null || !StringUtils.equals(selectedTab.getLabel(), previousTab.getLabel()))) {
            if (LoginPage.class.equals(navigationMap.get(selectedTab.getLabel()))) {
                SecuritySessionHandler.destroySession();
                return;
            }
            UI.getCurrent().navigate(
                    navigationMap.get(selectedTab.getLabel())
            ).ifPresent(pageComponent -> {
                if (pageComponent instanceof IHasDefaultHeader hasDefaultHeader) {
                    hasDefaultHeader.switchTab(selectedTab);
                }
            });
        }
    }
}
