package com.junbo.identity.data.repository.impl.cloudant
import com.junbo.common.cloudant.CloudantClient
import com.junbo.common.cloudant.model.CloudantViewQueryOptions
import com.junbo.common.enumid.CountryId
import com.junbo.common.id.TosId
import com.junbo.common.model.Results
import com.junbo.identity.data.repository.TosRepository
import com.junbo.identity.spec.v1.model.Tos
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic
/**
 * Created by haomin on 14-4-10.
 */
@CompileStatic
class TosRepositoryCloudantImpl extends CloudantClient<Tos> implements TosRepository {

    @Override
    Promise<Tos> get(TosId tosId) {
        return cloudantGet(tosId.toString())
    }

    @Override
    Promise<Tos> create(Tos tos) {
        return cloudantPost(tos)
    }

    @Override
    Promise<Void> delete(TosId tosId) {
        return cloudantDelete(tosId.toString())
    }

    @Override
    Promise<Tos> update(Tos model, Tos oldModel) {
        return cloudantPut(model, oldModel)
    }

    @Override
    Promise<List<Tos>> searchAll(Integer limit, Integer offset) {
        return cloudantGetAll(new CloudantViewQueryOptions(
                limit: limit,
                skip: offset
        )).then { Results<Tos> results ->
            return Promise.pure(results.items)
        }
    }

    @Override
    Promise<List<Tos>> searchByType(String type, Integer limit, Integer offset) {
        return super.queryView('by_type', type, limit, offset, false)
    }

    @Override
    Promise<List<Tos>> searchByState(String state, Integer limit, Integer offset) {
        return super.queryView('by_state', state, limit, offset, false)
    }

    @Override
    Promise<List<Tos>> searchByCountry(CountryId country, Integer limit, Integer offset) {
        return super.queryView('by_country', country.toString(), limit, offset, false)
    }

    @Override
    Promise<List<Tos>> searchByTypeAndState(String type, String state, Integer limit, Integer offset) {
        def startKey = [type, state]
        def endKey = [type, state]
        return queryView('by_type_state', startKey.toArray(new String()), endKey.toArray(new String()), false, limit,
                offset, false)
    }

    @Override
    Promise<List<Tos>> searchByTypeAndCountry(String type, CountryId country, Integer limit, Integer offset) {
        def startKey = [type, country.toString()]
        def endKey = [type, country.toString()]
        return queryView('by_type_country', startKey.toArray(new String()), endKey.toArray(new String()), false, limit,
                offset, false)
    }

    @Override
    Promise<List<Tos>> searchByStateAndCountry(String state, CountryId country, Integer limit, Integer offset) {
        def startKey = [state, country.toString()]
        def endKey = [state, country.toString()]
        return queryView('by_state_country', startKey.toArray(new String()), endKey.toArray(new String()), false, limit,
                offset, false)
    }

    @Override
    Promise<List<Tos>> searchByTypeAndStateAndCountry(String type, String state, CountryId country, Integer limit, Integer offset) {
        def startKey = [type, state, country.toString()]
        def endKey = [type, state, country.toString()]
        return queryView('by_type_state_country', startKey.toArray(new String()), endKey.toArray(new String()), false, limit,
                offset, false)
    }
}
