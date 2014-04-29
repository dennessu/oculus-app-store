package com.junbo.identity.data.repository

import com.junbo.common.enumid.CurrencyId
import com.junbo.identity.spec.v1.option.list.CurrencyListOptions
import com.junbo.langur.core.promise.Promise
import com.junbo.sharding.core.annotations.ReadMethod

/**
 * Created by minhao on 4/24/14.
 */
public interface CurrencyRepository extends IdentityBaseRepository<com.junbo.identity.spec.v1.model.Currency, CurrencyId> {
    @ReadMethod
    Promise<List<com.junbo.identity.spec.v1.model.Currency>> search(CurrencyListOptions options)
}