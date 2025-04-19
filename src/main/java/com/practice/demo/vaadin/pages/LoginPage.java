package com.practice.demo.vaadin.pages;

import com.practice.demo.config.Config;
import com.practice.demo.config.SecuritySessionHandler;
import com.practice.demo.models.entities.Client;
import com.practice.demo.service.AuthenticationService;
import com.practice.demo.vaadin.components.forms.ClientForm;
import com.vaadin.flow.component.*;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.login.AbstractLogin;
import com.vaadin.flow.component.login.LoginForm;
import com.vaadin.flow.component.login.LoginI18n;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.Route;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.annotation.security.PermitAll;
import java.time.LocalDate;
import java.util.Map;
import java.util.function.Predicate;

@Route("login")
@PermitAll
@Slf4j
public class LoginPage extends VerticalLayout implements BeforeEnterObserver {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationService authenticationService;

    private final LoginForm loginForm = new LoginForm();
    private final LoginI18n i18n = LoginI18n.createDefault();
    private final int minimalUserAge = Config.getInstance().getMinimalAge();

    public LoginPage() {
        setSizeFull();
        setJustifyContentMode(JustifyContentMode.CENTER);
        setAlignItems(Alignment.CENTER);

        Tab loginTab = new Tab("Вход");
        Tab registerTab = new Tab("Регистрация");
        VerticalLayout loginContent = createLoginContent();
        VerticalLayout registerContent = createRegisterContent();
        Map<Tab, Component> tabsToPages = Map.of(
                loginTab, loginContent,
                registerTab, registerContent
        );
        Tabs tabs = new Tabs(loginTab, registerTab);
        tabs.setSelectedTab(loginTab);

        Div pages = new Div(loginContent, registerContent);
        pages.setSizeFull();

        tabs.addSelectedChangeListener(event -> {
            tabsToPages.values().forEach(page -> page.setVisible(false));
            Component selectedPage = tabsToPages.get(tabs.getSelectedTab());
            selectedPage.setVisible(true);
        });

        add(tabs, pages);
    }

    private VerticalLayout createLoginContent() {
        loginForm.setAction("login");
        loginForm.setForgotPasswordButtonVisible(false);
        configureLocale();

        loginForm.addLoginListener(this::handleLogin);
        VerticalLayout verticalLayout = new VerticalLayout(loginForm);
        verticalLayout.setAlignItems(Alignment.CENTER);
        verticalLayout.setJustifyContentMode(JustifyContentMode.CENTER);
        return verticalLayout;
    }

    private void handleLogin(AbstractLogin.LoginEvent loginEvent) {
        try {
            authenticationService.login(loginEvent.getUsername(), loginEvent.getPassword());
        } catch (Exception e) {
            log.info("Неверный логин или пароль для пользователя {}", loginEvent.getUsername());
            throw e;
        }
    }

    private void configureLocale() {
        LoginI18n.ErrorMessage errorMessage = new LoginI18n.ErrorMessage();
        errorMessage.setMessage("Неверный логин или пароль");
        errorMessage.setTitle("Ошибка авторизации");
        i18n.setErrorMessage(errorMessage);

        LoginI18n.Form i18nForm = new LoginI18n.Form();
        i18nForm.setSubmit("Войти");
        i18nForm.setUsername("Логин");
        i18nForm.setPassword("Пароль");
        i18nForm.setTitle("Вход в аккаунт");
        i18n.setForm(i18nForm);

        loginForm.setI18n(i18n);
    }

    private VerticalLayout createRegisterContent() {
        ClientForm clientForm = new ClientForm();
        Button registerButton = genetateRegisterButton();
        Span register = generateRegisterTitle();
        Span requiredFieldsLabel = generateRequiredFieldsLabel();

        VerticalLayout registerForm = new VerticalLayout(
                register,
                clientForm,
                registerButton,
                requiredFieldsLabel
        );
        registerButton.addClickShortcut(Key.ENTER);
        int widthPx = 500;
        registerForm.getChildren().forEach(child -> {
            if (child instanceof HasSize hasSize) {
                hasSize.setWidth(widthPx, Unit.PIXELS);
            }
        });
        registerForm.setWidth(widthPx + 32, Unit.PIXELS);

        VerticalLayout registerLayout = new VerticalLayout(registerForm);
        registerLayout.setAlignItems(Alignment.CENTER);
        registerLayout.setJustifyContentMode(JustifyContentMode.CENTER);
        registerLayout.setVisible(false); // по умолчанию скрыта

        TextField usernameField = clientForm.getUsernameField();
        PasswordField passwordField = clientForm.getPasswordField();
        TextField firstNameField = clientForm.getFirstNameField();
        TextField lastNameField = clientForm.getLastNameField();
        DatePicker birthDateField = clientForm.getBirthDateField();
        EmailField emailField = clientForm.getEmailField();
        registerButton.addClickListener(e -> {
            boolean validationSucceeded = validateFields(
                    usernameField, passwordField, firstNameField, lastNameField, birthDateField, emailField
            );
            if (validationSucceeded) {
                Client client = new Client();
                client.setUsername(usernameField.getValue());
                client.setPassword(passwordEncoder.encode(passwordField.getValue()));
                client.setFirstName(firstNameField.getValue());
                client.setLastName(lastNameField.getValue());
                client.setBirthDate(birthDateField.getValue());
                client.setEmail(emailField.getValue());
                client.setRole(Client.Role.USER);
                try {
                    authenticationService.register(client);
                    UI.getCurrent().navigate(HomePage.class); // переход на главную
                } catch (DataIntegrityViolationException ex) {
                    log.error(ex.getMessage(), ex);
                    if (StringUtils.containsIgnoreCase(ex.getMessage(), "email")) {
                        emailField.setInvalid(true);
                        emailField.setErrorMessage("Email уже существует");
                    } else {
                        usernameField.setInvalid(true);
                        usernameField.setErrorMessage("Пользователь уже существует");
                    }
                }
            }
        });

        return registerLayout;
    }

