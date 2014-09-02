package com.junbo.store.rest.browse.impl
import com.junbo.catalog.spec.enums.EntitlementType
import com.junbo.catalog.spec.enums.ItemType
import com.junbo.catalog.spec.enums.PriceType
import com.junbo.catalog.spec.enums.Status
import com.junbo.catalog.spec.model.attribute.ItemAttribute
import com.junbo.catalog.spec.model.attribute.OfferAttribute
import com.junbo.catalog.spec.model.common.SimpleLocaleProperties
import com.junbo.catalog.spec.model.item.Binary
import com.junbo.catalog.spec.model.item.ItemRevision
import com.junbo.catalog.spec.model.item.ItemRevisionsGetOptions
import com.junbo.catalog.spec.model.item.ItemsGetOptions
import com.junbo.catalog.spec.model.offer.OfferRevision
import com.junbo.catalog.spec.model.offer.OfferRevisionGetOptions
import com.junbo.catalog.spec.model.offer.OffersGetOptions
import com.junbo.common.id.ItemId
import com.junbo.common.id.ItemRevisionId
import com.junbo.common.id.OfferId
import com.junbo.common.model.Results
import com.junbo.entitlement.spec.model.DownloadUrlGetOptions
import com.junbo.entitlement.spec.model.DownloadUrlResponse
import com.junbo.entitlement.spec.model.Entitlement
import com.junbo.entitlement.spec.model.EntitlementSearchParam
import com.junbo.entitlement.spec.model.PageMetadata
import com.junbo.identity.spec.v1.model.Organization
import com.junbo.identity.spec.v1.option.model.OrganizationGetOptions
import com.junbo.langur.core.promise.Promise
import com.junbo.rating.spec.model.priceRating.RatingItem
import com.junbo.rating.spec.model.priceRating.RatingRequest
import com.junbo.store.clientproxy.FacadeContainer
import com.junbo.store.common.cache.Cache
import com.junbo.store.common.utils.CommonUtils
import com.junbo.store.rest.browse.BrowseService
import com.junbo.store.rest.challenge.ChallengeHelper
import com.junbo.store.rest.utils.RequestValidator
import com.junbo.store.rest.utils.ResourceContainer
import com.junbo.store.spec.error.AppErrors
import com.junbo.store.spec.model.ApiContext
import com.junbo.store.spec.model.Challenge
import com.junbo.store.spec.model.browse.*
import com.junbo.store.spec.model.browse.document.Item
import com.junbo.store.spec.model.browse.document.Offer
import com.junbo.store.spec.model.browse.document.SectionInfo
import com.junbo.store.spec.model.browse.document.SectionInfoNode
import com.junbo.store.spec.model.catalog.data.ItemData
import com.junbo.store.spec.model.catalog.data.OfferData
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

    private int defaultPageSize = 10

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

    // hard coded sections
    private List<SectionInfoNode> sections = [
            new SectionInfoNode(
                    name: 'Featured', criteria: 'featured',
                    children: [
                        new SectionInfoNode(name: 'All', category: 'all', criteria: 'featured'),
                        new SectionInfoNode(name: 'SAMSUNG', category: 'samsung', criteria: 'featured')
                    ] as List
            ),
            new SectionInfoNode(
                    name: 'Games', category: 'Game'
            ),
            new SectionInfoNode(
                    name: 'Apps', category: 'App'
            ),
            new SectionInfoNode(
                    name: 'Experiences', category: 'Experience'
            ),
    ]

    @Override
    Promise<Item> getItemDetails(ItemId itemId, ApiContext apiContext) {
        Item result
        resourceContainer.itemResource.getItem(itemId.value).then { com.junbo.catalog.spec.model.item.Item catalogItem ->
            getItem(catalogItem, apiContext).then { Item item ->
                result = item
                return Promise.pure()
            }.then {
                catalogBrowseUtils.checkItemOwnedByUser(itemId, apiContext.user).then { Boolean owned ->
                    result.ownedByCurrentUser = owned
                    return Promise.pure()
                }
            }.then {
                return Promise.pure(result)
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

            result.sections = sections
            return Promise.pure(result)
        }
    }

    @Override
    Promise<SectionLayoutResponse> getSectionLayout(SectionLayoutRequest request, ApiContext apiContext) {
        SectionLayoutResponse result = new SectionLayoutResponse()
        Stack<SectionInfo> parents = new Stack<>()
        SectionInfoNode sectionInfoNode = getSectionInfoNode(request.category, request.criteria, sections, parents)
        if (sectionInfoNode == null) {
            throw AppErrors.INSTANCE.sectionNotFound().exception()
        }

        result.breadcrumbs = parents
        result.children = sectionInfoNode.children?.collect {SectionInfoNode s -> s.toSectionInfo()}
        result.title = sectionInfoNode.name
        result.ordered = sectionInfoNode.ordered == null ? false : sectionInfoNode.ordered

        return innerGetList(sectionInfoNode, null, request.count == null ? defaultPageSize : request.count, apiContext).then { ListResponse response ->
            result.items = response.items
            result.next = response.next
            return Promise.pure(result)
        }
    }

    @Override
    Promise<ListResponse> getList(ListRequest request, ApiContext apiContext) {
        Stack<SectionInfo> parents = new Stack<>()
        SectionInfoNode sectionInfoNode = getSectionInfoNode(request.category, request.criteria, sections, parents)
        if (sectionInfoNode == null) {
            throw AppErrors.INSTANCE.sectionNotFound().exception()
        }

        return innerGetList(sectionInfoNode, request.cursor, request.count == null ? defaultPageSize : request.count, apiContext)
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

    Promise<ListResponse> innerGetList(SectionInfoNode sectionInfoNode, String cursor, Integer count, ApiContext apiContext) {
        ListResponse result = new ListResponse()
        ItemsGetOptions itemsGetOptions = new ItemsGetOptions(cursor: cursor, size: count, type: ItemType.APP.name())
        resourceContainer.itemResource.getItems(itemsGetOptions).then { Results<com.junbo.catalog.spec.model.item.Item> itemResults ->
            result.items = [] as List
            Promise.each(itemResults.items) { com.junbo.catalog.spec.model.item.Item catalogItem ->
                getItem(catalogItem, apiContext).then { Item item ->
                    result.items << item
                    return Promise.pure()
                }
            }
        }.then {
            result.next = new ListResponse.NextOption(
                    category: sectionInfoNode.category,
                    criteria: sectionInfoNode.criteria,
                    cursor: itemsGetOptions.nextCursor,
                    count: count
            )
            return Promise.pure(result)
        }
    }

    private SectionInfoNode getSectionInfoNode(String category, String criteria, List<SectionInfoNode> sections, Stack<SectionInfo> parents) {
        for (SectionInfoNode sectionInfoNode: sections) {

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
            return getItem(catalogItem, apiContext)
        }
    }

    private Promise<Item> getItem(com.junbo.catalog.spec.model.item.Item catalogItem, ApiContext apiContext) {
        ItemData itemData
        Item result = new Item()
        catalogBrowseUtils.getItemData(catalogItem).then { ItemData e ->
            itemData = e
            browseDataBuilder.buildItemFromItemData(itemData, apiContext, result)
            return Promise.pure()
        }.then { // apply price info
            catalogBrowseUtils.getOffer(itemData?.offer, apiContext).then { Offer e ->
                result.offer = e
                return Promise.pure()
            }
        }.then {
            return Promise.pure(result)
        }
    }
}
