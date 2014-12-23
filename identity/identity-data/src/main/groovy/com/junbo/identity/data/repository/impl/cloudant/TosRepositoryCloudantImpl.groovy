package com.junbo.identity.data.repository.impl.cloudant
import com.junbo.common.cloudant.CloudantClient
import com.junbo.common.enumid.CountryId
import com.junbo.common.id.TosId
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
        return cloudantGetAll(limit, offset, false)
    }

    @Override
    Promise<List<Tos>> searchByTitle(String title, Integer limit, Integer offset) {
        return super.queryView('by_title', title, limit, offset, false)
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
    Promise<List<Tos>> searchByTitleAndType(String title, String type, Integer limit, Integer offset) {
        def startKey = [title, type]
        def endKey = [title, type]
        return queryView('by_title_type', startKey.toArray(new String()), endKey.toArray(new String()), false, limit,
                offset, false)
    }

    @Override
    Promise<List<Tos>> searchByTitleAndState(String title, String state, Integer limit, Integer offset) {
        def startKey = [title, state]
        def endKey = [title, state]
        return queryView('by_title_state', startKey.toArray(new String()), endKey.toArray(new String()), false, limit,
                offset, false)
    }

    @Override
    Promise<List<Tos>> searchByTitleAndCountry(String title, CountryId country, Integer limit, Integer offset) {
        def startKey = [title, country.toString()]
        def endKey = [title, country.toString()]
        return queryView('by_title_country', startKey.toArray(new String()), endKey.toArray(new String()), false, limit,
                offset, false)
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
    Promise<List<Tos>> searchByTitleAndTypeAndState(String title, String type, String state, Integer limit, Integer offset) {
        def startKey = [title, type, state]
        def endKey = [title, type, state]
        return queryView('by_title_type_state', startKey.toArray(new String()), endKey.toArray(new String()), false, limit,
                offset, false)
    }

    @Override
    Promise<List<Tos>> searchByTitleAndTypeAndCountry(String title, String type, CountryId country, Integer limit, Integer offset) {
        def startKey = [title, type, country.toString()]
        def endKey = [title, type, country.toString()]
        return queryView('by_title_type_country', startKey.toArray(new String()), endKey.toArray(new String()), false, limit,
                offset, false)
    }

    @Override
    Promise<List<Tos>> searchByTitleAndStateAndCountry(String title, String state, CountryId country, Integer limit, Integer offset) {
        def startKey = [title, state, country.toString()]
        def endKey = [title, state, country.toString()]
        return queryView('by_title_state_country', startKey.toArray(new String()), endKey.toArray(new String()), false, limit,
                offset, false)
    }

    @Override
    Promise<List<Tos>> searchByTypeAndStateAndCountry(String type, String state, CountryId country, Integer limit, Integer offset) {
        def startKey = [type, state, country.toString()]
        def endKey = [type, state, country.toString()]
        return queryView('by_type_state_country', startKey.toArray(new String()), endKey.toArray(new String()), false, limit,
                offset, false)
    }

    @Override
    Promise<List<Tos>> searchByTitleAndTypeAndStateAndCountry(String title, String type, String state, CountryId country, Integer limit, Integer offset) {
        def startKey = [title, type, state, country.toString()]
        def endKey = [title, type, state, country.toString()]
        return queryView('by_title_type_state_country', startKey.toArray(new String()), endKey.toArray(new String()), false, limit,
                offset, true)
    }
}
