package com.junbo.identity.data.repository.impl.cloudant
import com.junbo.common.cloudant.CloudantClient
import com.junbo.common.cloudant.model.CloudantViews
import com.junbo.common.enumid.CountryId
import com.junbo.common.enumid.CurrencyId
import com.junbo.common.enumid.LocaleId
import com.junbo.identity.data.repository.CountryRepository
import com.junbo.identity.spec.v1.model.Country
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
        return super.cloudantPost(model)
    }

    @Override
    Promise<Country> update(Country model) {
        return super.cloudantPut(model)
    }

    @Override
    Promise<Country> get(CountryId id) {
        return super.cloudantGet(id.toString())
    }

    @Override
    Promise<Void> delete(CountryId id) {
        return super.cloudantDelete(id.value)
    }

    CloudantViews getViews() {
        return views
    }

    void setViews(CloudantViews views) {
        this.views = views
    }

    @Override
    Promise<List<Country>> searchByDefaultCurrencyId(CurrencyId currencyId, Integer limit, Integer offset) {
        return super.queryView('by_default_currency', currencyId.value, limit, offset, false)
    }

    @Override
    Promise<List<Country>> searchByDefaultLocaleId(LocaleId localeId, Integer limit, Integer offset) {
        return super.queryView('by_default_locale', localeId.value, limit, offset, false)
    }

    @Override
    Promise<List<Country>> searchByDefaultCurrencyIdAndLocaleId(CurrencyId currencyId, LocaleId localeId, Integer limit, Integer offset) {
        return super.queryView('by_default_locale_currency', "${currencyId.value}:${localeId.value}", limit, offset, false)
    }

    @Override
    Promise<List<Country>> searchAll(Integer limit, Integer offset) {
        return super.cloudantGetAll()
    }

    protected CloudantViews views = new CloudantViews(
            views: [
                    'by_default_currency': new CloudantViews.CloudantView(
                            map: 'function(doc) {' +
                                    'emit(doc.defaultCurrency, doc._id)' +
                                  '}',
                            resultClass: String),
                    'by_default_locale': new CloudantViews.CloudantView(
                            map: 'function(doc) {' +
                                    'emit(doc.defaultLocale, doc._id)' +
                                  '}',
                            resultClass: String),
                    'by_default_locale_currency': new CloudantViews.CloudantView(
                            map: 'function(doc) {' +
                                    'emit(doc.defaultCurrency + \':\' + doc.defaultLocale, doc._id)' +
                                 '}',
                            resultClass: String)
            ]
    )
}
