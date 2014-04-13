package com.junbo.identity.core.service.validator.impl

import com.junbo.identity.core.service.validator.LocaleValidator
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Required

/**
 * Created by kg on 3/17/14.
 */
@CompileStatic
class LocaleValidatorImpl implements LocaleValidator {

    private List<String> validLocales

    private String defaultLocale

    @Required
    void setValidLocales(List<String> validLocales) {
        this.validLocales = validLocales
    }

    @Required
    void setDefaultLocale(String defaultLocale) {
        this.defaultLocale = defaultLocale
    }

    boolean isValidLocale(String locale) {
        if (locale == null) {
            throw new IllegalArgumentException('locale is null')
        }

        return validLocales.contains(locale)
    }

    String getDefaultLocale() {
        return defaultLocale
    }
}
