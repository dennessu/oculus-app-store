package com.junbo.identity.core.service.validator.impl

import com.junbo.common.enumid.LocaleId
import com.junbo.identity.common.util.ValidatorUtil
import com.junbo.identity.core.service.validator.LocaleValidator
import com.junbo.identity.data.repository.LocaleRepository
import com.junbo.identity.spec.error.AppErrors
import com.junbo.identity.spec.v1.model.Locale
import com.junbo.identity.spec.v1.option.list.LocaleListOptions
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Required

/**
 * Created by kg on 3/17/14.
 */
@CompileStatic
class LocaleValidatorImpl implements LocaleValidator {

    private LocaleRepository localeRepository

    @Required
    void setLocaleRepository(LocaleRepository localeRepository) {
        this.localeRepository = localeRepository
    }

    @Override
    Promise<Locale> validateForGet(LocaleId localeId) {
        if (localeId == null || localeId.value == null) {
            throw new IllegalArgumentException('localeId is null')
        }

        return localeRepository.get(localeId).then { Locale locale ->
            if (locale == null) {
                throw AppErrors.INSTANCE.localeNotFound(localeId).exception()
            }

            return Promise.pure(locale)
        }
    }

    @Override
    Promise<Void> validateForSearch(LocaleListOptions options) {
        if (options == null) {
            throw new IllegalArgumentException('options is null')
        }

        return Promise.pure(null)
    }

    @Override
    Promise<Void> validateForCreate(Locale locale) {
        checkBasicLocaleInfo(locale)

        if (locale.id != null) {
            throw AppErrors.INSTANCE.fieldNotWritable('id').exception()
        }

        return localeRepository.get(new LocaleId(locale.localeCode)).then { Locale existing ->
            if (existing != null) {
                throw AppErrors.INSTANCE.fieldDuplicate('localeCode').exception()
            }

            return Promise.pure(null)
        }
    }

    @Override
    Promise<Void> validateForUpdate(LocaleId localeId, Locale locale, Locale oldLocale) {
        if (localeId == null) {
            throw new IllegalArgumentException('localeId is null')
        }

        if (locale == null) {
            throw new IllegalArgumentException('locale is null')
        }

        if (localeId != locale.id) {
            throw AppErrors.INSTANCE.fieldInvalid('id').exception()
        }

        if (localeId != oldLocale.id) {
            throw AppErrors.INSTANCE.fieldInvalid('id').exception()
        }

        checkBasicLocaleInfo(locale)

        if (locale.localeCode != oldLocale.localeCode) {
            throw AppErrors.INSTANCE.fieldInvalid('localeCode').exception()
        }

        return Promise.pure(null)
    }

    private void checkBasicLocaleInfo(Locale locale) {
        if (locale == null) {
            throw new IllegalArgumentException('locale is null')
        }

        if (locale.localeCode == null) {
            throw AppErrors.INSTANCE.fieldRequired('localeCode').exception()
        }
        if (!ValidatorUtil.isValidLocale(locale.localeCode)) {
            throw AppErrors.INSTANCE.fieldInvalid('localeCode').exception()
        }
    }
}
