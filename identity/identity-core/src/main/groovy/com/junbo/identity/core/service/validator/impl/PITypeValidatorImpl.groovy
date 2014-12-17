package com.junbo.identity.core.service.validator.impl

import com.fasterxml.jackson.databind.JsonNode
import com.junbo.common.error.AppCommonErrors
import com.junbo.common.id.PITypeId
import com.junbo.common.model.Results
import com.junbo.identity.common.util.JsonHelper
import com.junbo.identity.common.util.ValidatorUtil
import com.junbo.identity.core.service.validator.PITypeValidator
import com.junbo.identity.service.PITypeService
import com.junbo.identity.spec.error.AppErrors
import com.junbo.identity.spec.v1.model.LocaleName
import com.junbo.identity.spec.v1.model.PIType
import com.junbo.identity.spec.v1.option.list.PITypeListOptions
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Required
import org.springframework.util.CollectionUtils
import org.springframework.util.StringUtils

/**
 * Created by haomin on 14-4-25.
 */
@CompileStatic
class PITypeValidatorImpl implements PITypeValidator {

    private PITypeService piTypeService

    private Integer minLocaleNameLength
    private Integer maxLocaleNameLength

    @Required
    void setPiTypeService(PITypeService piTypeService) {
        this.piTypeService = piTypeService
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
    Promise<PIType> validateForGet(PITypeId piTypeId) {
        if (piTypeId == null || piTypeId.value == null) {
            throw new IllegalArgumentException('piTypeId is null')
        }

        return piTypeService.get(piTypeId).then { PIType piType ->
            if (piType == null) {
                throw AppErrors.INSTANCE.piTypeNotFound(piTypeId).exception()
            }

            return Promise.pure(piType)
        }
    }

    @Override
    Promise<Void> validateForSearch(PITypeListOptions options) {
        if (options == null) {
            throw new IllegalArgumentException('options is null')
        }

        return Promise.pure(null)
    }

    @Override
    Promise<Void> validateForCreate(PIType piType) {
        checkBasicPITypeInfo(piType)
        if (piType.id != null) {
            throw AppCommonErrors.INSTANCE.fieldMustBeNull('id').exception()
        }

        return piTypeService.searchByTypeCode(piType.typeCode, Integer.MAX_VALUE, 0).then { Results<PIType> existing ->
            if (existing != null && !CollectionUtils.isEmpty(existing.items)) {
                throw AppCommonErrors.INSTANCE.fieldDuplicate('typeCode').exception()
            }

            return Promise.pure(null)
        }
    }

    @Override
    Promise<Void> validateForUpdate(PITypeId piTypeId, PIType piType, PIType oldPiType) {
        if (piTypeId == null) {
            throw new IllegalArgumentException('piTypeId is null')
        }
        if (piType == null) {
            throw new IllegalArgumentException('piType is null')
        }

        if (piTypeId != piType.id) {
            throw AppCommonErrors.INSTANCE.fieldInvalid('id').exception()
        }

        if (piTypeId != oldPiType.id) {
            throw AppCommonErrors.INSTANCE.fieldInvalid('id').exception()
        }

        checkBasicPITypeInfo(piType)

        return Promise.pure(null)
    }

    private void checkBasicPITypeInfo(PIType piType) {
        if (piType == null) {
            throw new IllegalArgumentException('piType is null')
        }

        if (piType.typeCode == null) {
            throw AppCommonErrors.INSTANCE.fieldRequired('typeCode').exception()
        }
        try {
            Enum.valueOf(com.junbo.common.id.PIType, piType.typeCode)
        }
        catch (IllegalArgumentException e) {
            throw AppCommonErrors.INSTANCE.fieldInvalidEnum('typeCode', com.junbo.common.id.PIType.allTypes()).exception()
        }
        if (CollectionUtils.isEmpty(piType.locales)) {
            throw AppCommonErrors.INSTANCE.fieldRequired('locales').exception()
        }
        piType.locales.each { Map.Entry<String, JsonNode> entry ->
            String key = entry.key
            JsonNode value = entry.value

            if (StringUtils.isEmpty(key)) {
                throw AppCommonErrors.INSTANCE.fieldRequired('locales.key').exception()
            }
            if (!ValidatorUtil.isValidLocale(key)) {
                throw AppCommonErrors.INSTANCE.fieldInvalid('locales.key').exception()
            }

            if (value == null) {
                throw AppCommonErrors.INSTANCE.fieldInvalid('locales.value').exception()
            }
            LocaleName localeName = (LocaleName)JsonHelper.jsonNodeToObj(value, LocaleName)
            if (localeName.description == null) {
                throw AppCommonErrors.INSTANCE.fieldInvalid('locales.value').exception()
            }
            if (localeName.description.length() < minLocaleNameLength) {
                throw AppCommonErrors.INSTANCE.fieldTooShort('locales.value', minLocaleNameLength).exception()
            }
            if (localeName.description.length() > maxLocaleNameLength) {
                throw AppCommonErrors.INSTANCE.fieldTooLong('locales.value', maxLocaleNameLength).exception()
            }
        }

        if (piType.capableOfRecurring == null) {
            throw AppCommonErrors.INSTANCE.fieldRequired('capableOfRecurring').exception()
        }
        if (piType.isRefundable == null) {
            throw AppCommonErrors.INSTANCE.fieldRequired('isRefundable').exception()
        }
    }
}
