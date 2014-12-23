package com.junbo.identity.core.service.validator.impl

import com.junbo.common.enumid.LocaleId
import com.junbo.common.error.AppCommonErrors
import com.junbo.identity.common.util.ValidatorUtil
import com.junbo.identity.core.service.validator.LocaleValidator
import com.junbo.identity.service.LocaleService
import com.junbo.identity.spec.error.AppErrors
import com.junbo.identity.spec.v1.model.Locale
import com.junbo.identity.spec.v1.option.list.LocaleListOptions
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Required

/**
 * Check localeCode not null and no duplicate
 * Check minimum and maximum shortName length
 * Check minimum and maximum longName length
 * Check minimum and maximum localeName length
 * Check fallbackLocale exists and fallbackLocale doesn't equals to itself.
 * Created by kg on 3/17/14.
 */
@CompileStatic
class LocaleValidatorImpl implements LocaleValidator {

    private LocaleService localeService

    private Integer minShortNameLength
    private Integer maxShortNameLength

    private Integer minLongNameLength
    private Integer maxLongNameLength

    private Integer minLocaleNameLength
    private Integer maxLocaleNameLength

    @Required
    void setLocaleService(LocaleService localeService) {
        this.localeService = localeService
    }

    @Required
    void setMinShortNameLength(Integer minShortNameLength) {
        this.minShortNameLength = minShortNameLength
    }

    @Required
    void setMaxShortNameLength(Integer maxShortNameLength) {
        this.maxShortNameLength = maxShortNameLength
    }

    @Required
    void setMinLongNameLength(Integer minLongNameLength) {
        this.minLongNameLength = minLongNameLength
    }

    @Required
    void setMaxLongNameLength(Integer maxLongNameLength) {
        this.maxLongNameLength = maxLongNameLength
    }

    @Required
    void setMinLocaleNameLength(Integer minLocaleNameLength) {
        this.minLocaleNameLength = minLocaleNameLength
    }

    @Required
    void setMaxLocaleNameLength(Integer maxLocaleNameLength) {
        this.maxLocaleNameLength = maxLocaleNameLength
    }

    @Override
    Promise<Locale> validateForGet(LocaleId localeId) {
        if (localeId == null || localeId.value == null) {
            throw new IllegalArgumentException('localeId is null')
        }

        return localeService.get(localeId).then { Locale locale ->
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
        if (locale.id != null) {
            throw AppCommonErrors.INSTANCE.fieldMustBeNull('id').exception()
        }

        return checkBasicLocaleInfo(locale).then {
            return localeService.get(new LocaleId(locale.localeCode)).then { Locale existing ->
                if (existing != null) {
                    throw AppCommonErrors.INSTANCE.fieldDuplicate('localeCode').exception()
                }

                return Promise.pure(null)
            }
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

        if (localeId.toString() != locale.id.toString()) {
            throw AppCommonErrors.INSTANCE.fieldInvalid('id').exception()
        }

        if (localeId.toString() != oldLocale.id.toString()) {
            throw AppCommonErrors.INSTANCE.fieldInvalid('id').exception()
        }

        return checkBasicLocaleInfo(locale).then {
            if (locale.localeCode != oldLocale.localeCode) {
                throw AppCommonErrors.INSTANCE.fieldInvalid('localeCode').exception()
            }
            return Promise.pure(null)
        }
    }

    private Promise<Void> checkBasicLocaleInfo(Locale locale) {
        if (locale == null) {
            throw new IllegalArgumentException('locale is null')
        }

        if (locale.localeCode == null) {
            throw AppCommonErrors.INSTANCE.fieldRequired('localeCode').exception()
        }
        if (!ValidatorUtil.isValidLocale(locale.localeCode)) {
            throw AppCommonErrors.INSTANCE.fieldInvalid('localeCode').exception()
        }

        if (locale.shortName != null) {
            if (locale.shortName.length() > maxShortNameLength) {
                throw AppCommonErrors.INSTANCE.fieldTooLong('shortName', maxShortNameLength).exception()
            }
            if (locale.shortName.length() < minShortNameLength) {
                throw AppCommonErrors.INSTANCE.fieldTooShort('shortName', minShortNameLength).exception()
            }
        }

        if (locale.longName != null) {
            if (locale.longName.length() > maxLongNameLength) {
                throw AppCommonErrors.INSTANCE.fieldTooLong('longName', maxLongNameLength).exception()
            }
            if (locale.longName.length() < minLongNameLength) {
                throw AppCommonErrors.INSTANCE.fieldTooShort('longName', minLongNameLength).exception()
            }
        }

        if (locale.localeName != null) {
            if (locale.localeName.length() > maxLocaleNameLength) {
                throw AppCommonErrors.INSTANCE.fieldTooLong('localeName', maxLocaleNameLength).exception()
            }
            if (locale.localeName.length() < minLocaleNameLength) {
                throw AppCommonErrors.INSTANCE.fieldTooShort('localeName', minLocaleNameLength).exception()
            }
        }

        if (locale.fallbackLocale != null) {
            return localeService.get(locale.fallbackLocale).then { Locale existing ->
                if (existing == null) {
                    throw AppErrors.INSTANCE.localeNotFound(locale.fallbackLocale).exception()
                }

                if (existing.localeCode == locale.localeCode) {
                    throw AppCommonErrors.INSTANCE.fieldInvalid('localeCode',
                            'Default localeCode is same as localeCode').exception()
                }

                return Promise.pure(null)
            }
        }

        return Promise.pure(null)
    }
}
