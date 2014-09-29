package com.junbo.store.clientproxy.catalog
import com.junbo.catalog.spec.enums.ItemType
import com.junbo.catalog.spec.enums.OfferAttributeType
import com.junbo.catalog.spec.enums.PriceType
import com.junbo.catalog.spec.enums.Status
import com.junbo.catalog.spec.model.attribute.*
import com.junbo.catalog.spec.model.item.Item
import com.junbo.catalog.spec.model.item.ItemRevision
import com.junbo.catalog.spec.model.item.ItemRevisionGetOptions
import com.junbo.catalog.spec.model.item.ItemRevisionsGetOptions
import com.junbo.catalog.spec.model.offer.ItemEntry
import com.junbo.catalog.spec.model.offer.OfferRevision
import com.junbo.catalog.spec.model.offer.OfferRevisionGetOptions
import com.junbo.catalog.spec.model.offer.OffersGetOptions
import com.junbo.common.enumid.LocaleId
import com.junbo.common.error.AppCommonErrors
import com.junbo.common.id.ItemId
import com.junbo.common.id.OrganizationId
import com.junbo.common.model.Results
import com.junbo.identity.spec.v1.model.Organization
import com.junbo.identity.spec.v1.option.model.OrganizationGetOptions
import com.junbo.langur.core.promise.Promise
import com.junbo.store.clientproxy.ResourceContainer
import com.junbo.store.clientproxy.casey.CaseyFacade
import com.junbo.store.clientproxy.error.AppErrorUtils
import com.junbo.store.clientproxy.error.ErrorCodes
import com.junbo.store.clientproxy.utils.ItemBuilder
import com.junbo.store.common.utils.CommonUtils
import com.junbo.store.spec.exception.casey.CaseyException
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
import org.springframework.util.StringUtils

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

    @Resource(name = 'storeItemBuilder')
    private ItemBuilder itemBuilder

    @Resource(name = 'storeAppErrorUtils')
    private AppErrorUtils appErrorUtils

    @Override
    Promise<Item> getItem(ItemId itemId, ApiContext apiContext) {
        ItemData itemData = new ItemData()
        itemData.genres = []
        Item catalogItem
        Promise.pure().then {
            resourceContainer.itemResource.getItem(itemId.value).recover { Throwable ex ->
                if (appErrorUtils.isAppError(ex, ErrorCodes.Catalog.ResourceNotFound)) {
                    throw AppCommonErrors.INSTANCE.resourceNotFound('Item', itemId).exception()
                }
                throw ex
            }.then { Item e ->
                catalogItem = e
                itemData.item = e
                return Promise.pure()
            }
        }.then { // get developer
            getOrganization(itemData.item?.ownerId).then { Organization organization ->
                itemData.developer = organization
                return Promise.pure()
            }
        }.then {  // get genres
            Promise.each(catalogItem.genres) { String genresId ->
                getItemAttribute(genresId, apiContext).then { ItemAttribute itemAttribute ->
                    if (itemAttribute != null) {
                        itemData.genres << itemAttribute
                    }
                    return Promise.pure()
                }
            }
        }.then {
            if (catalogItem.currentRevisionId == null) {
                return Promise.pure()
            }
            resourceContainer.itemRevisionResource.getItemRevision(catalogItem.currentRevisionId, new ItemRevisionGetOptions(locale: apiContext.locale.getId().value)).then { ItemRevision e ->
                itemData.currentRevision = e
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
                getOfferData(offerResults.items[0], apiContext).then { OfferData offerData ->
                    itemData.offer = offerData
                    return Promise.pure()
                }
            }
        }.then {
            return getCaseyData(catalogItem.getId(), apiContext).then { CaseyData caseyData ->
                itemData.caseyData = caseyData
                return Promise.pure()
            }
        }.then {
            return Promise.pure(itemBuilder.buildItem(itemData, apiContext))
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

    @Override
    Promise<ItemRevision> getAppItemRevision(ItemId itemId, Integer versionCode, ApiContext apiContext) {
        if (versionCode == null) {
            return resourceContainer.itemResource.getItem(itemId.value).then { Item catalogItem ->
                return resourceContainer.itemRevisionResource.getItemRevision(catalogItem.currentRevisionId,
                        new ItemRevisionGetOptions(locale: apiContext.locale.getId().value)).then { ItemRevision itemRevision ->
                    return Promise.pure(itemRevision)
                }
            }
        }

        ItemRevisionsGetOptions options = new ItemRevisionsGetOptions(status: Status.APPROVED.name(),
                itemIds: [itemId.value] as Set, locale: apiContext.locale.getId().value)
        ItemRevision result
        CommonUtils.loop {
            resourceContainer.itemRevisionResource.getItemRevisions(options).then { Results<ItemRevision> itemRevisionResults ->
                result = itemRevisionResults?.items?.find { ItemRevision e ->
                    return itemBuilder.getVersionCode(e?.binaries?.get(apiContext.platform.value)) == versionCode
                }
                if (result != null) {
                    return Promise.pure(Promise.BREAK)
                }

                String cursor = CommonUtils.getQueryParam(itemRevisionResults?.next?.href, 'cursor')
                if (StringUtils.isEmpty(cursor) || CollectionUtils.isEmpty(itemRevisionResults?.items)) {
                    return Promise.pure(Promise.BREAK)
                }
                options.cursor = cursor
                return Promise.pure()
            }
        }.then {
            return Promise.pure(result)
        }
    }

    @Override
    Promise<OfferAttribute> getOfferCategoryByName(String name, LocaleId locale) {
        String cursor
        OfferAttribute result
        CommonUtils.loop {
            resourceContainer.offerAttributeResource.getAttributes(new OfferAttributesGetOptions(attributeType: OfferAttributeType.CATEGORY.name(), cursor: cursor)).then { Results<OfferAttribute> results ->
                result = results.items.find { OfferAttribute offerAttribute ->
                    return offerAttribute.locales?.get(locale.value)?.name == name
                }
                if (result != null) {
                    return Promise.pure(Promise.BREAK)
                }

                cursor = CommonUtils.getQueryParam(results.next?.href, 'cursor')
                if (results.items.isEmpty() || StringUtils.isEmpty(cursor)) {
                    return Promise.pure(Promise.BREAK)
                }
                return Promise.pure()
            }
        }.then {
            return Promise.pure(result)
        }
    }

    @Override
    public Promise<OfferAttribute> getOfferAttribute(String attributeId, ApiContext apiContext) {
        Promise.pure().then {
            resourceContainer.offerAttributeResource.getAttribute(attributeId, new OfferAttributeGetOptions(locale: apiContext.locale.getId().value))
        }.recover { Throwable ex ->
            LOGGER.error('name=Store_Get_OfferAttribute_Fail, attribute={}', attributeId, ex)
            return Promise.pure()
        }
    }

    private Promise<OfferData> getOfferData(com.junbo.catalog.spec.model.offer.Offer catalogOffer, ApiContext apiContext) {
        OfferData result = new OfferData()
        result.offer = catalogOffer
        result.categories = []
        Promise.pure().then { // get offer revision
            if (catalogOffer.currentRevisionId == null) {
                return Promise.pure()
            }
            resourceContainer.offerRevisionResource.getOfferRevision(catalogOffer.currentRevisionId, new OfferRevisionGetOptions(locale: apiContext.locale.getId().value)).then { OfferRevision e ->
                result.offerRevision = e
                return Promise.pure()
            }
        }.then {
            if (!result.offerRevision?.countries?.get(apiContext.country.getId().value)?.isPurchasable) { // return null if offer not purchasable from the country
                return Promise.pure()
            }
            // get categories
            Promise.each(catalogOffer.categories) { String categoryId ->
                getOfferAttribute(categoryId, apiContext).then { OfferAttribute offerAttribute ->
                    if (offerAttribute != null) {
                        result.categories << offerAttribute
                    }
                    return Promise.pure()
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
    }

    private Promise<CaseyData> getCaseyData(String itemId, ApiContext apiContext) {
        CaseyData result = new CaseyData()
        caseyFacade.getAggregatedRatings(new ItemId(itemId), apiContext).recover { Throwable ex ->
            if (ex instanceof CaseyException) {
                LOGGER.error('name=GetCaseyData_Get_AggregateRatings_Error', ex)
                return Promise.pure()
            }
            throw ex
        }.then { Map<String, AggregatedRatings> aggregatedRatings ->
            result.aggregatedRatings = aggregatedRatings
            return Promise.pure(result)
        }
    }

    private Promise<Organization> getOrganization(OrganizationId organizationId) {
        if (organizationId == null) {
            return Promise.pure()
        }
        Promise.pure().then {
            resourceContainer.organizationResource.get(organizationId, new OrganizationGetOptions())
        }.recover { Throwable ex ->
            LOGGER.error('name=Store_Get_Organization_Fail, organization={}', organizationId, ex)
            return Promise.pure()
        }
    }

    private Promise<ItemAttribute> getItemAttribute(String attributeId, ApiContext apiContext) {
        Promise.pure().then {
            resourceContainer.itemAttributeResource.getAttribute(attributeId, new ItemAttributeGetOptions(locale: apiContext.locale.getId().value))
        }.recover { Throwable ex ->
            LOGGER.error('name=Store_Get_ItemAttribute_Fail, attribute={}', attributeId, ex)
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
