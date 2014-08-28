package com.junbo.identity.core.service.validator.impl

import com.junbo.common.enumid.CountryId
import com.junbo.common.error.AppCommonErrors
import com.junbo.common.id.TosId
import com.junbo.identity.core.service.validator.TosValidator
import com.junbo.identity.data.repository.CountryRepository
import com.junbo.identity.data.repository.TosRepository
import com.junbo.identity.spec.error.AppErrors
import com.junbo.identity.spec.v1.model.Country
import com.junbo.identity.spec.v1.model.Tos
import com.junbo.identity.spec.v1.option.list.TosListOptions
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic
import org.apache.commons.collections.CollectionUtils
import org.springframework.beans.factory.annotation.Required

/**
 * Created by liangfu on 4/9/14.
 */
@CompileStatic
class TosValidatorImpl implements TosValidator {

    private TosRepository tosRepository
    private CountryRepository countryRepository

    private List<String> allowedTosTypes
    private List<String> tosStatus

    private Integer minVersionLength
    private Integer maxVersionLength

    private Integer titleMinLength
    private Integer titleMaxLength

    private Integer contentMinLength
    private Integer contentMaxLength

    @Override
    Promise<Tos> validateForGet(TosId tosId) {
        if (tosId == null || tosId.value == null) {
            throw new IllegalArgumentException('tosId is null')
        }

        return tosRepository.get(tosId).then { Tos tos ->
            if (tos == null) {
                throw AppErrors.INSTANCE.tosNotFound(tosId).exception()
            }

            return Promise.pure(tos)
        }
    }

    @Override
    Promise<Void> validateForSearch(TosListOptions options) {
        if (options == null) {
            throw new IllegalArgumentException('options is null')
        }

        return Promise.pure(null)
    }

    @Override
    Promise<Void> validateForCreate(Tos tos) {
        return checkBasicTosInfo(tos).then {
            if (tos.id != null) {
                throw AppCommonErrors.INSTANCE.fieldMustBeNull('id').exception()
            }
            return Promise.pure(null)
        }
    }

    @Override
    Promise<Void> validateForUpdate(TosId tosId, Tos tos, Tos oldTos) {
        if (tos.id == null) {
            throw AppCommonErrors.INSTANCE.fieldRequired('id').exception()
        }

        if (tosId != tos.id) {
            throw AppCommonErrors.INSTANCE.fieldNotWritable('id', tos.id, tosId.toString()).exception()
        }

        return checkBasicTosInfo(tos).then {
            return Promise.pure(null)
        }
    }

    private Promise<Void> checkBasicTosInfo(Tos tos) {
        if (tos == null) {
            throw new IllegalArgumentException('tos is null')
        }

        if (tos.type == null) {
            throw AppCommonErrors.INSTANCE.fieldRequired('type').exception()
        }
        if (!(tos.type in allowedTosTypes)) {
            throw AppCommonErrors.INSTANCE.fieldInvalidEnum('type', allowedTosTypes.join(',')).exception()
        }

        if (tos.version == null) {
            throw AppCommonErrors.INSTANCE.fieldRequired('version').exception()
        }
        if (tos.version.length() < minVersionLength) {
            throw AppCommonErrors.INSTANCE.fieldTooShort('version', minVersionLength).exception()
        }
        if (tos.version.length() > maxVersionLength) {
            throw AppCommonErrors.INSTANCE.fieldTooLong('version', maxVersionLength).exception()
        }

        if (tos.title == null) {
            throw AppCommonErrors.INSTANCE.fieldRequired('title').exception()
        }
        if (tos.title.size() > titleMaxLength) {
            throw AppCommonErrors.INSTANCE.fieldTooLong('title', titleMaxLength).exception()
        }
        if (tos.title.size() < titleMinLength) {
            throw AppCommonErrors.INSTANCE.fieldTooShort('title', titleMinLength).exception()
        }

        if (tos.content == null) {
            throw AppCommonErrors.INSTANCE.fieldRequired('content').exception()
        }
        if (tos.content.size() > contentMaxLength) {
            throw AppCommonErrors.INSTANCE.fieldTooLong('content', contentMaxLength).exception()
        }
        if (tos.content.size() < contentMinLength) {
            throw AppCommonErrors.INSTANCE.fieldTooShort('content', contentMinLength).exception()
        }

        if (tos.state != null && !(tos.state in tosStatus)) {
            throw AppCommonErrors.INSTANCE.fieldInvalidEnum('state', tosStatus.join(',')).exception()
        }

        if (CollectionUtils.isEmpty(tos.countries)) {
            throw AppCommonErrors.INSTANCE.fieldRequired('countries').exception()
        }

        return Promise.each(tos.countries) { CountryId countryId ->
            return countryRepository.get(countryId).then { Country country ->
                if (country == null) {
                    throw AppErrors.INSTANCE.countryNotFound(countryId).exception()
                }

                return Promise.pure(null)
            }
        }.then {
            return Promise.pure(null)
        }
    }

    @Required
    void setTosRepository(TosRepository tosRepository) {
        this.tosRepository = tosRepository
    }

    @Required
    void setTitleMinLength(Integer titleMinLength) {
        this.titleMinLength = titleMinLength
    }

    @Required
    void setTitleMaxLength(Integer titleMaxLength) {
        this.titleMaxLength = titleMaxLength
    }

    @Required
    void setContentMinLength(Integer contentMinLength) {
        this.contentMinLength = contentMinLength
    }

    @Required
    void setContentMaxLength(Integer contentMaxLength) {
        this.contentMaxLength = contentMaxLength
    }

    @Required
    void setCountryRepository(CountryRepository countryRepository) {
        this.countryRepository = countryRepository
    }

    @Required
    void setAllowedTosTypes(List<String> allowedTosTypes) {
        this.allowedTosTypes = allowedTosTypes
    }

    @Required
    void setTosStatus(List<String> tosStatus) {
        this.tosStatus = tosStatus
    }

    @Required
    void setMinVersionLength(Integer minVersionLength) {
        this.minVersionLength = minVersionLength
    }

    @Required
    void setMaxVersionLength(Integer maxVersionLength) {
        this.maxVersionLength = maxVersionLength
    }
}
