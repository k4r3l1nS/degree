package com.practice.demo.vaadin.pages;

import com.practice.demo.config.Config;
import com.practice.demo.config.SecuritySessionHandler;
import com.practice.demo.service.OperationService;
import com.practice.demo.service.ServiceContainer;
import com.practice.demo.vaadin.IHasDefaultHeader;
import com.practice.demo.vaadin.components.DefaultHeader;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.router.Route;
import org.springframework.util.CollectionUtils;

import javax.annotation.security.RolesAllowed;

@Route("")
@RolesAllowed({"ADMIN", "USER", "SU"})
public class HomePage extends VerticalLayout implements IHasDefaultHeader {

    private final DefaultHeader defaultHeader = new DefaultHeader();
    private final OperationService operationService = ServiceContainer.getInstance().getOperationService();

    public HomePage() {
        add(defaultHeader, new WelcomeView());
        defaultHeader.setSelectedTab(null);
    }

    @Override
    public void switchTab(Tab activeTab) {
        defaultHeader.setSelectedTab(null);
    }

    private class WelcomeView extends VerticalLayout {

        WelcomeView() {
            setSizeFull();
            setAlignItems(Alignment.CENTER);
            setJustifyContentMode(JustifyContentMode.CENTER);
            addClassName("welcome-view");

            // Иконка
            Icon icon = VaadinIcon.GROUP.create();
            icon.setSize("72px");
            icon.getStyle()
                    .set("color", "#0d6efd")
                    .set("animation", "float 2s ease-in-out infinite");

            // Заголовок
            H1 title = new H1("Добро пожаловать в " + Config.getInstance().getApplicationName() + "!");
            title.getStyle()
                    .set("font-weight", "700")
                    .set("text-align", "center")
                    .set("margin", "1rem 0 0.5rem 0");

            // Подзаголовок
            Paragraph subtitle = new Paragraph("Здесь вы можете управлять своими счетами, отслеживать транзакции и анализировать валютные курсы.");
            subtitle.getStyle()
                    .set("color", "#6c757d")
                    .set("max-width", "500px")
                    .set("text-align", "center");

            // Кнопка "Перейти к счетам"
            Button accountsButton = new Button("Перейти к счетам", VaadinIcon.ARROW_RIGHT.create());
            accountsButton.addClassName("primary");
            accountsButton.getStyle().set("margin-top", "2rem");
            accountsButton.addClickListener(e -> getUI().ifPresent(ui -> ui.navigate(AccountsPage.class)));
            HorizontalLayout buttonsLayout = new HorizontalLayout(accountsButton);

            // Условие: если есть хотя бы одна операция — показать кнопку "История операций"
            if (hasAtLeastOneOperation()) {
                Button operationsButton = new Button("История операций", VaadinIcon.FILE_TEXT_O.create());
                operationsButton.getStyle().set("margin-top", "2rem");
                operationsButton.addClickListener(e -> getUI().ifPresent(ui -> ui.navigate(OperationsPage.class)));
                buttonsLayout.add(operationsButton);
            }

            add(icon, title, subtitle, buttonsLayout);
        }

    }

    private boolean hasAtLeastOneOperation() {
        return !CollectionUtils.isEmpty(
                operationService.fetchOperationViewsByUsername(SecuritySessionHandler.getLogin())
        );
    }
}
