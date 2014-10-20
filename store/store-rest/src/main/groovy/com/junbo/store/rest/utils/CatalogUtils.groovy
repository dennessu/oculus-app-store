/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.store.rest.utils

import com.junbo.catalog.spec.model.item.ItemsGetOptions
import com.junbo.common.id.ItemId
import com.junbo.common.id.UserId
import com.junbo.common.model.Results
import com.junbo.entitlement.spec.model.Entitlement
import com.junbo.entitlement.spec.model.EntitlementSearchParam
import com.junbo.entitlement.spec.model.PageMetadata
import com.junbo.langur.core.promise.Promise
import com.junbo.store.common.utils.CommonUtils
import com.junbo.store.spec.model.browse.document.Item
import com.junbo.store.clientproxy.ResourceContainer
import groovy.transform.CompileStatic
import org.apache.commons.lang3.StringUtils
import org.springframework.stereotype.Component
import org.springframework.util.CollectionUtils

import javax.annotation.Resource
/**
 * CatalogUtils.
 */
@CompileStatic
@Component('storeCatalogUtils')
class CatalogUtils {
    @Resource(name = 'storeResourceContainer')
    private ResourceContainer resourceContainer

    @Resource(name = "storeDataConverter")
    private DataConverter dataConverter

    Promise<List<Item>> getItems(String locale) {

        return resourceContainer.itemResource.getItems(new ItemsGetOptions(type: 'APP')).then {
            Results<com.junbo.catalog.spec.model.item.Item> results ->
            assert results.items != null : 'items is null'
            return Promise.pure(null)
        }
    }

    Promise<Boolean> checkItemOwnedByUser(ItemId itemId, UserId userId) {
        return checkItemsOwnedByUser(Collections.singleton(itemId), userId).then { Set<ItemId> ownedItemIds ->
            return Promise.pure(!ownedItemIds.isEmpty())
        }
    }

    Promise<Set<ItemId>> checkItemsOwnedByUser(Set<ItemId> itemIds, UserId userId) {
        String bookmark = null
        Set<ItemId> ownedItemIds = [] as Set
        CommonUtils.loop {
            return resourceContainer.entitlementResource.searchEntitlements(new EntitlementSearchParam(
                    userId: userId,
                    itemIds: itemIds,
                    isActive: true
            ), new PageMetadata(bookmark: bookmark, count: itemIds.size())).then { Results<Entitlement> results ->
                bookmark = CommonUtils.getQueryParam(results?.next?.href, 'bookmark')
                results.items.each { Entitlement entitlement ->
                    ownedItemIds << new ItemId(entitlement.itemId)
                }
                if (StringUtils.isBlank(bookmark) || CollectionUtils.isEmpty(results?.items)) {
                    return Promise.pure(Promise.BREAK)
                }
                return Promise.pure()
            }
        }.then {
            return Promise.pure(ownedItemIds)
        }
    }
}
