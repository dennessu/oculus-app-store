package com.junbo.order.clientproxy.catalog.impl
import com.junbo.langur.core.promise.Promise
import com.junbo.order.clientproxy.catalog.CatalogFacade
import com.junbo.order.clientproxy.model.Offer
import com.junbo.order.spec.error.AppErrors
import groovy.transform.CompileStatic
import groovy.transform.TypeChecked
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
@TypeChecked
class CachedCatalogFacadeImpl implements CatalogFacade {

    private static final Logger LOGGER = LoggerFactory.getLogger(CachedCatalogFacadeImpl)


    @Autowired
    @Qualifier('orderCatalogFacade')
    CatalogFacade catalogFacade

    CacheManager cacheManager

    CachedCatalogFacadeImpl(EhCacheCacheManager ehCacheCacheManager) {
        cacheManager = ehCacheCacheManager.cacheManager
    }

    @Override
    Promise<Offer> getOfferRevision(String offerId) {

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
                    def offer = (Offer) element.objectValue
                    LOGGER.info('name=Offer_Load_From_Cache. offerId: {}', offer.id)
                    return Promise.pure(offer)
                }
            }
        }

        LOGGER.info('name=Offer_Load_Without_Cache. offerId: {}', offerId)
        def offerPromise = catalogFacade.getOfferRevision(offerId)
        return offerPromise.syncRecover { Throwable throwable ->
            LOGGER.error('name=Offer_Not_Found. offerId: {}', offerId, throwable)
            throw AppErrors.INSTANCE.offerNotFound(offerId.toString()).exception()
        }.syncThen { Offer or ->
            if (or == null) {
                LOGGER.error('name=Offer_Null. offerId: {}', offerId)
                throw AppErrors.INSTANCE.offerNotFound(offerId.toString()).exception()
            }
            Element newElement = new Element(offerId.toString(), or)
            if (cache != null) {
                LOGGER.info('name=Offer_Cached. offerId: {}', offerId)
                cache.put(newElement)
            }
            return or
        }
    }

    @Override
    Promise<Offer> getOfferRevision(String offerId, Date honoredTime) {
        return catalogFacade.getOfferRevision(offerId, honoredTime)
    }
}
