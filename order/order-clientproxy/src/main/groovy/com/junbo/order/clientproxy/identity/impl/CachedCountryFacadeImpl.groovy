package com.junbo.order.clientproxy.identity.impl
import com.junbo.langur.core.promise.Promise
import com.junbo.order.clientproxy.identity.CountryFacade
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
 * Implementation of Country Facade with EH Cache.
 */
@CompileStatic
@TypeChecked
class CachedCountryFacadeImpl implements CountryFacade {

    private static final Logger LOGGER = LoggerFactory.getLogger(CachedCountryFacadeImpl)

    @Resource(name = 'orderCountryFacade')
    CountryFacade countryFacade

    CacheManager cacheManager

    CachedCountryFacadeImpl(EhCacheCacheManager ehCacheCacheManager) {
        cacheManager = ehCacheCacheManager.cacheManager
    }

    @Override
    Promise<com.junbo.identity.spec.v1.model.Country> getCountry(String country) {
        assert (country != null)
        Cache cache = null
        if (cacheManager.status != Status.STATUS_ALIVE) {
            LOGGER.error('name=Cache_Manager_Invalid. country: {}', country)
            return Promise.pure(null)
        }
        else {
            cache = cacheManager.getCache('COUNTRY')
            if (cache == null) {
                LOGGER.error('name=Country_Cache_Manager_Not_Found')
                return Promise.pure(null)
            }
            else {
                Element element = cache.get(country)
                if (element == null) {
                    LOGGER.info('name=Country_Missing_In_Cache. country: {}', country)
                    return countryFacade.getCountry(country)
                            .then { com.junbo.identity.spec.v1.model.Country newCountry ->
                        Element newElement = new Element(country, newCountry)
                        cache.put(newElement)
                        LOGGER.info('name=Country_Cached. country: {}', country)
                        return Promise.pure(newCountry)
                    }
                }
                else {
                    def c = (com.junbo.identity.spec.v1.model.Country) element.objectValue
                    LOGGER.info('name=Country_Load_From_Cache. country: {}', country)
                    return Promise.pure(c)
                }
            }
        }
    }
}
