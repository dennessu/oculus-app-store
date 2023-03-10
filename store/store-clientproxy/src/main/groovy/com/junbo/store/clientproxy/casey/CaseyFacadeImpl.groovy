package com.junbo.store.clientproxy.casey

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
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
import com.junbo.store.clientproxy.error.ErrorCodes
import com.junbo.store.clientproxy.utils.ItemBuilder
import com.junbo.store.clientproxy.utils.ReviewBuilder
import com.junbo.store.spec.error.AppErrors
import com.junbo.store.spec.exception.casey.CaseyException
import com.junbo.store.spec.model.ApiContext
import com.junbo.store.spec.model.browse.AddReviewRequest
import com.junbo.store.spec.model.browse.Images
import com.junbo.store.spec.model.browse.InitialDownloadItemsResponse
import com.junbo.store.spec.model.browse.ReviewsResponse
import com.junbo.store.spec.model.browse.document.AggregatedRatings
import com.junbo.store.spec.model.browse.document.Item
import com.junbo.store.spec.model.browse.document.Review
import com.junbo.store.spec.model.browse.document.SectionInfoNode
import com.junbo.store.spec.model.external.sewer.SewerParam
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

    private final static String CMSPAGE_GET_EXPAND = '(schedule)'

    private final static String CMSPAGE_LIST_EXPAND = '(results(schedule))'

    private final static String INVALID_COUNTRY_DETAIL = 'Invalid country'.toLowerCase()

    private final static ObjectMapper objectMapper;

    @Resource(name = 'store.jsonMessageCaseyTranscoder')
    JsonMessageCaseyTranscoder jsonMessageCaseyTranscoder

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
    Promise<CaseyResults<Item>> search(SectionInfoNode sectionInfoNode, String cursor, Integer count,
                                       Images.BuildType imageBuildType, boolean includeOrganization, ApiContext apiContext) {
        assert sectionInfoNode.sectionType != null, 'sectionType could not be null'
        CaseyResults<Item> results = new CaseyResults<Item>(
                items: [] as List,
        )

        if (!canSearch(sectionInfoNode)) {
            return Promise.pure(results)
        }
        OfferSearchParams searchParams = buildOfferSearchParams(sectionInfoNode, cursor, count, apiContext)
        return doSearch(searchParams, imageBuildType, includeOrganization, apiContext)
    }

    @Override
    Promise<CaseyResults<Item>> search(String cmsPage, String cmsSlot, String cursor, Integer count, Images.BuildType imageBuildType,
                                       boolean includeOrganization, ApiContext apiContext) {
        Assert.notNull(cmsPage)
        Assert.notNull(cmsSlot)
        Assert.notNull(apiContext)
        OfferSearchParams searchParams = buildOfferSearchParams(cmsPage, cmsSlot, cursor, count, apiContext)
        return doSearch(searchParams, imageBuildType, includeOrganization, apiContext)
    }

    @Override
    Promise<CaseyResults<Item>> search(ItemId itemId, Images.BuildType imageBuildType, boolean includeOrganization,  ApiContext apiContext) {
        OfferSearchParams searchParams = buildOfferSearchParams(itemId, apiContext)
        return doSearch(searchParams, imageBuildType, includeOrganization, apiContext)
    }

    @Override
    Promise<CaseyResults<CaseyOffer>> searchRaw(String cmsPage, String cmsSlot, String cursor, Integer count, ApiContext apiContext) {
        Assert.notNull(cmsPage)
        Assert.notNull(cmsSlot)
        Assert.notNull(apiContext)
        OfferSearchParams searchParams = buildOfferSearchParams(cmsPage, cmsSlot, cursor, count, apiContext)
        return doRawSearch(searchParams)
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
        Map<String, String> userIdAuthorNameMap = [:] as Map

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
                String reviewUserId = caseyReview?.user?.getId()?.trim()
                if (userIdAuthorNameMap.containsKey(reviewUserId)) {
                    reviews.reviews <<  reviewBuilder.buildItemReview(caseyReview, userIdAuthorNameMap[reviewUserId])
                    return Promise.pure()
                }
                getReviewAuthorName(reviewUserId).then { String author ->
                    userIdAuthorNameMap[reviewUserId] = author
                    reviews.reviews << reviewBuilder.buildItemReview(caseyReview, author)
                    return Promise.pure()
                }
            }.then {
                return Promise.pure(reviews)
            }
        }
    }

    @Override
    Promise<CmsPage> getCmsPage(String path, String label, String country, String locale) {
        resourceContainer.caseyResource.getCmsPages(
            new CmsPageGetParams(path: StringUtils.isBlank(path) ? null : "\"${path}\"", label: StringUtils.isBlank(label) ? null : "\"${label}\""),
            new SewerParam(country: country, locale: locale, expand: CMSPAGE_LIST_EXPAND)
        ).then { CaseyResults<JsonNode> results ->
            if (CollectionUtils.isEmpty(results?.items) || results.items.size() > 1) {
                LOGGER.error('name=GetCmsPageIncorrectResponse, payload={}', ObjectMapperProvider.instanceNotStrict().writeValueAsString(results))
                throw new RuntimeException('Invalid Number Of CmsPage returned, should be 1')
            }
            return Promise.pure(results.items[0] == null ? null :
                    jsonMessageCaseyTranscoder.objectMapper.readValue(results.items[0].traverse(), new TypeReference<CmsPage>() {}))
        }.recover { Throwable ex ->
            LOGGER.error('name=Store_GetCmsPage_Error, path={}, label={}', path, label, ex)
            return Promise.pure(null)
        }
    }

    @Override
    Promise<CmsPage> getCmsPage(String cmsPageId, String country, String locale) {
        resourceContainer.caseyResource.getCmsPages(cmsPageId, new SewerParam(country: country, locale: locale, expand: CMSPAGE_GET_EXPAND)).then { JsonNode pageNode ->
            return Promise.pure(pageNode == null ? null : jsonMessageCaseyTranscoder.objectMapper.readValue(pageNode.traverse(), new TypeReference<CmsPage>() {}))
        }.recover { Throwable ex ->
            LOGGER.error('name=Store_GetCmsPage_Error, id={}', cmsPageId, ex)
            return Promise.pure(null)
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
                        if (isCaseyError(ex, ErrorCodes.Casey.ReviewCreateError)) {
                            throw AppErrors.INSTANCE.retryableError().exception()
                        }
                        wrapAndThrow(ex)
                    }
                } else {
                    return resourceContainer.caseyReviewResource.putReview("Bearer $accessTokenResponse.accessToken", review.self.getId(), review).recover { Throwable ex ->
                        if (isCaseyError(ex, ErrorCodes.Casey.ReviewUpdateError)) {
                            throw AppErrors.INSTANCE.retryableError().exception()
                        }
                        wrapAndThrow(ex)
                    }
                }
            }.then { CaseyReview newReview ->
                getReviewAuthorName(newReview.user?.getId()?.trim()).then { String author ->
                    return Promise.pure(reviewBuilder.buildItemReview(newReview, author))
                }
            }
        }
    }

    @Override
    Promise<List<InitialDownloadItemsResponse.InitialDownloadItemEntry>> getInitialDownloadItemsFromCmsPage(String pagePath, String pageSlot, String contentName, ApiContext apiContext) {
        String expand = "(results(schedule(slots(${pageSlot}(content(contents(${contentName}(links(currentRevision)))))))))"
        String resultProperties =
                "(results(schedule(slots(${pageSlot}(content(contents(${contentName}(links(currentRevision(item,packageName,locales(${apiContext.locale.getId().value}(name))))))))))))"

        Set<ItemId> added = [] as Set
        resourceContainer.caseyResource.getCmsPages(new CmsPageGetParams(path: StringUtils.isBlank(pagePath) ? null : "\"${pagePath}\""),
                new SewerParam(country: apiContext.country.getId().value,  locale: apiContext.locale.getId().value, expand: expand,
                        resultProperties: resultProperties)).then { CaseyResults<JsonNode> cmsPageResults ->
            List<InitialDownloadItemsResponse.InitialDownloadItemEntry> results = []

            if (cmsPageResults?.items == null) {
                return Promise.pure(results)
            }

            cmsPageResults.items.each { JsonNode cmsPage ->
                JsonNode linkNodes = cmsPage?.get('schedule')?.get('slots')?.get(pageSlot)?.get('content')?.get('contents')?.get(contentName)?.get('links')
                if (linkNodes == null || !linkNodes.isArray()) {
                    return
                }

                for (int i = 0;i < linkNodes.size();++i) {
                    JsonNode itemRevision = linkNodes.get(i)?.get('currentRevision')
                    if (itemRevision == null || itemRevision.isNull()) {
                        continue
                    }
                    String itemId = itemRevision.get('item')?.get('id')?.asText()
                    if (itemId != null) {
                        InitialDownloadItemsResponse.InitialDownloadItemEntry entry = new InitialDownloadItemsResponse.InitialDownloadItemEntry()
                        entry.item = new ItemId(itemId)
                        if (added.contains(entry.item)) {
                            continue
                        }
                        entry.name = itemRevision.get('locales')?.get(apiContext.locale.getId().value)?.get('name')?.asText()
                        entry.packageName = itemRevision.get('packageName')?.asText()
                        added << entry.item
                        results << entry
                    }
                }
            }
            return Promise.pure(results)
        }.recover { Throwable ex ->
            if (isCaseyError(ex)) {
                LOGGER.error('name=CaseyError_GetInitialItems', ex)
                return Promise.pure([] as List)
            }
            throw ex
        }
    }

    private Promise<CaseyResults<CaseyOffer>> doRawSearch(OfferSearchParams searchParams) {
        resourceContainer.caseyResource.searchOffers(searchParams).recover { Throwable ex ->
            wrapAndThrow(ex)
        }.then { CaseyResults<CaseyOffer> results ->
            processCaseyResultsCursor(results)
            return Promise.pure(results)
        }
    }

    private Promise<CaseyResults<Item>> doSearch(OfferSearchParams searchParams, Images.BuildType imageBuildType, boolean includeOrganization, ApiContext apiContext) {
        Map<OrganizationId, Organization> organizationMap = [:] as Map
        CaseyResults<Item> results = new CaseyResults<Item>(
                items: [] as List
        )
        resourceContainer.caseyResource.searchOffers(searchParams).recover { Throwable ex ->
            if (isInvalidCountryCaseyError(ex)) {
                LOGGER.error('name=CaseyInvalidCountryError', ex);
                throw AppErrors.INSTANCE.unsupportedCountry().exception()
            }
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
                String publisher, developer
                Promise.pure().then {
                    if (!includeOrganization) {
                        return Promise.pure()
                    }
                    if (caseyOffer.publisher.organizationId == null) { // new organization format from search
                        publisher = caseyOffer.publisher.name
                        developer = caseyItem?.developer?.name
                        return Promise.pure()
                    } else { // old organization format from search
                        OrganizationId publisherId = new OrganizationId(IdFormatter.decodeId(OrganizationId, caseyOffer.publisher.organizationId))
                        getOrganization(publisherId, organizationMap).then { Organization organization ->
                            publisher = organization.name
                            return Promise.pure()
                        }.then {
                            OrganizationId developerId = caseyItem?.developer?.organizationId == null ? null : new OrganizationId(IdFormatter.decodeId(OrganizationId, caseyItem?.developer?.organizationId))
                            getOrganization(developerId, organizationMap).then { Organization organization ->
                                developer = organization.name
                                return Promise.pure()
                            }
                        }
                    }

                }.then {
                    Map<String, AggregatedRatings> aggregatedRatings = [:] as Map
                    aggregatedRatings[CaseyReview.RatingType.quality.name()] = reviewBuilder.buildAggregatedRatings(caseyItem?.qualityRating)
                    aggregatedRatings[CaseyReview.RatingType.comfort.name()] = reviewBuilder.buildAggregatedRatings(caseyItem?.comfortRating)
                    results.items << itemBuilder.buildItem(caseyOffer, aggregatedRatings, publisher, developer, imageBuildType, apiContext)
                    return Promise.pure()
                }.recover { Throwable ex ->
                    LOGGER.error('name=Process_Offer_Error', ex)
                    return Promise.pure()
                }
            }.then {
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

    private Promise<Organization> getOrganization(OrganizationId organizationId, Map<OrganizationId, Organization> organizationMap) {
        Organization cached = organizationMap?.get(organizationId)
        if (cached != null) {
            return Promise.pure(cached)
        }
        Promise.pure().then {
            resourceContainer.organizationResource.get(organizationId, new OrganizationGetOptions())
        }.recover { Throwable ex ->
            LOGGER.error('name=Store_Get_Organization_Fail, organization={}', organizationId, ex)
            return Promise.pure()
        }
    }

    private Promise<String> getReviewAuthorName(String userId) {
        Promise.pure().then {
            resourceContainer.userResource.get(new UserId(IdFormatter.decodeId(UserId, userId)), new UserGetOptions()).then { User user ->
                if (!StringUtils.isBlank(user.nickName)) {
                    return Promise.pure(user.nickName)
                }
                return resourceContainer.userPersonalInfoResource.get(user.username, new UserPersonalInfoGetOptions()).then { UserPersonalInfo username ->
                    return Promise.pure(ObjectMapperProvider.instanceNotStrict().treeToValue(username.value, UserLoginName).userName)
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
        LOGGER.error("name=CaseyResults_Unknown_CursorFormat, payload={}", ObjectMapperProvider.instanceNotStrict().writeValueAsString(caseyResults.rawCursor))
    }

    private static void wrapAndThrow(Throwable throwable) {
        if (throwable instanceof CaseyException) {
            throw throwable
        }
        throw new CaseyException('Call_Casey_Error', null, null, throwable)
    }

    private static boolean isCaseyError(Throwable throwable, String... errorCode) {
        if (throwable instanceof  CaseyException) {
            if (errorCode == null || errorCode.length == 0 || Arrays.asList(errorCode).contains(throwable.errorCode)) {
                return true
            }
        }
        return false
    }

    private static boolean isInvalidCountryCaseyError(Throwable throwable) {
        if (!isCaseyError(throwable, ErrorCodes.Casey.LinkedResourceValidationFailed)) {
            return false
        }
        CaseyException caseyException = (CaseyException) (throwable);
        return caseyException.details != null &&
                caseyException.details.toString().toLowerCase().contains(INVALID_COUNTRY_DETAIL)
    }
}
