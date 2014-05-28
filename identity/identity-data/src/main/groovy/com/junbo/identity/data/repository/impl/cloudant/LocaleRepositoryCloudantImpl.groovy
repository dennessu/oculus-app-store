package com.junbo.identity.data.repository.impl.cloudant

import com.junbo.common.cloudant.CloudantClient
import com.junbo.common.cloudant.model.CloudantViews
import com.junbo.common.enumid.LocaleId
import com.junbo.identity.data.repository.LocaleRepository
import com.junbo.identity.spec.v1.model.Locale
import com.junbo.identity.spec.v1.option.list.LocaleListOptions
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

        return Promise.pure((Locale)super.cloudantPost(model))
    }

    @Override
    Promise<Locale> update(Locale model) {
        return Promise.pure((Locale)super.cloudantPut(model))
    }

    @Override
    Promise<Locale> get(LocaleId id) {
        return Promise.pure((Locale)super.cloudantGet(id.toString()))
    }

    @Override
    Promise<Void> delete(LocaleId id) {
        super.cloudantDelete(id.value)
        return Promise.pure(null)
    }

    @Override
    Promise<List<Locale>> searchAll(Integer limit, Integer offset) {
        return Promise.pure(super.cloudantGetAll())
    }
}
