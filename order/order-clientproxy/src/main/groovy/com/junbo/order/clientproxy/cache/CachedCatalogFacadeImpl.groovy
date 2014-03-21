package com.junbo.order.clientproxy.cache

import com.junbo.catalog.spec.model.offer.Offer
import com.junbo.common.id.OfferId
import com.junbo.langur.core.promise.Promise
import com.junbo.order.clientproxy.catalog.CatalogFacade
import com.junbo.order.spec.error.AppErrors
import groovy.transform.CompileStatic
import net.sf.ehcache.Cache
import net.sf.ehcache.CacheManager
import net.sf.ehcache.Element
import net.sf.ehcache.Status
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.cache.ehcache.EhCacheCacheManager

/**
 * Implementation of catalog facade with cache
 */
@CompileStatic
class CachedCatalogFacadeImpl implements CatalogFacade {

    private final static Logger LOGGER = LoggerFactory.getLogger(CachedCatalogFacadeImpl)

    @Autowired
    @Qualifier('orderCatalogFacade')
    CatalogFacade catalogFacade

    CacheManager cacheManager

    CachedCatalogFacadeImpl(EhCacheCacheManager ehCacheCacheManager) {
        cacheManager = ehCacheCacheManager.cacheManager
    }

    @Override
    Promise<Offer> getOffer(Long offerId) {

        assert (offerId != null)

        Cache cache = null
        if (cacheManager.status != Status.STATUS_ALIVE) {
            LOGGER.error('name=Cache_Manager_Invalid. offerId: {}', offerId)
        } else {
            cache = cacheManager.getCache('OFFER')
            if (cache == null) {
                LOGGER.error('name=Offer_Cache_Manager_Not_Found')
            } else {
                Element element = cache.get(offerId.toString())
                if (element == null) {
                    LOGGER.info('name=Offer_Missing_In_Cache. offerId: {}', offerId)
                } else {
                    LOGGER.info('name=Offer_Load_From_Cache. offerId: {}', offerId)
                    return Promise.pure((Offer) element.objectValue)
                }
            }
        }

        LOGGER.info('name=Offer_Load_Without_Cache. offerId: {}', offerId)
        def offerPromise = catalogFacade.getOffer(offerId)
        return offerPromise.syncRecover { Throwable throwable ->
            LOGGER.error('name=Offer_Not_Found. offerId: {}', offerId, throwable)
            throw AppErrors.INSTANCE.offerNotFound(offerId.toString()).exception()
        }.syncThen { Offer offer ->
            Element newElement = new Element(offerId.toString(), offer)
            if (cache != null) {
                LOGGER.info('name=Offer_Cached. offerId: {}', offerId)
                cache.put(newElement)
            }
            return offer
        }
    }

    @Override
    Promise<Offer> getOffer(Long offerId, Date honoredTime) {
        return catalogFacade.getOffer(offerId, honoredTime)
    }

    @Override
    Promise<List<Offer>> getOffers(List<OfferId> offerIds) {
        return null
    }
}
