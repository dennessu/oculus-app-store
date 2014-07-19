/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.data.handler.catalog

import com.junbo.catalog.spec.model.item.Item
import com.junbo.catalog.spec.resource.ItemResource
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Required

/**
 * Created by jasonfu on 14-7-17.
 */
@CompileStatic
class ItemDataHandler extends BaseCatalogDataHandler {
    private ItemResource itemResource

    @Required
    void setItemResource(ItemResource itemResource) {
        this.itemResource = itemResource
    }

    String handle(Item item) {
        logger.debug('Create new item with this content')
        try {
            Item result = itemResource.create(item).get()
            return result.id
        } catch (Exception e) {
            logger.error("Error creating item $item.itemId.", e)
            return null
        }

    }
}
