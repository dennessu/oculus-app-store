package com.junbo.identity.data.repository.impl.cloudant

import com.junbo.common.cloudant.CloudantClient
import com.junbo.common.enumid.CurrencyId
import com.junbo.common.model.Results
import com.junbo.identity.data.repository.CurrencyRepository
import com.junbo.identity.spec.v1.model.Currency
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic
import net.sf.ehcache.Element

/**
 * Created by minhao on 4/24/14.
 */
@CompileStatic
class CurrencyRepositoryCloudantImpl extends CloudantClient<Currency> implements CurrencyRepository {

    @Override
    Promise<Currency> create(Currency model) {
        if (model.id == null) {
            model.id = new CurrencyId(model.currencyCode)
        }
        return cloudantPost(model).then { Currency currency ->
            return Promise.pure(currency)
        }
    }

    @Override
    Promise<Currency> update(Currency model, Currency oldModel) {
        return cloudantPut(model, oldModel).then { Currency currency ->
            Element element = new Element(currency.getId().value, currency)
            return Promise.pure(currency)
        }
    }

    @Override
    Promise<Currency> get(CurrencyId id) {
        return cloudantGet(id.toString())
    }

    @Override
    Promise<Void> delete(CurrencyId id) {
        return cloudantDelete(id.toString()).then {
            return Promise.pure(null)
        }
    }

    @Override
    Promise<Results<Currency>> searchAll(Integer limit, Integer offset) {
        return cloudantGetAll(limit, offset, false, true)
    }
}
