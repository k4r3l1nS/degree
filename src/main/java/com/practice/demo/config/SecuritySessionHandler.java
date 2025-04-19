package com.practice.demo.config;

import com.practice.demo.models.entities.Client;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.server.*;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

@UtilityClass
@Slf4j
public class SecuritySessionHandler {

    public boolean isLogin() {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        if (securityContext == null) {
            return false;
        }
        Authentication authentication = securityContext.getAuthentication();
        if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
            return false;
        }
        return authentication.getPrincipal() instanceof Client;
    }

    public void destroySession() {
        UI ui = UI.getCurrent();

        // Отложенный редирект до завершения текущего клиентского цикла
        ui.beforeClientResponse(ui, context -> {
            ui.getPage().setLocation("/login");

            // Очистка SecurityContext и сессии с задержкой
            ui.access(() -> {
                SecurityContextHolder.clearContext();
                VaadinSession vaadinSession = VaadinSession.getCurrent();
                if (vaadinSession != null) {
                    vaadinSession.getSession().invalidate();
                    vaadinSession.close();
                }
            });
        });
    }

    public String getLogin() {
        try {
            if (!(SecurityContextHolder.getContext().getAuthentication().getPrincipal() instanceof Client client)) {
                throw new UnsupportedOperationException("Пользователь не найден в контексте сессии");
            }
            return client.getUsername();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }

    public Client.Role getRole() {
        try {
            if (!(SecurityContextHolder.getContext().getAuthentication().getPrincipal() instanceof Client client)) {
                throw new UnsupportedOperationException("Пользователь не найден в контексте сессии");
            }
            return client.getRole();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }

    public static Long getClientId() {
        try {
            if (!(SecurityContextHolder.getContext().getAuthentication().getPrincipal() instanceof Client client)) {
                throw new UnsupportedOperationException("Пользователь не найден в контексте сессии");
            }
            return client.getId();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }
}
