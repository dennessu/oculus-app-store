package com.junbo.store.rest.utils

import com.junbo.common.id.ItemId
import com.junbo.common.id.OfferId
import com.junbo.common.id.UserId
import com.junbo.langur.core.context.JunboHttpContext
import com.junbo.langur.core.promise.Promise
import com.junbo.store.clientproxy.FacadeContainer
import com.junbo.store.clientproxy.ResourceContainer
import com.junbo.store.common.utils.CommonUtils
import com.junbo.store.spec.model.ApiContext
import com.junbo.store.spec.model.browse.Images
import com.junbo.store.spec.model.browse.document.Item
import com.junbo.store.spec.model.external.casey.CaseyResults
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

    @Resource(name = 'storeCatalogUtils')
    private CatalogUtils catalogUtils

    @Resource(name = 'storeResourceContainer')
    private ResourceContainer resourceContainer

    Promise checkAndPurchaseInitialOffers(UserId userId, ApiContext apiContext) {
        getInitialItems(apiContext).then { List<Item> items ->
            if (CollectionUtils.isEmpty(items)) {
                return Promise.pure()
            }
            Set<ItemId> itemIdsToCheck = new HashSet<>(items.collect {Item item -> return item.self})
            catalogUtils.checkItemsOwnedByUser(itemIdsToCheck, userId).then { Set<ItemId> itemIdsOwned ->
                List<OfferId> offerIdsToPurchase = items.findAll { Item item -> !itemIdsOwned.contains(item.self)}.collect {Item item -> item.offer.self}.asList()
                if (CollectionUtils.isEmpty(offerIdsToPurchase)) {
                    return Promise.pure()
                }
                facadeContainer.orderFacade.freePurchaseOrder(userId, offerIdsToPurchase, apiContext)
            }
        }.recover { Throwable ex ->
            LOGGER.error('name=Store_Error_CheckAndPurchaseInitialOffers, userId={}', userId, ex)
            return Promise.pure()
        }
    }

    private Promise<List<Item>> getInitialItems(ApiContext apiContext) {
        List<Item> result = []
        String cursor = null
        CommonUtils.loop {
            facadeContainer.caseyFacade.search(initialOfferSearchPage, initialOfferSearchSlot,
                    cursor, Page_Size, Images.BuildType.Item_List, apiContext).then { CaseyResults<Item> caseyResults ->
                if (!CollectionUtils.isEmpty(caseyResults?.items)) {
                    result.addAll(caseyResults.items.findAll {Item item -> item?.offer?.isFree})
                }
                cursor = caseyResults?.cursorString
                if (StringUtils.isEmpty(cursor) || CollectionUtils.isEmpty(caseyResults?.items)) {
                    return Promise.pure(Promise.BREAK)
                }
                return Promise.pure()
            }
        }.then {
            return Promise.pure(result)
        }
    }
}
