/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.data.handler.catalog

import com.junbo.catalog.spec.model.item.ItemRevision
import com.junbo.catalog.spec.resource.ItemRevisionResource
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Required

/**
 * Created by jasonfu on 14-7-17.
 */
@CompileStatic
class ItemRevisionDataHandler extends BaseCatalogDataHandler {
    private ItemRevisionResource itemRevisionResource

    @Required
    void setItemRevisionResource(ItemRevisionResource itemRevisionResource) {
        this.itemRevisionResource = itemRevisionResource
    }

    void handle(ItemRevision itemRevision) {
        logger.debug('Create new item revision with this content')
        try {
            ItemRevision itemRevisionCreated = itemRevisionResource.createItemRevision(itemRevision).get()

            logger.debug('put the item revision to APPROVED')
            itemRevisionCreated.setStatus("APPROVED")
            itemRevisionResource.updateItemRevision(itemRevisionCreated.revisionId, itemRevisionCreated).get()
        } catch (Exception e) {
            logger.error("Error creating item revision $itemRevision.revisionId.", e)
        }

    }
}
