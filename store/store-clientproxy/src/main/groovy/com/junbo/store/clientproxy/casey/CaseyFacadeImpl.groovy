package com.junbo.store.clientproxy.casey

import com.junbo.catalog.spec.model.common.RevisionNotes
import com.junbo.catalog.spec.model.item.Binary
import com.junbo.common.enumid.CurrencyId
import com.junbo.common.error.AppErrorException
import com.junbo.common.id.ItemId
import com.junbo.common.id.UserId
import com.junbo.common.model.Link
import com.junbo.common.util.IdFormatter
import com.junbo.identity.spec.v1.model.User
import com.junbo.identity.spec.v1.option.model.UserGetOptions
import com.junbo.langur.core.promise.Promise
import com.junbo.store.clientproxy.ResourceContainer
import com.junbo.store.clientproxy.utils.itemBuilder
import com.junbo.store.spec.model.ApiContext
import com.junbo.store.spec.model.Platform
import com.junbo.store.spec.model.browse.ReviewsResponse
import com.junbo.store.spec.model.browse.document.*
import com.junbo.store.spec.model.external.casey.CaseyAggregateRating
import com.junbo.store.spec.model.external.casey.CaseyResults
import com.junbo.store.spec.model.external.casey.CaseyReview
import com.junbo.store.spec.model.external.casey.ReviewSearchParams
import com.junbo.store.spec.model.external.casey.search.CaseyItem
import com.junbo.store.spec.model.external.casey.search.CaseyOffer
import com.junbo.store.spec.model.external.casey.search.CatalogAttribute
import com.junbo.store.spec.model.external.casey.search.OfferSearchParams
import groovy.transform.CompileStatic
import org.apache.commons.lang3.StringUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.util.CollectionUtils

import javax.annotation.Resource

/**
 * The CaseyFacadeImpl class.
 */
@CompileStatic
@Component('storeCaseyFacade')
class CaseyFacadeImpl implements CaseyFacade {

    private final static Logger LOGGER = LoggerFactory.getLogger(CaseyFacadeImpl)

    @Resource(name = 'storeResourceContainer')
    private ResourceContainer resourceContainer

    @Resource(name = 'storeImageConvertor')
    private itemBuilder imageConvertor

    @Value('${store.casey.search.expand}')
    private String expand

    @Override
    Promise<CaseyResults<Item>> search(SectionInfoNode sectionInfoNode, String cursor, Integer count, ApiContext apiContext) {
        CaseyResults<Item> results = new CaseyResults<Item>(
                items: [] as List,
                cursor: null as String
        )

        if (canSearch(sectionInfoNode)) {
            return Promise.pure(results)
        }
        OfferSearchParams searchParams = buildOfferSearchParams(sectionInfoNode, cursor, count, apiContext)

        resourceContainer.caseyResource.searchOffers(searchParams).recover { Throwable ex ->
            LOGGER.info('name=Store_SearchOffer_Error', ex)
            return Promise.pure(null)
        }.then { CaseyResults<CaseyOffer> rawResults ->
            if (rawResults == null) {
                return Promise.pure(results)
            }
            rawResults.each { CaseyOffer caseyOffer ->
                results.items << buildItem(caseyOffer, searchParams.country)
            }
            results.cursor = rawResults.cursor
            results.count = rawResults.count
            results.totalCount = rawResults.totalCount
            return Promise.pure(results)
        }
    }

    @Override
    Promise<List<AggregatedRatings>> getAggregatedRatings(ItemId itemId, ApiContext apiContext) {
        List<AggregatedRatings> aggregatedRatingsList = null
        resourceContainer.caseyReviewResource.getRatingByItemId(itemId.value).then { CaseyResults<CaseyAggregateRating> results ->
            aggregatedRatingsList = results.items.collect { CaseyAggregateRating rating ->
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
            LOGGER.error('name=Get_AggregateRating_Fail, item=', itemId, throwable)
            return Promise.pure()
        }.then {
            return Promise.pure(aggregatedRatingsList)
        }
    }

    @Override
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
            if (!org.springframework.util.StringUtils.isEmpty(results.cursor)) {
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

    private OfferSearchParams buildOfferSearchParams(SectionInfoNode sectionInfoNode, String cursor, Integer count, ApiContext apiContext) {
        OfferSearchParams offerSearchParams = new OfferSearchParams()
        switch (sectionInfoNode.sectionType) {
            case SectionInfoNode.SectionType.FeaturedSection:
                offerSearchParams.cmsPage = sectionInfoNode.cmsPage
                offerSearchParams.cmsSlot = sectionInfoNode.cmsSlot
                break;
            case SectionInfoNode.SectionType.CategorySection:
                offerSearchParams.category = sectionInfoNode.categoryId
        }
        offerSearchParams.locale = apiContext.locale.getId().value
        offerSearchParams.country = apiContext.country.getId().value
        offerSearchParams.expand = expand
        offerSearchParams.count = count
        offerSearchParams.cursor = cursor
        offerSearchParams.platform = apiContext.platform.value
        return offerSearchParams
    }

    private boolean canSearch(SectionInfoNode sectionInfoNode) {
        if (sectionInfoNode.sectionType == SectionInfoNode.SectionType.FeaturedSection) {
            return !StringUtils.isBlank(sectionInfoNode.cmsPage) && !StringUtils.isBlank(sectionInfoNode.cmsSlot)
        }
        return true
    }
}
