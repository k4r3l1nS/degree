package com.practice.demo.vaadin;

import com.vaadin.flow.i18n.I18NProvider;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Locale;

@Component
public class I18NProviderImpl implements I18NProvider {

    @Override
    public List<Locale> getProvidedLocales() {
        return List.of(Locale.ENGLISH, Locale.forLanguageTag("ru"));
    }

    @Override
    public String getTranslation(String s, Locale locale, Object... objects) {
        return s;
    }
}
