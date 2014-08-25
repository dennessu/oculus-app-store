package com.junbo.store.clientproxy.catalog

import com.junbo.catalog.spec.enums.ItemType
import com.junbo.catalog.spec.enums.PriceType
import com.junbo.catalog.spec.model.offer.ItemEntry
import com.junbo.catalog.spec.model.offer.OfferRevision
import com.junbo.catalog.spec.model.offer.OfferRevisionGetOptions
import com.junbo.catalog.spec.resource.ItemResource
import com.junbo.catalog.spec.resource.OfferResource
import com.junbo.catalog.spec.resource.OfferRevisionResource
import com.junbo.common.enumid.LocaleId
import com.junbo.common.model.Results
import com.junbo.langur.core.promise.Promise
import com.junbo.store.spec.model.browse.document.Document
import com.junbo.store.spec.model.catalog.Item
import com.junbo.store.spec.model.catalog.Offer
import com.junbo.store.spec.model.iap.IAPOfferGetRequest
import groovy.transform.CompileStatic
import org.springframework.stereotype.Component

import javax.annotation.Resource

/**
 * The CatalogFacadeImpl class.
 */
@CompileStatic
@Component('storeCatalogFacade')
class CatalogFacadeImpl implements CatalogFacade {

    @Resource(name = 'store.offerItemClient')
    ItemResource itemResource

    @Resource(name = 'store.offerClient')
    OfferResource offerResource

    @Resource(name = 'store.offerRevisionClient')
    OfferRevisionResource offerRevisionResource

    @Override
    Promise<Item> getItem(String itemId) {
        return null
    }

    @Override
    Promise<Offer> getOffer(String offerId, LocaleId locale) {
        com.junbo.catalog.spec.model.offer.Offer catalogOffer
        OfferRevision offerRevision
        Offer result = new Offer(hasPhysicalItem: false, hasStoreValueItem: false)
        offerResource.getOffer(offerId).then { com.junbo.catalog.spec.model.offer.Offer cof -> // todo fill other fields
            catalogOffer = cof
            offerRevisionResource.getOfferRevision(catalogOffer.currentRevisionId, new OfferRevisionGetOptions(locale: locale?.value)).then { OfferRevision it ->
                offerRevision = it
                loadPriceInfo(offerRevision, result)
                return Promise.pure(null)
            }
        }.then {
            result.setId(offerId)
            Promise.each(offerRevision.items) { ItemEntry itemEntry ->
                itemResource.getItem(itemEntry.itemId).then { com.junbo.catalog.spec.model.item.Item catalogItem ->
                    if (catalogItem.type == ItemType.STORED_VALUE.name()) {
                        result.hasStoreValueItem = true
                    }
                    if (catalogItem.type == ItemType.PHYSICAL.name()) {
                        result.hasPhysicalItem = true
                    }
                    return Promise.pure(null)
                }
            }
        }.then {
            return Promise.pure(result)
        }
    }

    @Override
    Promise<Results<Item>> getItemsByCategory(String categoryId, Long cursor, Long count) {
        return null
    }

    @Override
    Promise<Item> getItemByPackageName(String packageName) {
        return null
    }

    @Override
    Promise<com.junbo.store.spec.model.catalog.Category> getCategory(String categoryId) {
        return null
    }

    @Override
    Promise<List<com.junbo.store.spec.model.catalog.Category>> getCategoriesByParent(String parentId) {
        return null
    }

    @Override
    Promise<List<Offer>> getInAppOffers(Item hostItem, IAPOfferGetRequest request) {
        return null
    }

    @Override
    Promise<Void> fillItemDocument(Item item, Document document) {
        return null
    }

    private void loadPriceInfo(OfferRevision offerRevision, Offer offer) {
        PriceType priceType = PriceType.valueOf(offerRevision.price.priceType)
        switch (priceType) {
            case PriceType.FREE:
                offer.price = null
                offer.currencyCode = null
                offer.isFree = true
                break;
            case PriceType.CUSTOM:
                offer.isFree = false
                break;
            case PriceType.TIERED:
                offer.isFree = false
                break;
            // todo handle non-free prices
        }
    }
}