    private Span generateRequiredFieldsLabel() {
        Span requiredFieldsLabel = new Span("* - обязательные поля");
        requiredFieldsLabel.getStyle().set("font-size", "14px");
        return requiredFieldsLabel;
    }

    private Span generateRegisterTitle() {
        Span register = new Span("Зарегистрироваться");
        register.setClassName("fs-4");
        register.getStyle().set("font-size", "28px");
        return register;
    }

    private Button genetateRegisterButton() {
        Button registerButton = new Button("Sign up");
        registerButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        registerButton.getStyle()
                .set("background", "#006af5").set("font-weight", "600")
                .set("font-size", "16px").set("color", "white");
        return registerButton;
    }

    private boolean validateFields(
            TextField usernameField,
            PasswordField passwordField,
            TextField firstNameField,
            TextField lastNameField,
            DatePicker birthDateField,
            EmailField emailField
    ) {
        boolean validationSucceeded = checkIfValid(usernameField, value ->
                        value instanceof String str && str.matches("^[a-zA-Z0-9._-]{3,24}$"),
                "Имя пользователя должно быть от 3 до 24 латинских символов, цифр и/или нижних подчёркиваний"
        );
        validationSucceeded = checkIfValid(passwordField, value ->
                        value instanceof String str && str.matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&._-])[A-Za-z\\d@$!%*?&._-]{8,32}$"),
                "Пароль не соответствует требованиям безопасности. Допустимая длина - от 8 до 32 символов"
        ) && validationSucceeded;
        validationSucceeded = checkIfValid(firstNameField, value ->
                        value instanceof String str && str.matches("[А-Яа-яЁё]+$"),
                "Имя должно состоять из кириллицы"
        ) && validationSucceeded;
        validationSucceeded = checkIfValid(lastNameField, value ->
                        value instanceof String str && str.matches("[А-Яа-яЁё]+$"),
                "Фамилия должна состоять из кириллицы"
        ) && validationSucceeded;
        validationSucceeded = checkIfValid(birthDateField, value -> {
            if (!(value instanceof LocalDate date)) {
                return false;
            }
            LocalDate minDate = LocalDate.of(1900, 1, 1);
            LocalDate maxDate = LocalDate.now().minusYears(minimalUserAge);
            return !date.isBefore(minDate) && !date.isAfter(maxDate);
        }, "Недопустимая дата") && validationSucceeded;
        if (emailField instanceof HasValidation hasValidation) {
            if (hasValidation.isInvalid() || StringUtils.isBlank(emailField.getValue())) {
                hasValidation.setInvalid(true);
                hasValidation.setErrorMessage("Некорректный email");
                validationSucceeded = false;
            }
        }
        return validationSucceeded;
    }

    private boolean checkIfValid(HasValidation hasValidation, Predicate<Object> condition, String errorMessage) {
        if (!(hasValidation instanceof HasValue<?, ?> hasValue)) {
            throw new UnsupportedOperationException("Нет значения для проверки");
        }
        if (condition.test(hasValue.getValue())) {
            hasValidation.setInvalid(false);
            hasValidation.setErrorMessage(null);
            return true;
        }
        hasValidation.setInvalid(true);
        hasValidation.setErrorMessage(errorMessage);
        return false;
    }

    @Override
    public void beforeEnter(BeforeEnterEvent beforeEnterEvent) {
        if (SecuritySessionHandler.isLogin()) {
            beforeEnterEvent.forwardTo(HomePage.class);
        } else if (beforeEnterEvent.getLocation()
                .getQueryParameters()
                .getParameters()
                .containsKey("error")) {
            loginForm.setError(true);
        }
    }
}

