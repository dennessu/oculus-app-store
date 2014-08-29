package com.junbo.store.rest.browse.impl
import com.junbo.catalog.spec.enums.EntitlementType
import com.junbo.catalog.spec.enums.ItemType
import com.junbo.catalog.spec.model.item.ItemsGetOptions
import com.junbo.common.model.Results
import com.junbo.entitlement.spec.model.Entitlement
import com.junbo.entitlement.spec.model.EntitlementSearchParam
import com.junbo.entitlement.spec.model.PageMetadata
import com.junbo.langur.core.promise.Promise
import com.junbo.store.clientproxy.FacadeContainer
import com.junbo.store.common.utils.CommonUtils
import com.junbo.store.rest.browse.BrowseService
import com.junbo.store.rest.challenge.ChallengeHelper
import com.junbo.store.rest.utils.ResourceContainer
import com.junbo.store.spec.error.AppErrors
import com.junbo.store.spec.model.ApiContext
import com.junbo.store.spec.model.Challenge
import com.junbo.store.spec.model.browse.*
import com.junbo.store.spec.model.browse.document.Item
import com.junbo.store.spec.model.browse.document.SectionInfo
import com.junbo.store.spec.model.browse.document.SectionInfoNode
import groovy.transform.CompileStatic
import org.apache.http.NameValuePair
import org.apache.http.client.utils.URLEncodedUtils
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
                    name: 'Games', category: 'Game',
                    children: [
                            new SectionInfoNode(name: 'Top Free', category: 'Game', criteria: 'top_free'),
                            new SectionInfoNode(name: 'Top Paid', category: 'Game', criteria: 'top_paid')
                    ] as List
            ),
            new SectionInfoNode(
                    name: 'Apps', category: 'App',
                    children: [
                            new SectionInfoNode(name: 'Top Free', category: 'App', criteria: 'top_free'),
                            new SectionInfoNode(name: 'Top Paid', category: 'App', criteria: 'top_paid')
                    ] as List
            ),
            new SectionInfoNode(
                    name: 'Experiences', category: 'Experience',
                    children: [
                            new SectionInfoNode(name: 'Top Free', category: 'Experience', criteria: 'top_free'),
                            new SectionInfoNode(name: 'Top Paid', category: 'Experience', criteria: 'top_paid')
                    ] as List
            ),
    ]

    @Override
    Promise<Item> getItem(String itemId, ApiContext apiContext) {
        resourceContainer.itemResource.getItem(itemId).then { com.junbo.catalog.spec.model.item.Item catalogItem ->
            Item item = new Item()
            browseDataBuilder.buildItemFromCatalogItem(catalogItem, apiContext, item).then {
                return Promise.pure(item)
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

        return innerGetList(sectionInfoNode, null, defaultPageSize, apiContext).then { ListResponse response ->
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

        return innerGetList(sectionInfoNode, null, defaultPageSize, apiContext)
    }

    @Override
    Promise<LibraryResponse> getLibrary(ApiContext apiContext) {
        LibraryResponse result = new LibraryResponse(items: [])
        Set<String> itemIdSet = [] as Set
        PageMetadata pageMetadata = new PageMetadata()
        EntitlementSearchParam searchParam = new EntitlementSearchParam(userId: apiContext.user, type: EntitlementType.DOWNLOAD.name(), isActive: true)
        CommonUtils.loop {
            resourceContainer.entitlementResource.searchEntitlements(searchParam, pageMetadata).then { Results<Entitlement> results ->
                if (results.items.isEmpty() || results.next?.href == null) {
                    return Promise.pure(Promise.BREAK)
                }

                Promise.each(results.items) { Entitlement entitlement ->
                    if (itemIdSet.contains(entitlement.itemId)) {
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
                    NameValuePair cursorPair = URLEncodedUtils.parse(new URI(results.next.href), 'UTF-8').find { NameValuePair pair -> pair.name == 'bookmark'}
                    if (StringUtils.isEmpty(cursorPair?.value)) {
                        LOGGER.error('name=Store_Entitlement_Bookmark_Empty')
                        return Promise.pure(Promise.BREAK)
                    }
                    pageMetadata.bookmark = cursorPair.value
                    return Promise.pure()
                }
            }
        }.then {
            return Promise.pure(result)
        }
    }

    Promise<ListResponse> innerGetList(SectionInfoNode sectionInfoNode, String cursor, Integer count, ApiContext apiContext) {
        ListResponse result = new ListResponse()
        ItemsGetOptions itemsGetOptions = new ItemsGetOptions(cursor: cursor, size: count, type: ItemType.APP.name())
        resourceContainer.itemResource.getItems(itemsGetOptions).then { Results<com.junbo.catalog.spec.model.item.Item> itemResults ->
            result.items = [] as List
            Promise.each(itemResults.items) { com.junbo.catalog.spec.model.item.Item catalogItem ->
                Item item = new Item()
                result.items << item
                browseDataBuilder.buildItemFromCatalogItem(catalogItem, apiContext, item)
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
            Item result = new Item()
            browseDataBuilder.buildItemFromCatalogItem(catalogItem, apiContext, result).then {
                return Promise.pure(result)
            }
        }
    }

}
