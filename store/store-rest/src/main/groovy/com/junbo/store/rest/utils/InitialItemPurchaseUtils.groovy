package com.junbo.store.rest.utils
import com.junbo.catalog.spec.enums.EntitlementType
import com.junbo.common.id.ItemId
import com.junbo.common.id.OfferId
import com.junbo.common.id.UserId
import com.junbo.langur.core.promise.Promise
import com.junbo.store.clientproxy.FacadeContainer
import com.junbo.store.clientproxy.ResourceContainer
import com.junbo.store.clientproxy.error.AppErrorUtils
import com.junbo.store.common.cache.Cache
import com.junbo.store.spec.model.ApiContext
import com.junbo.store.spec.model.external.sewer.casey.CaseyResults
import com.junbo.store.spec.model.external.sewer.casey.search.CaseyItem
import com.junbo.store.spec.model.external.sewer.casey.search.CaseyOffer
import groovy.transform.CompileStatic
import org.apache.commons.lang.StringUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.util.CollectionUtils

import javax.annotation.Resource
/**
 * The InitialItemPurchaseUtils class.
 */
@CompileStatic
@Component('storeInitialItemPurchaseUtils')
class InitialItemPurchaseUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(InitialItemPurchaseUtils)

    @Value('${store.initialOffer.cmsPage}')
    private String initialOfferSearchPage

    @Value('${store.initialOffer.limit}')
    private int initialOfferLimit

    @Value('${store.initialOffer.cmsSlot}')
    private String initialOfferSearchSlot

    @Resource(name = 'storeFacadeContainer')
    private FacadeContainer facadeContainer

    @Resource(name = 'storeResourceContainer')
    private ResourceContainer resourceContainer

    @Resource(name = 'storeInitialOfferCache')
    private Cache<String, Map<ItemId, OfferId>> initialOfferCache

    @Resource(name = 'storeAppErrorUtils')
    private AppErrorUtils appErrorUtils

    Promise checkAndPurchaseInitialOffers(UserId userId, boolean newUser, ApiContext apiContext) {
        getPurchaseOfferIds(userId, newUser, apiContext).then { Set<OfferId> offerIdSet ->
            if (offerIdSet.isEmpty()) {
                return Promise.pure()
            }
            facadeContainer.orderFacade.freePurchaseOrder(userId, new ArrayList<OfferId>(offerIdSet), apiContext)
        }.recover { Throwable ex ->
            LOGGER.error('name=Store_Error_CheckAndPurchaseInitialOffers, userId={}', userId, ex)
            return Promise.pure()
        }
    }

    private Promise<Set<OfferId>> getPurchaseOfferIds(UserId userId, boolean newUser, ApiContext apiContext) {
        Set<OfferId> results = [] as Set

        searchInitialOfferIds(apiContext).then { Map<ItemId, OfferId> itemIdOfferIdMap ->
            results.addAll(itemIdOfferIdMap.values())
            if (results.isEmpty() || newUser) {
                return Promise.pure(results)
            }

            facadeContainer.entitlementFacade.checkEntitlements(userId, itemIdOfferIdMap.keySet(), EntitlementType.DOWNLOAD).then { Set<ItemId> itemIdsOwned ->
                itemIdsOwned.each { ItemId itemId ->
                    results.remove(itemIdOfferIdMap[itemId])
                }
                return Promise.pure(results)
            }
        }
    }

    private Promise<Map<ItemId, OfferId>> searchInitialOfferIds(ApiContext apiContext) {
        Map<ItemId, OfferId> itemIdOfferIdMap = initialOfferCache.get(apiContext.country.getId().value)
        if (itemIdOfferIdMap != null) {
            return Promise.pure(itemIdOfferIdMap)
        }

        itemIdOfferIdMap = [:] as Map
        facadeContainer.caseyFacade.searchRaw(initialOfferSearchPage, initialOfferSearchSlot,
                null, initialOfferLimit + 1, apiContext).then { CaseyResults<CaseyOffer> caseyResults ->
            if (caseyResults?.items != null && caseyResults.items.size() > initialOfferLimit) {
                LOGGER.warn('name=Too_Many_Initial_Offers')
                caseyResults.items = caseyResults.items.subList(0, initialOfferLimit)
            }

            Set<OfferId> filtered = [] as Set
            if (!CollectionUtils.isEmpty(caseyResults.items)) {
                caseyResults.items.each { CaseyOffer caseyOffer ->
                    if (CollectionUtils.isEmpty(caseyOffer.items) || (!caseyOffer.price.isFree)) {
                        return
                    }

                    boolean hasDuplicateItem = containsDuplicateItem(caseyOffer) || caseyOffer.items.any { CaseyItem caseyItem ->
                        itemIdOfferIdMap.containsKey(caseyItem.self)
                    }

                    if (!hasDuplicateItem) {
                        caseyOffer.items.each { CaseyItem caseyItem ->
                            itemIdOfferIdMap[caseyItem.self] = caseyOffer.self
                        }
                    } else {
                        filtered << caseyOffer.self
                    }
                }
            }

            if (!filtered.isEmpty()) {
                LOGGER.warn('name=InitialApp_DuplicatedOffers_Found, filter out offers:{}', StringUtils.join(filtered, ','))
            }

            initialOfferCache.put(apiContext.country.getId().value, itemIdOfferIdMap)
            return Promise.pure(itemIdOfferIdMap)
        }
    }

    private boolean containsDuplicateItem(CaseyOffer caseyOffer) {
        Set<ItemId> itemIdSet = [] as Set
        for (CaseyItem caseyItem : caseyOffer.items) {
            if (itemIdSet.contains(caseyItem.self)) {
                return true
            }
            itemIdSet << caseyItem.self
        }
        return false
    }
}
