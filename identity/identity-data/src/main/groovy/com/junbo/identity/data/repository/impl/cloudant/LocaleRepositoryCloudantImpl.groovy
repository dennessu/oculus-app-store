package com.junbo.identity.data.repository.impl.cloudant
import com.junbo.common.cloudant.CloudantClient
import com.junbo.common.cloudant.model.CloudantViews
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
    protected CloudantViews getCloudantViews() {
        return null
    }

    @Override
    Promise<Locale> create(Locale model) {
        if (model.id == null) {
            model.id = new LocaleId(model.localeCode)
        }

        return super.cloudantPost(model)
    }

    @Override
    Promise<Locale> update(Locale model) {
        return super.cloudantPut(model)
    }

    @Override
    Promise<Locale> get(LocaleId id) {
        return super.cloudantGet(id.toString())
    }

    @Override
    Promise<Void> delete(LocaleId id) {
        return super.cloudantDelete(id.value)
    }

    @Override
    Promise<List<Locale>> searchAll(Integer limit, Integer offset) {
        return super.cloudantGetAll(limit, offset, false)
    }
}
