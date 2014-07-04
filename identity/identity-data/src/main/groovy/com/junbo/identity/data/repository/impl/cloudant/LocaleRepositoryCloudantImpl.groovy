package com.junbo.identity.data.repository.impl.cloudant
import com.junbo.common.cloudant.CloudantClient
import com.junbo.common.enumid.LocaleId
import com.junbo.identity.data.repository.LocaleRepository
import com.junbo.identity.spec.v1.model.Locale
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic
/**
 * Created by minhao on 4/24/14.
 */
@CompileStatic
class LocaleRepositoryCloudantImpl extends CloudantClient<Locale> implements LocaleRepository {
    @Override
    Promise<Locale> create(Locale model) {
        if (model.id == null) {
            model.id = new LocaleId(model.localeCode)
        }

        return cloudantPost(model)
    }

    @Override
    Promise<Locale> update(Locale model) {
        return cloudantPut(model)
    }

    @Override
    Promise<Locale> get(LocaleId id) {
        return cloudantGet(id.toString())
    }

    @Override
    Promise<Void> delete(LocaleId id) {
        return cloudantDelete(id.toString())
    }

    @Override
    Promise<List<Locale>> searchAll(Integer limit, Integer offset) {
        return cloudantGetAll(limit, offset, false)
    }
}
