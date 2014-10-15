package com.junbo.store.rest.browse.impl
import com.junbo.catalog.spec.enums.EntitlementType
import com.junbo.catalog.spec.enums.ItemType
import com.junbo.catalog.spec.model.item.Binary
import com.junbo.catalog.spec.model.item.ItemRevision
import com.junbo.common.error.AppCommonErrors
import com.junbo.common.id.ItemId
import com.junbo.common.model.Results
import com.junbo.common.util.IdFormatter
import com.junbo.entitlement.spec.model.*
import com.junbo.langur.core.promise.Promise
import com.junbo.rating.spec.model.priceRating.RatingItem
import com.junbo.store.clientproxy.FacadeContainer
import com.junbo.store.clientproxy.ResourceContainer
import com.junbo.store.common.utils.CommonUtils
import com.junbo.store.rest.browse.BrowseService
import com.junbo.store.rest.browse.SectionService
import com.junbo.store.rest.challenge.ChallengeHelper
import com.junbo.store.rest.utils.CatalogUtils
import com.junbo.store.rest.validator.ReviewValidator
import com.junbo.store.spec.error.AppErrors
import com.junbo.store.spec.exception.casey.CaseyException
import com.junbo.store.spec.model.ApiContext
import com.junbo.store.spec.model.Challenge
import com.junbo.store.spec.model.browse.*
import com.junbo.store.spec.model.browse.document.Item
import com.junbo.store.spec.model.browse.document.Review
import com.junbo.store.spec.model.browse.document.SectionInfo
import com.junbo.store.spec.model.browse.document.SectionInfoNode
import com.junbo.store.spec.model.external.casey.CaseyResults
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
 * The BrowseServiceImpl class.
 */
@Component('storeBrowseService')
@CompileStatic
class BrowseServiceImpl implements BrowseService {

    private static final Logger LOGGER = LoggerFactory.getLogger(BrowseServiceImpl)

    private static final int DEFAULT_PAGE_SIZE = 10

    private static final int MAX_PAGE_SIZE = 50

    @Value('${store.tos.browse}')
    private String storeBrowseTos

    @Resource(name = 'storeFacadeContainer')
    private FacadeContainer facadeContainer

    @Resource(name = 'storeResourceContainer')
    private ResourceContainer resourceContainer

    @Resource(name = 'storeChallengeHelper')
    private ChallengeHelper challengeHelper

    @Resource(name = 'storeCatalogUtils')
    private CatalogUtils catalogUtils

    @Resource(name = 'storeSectionService')
    private SectionService sectionService

    @Resource(name = 'storeLocaleUtils')
    private LocaleUtils localeUtils

    @Resource(name = 'storeReviewValidator')
    private ReviewValidator reviewValidator

    private Set<String> libraryItemTypes = [
            ItemType.APP.name()
    ] as Set

    @Override
    Promise<Item> getItem(ItemId itemId, boolean useSearch, boolean includeDetails, ApiContext apiContext) {
        Promise.pure().then {
            if (!useSearch) {
                return facadeContainer.catalogFacade.getItem(itemId, Images.BuildType.Item_Details, apiContext)
            }
            facadeContainer.caseyFacade.search(itemId, Images.BuildType.Item_Details, apiContext).then { CaseyResults<Item> results ->
                if (CollectionUtils.isEmpty(results?.items)) {
                    throw AppCommonErrors.INSTANCE.resourceNotFound('Item', itemId).exception()
                }
                return Promise.pure(results.items[0])
            }
        }.then { Item item ->
            decorateItem(true, includeDetails, apiContext, item)
        }
    }

    @Override
    Promise<TocResponse> getToc(ApiContext apiContext) {
        TocResponse result = new TocResponse()
        challengeHelper.checkTosChallenge(apiContext.user, storeBrowseTos, apiContext.country.getId(), null).then { Challenge challenge ->
            if (challenge != null) {
                return Promise.pure(new TocResponse(challenge: challenge))
            }

            result.sections = sectionService.getTopLevelSectionInfoNode(apiContext)
            return Promise.pure(result)
        }
    }

    @Override
    Promise<SectionLayoutResponse> getSectionLayout(SectionLayoutRequest request, ApiContext apiContext) {
        SectionInfoNode sectionInfoNode = sectionService.getSectionInfoNode(request.category, request.criteria, apiContext)
        if (sectionInfoNode == null) {
            throw AppErrors.INSTANCE.sectionNotFound().exception()
        }

        SectionLayoutResponse response = new SectionLayoutResponse()
        response.breadcrumbs = generateBreadcrumbs(sectionInfoNode, apiContext)
        response.name = sectionInfoNode.name
        response.children = sectionInfoNode.children?.collect {SectionInfoNode e -> e.toSectionInfo() }
        response.ordered = false
        response.category = request.category
        response.criteria = request.criteria
        return Promise.pure(response)
    }

