package com.junbo.identity.data.repository

import com.junbo.common.enumid.CurrencyId
import com.junbo.common.model.Results
import com.junbo.langur.core.promise.Promise
import com.junbo.sharding.dualwrite.annotations.ReadMethod
import com.junbo.sharding.repo.BaseRepository

/**
 * Created by minhao on 4/24/14.
 */
public interface CurrencyRepository extends BaseRepository<com.junbo.identity.spec.v1.model.Currency, CurrencyId> {
    @ReadMethod
    Promise<Results<com.junbo.identity.spec.v1.model.Currency>> searchAll(Integer limit, Integer offset)
}