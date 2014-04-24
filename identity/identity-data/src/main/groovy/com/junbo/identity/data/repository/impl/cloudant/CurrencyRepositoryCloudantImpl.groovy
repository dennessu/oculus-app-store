package com.junbo.identity.data.repository.impl.cloudant

import com.junbo.common.cloudant.CloudantClient
import com.junbo.common.cloudant.model.CloudantViews
import com.junbo.common.enumid.CurrencyId
import com.junbo.identity.data.repository.CurrencyRepository
import com.junbo.langur.core.promise.Promise
import com.junbo.identity.spec.v1.model.Currency

/**
 * Created by minhao on 4/24/14.
 */
class CurrencyRepositoryCloudantImpl extends CloudantClient<Currency> implements CurrencyRepository {
    @Override
    protected CloudantViews getCloudantViews() {
        return null
    }

    @Override
    Promise<Currency> create(Currency model) {
        return Promise.pure((Currency)super.cloudantPost(model))
    }

    @Override
    Promise<Currency> update(Currency model) {
        return Promise.pure((Currency)super.cloudantPut(model))
    }

    @Override
    Promise<Currency> get(CurrencyId id) {
        return Promise.pure((Currency)super.cloudantGet(id.toString()))
    }

    @Override
    Promise<Void> delete(CurrencyId id) {
        super.cloudantDelete(id.value)
        return Promise.pure(null)
    }
}