    @Override
    Promise<ListResponse> getList(ListRequest request, ApiContext apiContext) {
        SectionInfoNode sectionInfoNode = sectionService.getSectionInfoNode(request.category, request.criteria, apiContext)
        if (sectionInfoNode == null) {
            throw AppErrors.INSTANCE.sectionNotFound().exception()
        }
        innerGetList(request, sectionInfoNode, apiContext)
    }

    @Override
    Promise<LibraryResponse> getLibrary(ApiContext apiContext) {
        LibraryResponse result = new LibraryResponse(items: [])
        Set<String> itemIdSet = [] as Set
        PageMetadata pageMetadata = new PageMetadata()
        EntitlementSearchParam searchParam = new EntitlementSearchParam(userId: apiContext.user, type: EntitlementType.DOWNLOAD.name(), isActive: true)
        CommonUtils.loop {
            resourceContainer.entitlementResource.searchEntitlements(searchParam, pageMetadata).then { Results<Entitlement> results ->
                Promise.each(results.items) { Entitlement entitlement ->
                    if (itemIdSet.contains(entitlement.itemId)) {
                        LOGGER.warn('name=Store_Library_Duplicate_Item_Found, itemId={}, userId={}', entitlement.itemId, apiContext.user)
                        return Promise.pure()
                    }
                    itemIdSet << entitlement.itemId
                    getLibraryItemFromEntitlement(entitlement, apiContext).then { Item item ->
                        if (item != null) {
                            result.items << item
                        }
                        return Promise.pure()
                    }
                }.then {
                    String cursor = CommonUtils.getQueryParam(results.next?.href, 'bookmark')
                    if (results.items.isEmpty() || StringUtils.isEmpty(cursor)) {
                        return Promise.pure(Promise.BREAK)
                    }
                    pageMetadata.bookmark = cursor
                    return Promise.pure()
                }
            }
        }.then {
            fillCurrentUserReview(result?.items, apiContext).then {
                return Promise.pure(result)
            }
        }
    }

    @Override
    Promise<DeliveryResponse> getDelivery(DeliveryRequest request, ApiContext apiContext) {
        ItemRevision itemRevision
        DeliveryResponse result = new DeliveryResponse()
        catalogUtils.checkItemOwnedByUser(request.itemId, apiContext.user).then { Boolean owned ->
            if (!owned) {
                throw AppErrors.INSTANCE.itemNotPurchased().exception()
            }
            return Promise.pure()
        }.then {
            facadeContainer.catalogFacade.getAppItemRevision(request.itemId, request.desiredVersionCode, apiContext).then { ItemRevision e ->
                if (e == null) {
                    throw AppErrors.INSTANCE.itemVersionCodeNotFound().exception()
                }
                itemRevision = e
                return resourceContainer.downloadUrlResource.getDownloadUrl(request.itemId, new DownloadUrlGetOptions(itemRevisionId: e.getRevisionId(), platform: apiContext.platform.value)).then { DownloadUrlResponse response ->
                    result.downloadUrl = response.redirectUrl
                    Binary binary = itemRevision.binaries?.get(apiContext.platform.value)
                    result.downloadSize = binary?.size
                    result.signature = binary?.md5
                    return Promise.pure(result)
                }
            }
        }
    }

    @Override
    Promise<ReviewsResponse> getReviews(ReviewsRequest request, ApiContext apiContext) {
        return facadeContainer.caseyFacade.getReviews(request.itemId.value, null, request.cursor, getPageSize(request.count))
    }

    @Override
    Promise<AddReviewResponse> addReview(AddReviewRequest request, ApiContext apiContext) {
        reviewValidator.validateAddReview(request, apiContext).then {
            return facadeContainer.caseyFacade.addReview(request, apiContext).then { Review review ->
                return Promise.pure(new AddReviewResponse(review: review))
            }
        }
    }

    private Promise<ListResponse> innerGetList(ListRequest request, SectionInfoNode sectionInfoNode, ApiContext apiContext) {
        ListResponse listResponse = new ListResponse(items: [])
        facadeContainer.caseyFacade.search(sectionInfoNode, request.cursor, request.count, Images.BuildType.Item_List, apiContext).then { CaseyResults<Item> caseyResults ->
            if (caseyResults.cursorString != null) {
                listResponse.next = new ListResponse.NextOption(
                        cursor: caseyResults.cursorString,
                        count: getPageSize(request.count),
                        category: request.category,
                        criteria: request.criteria
                )
            }
            Promise.each(caseyResults.items) { Item item ->
                listResponse.items << item
                decorateItem(true, false, apiContext, item)
            }
        }.then {
            return Promise.pure(listResponse)
        }
    }

    private SectionInfoNode getSectionInfoNode(String category, String criteria, List<SectionInfoNode> sections, Stack<SectionInfo> parents) {
        for (SectionInfoNode sectionInfoNode : sections) {

            if (sectionInfoNode.category == category && sectionInfoNode.criteria == criteria) {
                return sectionInfoNode
            }

            if (!CollectionUtils.isEmpty(sectionInfoNode.children)) {
                parents.push(sectionInfoNode.toSectionInfo())
                def result = getSectionInfoNode(category, criteria, sectionInfoNode.children, parents)
                if (result != null) {
                    return result
                }
                parents.pop()
            }

        }
        return null
    }

