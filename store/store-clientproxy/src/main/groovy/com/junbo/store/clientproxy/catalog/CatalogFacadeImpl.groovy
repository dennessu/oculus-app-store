package com.junbo.store.clientproxy.catalog
import com.junbo.catalog.spec.enums.ItemType
import com.junbo.catalog.spec.enums.PriceType
import com.junbo.catalog.spec.model.attribute.ItemAttribute
import com.junbo.catalog.spec.model.attribute.OfferAttribute
import com.junbo.catalog.spec.model.item.Item
import com.junbo.catalog.spec.model.item.ItemRevision
import com.junbo.catalog.spec.model.item.ItemRevisionGetOptions
import com.junbo.catalog.spec.model.offer.ItemEntry
import com.junbo.catalog.spec.model.offer.OfferRevision
import com.junbo.catalog.spec.model.offer.OfferRevisionGetOptions
import com.junbo.catalog.spec.model.offer.OffersGetOptions
import com.junbo.common.enumid.LocaleId
import com.junbo.common.id.ItemId
import com.junbo.common.id.OrganizationId
import com.junbo.common.model.Results
import com.junbo.identity.spec.v1.model.Organization
import com.junbo.identity.spec.v1.option.model.OrganizationGetOptions
import com.junbo.langur.core.promise.Promise
import com.junbo.store.clientproxy.ResourceContainer
import com.junbo.store.clientproxy.casey.CaseyFacade
import com.junbo.store.spec.model.ApiContext
import com.junbo.store.spec.model.browse.document.AggregatedRatings
import com.junbo.store.spec.model.catalog.Offer
import com.junbo.store.spec.model.catalog.data.CaseyData
import com.junbo.store.spec.model.catalog.data.ItemData
import com.junbo.store.spec.model.catalog.data.OfferData
import groovy.transform.CompileStatic
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.util.CollectionUtils

import javax.annotation.Resource
/**
 * The CatalogFacadeImpl class.
 */
@CompileStatic
@Component('storeCatalogFacade')
class CatalogFacadeImpl implements CatalogFacade {

    private final static Logger LOGGER = LoggerFactory.getLogger(CatalogFacadeImpl)

    @Resource(name = 'storeResourceContainer')
    private ResourceContainer resourceContainer

    @Resource(name = 'storeCaseyFacade')
    private CaseyFacade caseyFacade

    @Override
    Promise<ItemData> getItemData(ItemId itemId, ApiContext apiContext) {
        ItemData result = new ItemData()
        result.genres = []
        Item catalogItem
        Promise.pure().then {
            resourceContainer.itemResource.getItem(itemId.value).then { Item e ->
                catalogItem = e
                result.item = e
                return Promise.pure()
            }
        }.then { // get developer
            getOrganization(result.item?.ownerId).then { Organization organization ->
                result.developer = organization
                return Promise.pure()
            }
        }.then {  // get genres
            Promise.each(catalogItem.genres) { String genresId ->
                getItemAttribute(genresId).then { ItemAttribute itemAttribute ->
                    if (itemAttribute != null) {
                        result.genres << itemAttribute
                    }
                    return Promise.pure()
                }
            }
        }.then {
            if (catalogItem.currentRevisionId == null) {
                return Promise.pure()
            }
            resourceContainer.itemRevisionResource.getItemRevision(catalogItem.currentRevisionId, new ItemRevisionGetOptions()).then { ItemRevision e ->
                result.currentRevision = e
                return Promise.pure()
            }
        }.then {
            // get offer
            resourceContainer.offerResource.getOffers(new OffersGetOptions(itemId: catalogItem.itemId)).then { Results<com.junbo.catalog.spec.model.offer.Offer> offerResults ->
                if (offerResults.items.size() > 1) {
                    LOGGER.warn('name=Store_Multiple_Offers_Found, item={}, useOffer={}', catalogItem.itemId, offerResults.items[0].offerId)
                }
                if (CollectionUtils.isEmpty(offerResults.items)) {
                    return Promise.pure()
                }
                getOfferData(offerResults.items[0]).then { OfferData offerData ->
                    result.offer = offerData
                    return Promise.pure()
                }
            }
        }.then {
            return getCaseyData(catalogItem.getId(), apiContext).then { CaseyData caseyData ->
                result.caseyData = caseyData
                return Promise.pure()
            }
        }.then {
            return Promise.pure(result)
        }
    }

