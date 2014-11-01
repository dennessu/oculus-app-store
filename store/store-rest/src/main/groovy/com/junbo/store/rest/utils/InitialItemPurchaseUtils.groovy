package com.junbo.store.rest.utils

import com.junbo.catalog.spec.enums.EntitlementType
import com.junbo.catalog.spec.enums.PriceType
import com.junbo.catalog.spec.model.offer.*
import com.junbo.common.id.ItemId
import com.junbo.common.id.OfferId
import com.junbo.common.id.UserId
import com.junbo.common.model.Results
import com.junbo.langur.core.promise.Promise
import com.junbo.store.clientproxy.FacadeContainer
import com.junbo.store.clientproxy.ResourceContainer
import com.junbo.store.clientproxy.error.AppErrorUtils
import com.junbo.store.common.cache.Cache
import com.junbo.store.common.utils.CommonUtils
import com.junbo.store.spec.model.ApiContext
import com.junbo.store.spec.model.external.sewer.casey.CaseyResults
import com.junbo.store.spec.model.external.sewer.casey.search.CaseyOffer
import groovy.transform.CompileStatic
import org.apache.commons.lang3.StringUtils
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

    private static final int Page_Size = 10

    private static final Logger LOGGER = LoggerFactory.getLogger(InitialItemPurchaseUtils)

    @Value('${store.initialOffer.cmsPage}')
    private String initialOfferSearchPage

    @Value('${store.initialOffer.cmsSlot}')
    private String initialOfferSearchSlot

    @Resource(name = 'storeFacadeContainer')
    private FacadeContainer facadeContainer

    @Resource(name = 'storeResourceContainer')
    private ResourceContainer resourceContainer

    @Resource(name = 'storeInitialOfferCache')
    private Cache<String, Set<OfferId>> initialOfferCache

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
        Map<OfferId, Set<ItemId>> offerItemsMap = [:] as Map
        Map<ItemId, OfferId> itemOffersMap = [:] as Map
        Set<OfferId> filteredOfferId = [] as Set

        searchInitialOfferIds(apiContext).then { Set<OfferId> offerIds ->
            if (CollectionUtils.isEmpty(offerIds)) {
                return Promise.pure([] as Set)
            }
            // filter out duplicate offers that has same items
            resourceContainer.offerResource.getOffers(new OffersGetOptions(offerIds: new HashSet<String>(offerIds.collect { OfferId e -> return e.value }))).then { Results<Offer> offerResults ->
                Set<String> revisionIds = [] as Set
                offerResults.items.each { Offer offer ->
                    if (offer.currentRevisionId != null) {
                        revisionIds << offer.currentRevisionId
                    }
                }

                if (revisionIds.isEmpty()) {
                    return Promise.pure()
                }

                resourceContainer.offerRevisionResource.getOfferRevisions(new OfferRevisionsGetOptions(revisionIds: revisionIds,
                        locale: apiContext.locale.getId().value)).then { Results<OfferRevision> offerRevisions ->
                    offerRevisions.items.each { OfferRevision offerRevision ->
                        if (offerRevision.items.isEmpty() || offerRevision.price?.priceType != PriceType.FREE.name()) {
                            return
                        }

                        boolean duplicate = offerRevision.items.any { ItemEntry itemEntry ->
                            itemOffersMap.containsKey(new ItemId(itemEntry.itemId))
                        }

                        OfferId offerId = new OfferId(offerRevision.offerId)
                        if (!duplicate) {
                            Set<ItemId> itemIdSet = [] as Set
                            offerRevision.items.each { ItemEntry itemEntry ->
                                ItemId itemId = new ItemId(itemEntry.itemId)
                                itemIdSet << itemId
                                itemOffersMap[itemId] = offerId
                            }
                            offerItemsMap[offerId] = itemIdSet
                        } else {
                            filteredOfferId << offerId
                        }
                    }

                    return Promise.pure()
                }
            }.then {
                if (!filteredOfferId.isEmpty()) {
                    LOGGER.info('name=InitialApp_DuplicatedOffers_Found, filter out offers:{}', StringUtils.join(filteredOfferId, ','))
                }

                if (newUser) {
                    return Promise.pure(offerItemsMap.keySet())
                }
                facadeContainer.entitlementFacade.checkEntitlements(userId, itemOffersMap.keySet(), EntitlementType.DOWNLOAD).then { Set<ItemId> itemIdsOwned ->
                    itemIdsOwned.each { ItemId itemId ->
                        offerItemsMap.remove(itemOffersMap[itemId])
                    }
                    return Promise.pure(offerItemsMap.keySet())
                }
            }
        }
    }

    private Promise<Set<OfferId>> searchInitialOfferIds(ApiContext apiContext) {
        Set<OfferId> offerIds = initialOfferCache.get(apiContext.country.getId().value)
        if (offerIds != null) {
            return Promise.pure(offerIds)
        }

        offerIds = [] as Set
        String cursor = null
        CommonUtils.loop {
            facadeContainer.caseyFacade.searchRaw(initialOfferSearchPage, initialOfferSearchSlot,
                    cursor, Page_Size, apiContext).then { CaseyResults<CaseyOffer> caseyResults ->
                if (!CollectionUtils.isEmpty(caseyResults?.items)) {
                    caseyResults.items.each { CaseyOffer caseyOffer ->
                        if (caseyOffer?.self != null) {
                            offerIds << caseyOffer.self
                        }
                    }
                }
                cursor = caseyResults?.cursorString
                if (StringUtils.isEmpty(cursor) || CollectionUtils.isEmpty(caseyResults?.items)) {
                    return Promise.pure(Promise.BREAK)
                }
                return Promise.pure()
            }
        }.then {
            initialOfferCache.put(apiContext.country.getId().value, offerIds)
            return Promise.pure(offerIds)
        }
    }
}
