/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.db.repo.impl.cloudant;

import com.junbo.catalog.common.util.Utils;
import com.junbo.catalog.db.repo.ItemRevisionRepository;
import com.junbo.catalog.spec.model.item.ItemRevision;
import com.junbo.catalog.spec.model.item.ItemRevisionsGetOptions;
import com.junbo.common.cloudant.CloudantClient;
import com.junbo.common.cloudant.model.CloudantQueryResult;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.*;

/**
 * Offer revision repository.
 */
public class ItemRevisionRepositoryImpl extends CloudantClient<ItemRevision> implements ItemRevisionRepository {

    @Override
    public ItemRevision create(ItemRevision itemRevision) {
        return cloudantPostSync(itemRevision);
    }

    @Override
    public ItemRevision get(String revisionId) {
        if (revisionId == null) {
            return null;
        }
        return cloudantGetSync(revisionId);
    }

    @Override
    public List<ItemRevision> getRevisions(ItemRevisionsGetOptions options) {
        List<ItemRevision> itemRevisions = new ArrayList<>();
        if (!CollectionUtils.isEmpty(options.getRevisionIds())) {
            for (String revisionId : options.getRevisionIds()) {
                ItemRevision revision = cloudantGetSync(revisionId);
                if (revision==null) {
                    continue;
                } else if (!StringUtils.isEmpty(options.getStatus())
                        && !options.getStatus().equalsIgnoreCase(revision.getStatus())) {
                    continue;
                } else {
                    itemRevisions.add(revision);
                }
            }
        } else if (!CollectionUtils.isEmpty(options.getItemIds())) {
            for (String itemId : options.getItemIds()) {
                List<ItemRevision> revisions = queryView("by_itemId", itemId).syncGet();
                if (!StringUtils.isEmpty(options.getStatus())) {
                    Iterator<ItemRevision> iterator = revisions.iterator();
                    while (iterator.hasNext()) {
                        ItemRevision revision = iterator.next();
                        if (!options.getStatus().equalsIgnoreCase(revision.getStatus())) {
                            iterator.remove();
                        }
                    }
                }
                itemRevisions.addAll(revisions);
            }
        } else if (!StringUtils.isEmpty(options.getStatus())){
            itemRevisions = queryView("by_status", options.getStatus().toUpperCase(),
                    options.getValidSize(), options.getValidStart(), false).syncGet();
        } else {
            itemRevisions = queryView("by_itemId", null, options.getValidSize(), options.getValidStart(), false).syncGet();
        }

        return itemRevisions;
    }

    @Override
    public List<ItemRevision> getRevisions(Collection<String> itemIds, Long timestamp) {
        List<ItemRevision> revisions = new ArrayList<>();
        for (String itemId : itemIds) {
            List<ItemRevision> itemRevisions = queryView("by_itemId", itemId).syncGet();
            ItemRevision revision = null;
            Long maxTimestamp = 0L;
            for (ItemRevision itemRevision : itemRevisions) {
                if (itemRevision.getTimestamp() == null) {
                    continue;
                }
                if (itemRevision.getTimestamp() <= timestamp && itemRevision.getTimestamp() > maxTimestamp) {
                    maxTimestamp = itemRevision.getTimestamp();
                    revision = itemRevision;
                }
            }
            if (revision != null) {
                revisions.add(revision);
            }
        }

        return revisions;
    }

    @Override
    public List<ItemRevision> getRevisions(String hostItemId) {
        List<ItemRevision> itemRevisions = queryView("by_hostItemId", hostItemId).syncGet();
        Set<String> itemIds = new HashSet<>();
        Set<String> itemRevisionIds = new HashSet<>();
        for (ItemRevision itemRevision : itemRevisions) {
            itemIds.add(itemRevision.getItemId());
            itemRevisionIds.add(itemRevision.getRevisionId());
        }

        itemRevisions = getRevisions(itemIds, Utils.currentTimestamp());
        Iterator<ItemRevision> iterator = itemRevisions.iterator();
        while(iterator.hasNext()) {
            ItemRevision revision = iterator.next();
            if (!itemRevisionIds.contains(revision.getRevisionId())) {
                iterator.remove();
            }
        }

        return itemRevisions;
    }

    @Override
    public boolean checkPackageName(String itemId, String packageName) {
        String query = "packageName:'" + packageName.replace("'","") + "' AND -itemId:'" + itemId.replace("'","") + "'";
        CloudantQueryResult searchResult = searchSync("search", query, 1, null, false);
        return searchResult.getTotalRows() == 0;
    }

    @Override
    public ItemRevision update(ItemRevision revision) {
        return cloudantPutSync(revision);
    }

    @Override
    public void delete(String revisionId) {
        cloudantDeleteSync(revisionId);
    }

}
