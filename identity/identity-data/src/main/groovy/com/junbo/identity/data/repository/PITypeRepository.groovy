package com.junbo.identity.data.repository

import com.junbo.common.id.PITypeId
import com.junbo.common.model.Results
import com.junbo.identity.spec.v1.model.PIType
import com.junbo.identity.spec.v1.option.list.PITypeListOptions
import com.junbo.langur.core.promise.Promise
import com.junbo.sharding.dualwrite.annotations.ReadMethod
import com.junbo.sharding.repo.BaseRepository
import groovy.transform.CompileStatic

/**
 * Created by haomin on 14-4-25.
 */
@CompileStatic
public interface PITypeRepository extends BaseRepository<PIType, PITypeId> {
    @ReadMethod
    Promise<Results<PIType>> searchByTypeCode(String typeCode, Integer limit, Integer offset)

    @ReadMethod
    Promise<Results<PIType>> searchAll(Integer limit, Integer offset)
}