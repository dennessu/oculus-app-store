package com.junbo.identity.data.repository.impl.cloudant

import com.junbo.common.cloudant.CloudantClient
import com.junbo.common.cloudant.model.CloudantViews
import com.junbo.common.enumid.CountryId
import com.junbo.identity.data.repository.CountryRepository
import com.junbo.identity.spec.v1.model.Country
import com.junbo.identity.spec.v1.option.list.CountryListOptions
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic

/**
 * Created by minhao on 4/24/14.
 */
@CompileStatic
class CountryRepositoryCloudantImpl extends CloudantClient<Country> implements CountryRepository {
    @Override
    protected CloudantViews getCloudantViews() {
        return views
    }

    @Override
    Promise<Country> create(Country model) {
        if (model.id == null) {
            model.id = new CountryId(model.countryCode)
        }
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

    @Override
    Promise<List<Country>> search(CountryListOptions options) {
        if (options.currencyId != null && options.localeId != null) {
            List list = super.queryView('by_default_currency', options.currencyId.value)
            list.removeAll { Country country ->
                return country.defaultLocale != options.localeId
            }
            return Promise.pure(list)
        }

        if (options.currencyId != null) {
            return Promise.pure(super.queryView('by_default_currency', options.currencyId.value))
        }

        if (options.localeId != null) {
            return Promise.pure(super.queryView('by_default_locale', options.localeId.value))
        }

        return Promise.pure(super.cloudantGetAll())
    }

    protected CloudantViews views = new CloudantViews(
            views: [
                    'by_default_currency': new CloudantViews.CloudantView(
                            map: 'function(doc) {' +
                                    'emit(doc.defaultCurrency.value, doc._id)' +
                                  '}',
                            resultClass: String),
                    'by_default_locale': new CloudantViews.CloudantView(
                            map: 'function(doc) {' +
                                    'emit(doc.defaultLocale.value, doc._id)' +
                                  '}',
                            resultClass: String)
            ]
    )
}
