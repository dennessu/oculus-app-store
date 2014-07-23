/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.entitlement.clientproxy.catalog.impl;

import com.junbo.catalog.spec.model.item.Item;
import com.junbo.catalog.spec.model.item.ItemRevision;
import com.junbo.catalog.spec.model.item.ItemRevisionsGetOptions;
import com.junbo.catalog.spec.model.item.ItemsGetOptions;
import com.junbo.catalog.spec.resource.proxy.ItemResourceClientProxy;
import com.junbo.catalog.spec.resource.proxy.ItemRevisionResourceClientProxy;
import com.junbo.common.model.Results;
import com.junbo.entitlement.clientproxy.catalog.ItemFacade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.*;

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
    public ItemRevision getItem(String itemId) {
        Date now = new Date();
        ItemRevision itemRevision = null;
        ItemRevisionsGetOptions options = new ItemRevisionsGetOptions();
        options.setItemIds(Collections.singleton(itemId));
        options.setTimestamp(now.getTime());
        try {
            LOGGER.info("Getting itemRevisions for item [{}] started.", itemId);
            Results<ItemRevision> results = itemRevisionClient.getItemRevisions(options).get();
            if (results.getItems().size() == 0) {
                LOGGER.info("There is no itemRevision for item [{}].", itemId);
                return null;
            }
            itemRevision = results.getItems().get(0);
            LOGGER.info("Getting itemRevisions for item [{}] finished.", itemId);
        } catch (Exception e) {
            LOGGER.error("Getting itemRevisions for item [{" + itemId + "}] failed.", e);
        }
        return itemRevision;
    }

    @Override
    public Set<String> getItemIdsByHostItemId(String hostItemId) {
        List<Item> items = new LinkedList<>();
        ItemsGetOptions options = new ItemsGetOptions();
        options.setHostItemId(hostItemId);
        options.setSize(20000);  //temp work around
        try {
            LOGGER.info("Getting items by hostItemId [{}] started.", hostItemId);
            items = itemClient.getItems(options).get().getItems();
            LOGGER.info("Getting items by hostItemId [{}] finished.", hostItemId);
        } catch (Exception e) {
            LOGGER.error("Getting items by hostItemId [{" + hostItemId + "}] failed.", e);
        }
        Set<String> itemIds = new HashSet<>();
        for (Item item : items) {
            itemIds.add(item.getItemId());
        }
        return itemIds;
    }
}
