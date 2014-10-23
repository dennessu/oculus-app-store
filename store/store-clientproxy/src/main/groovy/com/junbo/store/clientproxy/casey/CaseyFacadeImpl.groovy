package com.junbo.store.clientproxy.casey
import com.fasterxml.jackson.databind.JsonNode
import com.junbo.authorization.AuthorizeContext
import com.junbo.common.id.ItemId
import com.junbo.common.id.OrganizationId
import com.junbo.common.id.UserId
import com.junbo.common.json.ObjectMapperProvider
import com.junbo.common.util.IdFormatter
import com.junbo.identity.spec.v1.model.Organization
import com.junbo.identity.spec.v1.model.User
import com.junbo.identity.spec.v1.model.UserLoginName
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
import com.junbo.store.spec.exception.casey.CaseyException
import com.junbo.store.spec.model.ApiContext
import com.junbo.store.spec.model.browse.AddReviewRequest
import com.junbo.store.spec.model.browse.Images
import com.junbo.store.spec.model.browse.ReviewsResponse
import com.junbo.store.spec.model.browse.document.AggregatedRatings
import com.junbo.store.spec.model.browse.document.Item
import com.junbo.store.spec.model.browse.document.Review
import com.junbo.store.spec.model.browse.document.SectionInfoNode
import com.junbo.store.spec.model.external.sewer.casey.CaseyAggregateRating
import com.junbo.store.spec.model.external.sewer.casey.CaseyResults
import com.junbo.store.spec.model.external.sewer.casey.CaseyReview
import com.junbo.store.spec.model.external.sewer.casey.ReviewSearchParams
import com.junbo.store.spec.model.external.sewer.casey.cms.*
import com.junbo.store.spec.model.external.sewer.casey.search.CaseyItem
import com.junbo.store.spec.model.external.sewer.casey.search.CaseyOffer
import com.junbo.store.spec.model.external.sewer.casey.search.OfferSearchParams
import groovy.transform.CompileStatic
import org.apache.commons.lang3.StringUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.util.Assert
import org.springframework.util.CollectionUtils

import javax.annotation.Resource
/**
 * The CaseyFacadeImpl class.
 */
@CompileStatic
@Component('storeCaseyFacade')
class CaseyFacadeImpl implements CaseyFacade {

    private static final int CASEY_RESULTS_CURSOR_INDEX = 1;

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
    Promise<CaseyResults<Item>> search(SectionInfoNode sectionInfoNode, String cursor, Integer count, Images.BuildType imageBuildType, ApiContext apiContext) {
        assert sectionInfoNode.sectionType != null, 'sectionType could not be null'
        CaseyResults<Item> results = new CaseyResults<Item>(
                items: [] as List,
        )

        if (!canSearch(sectionInfoNode)) {
            return Promise.pure(results)
        }
        OfferSearchParams searchParams = buildOfferSearchParams(sectionInfoNode, cursor, count, apiContext)
        return doSearch(searchParams, imageBuildType, apiContext)
    }

    @Override
    Promise<CaseyResults<Item>> search(String cmsPage, String cmsSlot, String cursor, Integer count, Images.BuildType imageBuildType, ApiContext apiContext) {
        Assert.notNull(cmsPage)
        Assert.notNull(cmsSlot)
        Assert.notNull(apiContext)
        OfferSearchParams searchParams = buildOfferSearchParams(cmsPage, cmsSlot, cursor, count, apiContext)
        return doSearch(searchParams, imageBuildType, apiContext)
    }
    
    @Override
    Promise<CaseyResults<Item>> search(ItemId itemId, Images.BuildType imageBuildType, ApiContext apiContext) {
        OfferSearchParams searchParams = buildOfferSearchParams(itemId, apiContext)
        return doSearch(searchParams, imageBuildType, apiContext)
    }

