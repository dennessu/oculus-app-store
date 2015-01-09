package com.junbo.store.clientproxy.entitlement

import com.fasterxml.jackson.core.JsonProcessingException
import com.junbo.catalog.spec.enums.EntitlementType
import com.junbo.catalog.spec.enums.ItemType
import com.junbo.common.id.EntitlementId
import com.junbo.common.id.ItemId
import com.junbo.common.id.UserId
import com.junbo.common.json.ObjectMapperProvider
import com.junbo.common.model.Results
import com.junbo.entitlement.spec.model.EntitlementSearchParam
import com.junbo.entitlement.spec.model.PageMetadata
import com.junbo.langur.core.promise.Promise
import com.junbo.store.clientproxy.ResourceContainer
import com.junbo.store.clientproxy.utils.ItemBuilder
import com.junbo.store.common.utils.CommonUtils
import com.junbo.store.spec.model.ApiContext
import com.junbo.store.spec.model.Entitlement
import com.junbo.store.spec.model.browse.Images
import com.junbo.store.spec.model.external.sewer.catalog.SewerItem
import com.junbo.store.spec.model.external.sewer.entitlement.SewerEntitlement
import groovy.transform.CompileStatic
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.util.CollectionUtils
import org.springframework.util.StringUtils

import javax.annotation.Resource

/**
 * The EntitlementFacadeImpl class.
 */
@CompileStatic
@Component('storeEntitlementFacade')
class EntitlementFacadeImpl implements EntitlementFacade {

    private static final String ENTITLEMENT_FULL_EXPAND = '(item(developer,currentRevision,categories,genres,rating))'

    private static final String ENTITLEMENT_ITEM_EXPAND = '(item)'

    private final static Logger LOGGER = LoggerFactory.getLogger(EntitlementFacadeImpl)

    private final static Set<String> acceptedEntitlementTypes = [EntitlementType.DOWNLOAD.name(), EntitlementType.ALLOW_IN_APP.name()] as Set

    @Resource(name = 'storeResourceContainer')
    private ResourceContainer resourceContainer

    @Resource(name = 'storeItemBuilder')
    private ItemBuilder itemBuilder

    @Override
    Promise<List<Entitlement>> getEntitlements(EntitlementType entitlementType, Set<String> itemTypes, ItemId hostItemId,  boolean includeItemDetails, ApiContext apiContext) {
        PageMetadata pageMetadata = new PageMetadata()
        EntitlementSearchParam searchParam = new EntitlementSearchParam(userId: apiContext.user, type: entitlementType.name(), isActive: true,
                                            hostItemId: hostItemId)
        String expand = "(results${includeItemDetails ? ENTITLEMENT_FULL_EXPAND : ENTITLEMENT_ITEM_EXPAND})"
        List<Entitlement> result = [] as List
        CommonUtils.loop {
            resourceContainer.sewerEntitlementResource.searchEntitlements(searchParam, pageMetadata, expand,  apiContext.locale.getId().value).then { Results<SewerEntitlement> sewerEntitlementResults ->
                sewerEntitlementResults.items.each { SewerEntitlement sewerEntitlement ->
                    SewerItem sewerItem = getSewerItemFromEntitlement(sewerEntitlement)
                    if (sewerItem != null) {
                        if (itemTypes == null || itemTypes.contains(sewerItem.type)) {
                            result << toEntitlement(sewerEntitlement, sewerItem, includeItemDetails, apiContext)
                        }
                    }
                }

                String cursor = CommonUtils.getQueryParam(sewerEntitlementResults.next?.href, 'bookmark')
                if (CollectionUtils.isEmpty(sewerEntitlementResults?.items) || StringUtils.isEmpty(cursor)) {
                    return Promise.pure(Promise.BREAK)
                }
                pageMetadata.bookmark = cursor
                return Promise.pure()
            }
        }.then {
            return Promise.pure(result)
        }
    }

