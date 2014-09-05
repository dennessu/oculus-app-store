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
import com.junbo.store.spec.model.browse.document.Item
import groovy.transform.CompileStatic
import org.springframework.stereotype.Component

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
        return resourceContainer.entitlementResource.searchEntitlements(new EntitlementSearchParam(
                userId: userId,
                itemIds: [itemId] as Set,
                isActive: true
        ), new PageMetadata()).then { Results<Entitlement> results ->
            return Promise.pure(!results.items.isEmpty())
        }
    }
}
