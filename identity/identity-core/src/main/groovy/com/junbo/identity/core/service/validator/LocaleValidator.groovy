package com.junbo.identity.core.service.validator

import com.junbo.common.enumid.LocaleId
import com.junbo.identity.spec.v1.model.Locale
import com.junbo.identity.spec.v1.option.list.LocaleListOptions
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic

/**
 * Created by kg on 3/17/14.
 */
@CompileStatic
interface LocaleValidator {
    boolean isValidLocale(String locale)
    String getDefaultLocale()

    Promise<Locale> validateForGet(LocaleId localeId)
    Promise<Void> validateForSearch(LocaleListOptions options)
    Promise<Void> validateForCreate(Locale locale)
    Promise<Void> validateForUpdate(LocaleId localeId, Locale locale, Locale oldLocale)
}
