package com.junbo.store.rest.browse.impl
import com.junbo.catalog.spec.enums.EntitlementType
import com.junbo.catalog.spec.enums.ItemType
import com.junbo.catalog.spec.model.attribute.OfferAttribute
import com.junbo.catalog.spec.model.common.SimpleLocaleProperties
import com.junbo.catalog.spec.model.item.Binary
import com.junbo.catalog.spec.model.item.ItemRevision
import com.junbo.common.error.AppCommonErrors
import com.junbo.common.id.ItemId
import com.junbo.common.model.Results
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
import com.junbo.store.spec.error.AppErrors
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

    @Override
    Promise<Item> getItem(ItemId itemId, boolean checkAvailable, boolean includeDetails, ApiContext apiContext) {
        Promise.pure().then {
            if (!checkAvailable) {
                return Promise.pure()
            }
            facadeContainer.caseyFacade.itemAvailable(itemId, apiContext).then { Boolean available ->
                if (!available) {
                    throw AppCommonErrors.INSTANCE.resourceNotFound('Item', itemId).exception()
                }
                return Promise.pure()
            }
        }.then {
            facadeContainer.catalogFacade.getItem(itemId, apiContext).then { Item item ->
                decorateItem(true, includeDetails, apiContext, item)
            }
        }
    }

    @Override
    Promise<TocResponse> getToc(ApiContext apiContext) {
        TocResponse result = new TocResponse()
        challengeHelper.checkTosChallenge(apiContext.user, storeBrowseTos, null).then { Challenge challenge ->
            if (challenge != null) {
                return Promise.pure(new TocResponse(challenge: challenge))
            }

            List<SectionInfoNode> sectionInfoNodes = sectionService.getTopLevelSectionInfoNode()
            result.sections = sectionInfoNodes.collect { SectionInfoNode sectionInfoNode ->
                return buildSectionInfoNodeForResponse(sectionInfoNode, true, apiContext)
            }
            return Promise.pure(result)
        }
    }

    @Override
    Promise<SectionLayoutResponse> getSectionLayout(SectionLayoutRequest request, ApiContext apiContext) {
        SectionInfoNode rawSectionInfoNode = sectionService.getSectionInfoNode(request.category, request.criteria)
        if (rawSectionInfoNode == null) {
            throw AppErrors.INSTANCE.sectionNotFound().exception()
        }

        SectionLayoutResponse response = new SectionLayoutResponse()
        response.breadcrumbs = generateBreadcrumbs(rawSectionInfoNode, apiContext)
        SectionInfoNode sectionInfoNode = buildSectionInfoNodeForResponse(rawSectionInfoNode, true, apiContext)
        response.name = sectionInfoNode.name
        response.children = sectionInfoNode.children?.collect {SectionInfoNode e -> e.toSectionInfo() }
        response.ordered = false

        SectionInfoNode parent = sectionInfoNode.parent
        while (parent != null) {
            response.breadcrumbs << parent.toSectionInfo()
            parent = parent.parent
        }

        innerGetList(new ListRequest(criteria: request.criteria, category: request.category, count: request.count), rawSectionInfoNode, apiContext).then { ListResponse listResponse ->
            response.items = listResponse.items
            response.next = listResponse.next
            return Promise.pure(response)
        }
    }

    @Override
    Promise<ListResponse> getList(ListRequest request, ApiContext apiContext) {
        SectionInfoNode sectionInfoNode = sectionService.getSectionInfoNode(request.category, request.criteria)
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
                    getAppItemFromEntitlement(entitlement, apiContext).then { Item item ->
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
            return Promise.pure(result)
        }
    }

    @Override
    Promise<DeliveryResponse> getDelivery(DeliveryRequest request, ApiContext apiContext) {
        ItemRevision itemRevision
        DeliveryResponse result = new DeliveryResponse()
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

    @Override
    Promise<ReviewsResponse> getReviews(ReviewsRequest request, ApiContext apiContext) {
        return facadeContainer.caseyFacade.getReviews(request.itemId.value, null, request.cursor, request.count)
    }

    @Override
    Promise<AddReviewResponse> addReview(AddReviewRequest request, ApiContext apiContext) {
        facadeContainer.caseyFacade.getReviews(request.itemId.value, apiContext.user, null, null).then { ReviewsResponse response ->
            if (!CollectionUtils.isEmpty(response?.reviews)) {
                throw AppErrors.INSTANCE.reviewAlreadyExists().exception()
            }
            return Promise.pure()
        }.then {
            return facadeContainer.caseyFacade.addReview(request, apiContext).then { Review review ->
                return Promise.pure(new AddReviewResponse(review: review))
            }
        }
    }

    private Promise<ListResponse> innerGetList(ListRequest request, SectionInfoNode sectionInfoNode, ApiContext apiContext) {
        ListResponse listResponse = new ListResponse(items: [])
        facadeContainer.caseyFacade.search(sectionInfoNode, request.cursor, request.count, apiContext).then { CaseyResults<Item> caseyResults ->
            if (caseyResults.cursor != null) {
                listResponse.next = new ListResponse.NextOption(
                        cursor: caseyResults.cursor,
                        count: request.count,
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

    private Promise<Item> getAppItemFromEntitlement(Entitlement entitlement, ApiContext apiContext) {
        resourceContainer.itemResource.getItem(entitlement.itemId).then { com.junbo.catalog.spec.model.item.Item catalogItem ->
            if (catalogItem.type != ItemType.APP.name()) {
                return Promise.pure(null)
            }
            getItem(new ItemId(catalogItem.getItemId()), false, false, apiContext).then { Item item ->
                item.ownedByCurrentUser = true
                return Promise.pure(item)
            }
        }
    }

    private SectionInfoNode buildSectionInfoNodeForResponse(SectionInfoNode rawNode, boolean recursive, ApiContext apiContext) {
        SectionInfoNode sectionInfoNode = new SectionInfoNode()
        if (rawNode.sectionType == SectionInfoNode.SectionType.CategorySection && rawNode?.categoryId != null) {
            OfferAttribute offerAttribute = facadeContainer.catalogFacade.getOfferAttribute(rawNode?.categoryId, apiContext).get()
            SimpleLocaleProperties simpleLocaleProperties = offerAttribute?.locales?.get(apiContext.locale.getId().value)
            sectionInfoNode.name = simpleLocaleProperties?.name
        } else {
            sectionInfoNode.name = rawNode.name
        }
        sectionInfoNode.category = rawNode.category
        sectionInfoNode.criteria = rawNode.criteria
        sectionInfoNode.children = []

        if (recursive) {
            sectionInfoNode.children = rawNode.children.collect { SectionInfoNode e ->
                return buildSectionInfoNodeForResponse(e, recursive, apiContext)
            }
        }
        return sectionInfoNode
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
            facadeContainer.caseyFacade.getReviews(item.self.value, apiContext.user, null, null).then { ReviewsResponse reviewsResponse ->
                if (CollectionUtils.isEmpty(reviewsResponse?.reviews)) {
                    item.currentUserReview = null
                } else {
                    item.currentUserReview = reviewsResponse.reviews.get(0)
                }
                return Promise.pure()
            }
        }.then { // get the review response
            facadeContainer.caseyFacade.getReviews(item.self.value, null, null, null).then { ReviewsResponse reviewsResponse ->
                item.reviews = reviewsResponse
                return Promise.pure()
            }
        }
    }

    private List<SectionInfo> generateBreadcrumbs(SectionInfoNode sectionInfoNode, ApiContext apiContext) {
        List<SectionInfo> breadCrumbs = []
        while (sectionInfoNode.parent != null) {
            breadCrumbs << buildSectionInfoNodeForResponse(sectionInfoNode.parent, false, apiContext).toSectionInfo()
            sectionInfoNode = sectionInfoNode.parent
        }
        return breadCrumbs.reverse()
    }
}
