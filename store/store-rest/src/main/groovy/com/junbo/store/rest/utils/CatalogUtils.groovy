/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.store.rest.utils

import com.junbo.catalog.spec.model.item.ItemsGetOptions
import com.junbo.common.model.Results
import com.junbo.langur.core.promise.Promise
import com.junbo.store.spec.model.browse.document.Item
import groovy.transform.CompileStatic

import javax.annotation.Resource

/**
 * CatalogUtils.
 */
@CompileStatic
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
}