    private Promise<Item> getLibraryItemFromEntitlement(Entitlement entitlement, ApiContext apiContext) {
        resourceContainer.itemResource.getItem(entitlement.itemId).then { com.junbo.catalog.spec.model.item.Item catalogItem ->
            if (!libraryItemTypes.contains(catalogItem.type)) {
                return Promise.pure(null)
            }
            getItem(new ItemId(entitlement.itemId), false, false, apiContext).then { Item item ->
                item.ownedByCurrentUser = true
                return Promise.pure(item)
            }
        }
    }

    private Promise<Item> decorateItem(boolean ratePrice, boolean includeDetails, ApiContext apiContext, Item item) {
        Promise.pure().then {
            if (!ratePrice || item?.offer == null) {
                return Promise.pure()
            }
            facadeContainer.priceRatingFacade.rateOffer(item.offer.self, apiContext).then { RatingItem ratingItem ->
                item.offer.price = ratingItem?.finalTotalAmount
                return Promise.pure()
            }
        }.then {
            if (!includeDetails) {
                return Promise.pure()
            } else {
                fillItemDetails(item, apiContext)
            }
        }.then {
            return Promise.pure(item)
        }
    }

    private Promise fillItemDetails(Item item, ApiContext apiContext) {
        catalogUtils.checkItemOwnedByUser(item.getSelf(), apiContext.user).then { Boolean owned ->
            item.ownedByCurrentUser = owned
            return Promise.pure()
        }.then { // get current user review
            facadeContainer.caseyFacade.getReviews(item.self.value, apiContext.user, null, null).recover { Throwable ex ->
                if (ex instanceof CaseyException) {
                    LOGGER.error('name=FillItemDetails_GetCurrentUserReview_Error, item={}, user={}', item?.self, IdFormatter.encodeId(apiContext.user), ex)
                    return Promise.pure()
                }
                throw ex
            }.then { ReviewsResponse reviewsResponse ->
                if (CollectionUtils.isEmpty(reviewsResponse?.reviews)) {
                    item.currentUserReview = null
                } else {
                    item.currentUserReview = reviewsResponse.reviews.get(0)
                }
                return Promise.pure()
            }
        }.then { // get the review response
            facadeContainer.caseyFacade.getReviews(item.self.value, null, null, DEFAULT_PAGE_SIZE).recover { Throwable ex ->
                if (ex instanceof CaseyException) {
                    LOGGER.error('name=FillItemDetails_GetReviews_Error, item={}', item?.self, ex)
                    return Promise.pure()
                }
                throw ex
            }.then { ReviewsResponse reviewsResponse ->
                item.reviews = reviewsResponse
                return Promise.pure()
            }
        }
    }

    private static List<SectionInfo> generateBreadcrumbs(SectionInfoNode sectionInfoNode, ApiContext apiContext) {
        List<SectionInfo> breadCrumbs = []
        while (sectionInfoNode.parent != null) {
            breadCrumbs << sectionInfoNode.parent.toSectionInfo()
            sectionInfoNode = sectionInfoNode.parent
        }
        return breadCrumbs.reverse()
    }

    private Promise fillCurrentUserReview(List<Item> items, ApiContext apiContext) {
        if (CollectionUtils.isEmpty(items)) {
            return Promise.pure()
        }

        Map<ItemId, Review> itemIdReviewMap = [:] as Map<ItemId, Review>
        items.each { Item item ->
            itemIdReviewMap.put(item.self, null)
        }

        String cursor = null
        CommonUtils.loop {
            facadeContainer.caseyFacade.getReviews(null, apiContext.user, cursor, null).recover { Throwable ex ->
                if (ex instanceof CaseyException) {
                    LOGGER.error('name=GetLibrary_GetReviewByUser_Error, user={}', IdFormatter.encodeId(apiContext.user), ex)
                    return Promise.pure()
                }
                throw ex
            }.then { ReviewsResponse response ->
                if (response == null) {
                    return Promise.pure(Promise.BREAK)
                }

                if (!CollectionUtils.isEmpty(response.reviews)) {
                    response.reviews.each { Review review ->
                        if (review.itemId != null && itemIdReviewMap.containsKey(review.itemId)) {
                            itemIdReviewMap[review.itemId] = review
                        }
                    }
                }
                if (CollectionUtils.isEmpty(response.reviews) || response.next == null) {
                    return Promise.pure(Promise.BREAK)
                }
                Assert.isTrue(!StringUtils.isEmpty(response.next.cursor))
                cursor = response.next.cursor
                return Promise.pure()
            }
        }.then {
            items.each { Item item ->
                item.currentUserReview = itemIdReviewMap[item.self]
            }
            return Promise.pure()
        }
    }

    int getPageSize(Integer count) {
        return (count == null || count <= 0) ? DEFAULT_PAGE_SIZE : Math.min(count, MAX_PAGE_SIZE)
    }
}
