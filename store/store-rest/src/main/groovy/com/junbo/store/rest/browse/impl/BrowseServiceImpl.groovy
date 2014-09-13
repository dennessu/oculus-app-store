package com.junbo.store.rest.browse.impl
import com.junbo.catalog.spec.enums.EntitlementType
import com.junbo.catalog.spec.enums.ItemType
import com.junbo.catalog.spec.model.common.SimpleLocaleProperties
import com.junbo.catalog.spec.model.item.Binary
import com.junbo.catalog.spec.model.item.ItemRevision
import com.junbo.common.id.ItemId
import com.junbo.common.id.util.IdUtil
import com.junbo.common.model.Link
import com.junbo.common.model.Results
import com.junbo.entitlement.spec.model.*
import com.junbo.identity.spec.v1.model.User
import com.junbo.identity.spec.v1.option.model.UserGetOptions
import com.junbo.langur.core.promise.Promise
import com.junbo.rating.spec.model.priceRating.RatingItem
import com.junbo.store.clientproxy.FacadeContainer
import com.junbo.store.clientproxy.ResourceContainer
import com.junbo.store.common.utils.CommonUtils
import com.junbo.store.rest.browse.BrowseService
import com.junbo.store.rest.browse.SectionHandler
import com.junbo.store.rest.browse.SectionService
import com.junbo.store.rest.challenge.ChallengeHelper
import com.junbo.store.rest.utils.CatalogUtils
import com.junbo.store.spec.error.AppErrors
import com.junbo.store.spec.model.ApiContext
import com.junbo.store.spec.model.Challenge
import com.junbo.store.spec.model.browse.*
import com.junbo.store.spec.model.browse.document.*
import com.junbo.store.spec.model.external.casey.CaseyLink
import com.junbo.store.spec.model.external.casey.CaseyResults
import com.junbo.store.spec.model.external.casey.CaseyReview
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

    @Resource(name = 'storeBrowseDataBuilder')
    private BrowseDataBuilder browseDataBuilder

    @Resource(name = 'storeChallengeHelper')
    private ChallengeHelper challengeHelper

    @Resource(name = 'storeCatalogBrowseUtils')
    private CatalogBrowseUtils catalogBrowseUtils

    @Resource(name = 'storeSectionHandlers')
    private List<SectionHandler> sectionHandlers

    @Resource(name = 'storeCatalogUtils')
    private CatalogUtils catalogUtils

    @Resource(name = 'storeSectionService')
    private SectionService sectionService

    @Resource(name = 'storeLocaleUtils')
    private LocaleUtils localeUtils

    private Map<SectionKey, SectionKey> legacyMap = new HashMap<>()

    @Override
    Promise<Item> getItem(ItemId itemId, boolean includeDetails, ApiContext apiContext) {
        return catalogBrowseUtils.getItem(itemId, includeDetails, apiContext)
    }

    @Override
    Promise<TocResponse> getToc(ApiContext apiContext) {
        TocResponse result = new TocResponse()
        challengeHelper.checkTosChallenge(apiContext.user, storeBrowseTos, null).then { Challenge challenge ->
            if (challenge != null) {
                return Promise.pure(new TocResponse(challenge: challenge))
            }

            List<SectionInfoNode> sectionInfoNodes = sectionService.getTopLevelSectionInfoNode()
            result.sections = sectionInfoNodes.each { SectionInfoNode sectionInfoNode ->
                return buildSectionInfoNodeForResponse(sectionInfoNode, apiContext)
            }
            return Promise.pure(result)
        }
    }

    @Override
    Promise<SectionLayoutResponse> getSectionLayout(SectionLayoutRequest request, ApiContext apiContext) {
        handleLegacy(request)
        SectionInfoNode sectionInfoNode = sectionService.getSectionInfoNode(request.category, request.criteria)
        if (sectionInfoNode == null) {
            throw AppErrors.INSTANCE.sectionNotFound().exception()
        }

        SectionLayoutResponse response = new SectionLayoutResponse()
        sectionInfoNode = buildSectionInfoNodeForResponse(sectionInfoNode, apiContext)
        response.name = sectionInfoNode.name
        response.children = sectionInfoNode.children?.collect {SectionInfoNode e -> e.toSectionInfo() }
        response.ordered = false
        response.breadcrumbs = []
        SectionInfoNode parent = sectionInfoNode.parent
        while (parent != null) {
            response.breadcrumbs << parent.toSectionInfo()
            parent = parent.parent
        }
        response.breadcrumbs = response.breadcrumbs.reverse()

        innerGetList(new ListRequest(criteria: request.criteria, category: request.category, count: request.count), sectionInfoNode, apiContext).then { ListResponse listResponse ->
            response.items = listResponse.items
            response.next = listResponse.next
            return Promise.pure(response)
        }
    }

    @Override
    Promise<ListResponse> getList(ListRequest request, ApiContext apiContext) {
        handleLegacy(request)
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
        catalogBrowseUtils.getAppItemRevision(request.itemId, request.desiredVersionCode).then { ItemRevision e ->
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
        return catalogBrowseUtils.getReviews(request.itemId.value, null, request.cursor, request.count)
    }

    @Override
    Promise<AddReviewResponse> addReview(AddReviewRequest request, ApiContext apiContext) {
        CaseyReview review = new CaseyReview(
                reviewTitle: request.title,
                review: request.content,
                resourceType: 'item',
                resource: new CaseyLink(
                        id: request.itemId.value,
                        href: IdUtil.toHref(request.itemId)
                )
        )

        review.ratings = []
        for (String type : request.starRatings.keySet()) {
            review.ratings << new CaseyReview.Rating(type: type, score: request.starRatings[type])
        }

        return resourceContainer.caseyReviewResource.addReview(review).then { CaseyReview newReview ->
            return resourceContainer.userResource.get(apiContext.user, new UserGetOptions()).then { User user ->
                return new AddReviewResponse(
                        review: new Review(
                                self: new Link(id: newReview.self.id, href: newReview.self.href),
                                authorName: user.nickName,
                                deviceName: apiContext.androidId,
                                title: newReview.reviewTitle,
                                content: newReview.review,
                                starRatings: request.starRatings,
                                timestamp: newReview.postedDate
                        )
                )
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
                decorateItem(true, true, false, apiContext, item)
            }
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
            return catalogBrowseUtils.getItem(catalogItem, false, apiContext).then { Item item ->
                item.ownedByCurrentUser = true
                return Promise.pure(item)

            }
        }
    }

    private SectionHandler findSectionHandler(String category, String criteria, ApiContext apiContext) {
        return sectionHandlers.find { SectionHandler sectionHandler ->
            sectionHandler.canHandle(category, criteria, apiContext)
        }
    }

    private void handleLegacy(SectionLayoutRequest request) {
        SectionKey key = fromLegacy(request.category, request.criteria);
        request.category = key.category
        request.criteria = key.criteria
    }

    private void handleLegacy(ListRequest request) {
        SectionKey key = fromLegacy(request.category, request.criteria);
        request.category = key.category
        request.criteria = key.criteria
    }

    private SectionKey fromLegacy(String category, String criteria) {
        SectionKey original = new SectionKey(category, criteria);
        SectionKey result = legacyMap.get(original);
        return result == null ? original : result;
    }

    private SectionInfoNode buildSectionInfoNodeForResponse(SectionInfoNode internalNode, ApiContext apiContext) {
        SectionInfoNode sectionInfoNode = new SectionInfoNode()
        if (internalNode.categoryLocales != null && internalNode.sectionType == SectionInfoNode.SectionType.CategorySection) {
            SimpleLocaleProperties simpleLocaleProperties =
                    localeUtils.getLocaleProperties(internalNode.categoryLocales, apiContext.locale, 'OfferAttribute', internalNode?.categoryId, 'locales') as SimpleLocaleProperties
            sectionInfoNode.name = simpleLocaleProperties?.name
        }
        sectionInfoNode.category = internalNode.category
        sectionInfoNode.criteria = internalNode.criteria
        sectionInfoNode.children = internalNode.children.each { SectionInfoNode e ->
            return buildSectionInfoNodeForResponse(e, apiContext)
        }
        return sectionInfoNode
    }

}
