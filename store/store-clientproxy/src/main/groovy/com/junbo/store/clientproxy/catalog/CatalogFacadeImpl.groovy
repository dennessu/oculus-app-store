package com.junbo.store.clientproxy.catalog
import com.junbo.catalog.spec.enums.ItemType
import com.junbo.catalog.spec.enums.OfferAttributeType
import com.junbo.catalog.spec.enums.PriceType
import com.junbo.catalog.spec.enums.Status
import com.junbo.catalog.spec.model.attribute.*
import com.junbo.catalog.spec.model.common.Price
import com.junbo.catalog.spec.model.item.Item
import com.junbo.catalog.spec.model.item.ItemRevision
import com.junbo.catalog.spec.model.item.ItemRevisionGetOptions
import com.junbo.catalog.spec.model.item.ItemRevisionsGetOptions
import com.junbo.catalog.spec.model.item.ItemsGetOptions
import com.junbo.catalog.spec.model.offer.ItemEntry
import com.junbo.catalog.spec.model.offer.OfferRevision
import com.junbo.catalog.spec.model.offer.OfferRevisionGetOptions
import com.junbo.catalog.spec.model.pricetier.PriceTierGetOptions
import com.junbo.common.enumid.CountryId
import com.junbo.common.enumid.CurrencyId
import com.junbo.common.enumid.LocaleId
import com.junbo.common.id.ItemId
import com.junbo.common.id.ItemRevisionId
import com.junbo.common.id.OfferId
import com.junbo.common.id.OrganizationId
import com.junbo.common.model.Results
import com.junbo.identity.spec.v1.model.Organization
import com.junbo.identity.spec.v1.option.model.OrganizationGetOptions
import com.junbo.langur.core.promise.Promise
import com.junbo.rating.spec.model.priceRating.RatingItem
import com.junbo.store.clientproxy.FacadeContainer
import com.junbo.store.clientproxy.ResourceContainer
import com.junbo.store.clientproxy.casey.CaseyFacade
import com.junbo.store.clientproxy.error.AppErrorUtils
import com.junbo.store.clientproxy.utils.ItemBuilder
import com.junbo.store.clientproxy.utils.ReviewBuilder
import com.junbo.store.common.cache.Cache
import com.junbo.store.common.utils.CommonUtils
import com.junbo.store.spec.error.AppErrors
import com.junbo.store.spec.model.ApiContext
import com.junbo.store.spec.model.browse.document.Offer
import com.junbo.store.spec.model.browse.document.RevisionNote
import groovy.transform.CompileStatic
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.util.Assert
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

    private final int ITEM_REVISION_PAGE_SIZE = 100

    @Value('${store.item.revisionNote.size}')
    private int revisionNoteSize

    @Resource(name = 'storeResourceContainer')
    private ResourceContainer resourceContainer

    @Resource(name = 'storeFacadeContainer')
    private FacadeContainer facadeContainer

    @Resource(name = 'storeCaseyFacade')
    private CaseyFacade caseyFacade

    @Resource(name = 'storeItemBuilder')
    private ItemBuilder itemBuilder

    @Resource(name = 'storeReviewBuilder')
    private ReviewBuilder reviewBuilder

    @Resource(name = 'storeAppErrorUtils')
    private AppErrorUtils appErrorUtils

    @Resource(name = 'storeItemLatestRevisionIdsCache')
    private Cache<ItemId, List<ItemRevisionId>> itemRevisionIdCache


    @Override
    Promise<Offer> getOffer(String offerId, ApiContext apiContext) {
        com.junbo.catalog.spec.model.offer.Offer catalogOffer
        OfferRevision offerRevision
        Offer result = new Offer(hasPhysicalItem: false, hasStoreValueItem: false, self: new OfferId(offerId))
        resourceContainer.offerResource.getOffer(offerId).then { com.junbo.catalog.spec.model.offer.Offer cof -> // todo fill other fields
            catalogOffer = cof
            resourceContainer.offerRevisionResource.getOfferRevision(catalogOffer.currentRevisionId, new OfferRevisionGetOptions(locale: apiContext.locale.getId().value)).then { OfferRevision it ->
                offerRevision = it
                loadPriceInfo(offerRevision, result, apiContext)
                return Promise.pure(null)
            }
        }.then {
            result.self = new OfferId(offerId)
            Promise.each(offerRevision.items) { ItemEntry itemEntry ->
                resourceContainer.itemResource.getItem(itemEntry.itemId).then { com.junbo.catalog.spec.model.item.Item catalogItem ->
                    if (catalogItem.type == ItemType.EWALLET.name()) {
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
    Promise<List<ItemId>> getItemsInOffer(String offerId) {
        List<ItemId> itemIds = [] as List<ItemId>
        resourceContainer.offerResource.getOffer(offerId).then { com.junbo.catalog.spec.model.offer.Offer cof ->
            resourceContainer.offerRevisionResource.getOfferRevision(cof.currentRevisionId, new OfferRevisionGetOptions()).then { OfferRevision it ->
                if (!org.apache.commons.collections.CollectionUtils.isEmpty(it.items)) {
                    it.items.each { ItemEntry itemEntry ->
                        itemIds << new ItemId(itemEntry.itemId)
                    }
                }
                return Promise.pure(itemIds)
            }
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

    @Override
    Promise<Item> getCatalogItemByPackageName(String packageName, Integer versionCode, String signatureHash) {
        ItemsGetOptions option = new ItemsGetOptions(packageName: packageName)
        return resourceContainer.itemResource.getItems(option).then { Results<Item> itemResults ->
            if (itemResults.items.isEmpty()) {
                throw AppErrors.INSTANCE.itemNotFoundWithPackageName().exception()
            }
            return Promise.pure(itemResults.items[0])
        } // todo verify the signatureHash if versionCode & signatureHash is provided
    }

    @Override
    Promise<List<RevisionNote>> getRevisionNotes(ItemId itemId, ApiContext apiContext) {
        getLatestItemRevisions(itemId, apiContext).then { List<ItemRevision> itemRevisionList ->
            List<RevisionNote> revisionNoteList = itemRevisionList.collect { ItemRevision itemRevision ->
                itemBuilder.buildRevisionNote(
                        itemRevision.locales?.get(apiContext.locale.getId().value)?.releaseNotes,
                        itemRevision.binaries?.get(apiContext.platform.value),
                        itemRevision.updatedTime
                )
            }
            return Promise.pure(revisionNoteList)
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

    private void loadPriceInfo(OfferRevision offerRevision, Offer offer, ApiContext apiContext) {
        PriceType priceType = PriceType.valueOf(offerRevision.price.priceType)
        if (priceType == PriceType.FREE) {
            offer.price = null
            offer.currency = null
            offer.isFree = true
            return
        }

        offer.isFree = false
        CurrencyId currency = getCurrency(offerRevision.price, apiContext.country.getId())
        offer.currency = currency
        RatingItem ratingItem = facadeContainer.priceRatingFacade.rateOffer(offer.self, currency, apiContext).get()
        offer.price = ratingItem.finalTotalAmount
    }

    private CurrencyId getCurrency(Price price, CountryId countryId) {
        Map<String, Map<String, BigDecimal>> prices
        PriceType priceType = PriceType.valueOf(price.priceType)
        if (priceType == PriceType.CUSTOM) {
            prices = price.prices
        } else {
            Assert.isTrue(priceType == PriceType.TIERED)
            prices = resourceContainer.priceTierResource.getPriceTier(price.priceTier, new PriceTierGetOptions()).get().prices
        }

        Map<String, BigDecimal> results = prices?.get(countryId.value)
        if (CollectionUtils.isEmpty(results)) {
            throw AppErrors.INSTANCE.priceNotAvailable(countryId?.value).exception()
        }

        return new CurrencyId(results.keySet().first())
    }

    private Promise<List<ItemRevision>> getLatestItemRevisions(ItemId itemId, ApiContext apiContext) {
        List<ItemRevisionId> cached = itemRevisionIdCache.get(itemId)

        if (cached != null) { // direct get revision by revision ids
            Set<String> revisionIdSet = new HashSet<>(cached.collect {ItemRevisionId itemRevisionId -> itemRevisionId.value})
            return resourceContainer.itemRevisionResource.getItemRevisions(new ItemRevisionsGetOptions(locale: apiContext.locale.getId().value,
                revisionIds: revisionIdSet)).then { Results<ItemRevision> itemRevisionResults ->
                Map<ItemRevisionId, ItemRevision> idItemRevisionMap = [:] as Map
                List<ItemRevision> result = [] as List

                itemRevisionResults.items.each { ItemRevision itemRevision ->
                    idItemRevisionMap[new ItemRevisionId(itemRevision.getId())] = itemRevision
                }
                cached.each { ItemRevisionId itemRevisionId ->
                    result << idItemRevisionMap[itemRevisionId]
                }

                return Promise.pure(result)
            }
        }

        String cursor = null
        TreeMap<Integer, ItemRevision> versionCodeToItemRevision = [:] as TreeMap
        CommonUtils.loop { // get top n item revision with highest version code, by getting all approved item revision.
            resourceContainer.itemRevisionResource.getItemRevisions(
                    new ItemRevisionsGetOptions(cursor: cursor, status: Status.APPROVED.name(), size: ITEM_REVISION_PAGE_SIZE,
                            itemIds: [itemId.value] as Set, locale: apiContext.getLocale().getId().value)).then { Results<ItemRevision> itemRevisionResults ->

                itemRevisionResults.items.each { ItemRevision itemRevision ->
                    Integer integer = itemBuilder.getVersionCode(itemRevision?.getBinaries()?.get(apiContext.platform.value))
                    if (integer != null) {
                        ItemRevision exists = versionCodeToItemRevision[integer] as ItemRevision
                        if (exists == null || exists.updatedTime.before(itemRevision.updatedTime)) {
                            versionCodeToItemRevision[integer] = itemRevision
                        }
                        if (versionCodeToItemRevision.size() > revisionNoteSize) {
                            versionCodeToItemRevision.pollFirstEntry()
                        }
                    }
                }

                cursor = CommonUtils.getQueryParam(itemRevisionResults.next?.href, 'cursor')
                if (itemRevisionResults.items.isEmpty() || StringUtils.isEmpty(cursor)) {
                    return Promise.pure(Promise.BREAK)
                }
                return Promise.pure()
            }
        }.then {
            List<ItemRevision> result = [] as List
            versionCodeToItemRevision.reverseEach { Map.Entry<Integer, ItemRevision> entry ->
                result << entry.value
            }
            itemRevisionIdCache.put(itemId, result.collect {ItemRevision itemRevision -> new ItemRevisionId(itemRevision.getId())})
            return Promise.pure(result)
        }
    }
}
