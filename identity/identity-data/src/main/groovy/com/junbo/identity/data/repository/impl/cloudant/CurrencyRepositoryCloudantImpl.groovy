package com.junbo.identity.data.repository.impl.cloudant
import com.junbo.common.cloudant.CloudantClient
import com.junbo.common.enumid.CurrencyId
import com.junbo.identity.data.repository.CurrencyRepository
import com.junbo.identity.spec.v1.model.Currency
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
class CurrencyRepositoryCloudantImpl extends CloudantClient<Currency> implements CurrencyRepository {

    private static final Logger LOGGER = LoggerFactory.getLogger(CurrencyRepositoryCloudantImpl)
    private static final String IDENTITY_CURRENCY_CACHE_NAME = 'IDENTITY_CURRENCY'

    private CacheManager cacheManager

    @Override
    Promise<Currency> create(Currency model) {
        if (model.id == null) {
            model.id = new CurrencyId(model.currencyCode)
        }
        return cloudantPost(model).then { Currency currency ->
            Element element = new Element(currency.getId().value, currency)
            getCurrencyCache().put(element)
            return Promise.pure(currency)
        }
    }

    @Override
    Promise<Currency> update(Currency model, Currency oldModel) {
        return cloudantPut(model, oldModel).then { Currency currency ->
            Element element = new Element(currency.getId().value, currency)
            getCurrencyCache().put(element)
            return Promise.pure(currency)
        }
    }

    @Override
    Promise<Currency> get(CurrencyId id) {
        Element element = getCurrencyCache().get(id.value)
        if (element != null) {
            return Promise.pure((Currency)element.objectValue)
        }
        return cloudantGet(id.toString()).then { Currency currency ->
            element = new Element(currency.getId().value, currency)
            getCurrencyCache().put(element)
            return Promise.pure(currency)
        }
    }

    @Override
    Promise<Void> delete(CurrencyId id) {
        return cloudantDelete(id.toString()).then {
            getCurrencyCache().remove(id.value)
            return Promise.pure(null)
        }
    }

    @Override
    Promise<List<Currency>> searchAll(Integer limit, Integer offset) {
        return cloudantGetAll(limit, offset, false)
    }

    @Required
    void setCacheManager(EhCacheCacheManager ehCacheCacheManager) {
        this.cacheManager = ehCacheCacheManager.cacheManager
    }

    public Cache getCurrencyCache() {
        if (cacheManager.status != Status.STATUS_ALIVE) {
            LOGGER.error('name=Cache_Manager_Invalid.')
            throw new IllegalStateException('Cache Manger Status is invalid')
        } else {
            Cache cache = cacheManager.getCache(IDENTITY_CURRENCY_CACHE_NAME)
            if (cache == null) {
                LOGGER.error('name=Cache_Manager_Invalid. Not exist Cache name : ' + IDENTITY_CURRENCY_CACHE_NAME)
                throw new IllegalStateException('Cache Manger Invalid. Not exist Cache name : ' + IDENTITY_CURRENCY_CACHE_NAME)
            }

            return cache
        }
    }
}
