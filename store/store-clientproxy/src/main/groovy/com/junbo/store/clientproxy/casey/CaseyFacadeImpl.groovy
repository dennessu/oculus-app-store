package com.junbo.store.clientproxy.casey
import com.junbo.catalog.spec.model.offer.Offer
import com.junbo.catalog.spec.model.offer.OffersGetOptions
import com.junbo.common.error.AppErrorException
import com.junbo.common.id.ItemId
import com.junbo.common.id.OrganizationId
import com.junbo.common.id.UserId
import com.junbo.common.id.util.IdUtil
import com.junbo.common.json.ObjectMapperProvider
import com.junbo.common.model.Link
import com.junbo.common.model.Results
import com.junbo.common.util.IdFormatter
import com.junbo.identity.spec.v1.model.Organization
import com.junbo.identity.spec.v1.model.User
import com.junbo.identity.spec.v1.option.model.OrganizationGetOptions
import com.junbo.identity.spec.v1.option.model.UserGetOptions
import com.junbo.langur.core.promise.Promise
import com.junbo.store.clientproxy.ResourceContainer
import com.junbo.store.clientproxy.utils.ItemBuilder
import com.junbo.store.spec.model.ApiContext
import com.junbo.store.spec.model.browse.AddReviewRequest
import com.junbo.store.spec.model.browse.ReviewsResponse
import com.junbo.store.spec.model.browse.document.AggregatedRatings
import com.junbo.store.spec.model.browse.document.Item
import com.junbo.store.spec.model.browse.document.Review
import com.junbo.store.spec.model.browse.document.SectionInfoNode
import com.junbo.store.spec.model.external.casey.*
import com.junbo.store.spec.model.external.casey.cms.CmsPage
import com.junbo.store.spec.model.external.casey.cms.CmsPageGetParams
import com.junbo.store.spec.model.external.casey.search.CaseyItem
import com.junbo.store.spec.model.external.casey.search.CaseyOffer
import com.junbo.store.spec.model.external.casey.search.OfferSearchParams
import groovy.transform.CompileStatic
import org.apache.commons.lang3.StringUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory
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

    @Resource(name = 'storeItemBuilder')
    private ItemBuilder itemBuilder

    @Override
    Promise<CaseyResults<Item>> search(SectionInfoNode sectionInfoNode, String cursor, Integer count, ApiContext apiContext) {
        assert sectionInfoNode.sectionType != null, 'sectionType could not be null'
        CaseyResults<Item> results = new CaseyResults<Item>(
                items: [] as List,
                cursor: null as String
        )

        if (!canSearch(sectionInfoNode)) {
            return Promise.pure(results)
        }
        OfferSearchParams searchParams = buildOfferSearchParams(sectionInfoNode, cursor, count, apiContext)

        resourceContainer.caseyResource.searchOffers(searchParams).recover { Throwable ex ->
            LOGGER.error('name=Store_SearchOffer_Error, searchParams={}', ObjectMapperProvider.instance().writeValueAsString(searchParams), ex)
            return Promise.pure(null)
        }.then { CaseyResults<CaseyOffer> rawResults ->
            if (rawResults?.items == null) {
                return Promise.pure(results)
            }

            results.cursor = rawResults.cursor
            results.count = rawResults.count
            results.totalCount = rawResults.totalCount
            Promise.each (rawResults.items) { CaseyOffer caseyOffer ->
                CaseyItem caseyItem = CollectionUtils.isEmpty(caseyOffer.items) ? null : caseyOffer.items[0]
                Organization publisher, developer
                Promise.pure().then {
                    getOrganization(caseyOffer.publisher).then { Organization organization ->
                        publisher = organization
                        return Promise.pure()
                    }
                }.then {
                    getOrganization(caseyItem?.developer).then { Organization organization ->
                        developer = organization
                        return Promise.pure()
                    }
                }.then {
                    if (CollectionUtils.isEmpty(caseyOffer?.items)) {
                        return Promise.pure()
                    }
                    getAggregatedRatings(caseyOffer.items.get(0).self, apiContext)
                }.then { List<AggregatedRatings> aggregatedRatings ->
                    results.items << itemBuilder.buildItem(caseyOffer, aggregatedRatings, publisher, developer, apiContext)
                    return Promise.pure()
                }
            }then {
                return Promise.pure(results)
            }
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

    @Override
    Promise<Boolean> itemAvailable(ItemId itemId, ApiContext apiContext) {
        resourceContainer.offerResource.getOffers(new OffersGetOptions(itemId: itemId?.value)).then { Results<Offer> offerResults ->
            if (offerResults.items.size() > 1) {
                LOGGER.warn('name=Store_Multiple_Offers_Found, item={}, useOffer={}', itemId, offerResults.items[0].offerId)
            }
            if (CollectionUtils.isEmpty(offerResults.items)) {
                return Promise.pure(false)
            }

            // call search to check whether the offer is available under the given country
            OfferSearchParams offerSearchParams = new OfferSearchParams()
            offerSearchParams.locale = apiContext.locale.getId().value
            offerSearchParams.country = apiContext.country.getId().value
            offerSearchParams.offerId = offerResults.items[0].offerId
            offerSearchParams.minimal = true
            resourceContainer.caseyResource.searchOffers(offerSearchParams).recover { Throwable ex ->
                LOGGER.info('name=Store_SearchOffer_Error', ex)
                return Promise.pure()
            }.then { CaseyResults<CaseyOffer> results ->
                if (CollectionUtils.isEmpty(results?.items)) {
                    return Promise.pure(false)
                }
                return Promise.pure(true)
            }
        }
    }

    @Override
    Promise<CmsPage> getCmsPage(String pageName) {
        resourceContainer.caseyResource.getCmsPages(
            new CmsPageGetParams(path: "\"/${pageName}\"")
        ).then { CaseyResults<CmsPage> results ->
            if (CollectionUtils.isEmpty(results.items)) {
                return Promise.pure()
            }
            return Promise.pure(results.items[0])
        }
    }

    @Override
    Promise<Review> addReview(AddReviewRequest request, ApiContext apiContext) {
        CaseyReview review = new CaseyReview(
                reviewTitle: request.title,
                review: request.content,
                resourceType: 'item',
                resource: new CaseyLink(
                        id: request.itemId.value,
                        href: IdUtil.toHref(request.itemId)
                ),
                country: apiContext.country.getId().value,
                locale: apiContext.locale.getId().value
        )

        review.ratings = []
        for (String type : request.starRatings.keySet()) {
            review.ratings << new CaseyReview.Rating(type: type, score: request.starRatings[type])
        }

        return resourceContainer.caseyReviewResource.addReview(review).then { CaseyReview newReview ->
            return resourceContainer.userResource.get(apiContext.user, new UserGetOptions()).then { User user ->
                return Promise.pure(
                            new Review(
                                self: new Link(id: newReview.self.id, href: newReview.self.href),
                                authorName: user.nickName,
                                title: newReview.reviewTitle,
                                content: newReview.review,
                                starRatings: request.starRatings,
                                timestamp: newReview.postedDate
                ))
            }
        }
    }

    private OfferSearchParams buildOfferSearchParams(SectionInfoNode sectionInfoNode, String cursor, Integer count, ApiContext apiContext) {
        OfferSearchParams offerSearchParams = new OfferSearchParams()
        switch (sectionInfoNode.sectionType) {
            case SectionInfoNode.SectionType.CmsSection:
                offerSearchParams.cmsPage = sectionInfoNode.cmsPage
                offerSearchParams.cmsSlot = sectionInfoNode.cmsSlot
                break;
            case SectionInfoNode.SectionType.CategorySection:
                offerSearchParams.category = sectionInfoNode.categoryId
        }
        offerSearchParams.locale = apiContext.locale.getId().value
        offerSearchParams.country = apiContext.country.getId().value
        offerSearchParams.count = count
        offerSearchParams.cursor = cursor
        offerSearchParams.platform = apiContext.platform.value
        return offerSearchParams
    }

    private boolean canSearch(SectionInfoNode sectionInfoNode) {
        if (sectionInfoNode.sectionType == SectionInfoNode.SectionType.CmsSection) {
            return !StringUtils.isBlank(sectionInfoNode.cmsPage) && !StringUtils.isBlank(sectionInfoNode.cmsSlot)
        }
        return true
    }

    private Promise<Organization> getOrganization(OrganizationId organizationId) {
        Promise.pure().then {
            resourceContainer.organizationResource.get(organizationId, new OrganizationGetOptions())
        }.recover { Throwable ex ->
            LOGGER.error('name=Store_Get_Organization_Fail, organization={}', organizationId, ex)
            return Promise.pure()
        }
    }
}
