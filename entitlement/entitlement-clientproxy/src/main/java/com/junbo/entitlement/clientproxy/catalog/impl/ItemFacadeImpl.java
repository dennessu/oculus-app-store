/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.entitlement.clientproxy.catalog.impl;

import com.junbo.catalog.common.util.Constants;
import com.junbo.catalog.spec.model.item.*;
import com.junbo.catalog.spec.resource.proxy.ItemResourceClientProxy;
import com.junbo.catalog.spec.resource.proxy.ItemRevisionResourceClientProxy;
import com.junbo.common.model.Results;
import com.junbo.entitlement.clientproxy.catalog.ItemFacade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Impl of CatalogFacade.
 */
public class ItemFacadeImpl implements ItemFacade {
    private static final Logger LOGGER = LoggerFactory.getLogger(ItemFacadeImpl.class);
    private static Pattern cursorPattern = Pattern.compile(".*&cursor=(.*)");
    @Autowired
    @Qualifier("entitlementItemClient")
    private ItemResourceClientProxy itemClient;
    @Autowired
    @Qualifier("entitlementItemRevisionClient")
    private ItemRevisionResourceClientProxy itemRevisionClient;

    @Override
    public ItemRevision getItemRevision(String itemRevisionId) {
        ItemRevision itemRevision = null;
        try {
            LOGGER.info("Getting itemRevision [{}] started.", itemRevisionId);
            itemRevision = itemRevisionClient.getItemRevision(itemRevisionId, new ItemRevisionGetOptions()).get();
            LOGGER.info("Getting itemRevision [{}] finished.", itemRevisionId);
        } catch (Exception e) {
            LOGGER.error("Getting itemRevision [{" + itemRevisionId + "}] failed.", e);
        }
        return itemRevision;
    }

    @Override
    public ItemRevision getItem(String itemId) {
        ItemRevision itemRevision = null;
        try {
            LOGGER.info("Getting itemRevisions for item [{}] started.", itemId);
            Item item = itemClient.getItem(itemId).get();
            if (item == null) {
                LOGGER.info("There is no item for itemId [{}].", itemId);
                return null;
            }
            itemRevision = itemRevisionClient.getItemRevision(item.getCurrentRevisionId(), new ItemRevisionGetOptions()).get();
            if (itemRevision == null) {
                LOGGER.info("There is no itemRevision for item [{}].", itemId);
                return null;
            }
            LOGGER.info("Getting itemRevisions for item [{}] finished.", itemId);
        } catch (Exception e) {
            LOGGER.error("Getting itemRevisions for item [{" + itemId + "}] failed.", e);
        }
        return itemRevision;
    }

    @Override
    public Set<String> getItemIdsByHostItemId(String hostItemId) {
        Results<Item> items = null;
        ItemsGetOptions options = new ItemsGetOptions();
        options.setHostItemId(hostItemId);
        options.setSize(Constants.DEFAULT_PAGING_SIZE);
        Set<String> itemIds = new HashSet<>();

        try {
            LOGGER.info("Getting items by hostItemId [{}] started.", hostItemId);
            while (true) {
                items = addNextItems(items, options, itemIds);
                if (items.getItems().size() < Constants.DEFAULT_PAGING_SIZE) {
                    break;
                }
            }
            LOGGER.info("Getting items by hostItemId [{}] finished.", hostItemId);
        } catch (Exception e) {
            LOGGER.error("Getting items by hostItemId [{" + hostItemId + "}] failed.", e);
        }

        return itemIds;
    }

    private Results<Item> addNextItems(Results<Item> items, ItemsGetOptions options, Set<String> itemIds) {
        if (items != null) {
            options.setCursor(getNextCursor(items.getNext().getHref()));
        }
        Results<Item> result = itemClient.getItems(options).get();
        for (Item item : result.getItems()) {
            itemIds.add(item.getItemId());
        }
        return result;
    }

    private String getNextCursor(String nextHref) {
        Matcher matcher = cursorPattern.matcher(nextHref);
        if (matcher.matches()) {
            return matcher.group(1);
        }
        return null;
    }
}
