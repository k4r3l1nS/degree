package com.practice.demo.vaadin.pages;

import com.practice.demo.config.SecuritySessionHandler;
import com.practice.demo.models.db_views.OperationView;
import com.practice.demo.models.specification.Condition;
import com.practice.demo.service.OperationService;
import com.practice.demo.vaadin.IHasDefaultHeader;
import com.practice.demo.vaadin.components.DefaultHeader;
import com.practice.demo.vaadin.components.MenuLayout;
import com.practice.demo.vaadin.components.filter.FilterComponent;
import com.practice.demo.vaadin.components.forms.AccountForm;
import com.practice.demo.vaadin.components.grids.OperationsGrid;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.shared.Registration;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.autoconfigure.validation.ValidationAutoConfiguration;

import javax.annotation.security.RolesAllowed;
import java.util.List;
import java.util.Objects;

@Route("operations")
@RolesAllowed({"ADMIN", "USER", "SU"})
public class OperationsPage extends VerticalLayout implements IHasDefaultHeader {

    private final OperationService operationService;

    private final DefaultHeader defaultHeader = new DefaultHeader();
    private final OperationsGrid operationsGrid = new OperationsGrid();
    private final MenuLayout menu = generateMenuLayout();
    private final NoOperationsView noOperationsView = new NoOperationsView();
    private final FilterComponent<OperationView> filterComponent = new FilterComponent<>(operationsGrid);

    public OperationsPage(OperationService operationService, ValidationAutoConfiguration validationAutoConfiguration) {
        this.operationService = operationService;
        add(defaultHeader, noOperationsView, menu, operationsGrid);

        initOperationGridListener();
        List<OperationView> operations = fetchMyOperations();
        if (operations.isEmpty()) {
            menu.setVisible(true);
            operationsGrid.setVisible(false);
        } else {
            noOperationsView.setVisible(false);
            operationsGrid.setItems(operations);
        }
    }

    private void initOperationGridListener() {
        operationsGrid.getListDataView().addItemCountChangeListener(dataChangeEvent -> {
            if (operationsGrid.getListDataView().getItems().findAny().isEmpty()) {
                menu.setVisible(false);
                operationsGrid.setVisible(false);
                noOperationsView.setVisible(true);
                noOperationsView.refreshView();
            }
        });
    }

    private List<OperationView> fetchMyOperations() {
        return operationService.fetchOperationViewsByUsername(SecuritySessionHandler.getLogin());
    }

    @Override
    public void switchTab(Tab activeTab) {
        defaultHeader.setSelectedTab(activeTab);
    }

    private class NoOperationsView extends VerticalLayout {

        private final Paragraph subtitle = new Paragraph();
        private final Button createButton = new Button();
        private Registration buttonRegistration;

        NoOperationsView() {
            setSizeFull();
            setAlignItems(Alignment.CENTER);
            setJustifyContentMode(JustifyContentMode.CENTER);
            addClassName("no-accounts");

            // Иконка сверху
            Icon icon = VaadinIcon.WALLET.create();
            icon.setSize("64px");
            icon.getStyle()
                    .set("color", "#0d6efd")
                    .set("animation", "float 2s ease-in-out infinite");

            // Заголовок
            H1 title = new H1("Операций пока нет");
            title.getStyle()
                    .set("font-weight", "700")
                    .set("margin-top", "1rem");

            refreshView();
            subtitle.getStyle()
                    .set("color", "#6c757d")
                    .set("max-width", "400px")
                    .set("text-align", "center");
            createButton.addClassName("primary");
            createButton.getStyle()
                    .set("cursor", "pointer")
                    .set("margin-top", "1rem");

            add(icon, title, subtitle, createButton);
        }

        public void refreshView() {
            boolean hasItems = ((ListDataProvider<?>) operationsGrid.getDataProvider()).getFilter() != null;
            String pMessage = hasItems
                    ? "По данному фильтру ничего не найдено."
                    : "Заведите первый счёт и начните вести учёт финансов прямо сейчас.";
            subtitle.setText(pMessage);
            createButton.setText(hasItems ? "Сбросить фильтр" : "Завести счёт");
            createButton.setIcon(hasItems ? VaadinIcon.ARROW_BACKWARD.create() : VaadinIcon.PLUS_CIRCLE.create());
            if (buttonRegistration != null) {
                buttonRegistration.remove();
            }
            buttonRegistration = createButton.addClickListener(hasItems
                    ? buttonClickEvent -> {
                        filterComponent.clearFilters();
                        this.setVisible(false);
                        operationsGrid.setVisible(true);
                        menu.setVisible(true);
                    }
                    : buttonClickEvent -> new AccountForm().withActionAfterAccountCreated(() -> {
                        this.setVisible(false);
                        operationsGrid.setItems(fetchMyOperations());
                        operationsGrid.setVisible(true);
                        menu.setVisible(true);
                    }
            ).open());
        }
    }

    private MenuLayout generateMenuLayout() {
        MenuLayout menuLayout = new MenuLayout();
        menuLayout.addMenuButton(
                VaadinIcon.PLUS_CIRCLE.create(),
                "Настроить фильтры",
                click -> filterComponent.withActionAfterApply(
                        () -> {
                            ListDataProvider<?> dataProvider = (ListDataProvider<?>) operationsGrid.getDataProvider();
                            dataProvider.clearFilters();
                            filterComponent.getFilterRows()
                                    .stream()
                                    .filter(filterRow -> filterRow.getSelectCheckbox().getValue())
                                    .forEach(filterRow -> {
                                        dataProvider.setFilter(o -> {
                                            if (!(o instanceof OperationView operationView)) {
                                                return false;
                                            }
                                            String field = filterRow.getGridField().getValue();
                                            Condition.OperationType operationType = filterRow.getOperationType().getValue();
                                            String value = filterRow.getValueField().getValue() == null ?
                                                    "" : filterRow.getValueField().getValue();
                                            Object o1 = OperationsGrid.VALUE_PROVIDER_MAP.get(field).apply(operationView);
                                            String valueFromGrid = o1 == null ? "" : o1.toString();
                                            if (operationType == null) {
                                                return true;
                                            }
                                            final int comparedStrings = valueFromGrid.toLowerCase().compareTo(value.toLowerCase());
                                            return switch (operationType) {
                                                case GREATER -> {
                                                    try {
                                                        double v1 = Double.parseDouble(valueFromGrid.replace(",", ".").replace(" ", ""));
                                                        double v2 = Double.parseDouble(value.replace(",", ".").replace(" ", ""));
                                                        yield v1 > v2;
                                                    } catch (Exception e) {
                                                        yield comparedStrings > 0;
                                                    }
                                                }
                                                case LESS -> {
                                                    try {
                                                        double v1 = Double.parseDouble(valueFromGrid.replace(",", "."));
                                                        double v2 = Double.parseDouble(value.replace(",", "."));
                                                        yield v1 < v2;
                                                    } catch (Exception e) {
                                                        yield comparedStrings < 0;
                                                    }
                                                }
                                                case EQUALS -> Objects.equals(StringUtils.trim(value), StringUtils.trim(valueFromGrid));
                                                case IN -> StringUtils.containsIgnoreCase(valueFromGrid, value);
                                                case BEGINS_WITH -> StringUtils.startsWithIgnoreCase(valueFromGrid, value);
                                            };
                                        });
                                    });
                        }
                ).open(),
                ButtonVariant.LUMO_PRIMARY
        );
        return menuLayout;
    }
}
