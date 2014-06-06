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
 * Check localeCode not null and no duplicate
 * Check minimum and maximum shortName length
 * Check minimum and maximum longName length
 * Check minimum and maximum localeName length
 * Check fallbackLocale exists and fallbackLocale doesn't equals to itself.
 * Created by kg on 3/17/14.
 */
@CompileStatic
class LocaleValidatorImpl implements LocaleValidator {

    private LocaleRepository localeRepository

    private Integer minShortNameLength
    private Integer maxShortNameLength

    private Integer minLongNameLength
    private Integer maxLongNameLength

    private Integer minLocaleNameLength
    private Integer maxLocaleNameLength

    @Required
    void setLocaleRepository(LocaleRepository localeRepository) {
        this.localeRepository = localeRepository
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
        if (locale.id != null) {
            throw AppErrors.INSTANCE.fieldNotWritable('id').exception()
        }

        return checkBasicLocaleInfo(locale).then {
            return localeRepository.get(new LocaleId(locale.localeCode)).then { Locale existing ->
                if (existing != null) {
                    throw AppErrors.INSTANCE.fieldDuplicate('localeCode').exception()
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
            throw AppErrors.INSTANCE.fieldInvalid('id').exception()
        }

        if (localeId.toString() != oldLocale.id.toString()) {
            throw AppErrors.INSTANCE.fieldInvalid('id').exception()
        }

        if (locale.localeCode != oldLocale.localeCode) {
            throw AppErrors.INSTANCE.fieldInvalid('localeCode').exception()
        }

        return checkBasicLocaleInfo(locale)
    }

    private Promise<Void> checkBasicLocaleInfo(Locale locale) {
        if (locale == null) {
            throw new IllegalArgumentException('locale is null')
        }

        if (locale.localeCode == null) {
            throw AppErrors.INSTANCE.fieldRequired('localeCode').exception()
        }
        locale.localeCode = locale.localeCode.replace('_', '-')
        if (!ValidatorUtil.isValidLocale(locale.localeCode)) {
            throw AppErrors.INSTANCE.fieldInvalid('localeCode').exception()
        }

        if (locale.shortName != null) {
            if (locale.shortName.length() > maxShortNameLength) {
                throw AppErrors.INSTANCE.fieldTooLong('shortName', maxShortNameLength).exception()
            }
            if (locale.shortName.length() < minShortNameLength) {
                throw AppErrors.INSTANCE.fieldTooShort('shortName', minShortNameLength).exception()
            }
        }

        if (locale.longName != null) {
            if (locale.longName.length() > maxLongNameLength) {
                throw AppErrors.INSTANCE.fieldTooLong('longName', maxLongNameLength).exception()
            }
            if (locale.longName.length() < minLongNameLength) {
                throw AppErrors.INSTANCE.fieldTooShort('longName', minLongNameLength).exception()
            }
        }

        if (locale.localeName != null) {
            if (locale.localeName.length() > maxLocaleNameLength) {
                throw AppErrors.INSTANCE.fieldTooLong('localeName', maxLocaleNameLength).exception()
            }
            if (locale.localeName.length() < minLocaleNameLength) {
                throw AppErrors.INSTANCE.fieldTooShort('localeName', minLocaleNameLength).exception()
            }
        }

        if (locale.fallbackLocale != null) {
            return localeRepository.get(locale.fallbackLocale).then { Locale existing ->
                if (existing == null) {
                    throw AppErrors.INSTANCE.localeNotFound(locale.fallbackLocale).exception()
                }

                if (existing.localeCode == locale.localeCode) {
                    throw AppErrors.INSTANCE.fieldInvalidException('localeCode',
                            'Default localeCode is same as localeCode').exception()
                }

                return Promise.pure(null)
            }
        }

        return Promise.pure(null)
    }
}
