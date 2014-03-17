package com.junbo.order.clientproxy.cache

import com.junbo.catalog.spec.model.offer.Offer
import com.junbo.common.id.OfferId
import com.junbo.langur.core.promise.Promise
import com.junbo.order.clientproxy.catalog.CatalogFacade
import groovy.transform.CompileStatic
import net.sf.ehcache.Cache
import net.sf.ehcache.CacheManager
import net.sf.ehcache.Element
import net.sf.ehcache.Status
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.cache.ehcache.EhCacheCacheManager

/**
 * Implementation of catalog facade with cache
 */
@CompileStatic
class CachedCatalogFacadeImpl implements CatalogFacade {

    @Autowired
    CatalogFacade catalogFacade

    CacheManager cacheManager

    CachedCatalogFacadeImpl(EhCacheCacheManager ehCacheCacheManager) {
        cacheManager = ehCacheCacheManager.cacheManager
    }

    @Override
    Promise<Offer> getOffer(Long offerId) {
        if (!cacheManager.status == Status.STATUS_ALIVE || offerId == null) {
            return null
        }

        Cache cache = cacheManager.getCache('OFFER')
        if (cache == null) {
            return null
        }
        Element element = cache.get(offerId.toString())
        if (element != null) {
            return Promise.pure((Offer)element.objectValue)
        }

        def offerPromise = catalogFacade.getOffer(offerId)
        offerPromise.syncThen( new Promise.Func <Offer, Promise>() {
            @Override
            Promise apply(Offer offer) {
                Element newElement = new Element(offerId.toString(), offer)
                cache.put(newElement)
                return Promise.pure(offer)
            }
        } )
        return offerPromise

    }

    @Override
    Promise<List<Offer>> getOffers(List<OfferId> offerIds) {
        return null
    }
}
