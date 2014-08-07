package com.junbo.store.clientproxy.catalog

import com.junbo.catalog.spec.enums.ItemType
import com.junbo.catalog.spec.model.offer.ItemEntry
import com.junbo.catalog.spec.model.offer.OfferRevision
import com.junbo.catalog.spec.model.offer.OfferRevisionGetOptions
import com.junbo.catalog.spec.resource.ItemResource
import com.junbo.catalog.spec.resource.OfferResource
import com.junbo.catalog.spec.resource.OfferRevisionResource
import com.junbo.common.enumid.LocaleId
import com.junbo.common.model.Results
import com.junbo.entitlement.common.lib.CommonUtils
import com.junbo.langur.core.promise.Promise
import com.junbo.store.spec.model.browse.document.AppDetails
import com.junbo.store.spec.model.browse.document.Document
import com.junbo.store.spec.model.browse.document.DocumentDetails
import com.junbo.store.spec.model.browse.document.DocumentType
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
        Offer result = new Offer()
        offerResource.getOffer(offerId).then { com.junbo.catalog.spec.model.offer.Offer cof -> // todo fill other fields
            catalogOffer = cof
            offerRevisionResource.getOfferRevision(catalogOffer.currentRevisionId, new OfferRevisionGetOptions(locale: locale?.value)).then { OfferRevision it ->
                offerRevision = it
                return Promise.pure(null)
            }
        }.then {
            result.setId(offerId)
            Promise.each(offerRevision.items) { ItemEntry itemEntry ->
                itemResource.getItem(itemEntry.itemId).then { com.junbo.catalog.spec.model.item.Item catalogItem ->
                    if (catalogItem.type == ItemType.STORED_VALUE.name()) {
                        result.hasStoreValueItem = true
                    }
                    return Promise.pure(null)
                }
            }
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
}
