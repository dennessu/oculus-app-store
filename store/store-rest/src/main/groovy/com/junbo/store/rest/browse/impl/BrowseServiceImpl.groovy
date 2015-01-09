package com.junbo.store.rest.browse.impl

import com.fasterxml.jackson.core.type.TypeReference
import com.junbo.catalog.spec.enums.EntitlementType
import com.junbo.catalog.spec.enums.ItemType
import com.junbo.catalog.spec.model.item.Binary
import com.junbo.catalog.spec.model.item.ItemRevision
import com.junbo.common.error.AppCommonErrors
import com.junbo.common.id.ItemId
import com.junbo.common.json.ObjectMapperProvider
import com.junbo.common.util.IdFormatter
import com.junbo.entitlement.spec.model.DownloadUrlGetOptions
import com.junbo.entitlement.spec.model.DownloadUrlResponse
import com.junbo.langur.core.promise.Promise
import com.junbo.rating.spec.model.priceRating.RatingItem
import com.junbo.store.clientproxy.FacadeContainer
import com.junbo.store.clientproxy.ResourceContainer
import com.junbo.store.clientproxy.utils.ItemBuilder
import com.junbo.store.common.utils.CommonUtils
import com.junbo.store.rest.browse.BrowseService
import com.junbo.store.rest.browse.SectionService
import com.junbo.store.rest.challenge.ChallengeHelper
import com.junbo.store.rest.validator.ResponseValidator
import com.junbo.store.rest.validator.ReviewValidator
import com.junbo.store.spec.error.AppErrors
import com.junbo.store.spec.exception.casey.CaseyException
import com.junbo.store.spec.model.ApiContext
import com.junbo.store.spec.model.Challenge
import com.junbo.store.spec.model.browse.*
import com.junbo.store.spec.model.browse.document.*
import com.junbo.store.spec.model.external.sewer.casey.CaseyResults
import groovy.transform.CompileStatic
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.InitializingBean
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
class BrowseServiceImpl implements BrowseService, InitializingBean {

    private static final Logger LOGGER = LoggerFactory.getLogger(BrowseServiceImpl)

    private static final int DEFAULT_PAGE_SIZE = 10

    private static final int MAX_PAGE_SIZE = 50

    @Value('${store.tos.browsetostype}')
    private String storeBrowseTosType

    @Value('${store.browse.cmsPage.initialItems.path}')
    private String initialItemsCmsPagePath

    @Value('${store.browse.cmsPage.initialItems.slot}')
    private String initialItemsCmsPageSlot

    @Value('${store.browse.cmsPage.initialItems.contentName}')
    private String initialItemsCmsPageContentName

    @Value('${store.browse.cmsPage.initialItems.version.Map}')
    private String initialItemsVersionedMapString;

    @Resource(name = 'storeFacadeContainer')
    private FacadeContainer facadeContainer

    @Resource(name = 'storeResourceContainer')
    private ResourceContainer resourceContainer

    @Resource(name = 'storeChallengeHelper')
    private ChallengeHelper challengeHelper

    @Resource(name = 'storeSectionService')
    private SectionService sectionService

    @Resource(name = 'storeReviewValidator')
    private ReviewValidator reviewValidator

    @Resource(name = 'storeItemBuilder')
    private ItemBuilder itemBuilder

    @Resource(name = 'storeResponseValidator')
    private ResponseValidator responseValidator

    private Map<Integer, Map> initialItemsVersionedMap;

    private Set<String> libraryItemTypes = [
            ItemType.APP.name()
    ] as Set

    private Set<String> iapLibraryItemTypes = [
            ItemType.ADDITIONAL_CONTENT.name()
    ] as Set

    @Override
    Promise<DetailsResponse> getItem(ItemId itemId, boolean includeDetails, ApiContext apiContext) {
        Promise.pure().then {
            facadeContainer.caseyFacade.search(itemId, Images.BuildType.Item_Details, true, apiContext).then { CaseyResults<Item> results ->
                if (CollectionUtils.isEmpty(results?.items)) {
                    throw AppCommonErrors.INSTANCE.resourceNotFound('Item', itemId).exception()
                }
                return Promise.pure(results.items[0])
            }
        }.then { Item item ->
            decorateItem(true, includeDetails, apiContext, item).then {
                DetailsResponse response = new DetailsResponse(item: item)
                responseValidator.validateItemDetailsResponse(response)
                return Promise.pure(response)
            }
        }
    }

    @Override
    Promise<TocResponse> getToc(ApiContext apiContext) {
        TocResponse result = new TocResponse()
        challengeHelper.checkTosChallenge(apiContext.user, storeBrowseTosType, apiContext.country.getId(), null, apiContext.locale.getId()).then { Challenge challenge ->
            if (challenge != null) {
                return Promise.pure(new TocResponse(challenge: challenge))
            }

            sectionService.getTopLevelSectionInfoNode(apiContext).then { List<SectionInfoNode> sections ->
                result.sections = sections
                responseValidator.validateTocResponse(result)
                return Promise.pure(result)
            }
        }
    }