    @Override
    Promise<Map<String, AggregatedRatings>> getAggregatedRatings(ItemId itemId, ApiContext apiContext) {
        resourceContainer.caseyResource.getRatingByItemId(itemId.value).recover { Throwable throwable ->
            wrapAndThrow(throwable)
        }.then { CaseyResults<CaseyAggregateRating> results ->
            Map<String, AggregatedRatings> aggregatedRatingsMap = reviewBuilder.buildAggregatedRatingsMap(results?.items)
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
            wrapAndThrow(ex)
        }.then { CaseyResults<CaseyReview> results ->
            if (results == null) {
                return Promise.pure(new ReviewsResponse(
                        reviews: []
                ))
            }

            processCaseyResultsCursor(results)
            ReviewsResponse reviews = new ReviewsResponse()
            if (!org.springframework.util.StringUtils.isEmpty(results.cursorString)) {
                reviews.next = new ReviewsResponse.NextOption(
                        itemId: new ItemId(itemId),
                        cursor: results.cursorString,
                        count: results.count
                )
            }

            reviews.reviews = []
            Promise.each(results.items) { CaseyReview caseyReview ->
                getReviewAuthorName(caseyReview).then { String author ->
                    Review review = reviewBuilder.buildItemReview(caseyReview, author)
                    reviews.reviews << review
                    return Promise.pure()
                }
            }.then {
                return Promise.pure(reviews)
            }
        }
    }

    @Override
    Promise<CmsPage> getCmsPage(String path, String label, String country, String locale) {
        CmsPage page
        resourceContainer.caseyResource.getCmsPages(
            new CmsPageGetParams(path: StringUtils.isBlank(path) ? null : "\"${path}\"", label: StringUtils.isBlank(label) ? null : "\"${label}\"")
        ).then { CaseyResults<CmsPage> results ->
            if (CollectionUtils.isEmpty(results?.items) || results.items.size() > 1) {
                LOGGER.error('name=GetCmsPageIncorrectResponse, payload={}', ObjectMapperProvider.instance().writeValueAsString(results))
                throw new RuntimeException('Invalid Number Of CmsPage returned, should be 1')
            }
            page = results.items[0]
            if (CollectionUtils.isEmpty(page?.slots)) {
                return Promise.pure(page)
            }
            return fillPageContent(page, country, locale)
        }.recover { Throwable ex ->
            LOGGER.error('name=Store_GetCmsPage_Error, path={}, label={}', path, label, ex)
            return Promise.pure(page)
        }
    }

