/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.entitlement.clientproxy.catalog.impl;

import com.junbo.catalog.spec.model.item.Item;
import com.junbo.catalog.spec.model.item.ItemRevision;
import com.junbo.catalog.spec.resource.proxy.ItemResourceClientProxy;
import com.junbo.catalog.spec.resource.proxy.ItemRevisionResourceClientProxy;
import com.junbo.common.id.ItemId;
import com.junbo.common.id.ItemRevisionId;
import com.junbo.entitlement.clientproxy.catalog.ItemFacade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

/**
 * Impl of CatalogFacade.
 */
public class ItemFacadeImpl implements ItemFacade {
    private static final Logger LOGGER = LoggerFactory.getLogger(ItemFacadeImpl.class);
    @Autowired
    @Qualifier("entitlementItemClient")
    private ItemResourceClientProxy itemClient;
    @Autowired
    @Qualifier("entitlementItemRevisionClient")
    private ItemRevisionResourceClientProxy itemRevisionClient;

    @Override
    public ItemRevision getItem(Long itemId) {
        Item item = null;
        try {
            LOGGER.info("Getting item [{}] started.", itemId);
            item = itemClient.getItem(
                    new ItemId(itemId)).wrapped().get();
            LOGGER.info("Getting item [{}] finished.", itemId);
        } catch (Exception e) {
            LOGGER.error("Getting item [{" + itemId + "}] failed.", e);
        }
        if (item == null) {
            return null;
        }
        ItemRevision itemRevision = null;
        Long revisionId = item.getCurrentRevisionId();
        try {
            LOGGER.info("Getting itemRevision [{}] started.", revisionId);
            itemRevision = itemRevisionClient.getItemRevision(
                    new ItemRevisionId(revisionId)).wrapped().get();
            LOGGER.info("Getting itemRevision [{}] finished.", revisionId);
        } catch (Exception e) {
            LOGGER.error("Getting itemRevision [{" + revisionId + "}] failed.", e);
        }
        return itemRevision;
    }
}
