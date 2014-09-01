package com.junbo.store.rest.browse.impl

import com.junbo.catalog.spec.enums.EntitlementType
import com.junbo.catalog.spec.enums.PriceType
import com.junbo.catalog.spec.enums.Status
import com.junbo.catalog.spec.model.attribute.ItemAttribute
import com.junbo.catalog.spec.model.attribute.OfferAttribute
import com.junbo.catalog.spec.model.item.Item
import com.junbo.catalog.spec.model.item.ItemRevision
import com.junbo.catalog.spec.model.item.ItemRevisionGetOptions
import com.junbo.catalog.spec.model.item.ItemRevisionsGetOptions
import com.junbo.catalog.spec.model.offer.OfferRevision
import com.junbo.catalog.spec.model.offer.OfferRevisionGetOptions
import com.junbo.catalog.spec.model.offer.OffersGetOptions
import com.junbo.common.error.AppErrorException
import com.junbo.common.id.ItemId
import com.junbo.common.id.ItemRevisionId
import com.junbo.common.id.OfferId
import com.junbo.common.id.UserId
import com.junbo.common.id.util.IdUtil
import com.junbo.common.model.Link
import com.junbo.common.model.Results
import com.junbo.entitlement.spec.model.Entitlement
import com.junbo.entitlement.spec.model.EntitlementSearchParam
import com.junbo.entitlement.spec.model.PageMetadata
import com.junbo.identity.spec.v1.model.Organization
import com.junbo.identity.spec.v1.model.User
import com.junbo.identity.spec.v1.option.model.OrganizationGetOptions
import com.junbo.identity.spec.v1.option.model.UserGetOptions
import com.junbo.langur.core.promise.Promise
import com.junbo.rating.spec.model.priceRating.RatingItem
import com.junbo.rating.spec.model.priceRating.RatingRequest
import com.junbo.store.common.cache.Cache
import com.junbo.store.common.utils.CommonUtils
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

    @Resource(name = 'storeItemDataCache')
    private Cache<ItemId, ItemData> storeItemDataCache

    @Resource(name = 'storeItemPriceCache')
    private Cache<Object, Offer> storeItemPriceCache

    @Resource(name = 'storeResourceContainer')
    private ResourceContainer resourceContainer

    Promise<ItemData> getItemData(com.junbo.catalog.spec.model.item.Item catalogItem) {
        ItemData result = storeItemDataCache.get(new ItemId(catalogItem.getItemId()))
        if (result != null) {
            return Promise.pure(result)
        }

        result = new ItemData()
        result.item = catalogItem
        result.genres = []
        Promise.pure().then { // get developer
            resourceContainer.organizationResource.get(result.item.ownerId, new OrganizationGetOptions()).then { Organization organization ->
                result.developer = organization
                return Promise.pure()
            }
        }.then {  // get genres
            Promise.each(catalogItem.genres) { String genresId ->
                resourceContainer.itemAttributeResource.getAttribute(genresId).then { ItemAttribute itemAttribute ->
                    result.genres << itemAttribute
                    return Promise.pure()
                }
            }
        }.then {  // get revisions
            getHistoryItemRevisions(new ItemId(catalogItem.getId())).then { List<ItemRevision> list ->
                result.revisions = list
                return Promise.pure()
            }.then {
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

    Promise<Offer> getOffer(OfferData offerData, ApiContext apiContext) {
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
        result.formattedDescription = offerData.offerRevision?.locales?.get(apiContext.locale.getId().value)?.shortDescription
        result.isFree = offerData.offerRevision?.price?.priceType == PriceType.FREE.name()
        resourceContainer.ratingResource.priceRating(new RatingRequest(
                includeCrossOfferPromos: false,
                country: apiContext.country.getId().value,
                currency: apiContext.currency.getId().value,
                lineItems: [
                        new RatingItem(
                                offerId: offerData.offer.offerId,
                                quantity: 1
                        )
                ] as Set
        )).then { RatingRequest ratingResult ->
            result.price = ratingResult.lineItems[0].finalTotalAmount
            storeItemPriceCache.put(cacheKey, result)
            return Promise.pure(result)
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

    Promise<Boolean> checkItemOwnedByUser(ItemId itemId, UserId userId) {
        return resourceContainer.entitlementResource.searchEntitlements(new EntitlementSearchParam(
                userId: userId,
                itemIds: [itemId] as Set,
                isActive: true
        ), new PageMetadata()).then { Results<Entitlement> results ->
            return Promise.pure(!results.items.isEmpty())
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
                resourceContainer.offerAttributeResource.getAttribute(categoryId).then { OfferAttribute offerAttribute ->
                    result.categories << offerAttribute
                    return Promise.pure()
                }
            }
        }.then { // get publisher
            resourceContainer.organizationResource.get(catalogOffer.ownerId, new OrganizationGetOptions()).then { Organization organization->
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
        resourceContainer.caseyResource.getRatingByItemId(itemId).then { CaseyResults<CaseyAggregateRating> results ->
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
        }.then {
            resourceContainer.caseyResource.getReviews(new ReviewSearchParams(resourceType: 'item', resourceId: itemId))
                    .then { CaseyResults<CaseyReview> results ->
                ReviewsResponse reviews = new ReviewsResponse()
                reviews.next = new ReviewsResponse.NextOption(
                        itemId: new ItemId(itemId),
                        cursor: results.cursor,
                        count: results.count
                )

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

                    return resourceContainer.userResource.get(IdUtil.fromLink(new Link(id: caseyReview.user.id,
                            href: caseyReview.user.href)) as UserId, new UserGetOptions()).recover { Throwable e ->
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
                    result.reviewsResponse = reviews
                }
            }
        }.then {
            return Promise.pure(result)
        }
    }
}
