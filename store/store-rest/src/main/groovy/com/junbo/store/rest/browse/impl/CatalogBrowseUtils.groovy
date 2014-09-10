package com.junbo.store.rest.browse.impl
import com.junbo.catalog.spec.enums.OfferAttributeType
import com.junbo.catalog.spec.enums.PriceType
import com.junbo.catalog.spec.enums.Status
import com.junbo.catalog.spec.model.attribute.ItemAttribute
import com.junbo.catalog.spec.model.attribute.OfferAttribute
import com.junbo.catalog.spec.model.attribute.OfferAttributesGetOptions
import com.junbo.catalog.spec.model.item.Item
import com.junbo.catalog.spec.model.item.ItemRevision
import com.junbo.catalog.spec.model.item.ItemRevisionGetOptions
import com.junbo.catalog.spec.model.item.ItemRevisionsGetOptions
import com.junbo.catalog.spec.model.offer.OfferRevision
import com.junbo.catalog.spec.model.offer.OfferRevisionGetOptions
import com.junbo.catalog.spec.model.offer.OfferRevisionLocaleProperties
import com.junbo.catalog.spec.model.offer.OffersGetOptions
import com.junbo.common.enumid.LocaleId
import com.junbo.common.error.AppErrorException
import com.junbo.common.id.ItemId
import com.junbo.common.id.OfferId
import com.junbo.common.id.UserId
import com.junbo.common.id.OrganizationId
import com.junbo.common.model.Link
import com.junbo.common.model.Results
import com.junbo.common.util.IdFormatter
import com.junbo.identity.spec.v1.model.Organization
import com.junbo.identity.spec.v1.model.User
import com.junbo.identity.spec.v1.option.model.OrganizationGetOptions
import com.junbo.identity.spec.v1.option.model.UserGetOptions
import com.junbo.langur.core.promise.Promise
import com.junbo.rating.spec.model.priceRating.RatingItem
import com.junbo.rating.spec.model.priceRating.RatingRequest
import com.junbo.store.common.cache.Cache
import com.junbo.store.common.utils.CommonUtils
import com.junbo.store.rest.utils.CatalogUtils
import com.junbo.store.rest.utils.ResourceContainer
import com.junbo.store.spec.model.ApiContext
import com.junbo.store.spec.model.browse.ReviewsResponse
import com.junbo.store.spec.model.browse.document.AggregatedRatings
import com.junbo.store.spec.model.browse.document.Offer
import com.junbo.store.spec.model.browse.document.Review
import com.junbo.store.spec.model.catalog.data.CaseyData
import com.junbo.store.spec.model.catalog.data.ItemData
import com.junbo.store.spec.model.catalog.data.OfferData
import com.junbo.store.spec.model.external.casey.CaseyAggregateRating
import com.junbo.store.spec.model.external.casey.CaseyResults
import com.junbo.store.spec.model.external.casey.CaseyReview
import com.junbo.store.spec.model.external.casey.ReviewSearchParams
import groovy.transform.CompileStatic
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.util.CollectionUtils
import org.springframework.util.StringUtils

import javax.annotation.Resource
/**
 * The CatalogServiceUtils class.
 */
