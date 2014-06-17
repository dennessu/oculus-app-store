package com.junbo.identity.data.repository.impl.cloudant
import com.junbo.common.cloudant.CloudantClient
import com.junbo.common.cloudant.model.CloudantViews
import com.junbo.common.enumid.CurrencyId
import com.junbo.identity.data.repository.CurrencyRepository
import com.junbo.identity.spec.v1.model.Currency
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic
/**
 * Created by minhao on 4/24/14.
 */
@CompileStatic
class CurrencyRepositoryCloudantImpl extends CloudantClient<Currency> implements CurrencyRepository {
    @Override
    protected CloudantViews getCloudantViews() {
        return null
    }

    @Override
    Promise<Currency> create(Currency model) {
        if (model.id == null) {
            model.id = new CurrencyId(model.currencyCode)
        }
        return super.cloudantPost(model)
    }

    @Override
    Promise<Currency> update(Currency model) {
        return super.cloudantPut(model)
    }

    @Override
    Promise<Currency> get(CurrencyId id) {
        return super.cloudantGet(id.toString())
    }

    @Override
    Promise<Void> delete(CurrencyId id) {
        return super.cloudantDelete(id.value)
    }

    @Override
    Promise<List<Currency>> searchAll(Integer limit, Integer offset) {
        return super.cloudantGetAll(limit, offset, false)
    }
}
