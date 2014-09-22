package com.junbo.store.clientproxy.casey

import com.junbo.authorization.AuthorizeContext
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
import com.junbo.identity.spec.v1.model.UserLoginName
import com.junbo.identity.spec.v1.model.UserName
import com.junbo.identity.spec.v1.model.UserPersonalInfo
import com.junbo.identity.spec.v1.option.model.OrganizationGetOptions
import com.junbo.identity.spec.v1.option.model.UserGetOptions
import com.junbo.identity.spec.v1.option.model.UserPersonalInfoGetOptions
import com.junbo.langur.core.promise.Promise
import com.junbo.oauth.spec.model.AccessTokenRequest
import com.junbo.oauth.spec.model.AccessTokenResponse
import com.junbo.oauth.spec.model.GrantType
import com.junbo.store.clientproxy.ResourceContainer
import com.junbo.store.clientproxy.utils.ItemBuilder
import com.junbo.store.clientproxy.utils.ReviewBuilder
import com.junbo.store.spec.model.ApiContext
import com.junbo.store.spec.model.browse.AddReviewRequest
import com.junbo.store.spec.model.browse.ReviewsResponse
import com.junbo.store.spec.model.browse.document.AggregatedRatings
import com.junbo.store.spec.model.browse.document.Item
import com.junbo.store.spec.model.browse.document.Review
import com.junbo.store.spec.model.browse.document.SectionInfoNode
import com.junbo.store.spec.model.external.casey.*
import com.junbo.store.spec.model.external.casey.cms.CmsCampaign
import com.junbo.store.spec.model.external.casey.cms.CmsCampaignGetParam
import com.junbo.store.spec.model.external.casey.cms.CmsContentSlot
import com.junbo.store.spec.model.external.casey.cms.CmsPage
import com.junbo.store.spec.model.external.casey.cms.CmsPageGetParams
import com.junbo.store.spec.model.external.casey.cms.ContentItem
import com.junbo.store.spec.model.external.casey.cms.Placement
import com.junbo.store.spec.model.external.casey.search.CaseyItem
import com.junbo.store.spec.model.external.casey.search.CaseyOffer
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

    @Resource(name = 'storeItemBuilder')
    private ItemBuilder itemBuilder

    @Resource(name = 'storeReviewBuilder')
    private ReviewBuilder reviewBuilder

    @Value('${store.onbehalf.clientId}')
    private String clientId

    @Value('${store.onbehalf.clientSecret}')
    private String clientSecret

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
        return doSearch(searchParams, apiContext)
    }

    @Override
    Promise<CaseyResults<Item>> search(ItemId itemId, ApiContext apiContext) {
        OfferSearchParams searchParams = buildOfferSearchParams(itemId, apiContext)
        return doSearch(searchParams, apiContext)
    }

    @Override
    Promise<Map<String, AggregatedRatings>> getAggregatedRatings(ItemId itemId, ApiContext apiContext) {
        Map<String, AggregatedRatings> aggregatedRatingsMap = [:]
        resourceContainer.caseyResource.getRatingByItemId(itemId.value).then { CaseyResults<CaseyAggregateRating> results ->
            results.items.each { CaseyAggregateRating caseyAggregateRating ->
                if (caseyAggregateRating.type != null) {
                    aggregatedRatingsMap[caseyAggregateRating.type] = reviewBuilder.buildAggregatedRatings(caseyAggregateRating)
                }
            }
            return Promise.pure()
        }.recover { Throwable throwable ->
            LOGGER.error('name=Get_AggregateRating_Fail, item=', itemId, throwable)
            return Promise.pure()
        }.then {
            return Promise.pure(aggregatedRatingsMap)
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

        return resourceContainer.caseyResource.getReviews(params).recover { Throwable ex ->
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
                getReviewAuthorName(caseyReview).then { String author ->
                    Review review = reviewBuilder.buildReview(caseyReview, author)
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
    Promise<CmsPage> getCmsPage(String path, String label) {
        resourceContainer.caseyResource.getCmsPages(
            new CmsPageGetParams(path: "\"${path}\"", label: "\"${label}\"")
        ).then { CaseyResults<CmsPage> results ->
            if (CollectionUtils.isEmpty(results?.items)) {
                return Promise.pure()
            }
            CmsPage page = results.items[0]
            if (CollectionUtils.isEmpty(page?.slots)) {
                return Promise.pure(page)
            }
            return fillPageContent(page)
        }
    }

    @Override
    Promise<CmsCampaign> getCmsCampaign(String label) {
        CaseyResults<CmsCampaign> caseyResults
        resourceContainer.caseyResource.getCmsCampaigns(new CmsCampaignGetParam(expand: 'results/placements/content')).then { CaseyResults<CmsCampaign> results ->
            caseyResults = results
            if (CollectionUtils.isEmpty(caseyResults?.items)) {
                return Promise.pure()
            }
            CmsCampaign result = caseyResults.items.find { CmsCampaign campaign ->
                return campaign?.status == 'APPROVED' && campaign?.label == label
            }
            return Promise.pure(result)
        }
    }

    @Override
    Promise<Review> addReview(AddReviewRequest request, ApiContext apiContext) {
        CaseyReview review = reviewBuilder.buildCaseyReview(request, apiContext)

        // Create an access token with this user and catalog scope.
        return resourceContainer.tokenEndpoint.postToken(
                new AccessTokenRequest(
                        accessToken: AuthorizeContext.currentAccessToken,
                        clientId: clientId,
                        clientSecret: clientSecret,
                        grantType: GrantType.EXCHANGE.name(),
                        scope: 'catalog'
                )
        ).then { AccessTokenResponse accessTokenResponse ->
            return resourceContainer.caseyReviewResource.addReview("Bearer $accessTokenResponse.accessToken", review).then { CaseyReview newReview ->
                getReviewAuthorName(newReview).then { String author ->
                    return Promise.pure(reviewBuilder.buildReview(newReview, author))
                }
            }
        }
    }

    private Promise<CaseyResults<Item>> doSearch(OfferSearchParams searchParams, ApiContext apiContext) {
        CaseyResults<Item> results = new CaseyResults<Item>(
                items: [] as List,
                cursor: null as String
        )
        resourceContainer.caseyResource.searchOffers(searchParams).then { CaseyResults<CaseyOffer> rawResults ->
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
                    Map<String, AggregatedRatings> aggregatedRatings = [:] as Map
                    if (caseyItem?.qualityRating != null) {
                        aggregatedRatings[CaseyReview.RatingType.quality.name()] = reviewBuilder.buildAggregatedRatings(caseyItem.qualityRating)
                    }
                    results.items << itemBuilder.buildItem(caseyOffer, aggregatedRatings, publisher, developer, apiContext)
                    return Promise.pure()
                }
            }then {
                return Promise.pure(results)
            }
        }
    }

    private OfferSearchParams buildOfferSearchParams(SectionInfoNode sectionInfoNode, String cursor, Integer count, ApiContext apiContext) {
        OfferSearchParams offerSearchParams = new OfferSearchParams()
        switch (sectionInfoNode.sectionType) {
            case SectionInfoNode.SectionType.CmsSection:
                offerSearchParams.cmsPage = sectionInfoNode.cmsPageSearch
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

    private OfferSearchParams buildOfferSearchParams(ItemId itemId, ApiContext apiContext) {
        OfferSearchParams offerSearchParams = new OfferSearchParams()
        offerSearchParams.locale = apiContext.locale.getId().value
        offerSearchParams.country = apiContext.country.getId().value
        offerSearchParams.platform = apiContext.platform.value
        offerSearchParams.itemId = itemId
        return offerSearchParams
    }

    private boolean canSearch(SectionInfoNode sectionInfoNode) {
        if (sectionInfoNode.sectionType == SectionInfoNode.SectionType.CmsSection) {
            return !StringUtils.isBlank(sectionInfoNode.cmsPageSearch) && !StringUtils.isBlank(sectionInfoNode.cmsSlot)
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

    private Promise<CmsPage> fillPageContent(CmsPage cmsPage) {
        assert cmsPage?.slots != null, 'cmsPage.slot should not be null'
        resourceContainer.caseyResource.getCmsCampaigns(new CmsCampaignGetParam(expand: 'results/placements/content')).then { CaseyResults<CmsCampaign> caseyResults ->
            if (CollectionUtils.isEmpty(caseyResults?.items)) {
                return Promise.pure(cmsPage)
            }
            caseyResults.items.each { CmsCampaign campaign ->
                if (CollectionUtils.isEmpty(campaign.placements)) {
                    return
                }
                campaign.placements.each { Placement placement ->
                    if (placement != null) {
                        fillStringContent(cmsPage, placement)
                    }
                }
            }
            return Promise.pure(cmsPage)
        }
    }

    private static void fillStringContent(CmsPage cmsPage, Placement placement) {
        assert cmsPage?.slots != null, 'cmsPage.slot should not be null'
        if (!(placement?.page?.getId() == cmsPage.self.getId() && cmsPage.slots.get(placement.slot) != null)) {
            return
        }
        CmsContentSlot pageSlot = cmsPage.slots.get(placement.slot)
        if (!CollectionUtils.isEmpty(placement?.content?.contents)) {
            placement.content.contents.each { Map.Entry<String, ContentItem> placementContent ->
                if (placementContent?.value?.type != ContentItem.Type.string.name()) { // only interested in string content
                    return
                }
                if (pageSlot.contents.get(placementContent.key) == null) {
                    pageSlot.contents[placementContent.key] = new ContentItem(type: placementContent.value.type, links: [] as List, strings: [] as List)
                }

                ContentItem contentItem = pageSlot.contents[placementContent.key]
                if (!CollectionUtils.isEmpty(placementContent.value.strings)) {
                    contentItem.strings.addAll(placementContent.value.strings)
                }
            }
        }
    }

    Promise<String> getReviewAuthorName(CaseyReview caseyReview) {
        Promise.pure().then {
            resourceContainer.userResource.get(new UserId(IdFormatter.decodeId(UserId, caseyReview.user.getId())), new UserGetOptions()).then { User user ->
                if (!StringUtils.isBlank(user.nickName)) {
                    return Promise.pure(user.nickName)
                }
                return resourceContainer.userPersonalInfoResource.get(user.username, new UserPersonalInfoGetOptions()).then { UserPersonalInfo username ->
                    return Promise.pure(ObjectMapperProvider.instance().treeToValue(username.value, UserLoginName).userName)
                }
            }
        }.recover { Throwable e ->
            LOGGER.error('Exception happened while getting review author', e)
            return Promise.pure(null)
        }
    }
}
