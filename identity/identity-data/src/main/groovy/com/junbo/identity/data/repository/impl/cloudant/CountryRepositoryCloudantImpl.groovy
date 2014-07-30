package com.junbo.identity.data.repository.impl.cloudant
import com.junbo.common.cloudant.CloudantClient
import com.junbo.common.enumid.CountryId
import com.junbo.common.enumid.CurrencyId
import com.junbo.common.enumid.LocaleId
import com.junbo.identity.data.repository.CountryRepository
import com.junbo.identity.spec.v1.model.Country
import com.junbo.langur.core.promise.Promise
import groovy.transform.CompileStatic
import net.sf.ehcache.Cache
import net.sf.ehcache.CacheManager
import net.sf.ehcache.Element
import net.sf.ehcache.Status
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Required
import org.springframework.cache.ehcache.EhCacheCacheManager

/**
 * Created by minhao on 4/24/14.
 */
@CompileStatic
class CountryRepositoryCloudantImpl extends CloudantClient<Country> implements CountryRepository {

    private static final Logger LOGGER = LoggerFactory.getLogger(CountryRepositoryCloudantImpl)
    private static final String IDENTITY_COUNTRY_CACHE_NAME = 'IDENTITY_COUNTRY'

    private CacheManager cacheManager

    @Override
    Promise<Country> create(Country model) {
        if (model.id == null) {
            model.id = new CountryId(model.countryCode)
        }
        return cloudantPost(model).then { Country country ->
            Element element = new Element(country.getId().value, country)
            getCountryCache().put(element)
            return Promise.pure(country)
        }
    }

    @Override
    Promise<Country> update(Country model, Country oldModel) {
        return cloudantPut(model, oldModel).then { Country country ->
            Element element = new Element(country.getId().value, country)
            getCountryCache().put(element)
            return Promise.pure(country)
        }
    }

    @Override
    Promise<Country> get(CountryId id) {
        Element element = getCountryCache().get(id.value)

        if (element != null) {
            return Promise.pure((Country)element.objectValue)
        }

        return cloudantGet(id.toString()).then { Country country ->
            if (country == null) {
                return Promise.pure(null)
            }

            element = new Element(country.getId().value, country)
            getCountryCache().put(element)
            return Promise.pure(country)
        }
    }

    @Override
    Promise<Void> delete(CountryId id) {
        return cloudantDelete(id.toString()).then {
            getCountryCache().remove(id.value)
            return Promise.pure(null)
        }
    }

    @Override
    Promise<List<Country>> searchByDefaultCurrencyId(CurrencyId currencyId, Integer limit, Integer offset) {
        return queryView('by_default_currency', currencyId.toString(), limit, offset, false)
    }

    @Override
    Promise<List<Country>> searchByDefaultLocaleId(LocaleId localeId, Integer limit, Integer offset) {
        return queryView('by_default_locale', localeId.toString(), limit, offset, false)
    }

    @Override
    Promise<List<Country>> searchByDefaultCurrencyIdAndLocaleId(CurrencyId currencyId, LocaleId localeId, Integer limit, Integer offset) {
        def startKey = [currencyId.toString(), localeId.toString()]
        def endKey = [currencyId.toString(), localeId.toString()]
        return queryView('by_default_locale_currency', startKey.toArray(new String()), endKey.toArray(new String()), false, limit, offset, false)
    }

    @Override
    Promise<List<Country>> searchAll(Integer limit, Integer offset) {
        return cloudantGetAll(limit, offset, false)
    }

    @Required
    void setCacheManager(EhCacheCacheManager ehCacheCacheManager) {
        this.cacheManager = ehCacheCacheManager.getCacheManager()
    }

    public Cache getCountryCache() {
        if (cacheManager.status != Status.STATUS_ALIVE) {
            LOGGER.error('name=Cache_Manager_Invalid.')
            throw new IllegalStateException('Cache Manger Status is invalid')
        } else {
            Cache cache = cacheManager.getCache(IDENTITY_COUNTRY_CACHE_NAME)
            if (cache == null) {
                LOGGER.error('name=Cache_Manager_Invalid. Not exist Cache name : ' + IDENTITY_COUNTRY_CACHE_NAME)
                throw new IllegalStateException('Cache Manger Invalid. Not exist Cache name : ' + IDENTITY_COUNTRY_CACHE_NAME)
            }

            return cache
        }
    }
}