    @Override
    Promise<Offer> getOffer(String offerId, LocaleId locale) {
        com.junbo.catalog.spec.model.offer.Offer catalogOffer
        OfferRevision offerRevision
        Offer result = new Offer(hasPhysicalItem: false, hasStoreValueItem: false)
        resourceContainer.offerResource.getOffer(offerId).then { com.junbo.catalog.spec.model.offer.Offer cof -> // todo fill other fields
            catalogOffer = cof
            resourceContainer.offerRevisionResource.getOfferRevision(catalogOffer.currentRevisionId, new OfferRevisionGetOptions(locale: locale?.value)).then { OfferRevision it ->
                offerRevision = it
                loadPriceInfo(offerRevision, result)
                return Promise.pure(null)
            }
        }.then {
            result.setId(offerId)
            Promise.each(offerRevision.items) { ItemEntry itemEntry ->
                resourceContainer.itemResource.getItem(itemEntry.itemId).then { com.junbo.catalog.spec.model.item.Item catalogItem ->
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

    private Promise<OfferData> getOfferData(com.junbo.catalog.spec.model.offer.Offer catalogOffer) {
        OfferData result = new OfferData()
        result.offer = catalogOffer
        result.categories = []
        Promise.pure().then { // get offer revision
            if (catalogOffer.currentRevisionId == null) {
                return Promise.pure()
            }
            resourceContainer.offerRevisionResource.getOfferRevision(catalogOffer.currentRevisionId, new OfferRevisionGetOptions()).then { OfferRevision e ->
                result.offerRevision = e
                return Promise.pure()
            }
        }.then { // get categories
            Promise.each(catalogOffer.categories) { String categoryId ->
                getOfferAttribute(categoryId).then { OfferAttribute offerAttribute ->
                    if (offerAttribute != null) {
                        result.categories << offerAttribute
                    }
                    return Promise.pure()
                }
            }
        }.then { // get publisher
            getOrganization(catalogOffer.ownerId).then { Organization organization->
                result.publisher = organization
                return Promise.pure()
            }
        }.then {
            return Promise.pure(result)
        }
    }

    private Promise<CaseyData> getCaseyData(String itemId, ApiContext apiContext) {
        CaseyData result = new CaseyData()
        caseyFacade.getAggregatedRatings(new ItemId(itemId), apiContext).then { List<AggregatedRatings> aggregatedRatings ->
            result.aggregatedRatings = aggregatedRatings
            return Promise.pure(result)
        }
    }

    private Promise<Organization> getOrganization(OrganizationId organizationId) {
        Promise.pure().then {
            resourceContainer.organizationResource.get(organizationId, new OrganizationGetOptions())
        }.recover { Throwable ex ->
            LOGGER.error('name=Store_Get_Organization_Fail, organization={}', organizationId, ex)
            return Promise.pure()
        }
    }

    private Promise<ItemAttribute> getItemAttribute(String attributeId) {
        Promise.pure().then {
            resourceContainer.itemAttributeResource.getAttribute(attributeId)
        }.recover { Throwable ex ->
            LOGGER.error('name=Store_Get_ItemAttribute_Fail, attribute={}', attributeId, ex)
            return Promise.pure()
        }
    }

    private Promise<OfferAttribute> getOfferAttribute(String attributeId) {
        Promise.pure().then {
            resourceContainer.offerAttributeResource.getAttribute(attributeId)
        }.recover { Throwable ex ->
            LOGGER.error('name=Store_Get_OfferAttribute_Fail, attribute={}', attributeId, ex)
            return Promise.pure()
        }
    }

    private void loadPriceInfo(OfferRevision offerRevision, Offer offer) {
        // todo:    Need to call rating service to get the information
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
