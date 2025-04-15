package com.practice.demo.vaadin.pages;

import com.practice.demo.config.Config;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.Route;

@Route("login")
public class LoginPage extends VerticalLayout {

    private final Span label = new Span("Добро пожаловать в " + Config.getInstance().getApplicationName() + "!");
    private final TextField usernameField;
    private final PasswordField passwordField;
    private final Button loginButton;
    private final Span errorLabel = new Span();

    public LoginPage() {
        // Настройка страницы
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);
        setSizeFull();
        getStyle().set("background-color", "#f4f7fa");

        label.setClassName("fs-4");
        errorLabel.getStyle().set("color", "red");

        // Создание поля для ввода имени пользователя
        usernameField = new TextField("Username");
        usernameField.setValueChangeMode(ValueChangeMode.EAGER);
        usernameField.setWidth("300px");

        // Создание поля для ввода пароля
        passwordField = new PasswordField("Password");
        passwordField.setValueChangeMode(ValueChangeMode.EAGER);
        passwordField.setWidth("300px");

        // Создание кнопки для входа
        loginButton = new Button("Log In", event -> login());
        loginButton.setEnabled(false);
        loginButton.addClickShortcut(Key.ENTER);

        usernameField.addInputListener(inputEvent -> changeVisibilityMode(false));
        passwordField.addInputListener(inputEvent -> changeVisibilityMode(false));

        VerticalLayout formLayout = new VerticalLayout(label, usernameField, passwordField, loginButton, errorLabel);
        formLayout.setSpacing(true);
        formLayout.setAlignItems(Alignment.CENTER);
        formLayout.setSizeUndefined();

        // Размещение формы по центру
        add(formLayout);
    }

    private void changeVisibilityMode(boolean error) {
        loginButton.setEnabled(!error);
        errorLabel.setText(error ? "Некорректный логин или пароль" : "");
    }

    private void login() {
        String username = usernameField.getValue();
        String password = passwordField.getValue();

        // Простая проверка на существование данных
        if (username.isEmpty() || password.isEmpty()) {
            Notification.show("Необходимо ввести логин и пароль", 3000, Notification.Position.MIDDLE);
            return;
        }

        // todo -> Логика для проверки логина и пароля через сервис аутентификации
        if ("user".equals(username) && "password".equals(password)) {
            Notification.show("Авторизация успешна, " + username, 3000, Notification.Position.BOTTOM_END);
            UI.getCurrent().navigate(HomePage.class);
        } else {
            changeVisibilityMode(true);
        }
    }
}
