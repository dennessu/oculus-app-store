package com.junbo.order.clientproxy.identity.impl
import com.junbo.langur.core.promise.Promise
import com.junbo.order.clientproxy.identity.CurrencyFacade
import groovy.transform.CompileStatic
import groovy.transform.TypeChecked
import net.sf.ehcache.Cache
import net.sf.ehcache.CacheManager
import net.sf.ehcache.Element
import net.sf.ehcache.Status
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.cache.ehcache.EhCacheCacheManager

import javax.annotation.Resource
/**
 * Implementation of Currency Facade with EH Cache.
 */
@CompileStatic
@TypeChecked
class CachedCurrencyFacadeImpl implements CurrencyFacade {

    private static final Logger LOGGER = LoggerFactory.getLogger(CachedCurrencyFacadeImpl)

    @Resource(name = 'orderCurrencyFacade')
    CurrencyFacade currencyFacade

    CacheManager cacheManager

    CachedCurrencyFacadeImpl(EhCacheCacheManager ehCacheCacheManager) {
        cacheManager = ehCacheCacheManager.cacheManager
    }

    @Override
    Promise<com.junbo.identity.spec.v1.model.Currency> getCurrency(String currency) {
        assert (currency != null)
        Cache cache = null
        if (cacheManager.status != Status.STATUS_ALIVE) {
            LOGGER.error('name=Cache_Manager_Invalid. currency: {}', currency)
            return Promise.pure(null)
        }
        else {
            cache = cacheManager.getCache('CURRENCY')
            if (cache == null) {
                LOGGER.error('name=Currency_Cache_Manager_Not_Found')
                return Promise.pure(null)
            }
            else {
                Element element = cache.get(currency)
                if (element == null) {
                    LOGGER.info('name=Currency_Missing_In_Cache. currency: {}', currency)
                    return currencyFacade.getCurrency(currency)
                            .then { com.junbo.identity.spec.v1.model.Currency newCurrency ->
                        Element newElement = new Element(currency, newCurrency)
                        cache.put(newElement)
                        LOGGER.info('name=Currency_Cached. currency: {}', currency)
                        return Promise.pure(newCurrency)
                    }
                }
                else {
                    def c = (com.junbo.identity.spec.v1.model.Currency) element.objectValue
                    LOGGER.info('name=Currency_Load_From_Cache. currency: {}', currency)
                    return Promise.pure(c)
                }
            }
        }
    }
}
