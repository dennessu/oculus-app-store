package com.junbo.identity.core.service.validator.impl

import com.junbo.common.id.PITypeId
import com.junbo.identity.core.service.validator.PITypeValidator
import com.junbo.identity.data.repository.PITypeRepository
import com.junbo.identity.spec.error.AppErrors
import com.junbo.identity.spec.v1.model.PIType
import com.junbo.identity.spec.v1.option.list.PITypeListOptions
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Required
import org.springframework.util.CollectionUtils

/**
 * Created by haomin on 14-4-25.
 */
@CompileStatic
class PITypeValidatorImpl implements PITypeValidator {

    private PITypeRepository piTypeRepository

    @Required
    void setPiTypeRepository(PITypeRepository piTypeRepository) {
        this.piTypeRepository = piTypeRepository
    }

    @Override
    Promise<PIType> validateForGet(PITypeId piTypeId) {
        if (piTypeId == null || piTypeId.value == null) {
            throw new IllegalArgumentException('piTypeId is null')
        }

        return piTypeRepository.get(piTypeId).then { PIType piType ->
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
            throw AppErrors.INSTANCE.fieldNotWritable('id').exception()
        }

        return piTypeRepository.search(new PITypeListOptions(
            typeCode: piType.typeCode
        )).then { List<PIType> existing ->
            if (!CollectionUtils.isEmpty(existing)) {
                throw AppErrors.INSTANCE.fieldDuplicate('typeCode').exception()
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
            throw AppErrors.INSTANCE.fieldInvalid('id').exception()
        }

        if (piTypeId != oldPiType.id) {
            throw AppErrors.INSTANCE.fieldInvalid('id').exception()
        }

        checkBasicPITypeInfo(piType)

        return Promise.pure(null)
    }

    private void checkBasicPITypeInfo(PIType piType) {
        if (piType == null) {
            throw new IllegalArgumentException('piType is null')
        }

        if (piType.typeCode == null) {
            throw AppErrors.INSTANCE.fieldRequired('typeCode').exception()
        }
        if (CollectionUtils.isEmpty(piType.locales)) {
            throw AppErrors.INSTANCE.fieldRequired('locales').exception()
        }
        if (piType.capableOfRecurring == null) {
            throw AppErrors.INSTANCE.fieldRequired('capableOfRecurring').exception()
        }
        if (piType.isRefundable == null) {
            throw AppErrors.INSTANCE.fieldRequired('isRefundable').exception()
        }
    }
}
