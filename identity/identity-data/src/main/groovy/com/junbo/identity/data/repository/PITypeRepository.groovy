package com.junbo.identity.data.repository

import com.junbo.common.id.PITypeId
import com.junbo.identity.spec.v1.model.PIType
import com.junbo.identity.spec.v1.option.list.PITypeListOptions
import com.junbo.langur.core.promise.Promise
import com.junbo.sharding.core.annotations.ReadMethod

/**
 * Created by haomin on 14-4-25.
 */
public interface PITypeRepository extends IdentityBaseRepository<PIType, PITypeId> {
    @ReadMethod
    Promise<List<PIType>> search(PITypeListOptions options)
}