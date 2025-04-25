package com.practice.demo.vaadin.pages;

import com.practice.demo.config.Config;
import com.practice.demo.vaadin.IHasDefaultHeader;
import com.practice.demo.vaadin.components.DefaultHeader;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.router.Route;

import javax.annotation.security.RolesAllowed;

@Route("about-us")
@RolesAllowed({"ADMIN", "USER", "SU"})
public class AboutUsPage extends VerticalLayout implements IHasDefaultHeader {

    private final DefaultHeader defaultHeader = new DefaultHeader();

    public AboutUsPage() {
        defaultHeader.selectTabByClass(this.getClass());
        add(defaultHeader);

        setSizeFull();
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.START);
        setSpacing(true);
        setPadding(true);

        add(createIntroSection());
        add(createMissionSection());
        add(createFeaturesSection());
        add(createContactSection());
    }

    private Component createIntroSection() {
        VerticalLayout layout = new VerticalLayout();
        layout.setAlignItems(Alignment.CENTER);
        layout.setSpacing(false);

        Icon icon = VaadinIcon.ROCKET.create();
        icon.setSize("64px");
        icon.getStyle().set("color", "#0d6efd");

        H1 title = new H1("О компании " + Config.getInstance().getApplicationName());
        title.getStyle().set("font-weight", "700");

        Paragraph subtitle = new Paragraph("Мы создаём инновационные решения для управления вашими финансами. " +
                "Удобство, безопасность и скорость — наша философия.");
        subtitle.getStyle().set("text-align", "center").set("max-width", "700px");

        layout.add(icon, title, subtitle);
        return layout;
    }

    private Component createMissionSection() {
        VerticalLayout layout = new VerticalLayout();
        layout.setAlignItems(Alignment.CENTER);
        layout.setSpacing(false);

        H2 missionTitle = new H2("Наша миссия");
        missionTitle.getStyle().set("margin-top", "2rem");

        Paragraph missionText = new Paragraph("Мы стремимся предоставить каждому пользователю мощные инструменты " +
                "для управления своими финансами — будь то личные расходы, семейный бюджет или бизнес-операции.");
        missionText.getStyle().set("text-align", "center").set("max-width", "600px");

        layout.add(missionTitle, missionText);
        return layout;
    }

    private Component createFeaturesSection() {
        HorizontalLayout features = new HorizontalLayout();
        features.setWidthFull();
        features.setJustifyContentMode(JustifyContentMode.CENTER);
        features.setSpacing(true);

        features.add(createFeatureCard("Простота", "Интуитивно понятный интерфейс без лишних элементов.", VaadinIcon.SMILEY_O));
        features.add(createFeatureCard("Безопасность", "Ваши данные под защитой — мы используем современные технологии шифрования.", VaadinIcon.LOCK));
        features.add(createFeatureCard("Мгновенные отчёты", "Отчёты и аналитика в пару кликов.", VaadinIcon.TRENDING_UP));

        return features;
    }

    private Component createFeatureCard(String title, String description, VaadinIcon iconType) {
        VerticalLayout card = new VerticalLayout();
        card.setWidth("220px");
        card.setPadding(true);
        card.setSpacing(true);
        card.getStyle().set("border", "1px solid #e0e0e0").set("border-radius", "12px").set("box-shadow", "0 2px 8px rgba(0,0,0,0.05)");

        Icon icon = iconType.create();
        icon.setSize("32px");
        icon.getStyle().set("color", "#0d6efd");

        H3 cardTitle = new H3(title);
        Paragraph cardDesc = new Paragraph(description);
        cardDesc.getStyle().set("font-size", "14px").set("color", "#6c757d");

        card.setAlignItems(Alignment.CENTER);
        card.add(icon, cardTitle, cardDesc);
        return card;
    }

    private Component createContactSection() {
        VerticalLayout layout = new VerticalLayout();
        layout.setAlignItems(Alignment.CENTER);
        layout.setPadding(true);
        layout.getStyle().set("margin-top", "2rem");

        H2 contactTitle = new H2("Связаться с нами");
        Paragraph contactText = new Paragraph("Есть вопросы или предложения? Мы всегда открыты к общению!");

        Button contactButton = new Button("Написать нам", VaadinIcon.ENVELOPE.create());
        contactButton.getStyle().set("background-color", "#0d6efd").set("color", "white");
        Anchor contactUs = new Anchor("mailto:" + Config.getInstance().getSupportEmail(), contactButton);
        contactUs.getElement().setAttribute("target", "_blank");

        layout.add(contactTitle, contactText, contactUs);
        return layout;
    }

    @Override
    public void switchTab(Tab activeTab) {
        defaultHeader.setSelectedTab(activeTab);
    }
}
