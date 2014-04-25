package com.junbo.identity.core.service.validator.impl

import com.junbo.common.enumid.PITypeId
import com.junbo.identity.core.service.validator.PITypeValidator
import com.junbo.identity.spec.v1.model.PIType
import com.junbo.identity.spec.v1.option.list.PITypeListOptions
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic

/**
 * Created by haomin on 14-4-25.
 */
@CompileStatic
class PITypeValidatorImpl implements PITypeValidator {
    @Override
    Promise<PIType> validateForGet(PITypeId piTypeId) {
        return null
    }

    @Override
    Promise<Void> validateForSearch(PITypeListOptions options) {
        return null
    }

    @Override
    Promise<Void> validateForCreate(PIType piType) {
        return null
    }

    @Override
    Promise<Void> validateForUpdate(PITypeId piTypeId, PIType piType, PIType oldPiType) {
        return null
    }
}