@Component('storeCatalogBrowseUtils')
@CompileStatic
class CatalogBrowseUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(CatalogBrowseUtils)

    @Resource(name = 'storeLocaleUtils')
    private LocaleUtils localeUtils

    @Resource(name = 'storeOfferToItemCache')
    private Cache<OfferId, ItemId> storeOfferToItemCache

    @Resource(name = 'storeItemDataCache')
    private Cache<ItemId, ItemData> storeItemDataCache

    @Resource(name = 'storeItemPriceCache')
    private Cache<Object, Offer> storeItemPriceCache

    @Resource(name = 'storeResourceContainer')
    private ResourceContainer resourceContainer

    @Resource(name = 'storeBrowseDataBuilder')
    private BrowseDataBuilder browseDataBuilder

    @Resource(name = 'storeCatalogUtils')
    private CatalogUtils catalogUtils

    Promise<com.junbo.store.spec.model.browse.document.Item> getItem(ItemId itemId, boolean includeDetails, ApiContext apiContext) {
        Promise.pure().then {
            resourceContainer.itemResource.getItem(itemId.value)
        }.recover { Throwable ex ->
            LOGGER.error('name=Store_GetItem_Fail, item={}', itemId.value, ex)
            return Promise.pure()
        }.then { com.junbo.catalog.spec.model.item.Item catalogItem ->
            if (catalogItem  == null) {
                return Promise.pure()
            }
            return getItem(catalogItem, includeDetails, apiContext)
        }
    }

    Promise<com.junbo.store.spec.model.browse.document.Item> getItem(OfferId offerId, boolean includeDetails, ApiContext apiContext) {
        lookupItemId(offerId).then { ItemId itemId ->
            if (itemId == null) {
                return Promise.pure()
            }
            getItem(itemId, includeDetails, apiContext)
        }
    }

    Promise<com.junbo.store.spec.model.browse.document.Item> getItem(Item catalogItem, boolean includeDetails, ApiContext apiContext) {
        com.junbo.store.spec.model.browse.document.Item result
        innerGetItem(catalogItem, apiContext).then { com.junbo.store.spec.model.browse.document.Item item ->
            result = item
            return Promise.pure()
        }.then {
            if (!includeDetails) {
                return Promise.pure()
            }
            fillItemDetails(result, apiContext)
        }.then {
            return Promise.pure(result)
        }
    }

    private Promise<ItemId> lookupItemId(OfferId offerId) {
        ItemId itemId = storeOfferToItemCache.get(offerId)
        if (itemId != null) {
            return Promise.pure(itemId)
        }
        Promise.pure().then {
            resourceContainer.offerResource.getOffer(offerId.value).then { com.junbo.catalog.spec.model.offer.Offer catalogOffer ->
                return resourceContainer.offerRevisionResource.getOfferRevision(catalogOffer.currentRevisionId, new OfferRevisionGetOptions()).then { OfferRevision offerRevision ->
                    if (CollectionUtils.isEmpty(offerRevision.items)) {
                        LOGGER.error('name=Store_Items_Empty, offer={}, offerRevision={}', catalogOffer.getOfferId(), offerRevision.getOfferId())
                        return Promise.pure()
                    }
                    if (offerRevision.items.size() > 1) {
                        LOGGER.error('name=Store_Items_Multiple, offer={}, offerRevision={}', catalogOffer.getOfferId(), offerRevision.getOfferId())
                    }
                    itemId = new ItemId(offerRevision.items[0].itemId)
                    storeOfferToItemCache.put(offerId, itemId)
                    return Promise.pure(itemId)
                }
            }
        }
    }

    Promise<ItemRevision> getAppItemRevision(ItemId itemId, Integer versionCode) {
        // todo get item revision by version code
        return resourceContainer.itemResource.getItem(itemId.value).then { Item catalogItem ->
            return resourceContainer.itemRevisionResource.getItemRevision(catalogItem.currentRevisionId, new ItemRevisionGetOptions()).then { ItemRevision itemRevision ->
                return Promise.pure(itemRevision)
            }
        }
    }

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

    Promise<ReviewsResponse> getReviews(String itemId, UserId userId, String cursor, Integer count) {
        ReviewSearchParams params = new ReviewSearchParams(
                resourceType: 'item',
                resourceId: itemId,
                userId: userId,
                cursor: cursor,
                count: count
        )

        return resourceContainer.caseyReviewResource.getReviews(params).recover { Throwable ex ->
            LOGGER.error('name=Get_Casey_Review_Fail', ex)
            return Promise.pure()
        }.then { CaseyResults<CaseyReview> results ->
            if (results == null) {
                return Promise.pure(new ReviewsResponse(
                    reviews: []
                ))
            }

            ReviewsResponse reviews = new ReviewsResponse()
            if (!StringUtils.isEmpty(results.cursor)) {
                reviews.next = new ReviewsResponse.NextOption(
                        itemId: new ItemId(itemId),
                        cursor: results.cursor,
                        count: results.count
                )
            }

            reviews.reviews = []
            Promise.each(results.items) { CaseyReview caseyReview ->
                Review review = new Review(
                        self: new Link(id: caseyReview.self.id, href: caseyReview.self.href),
                        title: caseyReview.reviewTitle,
                        content: caseyReview.review,
                        timestamp: caseyReview.postedDate
                )
                review.starRatings = [:] as Map<String, Integer>
                for (CaseyReview.Rating rating : caseyReview.ratings) {
                    review.starRatings[rating.type] = rating.score
                }

                Promise.pure().then {
                    resourceContainer.userResource.get(new UserId(IdFormatter.decodeId(UserId, caseyReview.user.getId())), new UserGetOptions())
                }.recover { Throwable e ->
                    if (e instanceof AppErrorException) {
                        return Promise.pure(null)
                    } else {
                        LOGGER.error('Exception happened while calling identity', e)
                        return Promise.pure(null)
                    }
                }.then { User user ->
                    if (user != null) {
                        review.authorName = user.nickName
                    }

                    reviews.reviews << review
                    return Promise.pure()
                }
            }.then {
                return Promise.pure(reviews)
            }
        }
    }


    private Promise<Offer> getOffer(OfferData offerData, ApiContext apiContext) {
        if (offerData == null) {
            return Promise.pure()
        }
        List cacheKey = [offerData.offer.id, apiContext.country.getId(), apiContext.currency.getId()] as List
        Offer result = storeItemPriceCache.get(cacheKey)
        if (result != null) {
            return Promise.pure(result)
        }

        result = new Offer()
        if (offerData?.offer == null || offerData?.offerRevision == null) {
            return Promise.pure(null)
        }
        result.self = new OfferId(offerData.offer.getId())
        result.currency = apiContext.currency.getId()
        OfferRevisionLocaleProperties localeProperties = localeUtils.getLocaleProperties(offerData.offerRevision?.locales, apiContext.locale , 'offerRevision', offerData.offerRevision?.getId(), 'locales') as OfferRevisionLocaleProperties
        result.formattedDescription = localeProperties?.shortDescription
        result.isFree = offerData.offerRevision?.price?.priceType == PriceType.FREE.name()
        Promise.pure().then {
            resourceContainer.ratingResource.priceRating(new RatingRequest(
                    includeCrossOfferPromos: false,
                    country: apiContext.country.getId().value,
                    currency: apiContext.currency.getId().value,
                    lineItems: [
                            new RatingItem(
                                    offerId: offerData.offer.offerId,
                                    quantity: 1
                            )
                    ] as Set))
        }.recover { Throwable ex ->
            LOGGER.error('name=Store_Price_Rating_Fail, offer={}', offerData.offer.getId(), ex)
            return Promise.pure()
        }.then { RatingRequest ratingResult ->
            if (ratingResult != null) {
                result.price = ratingResult.lineItems[0].finalTotalAmount
                storeItemPriceCache.put(cacheKey, result)
            }
            return Promise.pure(result)
        }
    }

    private Promise<ItemData> getItemData(ItemId itemId) {
        ItemData result = storeItemDataCache.get(itemId)
        Item catalogItem
        if (result != null) {
            return Promise.pure(result)
        }

        result = new ItemData()
        result.genres = []
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
        }.then {  // get revisions
            getHistoryItemRevisions(new ItemId(catalogItem.getId())).then { List<ItemRevision> list ->
                result.revisions = list
                return Promise.pure()
            }.then {
                if (catalogItem.currentRevisionId == null) {
                    return Promise.pure()
                }
                resourceContainer.itemRevisionResource.getItemRevision(catalogItem.currentRevisionId, new ItemRevisionGetOptions()).then { ItemRevision e ->
                    result.currentRevision = e
                    return Promise.pure()
                }
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
            return getCaseyData(catalogItem.getId()).then { CaseyData caseyData ->
                result.caseyData = caseyData
                return Promise.pure()
            }
        }.then {
            storeItemDataCache.put(new ItemId(catalogItem.getItemId()), result)
            return Promise.pure(result)
        }
    }

    private Promise<com.junbo.store.spec.model.browse.document.Item> innerGetItem(com.junbo.catalog.spec.model.item.Item catalogItem, ApiContext apiContext) {
        ItemData itemData
        com.junbo.store.spec.model.browse.document.Item result = new com.junbo.store.spec.model.browse.document.Item()
        getItemData(new ItemId(catalogItem.getId())).then { ItemData e ->
            itemData = e
            browseDataBuilder.buildItemFromItemData(itemData, apiContext, result)
            return Promise.pure()
        }.then { // apply price info
            getOffer(itemData?.offer, apiContext).then { Offer e ->
                result.offer = e
                return Promise.pure()
            }
        }.then {
            return Promise.pure(result)
        }
    }

    private Promise fillItemDetails(com.junbo.store.spec.model.browse.document.Item item, ApiContext apiContext) {
        catalogUtils.checkItemOwnedByUser(item.getSelf(), apiContext.user).then { Boolean owned ->
            item.ownedByCurrentUser = owned
            return Promise.pure()
        }.then { // get current user review
            getReviews(item.self.value, apiContext.user, null, null).then { ReviewsResponse reviewsResponse ->
                if (CollectionUtils.isEmpty(reviewsResponse?.reviews)) {
                    item.currentUserReview = null
                } else {
                    item.currentUserReview = reviewsResponse.reviews.get(0)
                }
                return Promise.pure()
            }
        }.then { // get the review response
            getReviews(item.self.value, null, null, null).then { ReviewsResponse reviewsResponse ->
                item.reviews = reviewsResponse
                return Promise.pure()
            }
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

    private Promise<List<ItemRevision>> getHistoryItemRevisions(ItemId itemId) {
        List<ItemRevision> results = [] as List
        ItemRevisionsGetOptions options = new ItemRevisionsGetOptions(
                itemIds: [itemId.value] as Set,
                status: Status.APPROVED.name()
        )
        CommonUtils.loop {
            resourceContainer.itemRevisionResource.getItemRevisions(options).then { Results<ItemRevision> revisionResults ->
                results.addAll(revisionResults.items)
                String cursor = CommonUtils.getQueryParam(revisionResults.next?.href, 'cursor')
                if (revisionResults.items.isEmpty() || StringUtils.isEmpty(cursor)) {
                    return Promise.pure(Promise.BREAK)
                }
                options.cursor = cursor
                return Promise.pure()
            }
        }.then {
            results = results.sort { ItemRevision e -> e.updatedTime }.reverse()
            return Promise.pure(results)
        }
    }

    private Promise<CaseyData> getCaseyData(String itemId) {
        CaseyData result = new CaseyData()
        resourceContainer.caseyReviewResource.getRatingByItemId(itemId).then { CaseyResults<CaseyAggregateRating> results ->
            result.aggregatedRatings = results.items.collect { CaseyAggregateRating rating ->
                AggregatedRatings aggregatedRatings = new AggregatedRatings(
                        type: rating.type,
                        averageRating: rating.average,
                        ratingsCount: rating.count,
                )

                Map<Integer, Long> histogram = new HashMap<>()
                for (int i = 0; i < rating.histogram.length; i++) {
                    histogram.put(i, rating.histogram[i])
                }
                aggregatedRatings.ratingsHistogram = histogram

                return aggregatedRatings
            }
            return Promise.pure()
        }.recover { Throwable throwable ->
            LOGGER.error('name=Get_Casey_Fail', throwable)
            return Promise.pure()
        }.then {
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
}
