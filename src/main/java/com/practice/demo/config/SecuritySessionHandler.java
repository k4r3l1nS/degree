package com.practice.demo.config;

import com.practice.demo.models.entities.Client;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.server.VaadinSession;
import lombok.experimental.UtilityClass;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

@UtilityClass
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
        UI.getCurrent().getPage().executeJs("window.history.replaceState({}, document.title, '/login')");
        SecurityContextHolder.clearContext();
        VaadinSession.getCurrent().getSession().invalidate();
        VaadinSession.getCurrent().close();
    }

    public String getLogin() {
        try {
            if (!(SecurityContextHolder.getContext().getAuthentication().getPrincipal() instanceof Client client)) {
                throw new UnsupportedOperationException("Логин не найден в контексте сессии");
            }
            return client.getUsername();
        } catch (Exception e) {
            return null;
        }
    }
}
