package com.junbo.identity.data.repository.impl.cloudant
import com.junbo.common.cloudant.CloudantClient
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
    Promise<Results<Tos>> searchAll(Integer limit, Integer offset) {
        return cloudantGetAll(limit, offset, false, true)
    }

    @Override
    Promise<Results<Tos>> searchByTitle(String title, Integer limit, Integer offset) {
        return super.queryViewResults('by_title', title, limit, offset, false)
    }

    @Override
    Promise<Results<Tos>> searchByType(String type, Integer limit, Integer offset) {
        return super.queryViewResults('by_type', type, limit, offset, false)
    }

    @Override
    Promise<Results<Tos>> searchByState(String state, Integer limit, Integer offset) {
        return super.queryViewResults('by_state', state, limit, offset, false)
    }

    @Override
    Promise<Results<Tos>> searchByCountry(CountryId country, Integer limit, Integer offset) {
        return super.queryViewResults('by_country', country.toString(), limit, offset, false)
    }

    @Override
    Promise<Results<Tos>> searchByTitleAndType(String title, String type, Integer limit, Integer offset) {
        def startKey = [title, type]
        def endKey = [title, type]
        return queryViewResults('by_title_type', startKey.toArray(new String()), endKey.toArray(new String()), false, limit,
                offset, false)
    }

    @Override
    Promise<Results<Tos>> searchByTitleAndState(String title, String state, Integer limit, Integer offset) {
        def startKey = [title, state]
        def endKey = [title, state]
        return queryViewResults('by_title_state', startKey.toArray(new String()), endKey.toArray(new String()), false, limit,
                offset, false)
    }

    @Override
    Promise<Results<Tos>> searchByTitleAndCountry(String title, CountryId country, Integer limit, Integer offset) {
        def startKey = [title, country.toString()]
        def endKey = [title, country.toString()]
        return queryViewResults('by_title_country', startKey.toArray(new String()), endKey.toArray(new String()), false, limit,
                offset, false)
    }

    @Override
    Promise<Results<Tos>> searchByTypeAndState(String type, String state, Integer limit, Integer offset) {
        def startKey = [type, state]
        def endKey = [type, state]
        return queryViewResults('by_type_state', startKey.toArray(new String()), endKey.toArray(new String()), false, limit,
                offset, false)
    }

    @Override
    Promise<Results<Tos>> searchByTypeAndCountry(String type, CountryId country, Integer limit, Integer offset) {
        def startKey = [type, country.toString()]
        def endKey = [type, country.toString()]
        return queryViewResults('by_type_country', startKey.toArray(new String()), endKey.toArray(new String()), false, limit,
                offset, false)
    }

    @Override
    Promise<Results<Tos>> searchByStateAndCountry(String state, CountryId country, Integer limit, Integer offset) {
        def startKey = [state, country.toString()]
        def endKey = [state, country.toString()]
        return queryViewResults('by_state_country', startKey.toArray(new String()), endKey.toArray(new String()), false, limit,
                offset, false)
    }

    @Override
    Promise<Results<Tos>> searchByTitleAndTypeAndState(String title, String type, String state, Integer limit, Integer offset) {
        def startKey = [title, type, state]
        def endKey = [title, type, state]
        return queryViewResults('by_title_type_state', startKey.toArray(new String()), endKey.toArray(new String()), false, limit,
                offset, false)
    }

    @Override
    Promise<Results<Tos>> searchByTitleAndTypeAndCountry(String title, String type, CountryId country, Integer limit, Integer offset) {
        def startKey = [title, type, country.toString()]
        def endKey = [title, type, country.toString()]
        return queryViewResults('by_title_type_country', startKey.toArray(new String()), endKey.toArray(new String()), false, limit,
                offset, false)
    }

    @Override
    Promise<Results<Tos>> searchByTitleAndStateAndCountry(String title, String state, CountryId country, Integer limit, Integer offset) {
        def startKey = [title, state, country.toString()]
        def endKey = [title, state, country.toString()]
        return queryViewResults('by_title_state_country', startKey.toArray(new String()), endKey.toArray(new String()), false, limit,
                offset, false)
    }

    @Override
    Promise<Results<Tos>> searchByTypeAndStateAndCountry(String type, String state, CountryId country, Integer limit, Integer offset) {
        def startKey = [type, state, country.toString()]
        def endKey = [type, state, country.toString()]
        return queryViewResults('by_type_state_country', startKey.toArray(new String()), endKey.toArray(new String()), false, limit,
                offset, false)
    }

    @Override
    Promise<Results<Tos>> searchByTitleAndTypeAndStateAndCountry(String title, String type, String state, CountryId country, Integer limit, Integer offset) {
        def startKey = [title, type, state, country.toString()]
        def endKey = [title, type, state, country.toString()]
        return queryViewResults('by_title_type_state_country', startKey.toArray(new String()), endKey.toArray(new String()), false, limit,
                offset, false)
    }
}
