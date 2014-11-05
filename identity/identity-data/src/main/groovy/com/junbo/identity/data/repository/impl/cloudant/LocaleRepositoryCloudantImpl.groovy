package com.junbo.identity.data.repository.impl.cloudant

import com.junbo.common.cloudant.CloudantClient
import com.junbo.common.enumid.LocaleId
import com.junbo.common.model.Results
import com.junbo.identity.data.repository.LocaleRepository
import com.junbo.identity.spec.v1.model.Locale
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic
import net.sf.ehcache.Element

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

        return cloudantPost(model).then { Locale locale ->
            return Promise.pure(locale)
        }
    }

    @Override
    Promise<Locale> update(Locale model, Locale oldModel) {
        return cloudantPut(model, oldModel).then { Locale locale ->
            Element element = new Element(locale.getId().value, locale)
            return Promise.pure(locale)
        }
    }

    @Override
    Promise<Locale> get(LocaleId id) {
        return cloudantGet(id.toString()).then { Locale locale ->
            if (locale == null) {
                return Promise.pure(null)
            }

            return Promise.pure(locale)
        }
    }

    @Override
    Promise<Void> delete(LocaleId id) {
        return cloudantDelete(id.toString()).then {
            return Promise.pure(null)
        }
    }

    @Override
    Promise<Results<Locale>> searchAll(Integer limit, Integer offset) {
        return cloudantGetAll(limit, offset, false, true)
    }
}
