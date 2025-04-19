package com.practice.demo.vaadin.components.forms;

import com.practice.demo.config.Config;
import com.vaadin.flow.component.HasSize;
import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import lombok.Getter;

import java.time.LocalDate;

@Getter
public class ClientForm extends VerticalLayout {

    private final TextField usernameField = new TextField("Имя пользователя *");
    private final PasswordField passwordField = new PasswordField("Пароль *");
    private final TextField firstNameField = new TextField("Имя *");
    private final TextField lastNameField = new TextField("Фамилия *");
    private final DatePicker birthDateField = new DatePicker("Дата рождения *");
    private final EmailField emailField = new EmailField("Email *");


    public ClientForm() {
        setPadding(false);

        usernameField.setMaxLength(3);
        usernameField.setMaxLength(24);

        passwordField.setMinLength(8);
        passwordField.setMaxLength(32);

        firstNameField.setMaxLength(255);

        lastNameField.setMaxLength(255);

        birthDateField.setMin(LocalDate.of(1900, 1, 1));
        birthDateField.setMax(LocalDate.now().minusYears(Config.getInstance().getMinimalAge()));

        emailField.setMaxLength(255);

        add(usernameField, passwordField, firstNameField, lastNameField, birthDateField, emailField);
    }

    @Override
    public void setWidth(String width) {
        super.setWidth(width);
        getChildren().forEach(child -> {
            if (child instanceof HasSize hasSize) {
                hasSize.setWidth(width);
            }
        });
    }

    @Override
    public void setWidth(float width, Unit unit) {
        super.setWidth(width, unit);
        getChildren().forEach(child -> {
            if (child instanceof HasSize hasSize) {
                hasSize.setWidth(width, unit);
            }
        });
    }
}