    @Override
    Promise<Review> addReview(AddReviewRequest request, ApiContext apiContext) {
        resourceContainer.caseyResource.getReviews(new ReviewSearchParams( // check the review already exists
                resourceType: 'item',
                resourceId: request.itemId.value,
                userId: apiContext.user,
        )).recover { Throwable ex ->
            wrapAndThrow(ex)
        }.then { CaseyResults<CaseyReview> caseyReviews ->
            boolean createReview = true
            CaseyReview review
            if (CollectionUtils.isEmpty(caseyReviews.items)) { // add new review
                review = reviewBuilder.buildCaseyReview(request, apiContext)
            } else { // update the existing one, only update ratings
                createReview = false
                review = caseyReviews.items[0]
                review.ratings = reviewBuilder.buildCaseyRatings(request.starRatings, review.ratings)
            }

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
                if (createReview) {
                    return resourceContainer.caseyReviewResource.addReview("Bearer $accessTokenResponse.accessToken", review).recover { Throwable ex ->
                        wrapAndThrow(ex)
                    }
                } else {
                    return resourceContainer.caseyReviewResource.putReview("Bearer $accessTokenResponse.accessToken", review.self.getId(), review).recover { Throwable ex ->
                        wrapAndThrow(ex)
                    }
                }
            }.then { CaseyReview newReview ->
                getReviewAuthorName(newReview).then { String author ->
                    return Promise.pure(reviewBuilder.buildItemReview(newReview, author))
                }
            }
        }
    }

    private Promise<CaseyResults<Item>> doSearch(OfferSearchParams searchParams, Images.BuildType imageBuildType, ApiContext apiContext) {
        CaseyResults<Item> results = new CaseyResults<Item>(
                items: [] as List
        )
        resourceContainer.caseyResource.searchOffers(searchParams).recover { Throwable ex ->
            wrapAndThrow(ex)
        }.then { CaseyResults<CaseyOffer> rawResults ->
            if (rawResults?.items == null) {
                return Promise.pure(results)
            }

            results.rawCursor = rawResults.rawCursor
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
                    aggregatedRatings[CaseyReview.RatingType.quality.name()] = reviewBuilder.buildAggregatedRatings(caseyItem?.qualityRating)
                    aggregatedRatings[CaseyReview.RatingType.comfort.name()] = reviewBuilder.buildAggregatedRatings(caseyItem?.comfortRating)
                    results.items << itemBuilder.buildItem(caseyOffer, aggregatedRatings, publisher, developer, imageBuildType, apiContext)
                    return Promise.pure()
                }
            }then {
                processCaseyResultsCursor(results)
                return Promise.pure(results)
            }
        }
    }

    private OfferSearchParams buildOfferSearchParams(SectionInfoNode sectionInfoNode, String cursor, Integer count, ApiContext apiContext) {
        OfferSearchParams offerSearchParams = new OfferSearchParams()
        switch (sectionInfoNode.sectionType) {
            case SectionInfoNode.SectionType.CmsSection:
                return buildOfferSearchParams(sectionInfoNode.cmsPageSearch?.toLowerCase(), sectionInfoNode.cmsSlot?.toLowerCase(), cursor, count, apiContext)
            case SectionInfoNode.SectionType.CategorySection:
                offerSearchParams.category = sectionInfoNode.category
                fillOfferSearchParamsCommon(cursor, count, apiContext, offerSearchParams)
                return offerSearchParams
        }
        Assert.isTrue(false, 'should not reach here')
    }

    private OfferSearchParams buildOfferSearchParams(String cmsPage, String cmsSlot, String cursor, Integer count, ApiContext apiContext) {
        OfferSearchParams offerSearchParams = new OfferSearchParams()
        offerSearchParams.cmsPage = cmsPage
        offerSearchParams.cmsSlot = cmsSlot
        fillOfferSearchParamsCommon(cursor, count, apiContext, offerSearchParams)
        return offerSearchParams
    }

    private OfferSearchParams buildOfferSearchParams(ItemId itemId, ApiContext apiContext) {
        OfferSearchParams offerSearchParams = new OfferSearchParams()
        offerSearchParams.itemId = itemId
        fillOfferSearchParamsCommon(null, null, apiContext, offerSearchParams)
        return offerSearchParams
    }

    private OfferSearchParams fillOfferSearchParamsCommon(String cursor, Integer count, ApiContext apiContext, OfferSearchParams offerSearchParams) {
        Assert.notNull(offerSearchParams)
        offerSearchParams.cursor = cursor
        offerSearchParams.count = count
        offerSearchParams.locale = apiContext.locale.getId().value
        offerSearchParams.country = apiContext.country.getId().value
        offerSearchParams.platform = apiContext.platform.value
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

    private Promise<CmsPage> fillPageContent(CmsPage cmsPage, String country, String locale) {
        assert cmsPage?.slots != null, 'cmsPage.slot should not be null'
        resourceContainer.caseyResource.getCmsSchedules(cmsPage?.self?.id, new CmsScheduleGetParams(country: country, locale: locale)).then { CmsSchedule cmsSchedule ->
            cmsPage.slots.each { Map.Entry<String, CmsContentSlot> entry ->
                String slot = entry.key
                CmsContentSlot content = entry.value
                if (content != null) {
                    content.contents = cmsSchedule?.slots?.get(slot)?.content?.getContents()
                }
            }
            return Promise.pure(cmsPage)
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

    private static void processCaseyResultsCursor(CaseyResults caseyResults) {
        if (caseyResults?.rawCursor == null) {
            return
        }
        if (caseyResults.rawCursor.isTextual() || caseyResults.rawCursor.isNull()) {
            caseyResults.cursorString = caseyResults.rawCursor.textValue()
            return
        } else {
            JsonNode element = caseyResults.rawCursor.get(CASEY_RESULTS_CURSOR_INDEX);
            if (element != null && (element.isNull() || element.isTextual())) {
                caseyResults.cursorString = element.textValue()
                return
            }
        }
        LOGGER.error("name=CaseyResults_Unknown_CursorFormat, payload={}", ObjectMapperProvider.instance().writeValueAsString(caseyResults.rawCursor))
    }

    private static void wrapAndThrow(Throwable throwable) {
        if (throwable instanceof CaseyException) {
            throw throwable
        }
        throw new CaseyException('Call_Casey_Error', throwable)
    }
}
