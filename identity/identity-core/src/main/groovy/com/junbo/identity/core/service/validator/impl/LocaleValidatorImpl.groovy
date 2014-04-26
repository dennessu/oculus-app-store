package com.junbo.identity.core.service.validator.impl

import com.junbo.common.enumid.LocaleId
import com.junbo.identity.core.service.validator.LocaleValidator
import com.junbo.identity.spec.v1.option.list.LocaleListOptions
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic

/**
 * Created by kg on 3/17/14.
 */
@CompileStatic
class LocaleValidatorImpl implements LocaleValidator {

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
