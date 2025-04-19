package com.practice.demo.vaadin.pages;

import com.practice.demo.config.SecuritySessionHandler;
import com.practice.demo.models.db_views.AccountView;
import com.practice.demo.service.AccountService;
import com.practice.demo.vaadin.IHasDefaultHeader;
import com.practice.demo.vaadin.components.DefaultHeader;
import com.practice.demo.vaadin.components.MenuLayout;
import com.practice.demo.vaadin.components.forms.AccountForm;
import com.practice.demo.vaadin.components.forms.RefillBalanceForm;
import com.practice.demo.vaadin.components.forms.TransferMoneyForm;
import com.practice.demo.vaadin.components.grids.AccountsGrid;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.data.provider.Query;
import com.vaadin.flow.router.Route;

import javax.annotation.security.RolesAllowed;
import java.util.List;

@Route("accounts")
@RolesAllowed({"ADMIN", "USER", "SU"})
public class AccountsPage extends VerticalLayout implements IHasDefaultHeader {

    private final AccountService accountService;

    private final DefaultHeader defaultHeader = new DefaultHeader();
    private final AccountsGrid accountsGrid = new AccountsGrid();
    private final MenuLayout menu = generateMenuLayout();
    private final NoAccountsView noAccountsView = new NoAccountsView();

    public AccountsPage(AccountService accountService) {
        this.accountService = accountService;
        add(defaultHeader, noAccountsView, menu, accountsGrid);

        List<AccountView> accounts = fetchMyAccounts();
        if (accounts.isEmpty()) {
            add();
            menu.setVisible(false);
            accountsGrid.setVisible(false);
        } else {
            noAccountsView.setVisible(false);
            accountsGrid.setItems(accounts);
        }
    }

    private MenuLayout generateMenuLayout() {
        MenuLayout menuLayout = new MenuLayout();
        menuLayout.addMenuButton(
                VaadinIcon.PLUS_CIRCLE.create(),
                "Завести счёт",
                click -> new AccountForm().withActionAfterAccountCreated(
                        () -> accountsGrid.setItems(fetchMyAccounts())
                ).open(),
                ButtonVariant.LUMO_PRIMARY
        );
        menuLayout.addMenuButton(
                VaadinIcon.CREDIT_CARD.create(),
                "Перевести деньги",
                click -> new TransferMoneyForm(
                        () -> accountsGrid.getDataProvider()
                                .fetch(new Query<>())
                                .map(AccountView::getAccountName)
                                .toList()
                ).withActionAfterTransfer(
                        () -> accountsGrid.setItems(fetchMyAccounts())
                ).open(),
                ButtonVariant.LUMO_TERTIARY
        );
        menuLayout.addMenuButton(
                VaadinIcon.MONEY_DEPOSIT.create(),
                "Пополнить счёт",
                click -> new RefillBalanceForm(
                        () -> accountsGrid.getDataProvider()
                                .fetch(new Query<>())
                                .map(AccountView::getAccountName)
                                .toList()
                ).withActionAfterRefill(
                        () -> accountsGrid.setItems(fetchMyAccounts())
                ).open(),
                ButtonVariant.LUMO_TERTIARY
        );
        return menuLayout;
    }

    private List<AccountView> fetchMyAccounts() {
        return accountService.fetchAccountViewsByUsername(SecuritySessionHandler.getLogin());
    }

    @Override
    public void switchTab(Tab activeTab) {
        defaultHeader.setSelectedTab(activeTab);
    }

    private class NoAccountsView extends VerticalLayout {

        NoAccountsView() {
            setSizeFull();
            setAlignItems(FlexComponent.Alignment.CENTER);
            setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
            addClassName("no-accounts");

            // Иконка сверху
            Icon icon = VaadinIcon.WALLET.create();
            icon.setSize("64px");
            icon.getStyle()
                    .set("color", "#0d6efd")
                    .set("animation", "float 2s ease-in-out infinite");

            // Заголовок
            H1 title = new H1("Счета не найдены");
            title.getStyle()
                    .set("font-weight", "700")
                    .set("margin-top", "1rem");

            // Подзаголовок
            Paragraph subtitle = new Paragraph("Заведите первый счёт и начните вести учёт финансов прямо сейчас.");
            subtitle.getStyle()
                    .set("color", "#6c757d")
                    .set("max-width", "400px")
                    .set("text-align", "center");

            Button createButton = new Button(
                    "Завести счёт",
                    VaadinIcon.PLUS_CIRCLE.create(),
                    buttonClickEvent -> new AccountForm().withActionAfterAccountCreated(
                            () -> {
                                this.setVisible(false);
                                accountsGrid.setItems(fetchMyAccounts());
                                accountsGrid.setVisible(true);
                                menu.setVisible(true);
                            }
                    ).open()
            );
            createButton.addClassName("primary");
            createButton.getStyle()
                    .set("cursor", "pointer")
                    .set("margin-top", "1rem");

            add(icon, title, subtitle, createButton);
        }
    }
}
