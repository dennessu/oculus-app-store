package com.junbo.identity.data.repository.impl.cloudant

import com.junbo.common.cloudant.CloudantClient
import com.junbo.common.cloudant.model.CloudantViews
import com.junbo.common.enumid.CountryId
import com.junbo.identity.data.repository.CountryRepository
import com.junbo.identity.spec.v1.model.Country
import com.junbo.langur.core.promise.Promise

/**
 * Created by minhao on 4/24/14.
 */
class CountryRepositoryCloudantImpl extends CloudantClient<Country> implements CountryRepository {
    @Override
    protected CloudantViews getCloudantViews() {
        return null
    }

    @Override
    Promise<Country> create(Country model) {
        return Promise.pure((Country)super.cloudantPost(model))
    }

    @Override
    Promise<Country> update(Country model) {
        return Promise.pure((Country)super.cloudantPut(model))
    }

    @Override
    Promise<Country> get(CountryId id) {
        return Promise.pure((Country)super.cloudantGet(id.toString()))
    }

    @Override
    Promise<Void> delete(CountryId id) {
        super.cloudantDelete(id.value)
        return Promise.pure(null)
    }
}
