package com.junbo.identity.service

import com.junbo.common.id.PITypeId
import com.junbo.common.model.Results
import com.junbo.identity.spec.v1.model.PIType
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic

/**
 * Created by liangfu on 10/21/14.
 */
@CompileStatic
interface PITypeService {
    Promise<PIType> get(PITypeId id)

    Promise<PIType> create(PIType model)

    Promise<PIType> update(PIType model, PIType oldModel)

    Promise<Void> delete(PITypeId id)

    Promise<Results<PIType>> searchByTypeCode(String typeCode, Integer limit, Integer offset)

    Promise<Results<PIType>> searchAll(Integer limit, Integer offset)
}
