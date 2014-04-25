package com.junbo.identity.data.repository

import com.junbo.common.enumid.PITypeId
import com.junbo.identity.spec.v1.model.PIType
import com.junbo.identity.spec.v1.option.list.PITypeListOptions
import com.junbo.langur.core.promise.Promise

/**
 * Created by haomin on 14-4-25.
 */
public interface PITypeRepository extends IdentityBaseRepository<PIType, PITypeId> {
    Promise<List<PIType>> search(PITypeListOptions options)
}