    @Override
    Promise<List<Entitlement>> getEntitlementsByIds(Set<EntitlementId> entitlementIdList, boolean includeItemDetails, ApiContext apiContext) {
        List<Entitlement> result = [] as List
        String expand = includeItemDetails ? ENTITLEMENT_FULL_EXPAND : ENTITLEMENT_ITEM_EXPAND

        return Promise.each(entitlementIdList) { EntitlementId entitlementId ->
            return resourceContainer.sewerEntitlementResource.getEntitlement(entitlementId, expand, apiContext.getLocale().getId().value).then { SewerEntitlement sewerEntitlement ->
                if (!acceptedEntitlementTypes.contains(sewerEntitlement.type)) {
                    return Promise.pure()
                }
                SewerItem sewerItem = getSewerItemFromEntitlement(sewerEntitlement)
                if (sewerItem != null) {
                    result << toEntitlement(sewerEntitlement, sewerItem, includeItemDetails, apiContext)
                }
                return Promise.pure()
            }
        }.then {
            return Promise.pure(result)
        }
    }

    @Override
    Promise<Boolean> checkEntitlements(UserId userId, ItemId itemId, EntitlementType entitlementType) {
        return checkEntitlements(userId, Collections.singleton(itemId), entitlementType).then { Set<ItemId> ownedItemIds ->
            return Promise.pure(!ownedItemIds.isEmpty())
        }
    }

    @Override
    Promise<Set<ItemId>> checkEntitlements(UserId userId, Set<ItemId> itemIds, EntitlementType entitlementType) {
        String bookmark = null
        Set<ItemId> ownedItemIds = [] as Set
        CommonUtils.loop {
            return resourceContainer.entitlementResource.searchEntitlements(new EntitlementSearchParam(
                    userId: userId,
                    itemIds: itemIds,
                    isActive: true,
                    type: entitlementType?.name()
            ), new PageMetadata(bookmark: bookmark, count: itemIds.size())).then { Results<com.junbo.entitlement.spec.model.Entitlement> results ->
                bookmark = CommonUtils.getQueryParam(results?.next?.href, 'bookmark')
                results.items.each { com.junbo.entitlement.spec.model.Entitlement entitlement ->
                    ownedItemIds << new ItemId(entitlement.itemId)
                }
                if (org.apache.commons.lang3.StringUtils.isBlank(bookmark) || CollectionUtils.isEmpty(results?.items)
                    || ownedItemIds.size() == itemIds.size()) {
                    return Promise.pure(Promise.BREAK)
                }
                return Promise.pure()
            }
        }.then {
            return Promise.pure(ownedItemIds)
        }
    }

    private Entitlement toEntitlement(SewerEntitlement sewerEntitlement, SewerItem sewerItem, boolean includeDetails, ApiContext apiContext) {
        Entitlement result = new Entitlement()
        result.self = new EntitlementId(sewerEntitlement.getId())
        result.user = new UserId(sewerEntitlement.userId)
        result.entitlementType = sewerEntitlement.type
        result.itemType = sewerItem.type
        result.item = new ItemId(sewerItem.itemId)
        boolean isIAP = (result.itemType == ItemType.ADDITIONAL_CONTENT.name());
        if (includeDetails) {
            result.itemDetails = itemBuilder.buildItem(sewerItem, Images.BuildType.Item_Details, apiContext)
            if (isIAP && result.itemDetails != null) {
                result.itemDetails.useCount = sewerEntitlement.useCount
            }
            result.itemDetails.ownedByCurrentUser = (apiContext.user == new UserId(sewerEntitlement.userId))
    }
        return result
    }

    private SewerItem getSewerItemFromEntitlement(SewerEntitlement sewerEntitlement) {
        try {
            if (sewerEntitlement.itemNode == null || sewerEntitlement.itemNode.isNull()) {
                return null
            }
            return ObjectMapperProvider.instance().treeToValue(sewerEntitlement.itemNode, SewerItem)
        } catch (JsonProcessingException ex) {
            LOGGER.error('name=Bad_Sewer_Entitlement_ItemNode, payload:\n{}',
                    String.valueOf(sewerEntitlement.itemNode), ex)
            throw new RuntimeException(ex)
        }
    }
}
