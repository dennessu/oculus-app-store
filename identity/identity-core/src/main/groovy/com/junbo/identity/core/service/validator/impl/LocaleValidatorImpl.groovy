package com.junbo.identity.core.service.validator.impl

import com.junbo.common.enumid.LocaleId
import com.junbo.identity.core.service.validator.LocaleValidator
import com.junbo.identity.spec.v1.option.list.LocaleListOptions
import com.junbo.langur.core.promise.Promise
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

    @Override
    Promise<com.junbo.identity.spec.v1.model.Locale> validateForGet(LocaleId localeId) {
        return null
    }

    @Override
    Promise<Void> validateForSearch(LocaleListOptions options) {
        return null
    }

    @Override
    Promise<Void> validateForCreate(com.junbo.identity.spec.v1.model.Locale locale) {
        return null
    }

    @Override
    Promise<Void> validateForUpdate(LocaleId localeId, com.junbo.identity.spec.v1.model.Locale locale, com.junbo.identity.spec.v1.model.Locale oldLocale) {
        return null
    }
}