    @Override
    Promise<SectionLayoutResponse> getSectionLayout(SectionLayoutRequest request, ApiContext apiContext) {
        sectionService.getSectionInfoNode(request.category, request.criteria, apiContext).then { SectionInfoNode sectionInfoNode ->
            if (sectionInfoNode == null) {
                throw AppErrors.INSTANCE.sectionNotFound().exception()
            }

            SectionLayoutResponse response = new SectionLayoutResponse()
            response.breadcrumbs = generateBreadcrumbs(sectionInfoNode)
            response.name = sectionInfoNode.name
            response.children = sectionInfoNode.children?.collect {SectionInfoNode e -> e.toSectionInfo() }
            response.ordered = false
            response.category = request.category
            response.criteria = request.criteria

            responseValidator.validateSectionLayoutResponse(response)
            return Promise.pure(response)
        }
    }

    @Override
    Promise<ListResponse> getList(ListRequest request, ApiContext apiContext) {
        sectionService.getSectionInfoNode(request.category, request.criteria, apiContext).then { SectionInfoNode sectionInfoNode ->
            if (sectionInfoNode == null) {
                throw AppErrors.INSTANCE.sectionNotFound().exception()
            }
            innerGetList(request, sectionInfoNode, apiContext).then { ListResponse listResponse ->
                responseValidator.validateListResponse(listResponse)
                return Promise.pure(listResponse)
            }
        }
    }

    @Override
    Promise<InitialDownloadItemsResponse> getInitialDownloadItems(Integer version, ApiContext apiContext) {
        String path, slot;
        if (version != null && initialItemsVersionedMap.containsKey(version)) {
            path = initialItemsVersionedMap[version]['path'];
            slot = initialItemsVersionedMap[version]['slot'];
        } else {
            path = initialItemsCmsPagePath;
            slot = initialItemsCmsPageSlot;
        }
        facadeContainer.caseyFacade.getInitialDownloadItemsFromCmsPage(path, slot, initialItemsCmsPageContentName,
                apiContext).then { List<InitialDownloadItemsResponse.InitialDownloadItemEntry> list ->
            return Promise.pure(new InitialDownloadItemsResponse(items: list))
        }
    }

    @Override
    Promise<LibraryResponse> getLibrary(boolean isIAP, ItemId hostItemId, ApiContext apiContext) {
        LibraryResponse result = new LibraryResponse(items: [])
        EntitlementType entitlementType = isIAP ? EntitlementType.ALLOW_IN_APP : EntitlementType.DOWNLOAD
        Set<String> itemTypes = isIAP ? iapLibraryItemTypes : libraryItemTypes

        facadeContainer.entitlementFacade.getEntitlements(entitlementType, itemTypes, hostItemId, true, apiContext).then { List<com.junbo.store.spec.model.Entitlement> entitlementList ->
            result.items = entitlementList.collect {com.junbo.store.spec.model.Entitlement entitlement -> entitlement.itemDetails}.asList()
            if (isIAP) {
                return Promise.pure()
            }
            fillCurrentUserReview(result?.items, apiContext).then {
                return Promise.pure(result)
            }
        }.then {
            responseValidator.validateLibraryResponse(result)
            return Promise.pure(result)
        }
    }

    @Override
    Promise<DeliveryResponse> getDelivery(DeliveryRequest request, ApiContext apiContext) {
        ItemRevision itemRevision
        DeliveryResponse result = new DeliveryResponse()
        facadeContainer.entitlementFacade.checkEntitlements(apiContext.user, request.itemId, null).then { Boolean owned ->
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
        facadeContainer.caseyFacade.search(sectionInfoNode, request.cursor, request.count, Images.BuildType.Item_List, true,
                apiContext).then { CaseyResults<Item> caseyResults ->
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

    private Promise<Item> decorateItem(boolean ratePrice, boolean includeDetails, ApiContext apiContext, Item item) {
        Promise.pure().then {
            if (!ratePrice || item?.offer == null) {
                return Promise.pure()
            }
            if (item.offer.isFree) {
                item.offer.price = BigDecimal.ZERO
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
        facadeContainer.entitlementFacade.checkEntitlements(apiContext.user, item.getSelf(), null).then { Boolean owned ->
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
        }.then { // fill revision notes
            if (item?.itemType == ItemType.APP.name()) {
                if (item.appDetails != null) {
                    return facadeContainer.catalogFacade.getRevisionNotes(item.self, apiContext).then { List<RevisionNote> revisionNotes ->
                        item.appDetails.revisionNotes = revisionNotes
                        return Promise.pure()
                    }
                }
            }
            return Promise.pure()
        }
    }

    private static List<SectionInfo> generateBreadcrumbs(SectionInfoNode sectionInfoNode) {
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

    private int getPageSize(Integer count) {
        return (count == null || count <= 0) ? DEFAULT_PAGE_SIZE : Math.min(count, MAX_PAGE_SIZE)
    }

    @Override
    void afterPropertiesSet() throws Exception {
        initialItemsVersionedMap = (Map <Integer, Map>)ObjectMapperProvider.instance().readValue(initialItemsVersionedMapString,
                new TypeReference<Map<Integer, Map>>() {});
    }
}
