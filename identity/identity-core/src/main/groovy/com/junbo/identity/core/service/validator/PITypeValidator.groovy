package com.junbo.identity.core.service.validator

import com.junbo.common.enumid.PITypeId
import com.junbo.identity.spec.v1.model.PIType
import com.junbo.identity.spec.v1.option.list.PITypeListOptions
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic

/**
 * Created by haomin on 14-4-25.
 */
@CompileStatic
public interface PITypeValidator {
    Promise<PIType> validateForGet(PITypeId piTypeId)
    Promise<Void> validateForSearch(PITypeListOptions options)
    Promise<Void> validateForCreate(PIType piType)
    Promise<Void> validateForUpdate(PITypeId piTypeId, PIType piType, PIType oldPiType)
}