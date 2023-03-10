/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.db.repo.impl.cloudant;

import com.junbo.catalog.common.cache.CacheFacade;
import com.junbo.catalog.common.util.Callable;
import com.junbo.catalog.common.util.Utils;
import com.junbo.catalog.db.repo.ItemRevisionRepository;
import com.junbo.catalog.spec.enums.Status;
import com.junbo.catalog.spec.model.item.ItemRevision;
import com.junbo.catalog.spec.model.item.ItemRevisionInfo;
import com.junbo.catalog.spec.model.item.ItemRevisionsGetOptions;
import com.junbo.common.cloudant.CloudantClient;
import com.junbo.common.cloudant.model.CloudantQueryResult;
import com.junbo.common.cloudant.model.CloudantSearchResult;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.*;

/**
 * Offer revision repository.
 */
public class ItemRevisionRepositoryImpl extends CloudantClient<ItemRevision> implements ItemRevisionRepository {

    @Override
    public ItemRevision create(ItemRevision itemRevision) {
        ItemRevision createdRevision = cloudantPostSync(itemRevision);
        CacheFacade.ITEM_REVISION.put(createdRevision.getRevisionId(), createdRevision);
        return createdRevision;
    }

    @Override
    public ItemRevision get(final String revisionId) {
        if (revisionId == null) {
            return null;
        }
        return CacheFacade.ITEM_REVISION.get(revisionId, new Callable<ItemRevision>() {
            @Override
            public ItemRevision execute() {
                return cloudantGetSync(revisionId);
            }
        });
    }

    @Override
    public List<ItemRevision> getRevisions(ItemRevisionsGetOptions options) {
        List<ItemRevision> itemRevisions = new ArrayList<>();
        if (!CollectionUtils.isEmpty(options.getRevisionIds())) {
            for (String revisionId : options.getRevisionIds()) {
                ItemRevision revision = get(revisionId);
                if (revision==null
                        || !StringUtils.isEmpty(options.getStatus()) && !options.getStatus().equalsIgnoreCase(revision.getStatus())
                        || options.getDeveloperId() != null && !options.getDeveloperId().equals(revision.getOwnerId())) {
                    continue;
                } else {
                    itemRevisions.add(revision);
                }
            }
            options.setTotal(Long.valueOf(itemRevisions.size()));
        } else if (!CollectionUtils.isEmpty(options.getItemIds())) {
            for (String itemId : options.getItemIds()) {
                List<ItemRevision> revisions = queryViewSync("by_itemId", itemId);
                if (!StringUtils.isEmpty(options.getStatus()) || options.getDeveloperId() != null) {
                    Iterator<ItemRevision> iterator = revisions.iterator();
                    while (iterator.hasNext()) {
                        ItemRevision revision = iterator.next();
                        if (!StringUtils.isEmpty(options.getStatus()) && !options.getStatus().equalsIgnoreCase(revision.getStatus())
                                || options.getDeveloperId() != null && !options.getDeveloperId().equals(revision.getOwnerId())) {
                            iterator.remove();
                        }
                    }
                }
                options.setTotal(Long.valueOf(revisions.size()));
                itemRevisions.addAll(revisions);
            }
        } else if (options.getDeveloperId() != null || !StringUtils.isEmpty(options.getStatus())) {
            StringBuilder sb = new StringBuilder();
            if (options.getDeveloperId() != null) {
                sb.append("ownerId:\"").append(options.getDeveloperId().getValue()).append("\"");
            }
            if (!StringUtils.isEmpty(options.getStatus())) {
                if (sb.length() > 0) {
                    sb.append(" AND ");
                }
                sb.append("status:\"").append(options.getStatus()).append("\"");
            }
            CloudantSearchResult<ItemRevision> searchResult = searchSync("search", sb.toString(), options.getValidSize(), options.getCursor());
            itemRevisions = searchResult.getResults();
            options.setNextCursor(searchResult.getBookmark());
            options.setTotal(searchResult.getTotal());
        } else {
            CloudantQueryResult queryResult = queryViewSync("by_itemId", null, options.getValidSize(), options.getValidStart(), false, true);
            itemRevisions = Utils.getDocs(queryResult.getRows());
            options.setTotal(queryResult.getTotalRows());
        }

        return itemRevisions;
    }

    @Override
    public List<ItemRevision> getRevisions(Collection<String> itemIds, Long timestamp) {
        List<ItemRevision> revisions = new ArrayList<>();
        for (final String itemId : itemIds) {
            List<ItemRevisionInfo> revisionInfoList = CacheFacade.ITEM_CONTROL.get(itemId, new Callable<List<ItemRevisionInfo>>() {
                @Override
                public List<ItemRevisionInfo> execute() {
                    List<ItemRevisionInfo> result = new ArrayList<>();
                    List<ItemRevision> itemRevisions = queryViewSync("by_itemId", itemId);
                    for (ItemRevision itemRevision : itemRevisions) {
                        if (itemRevision.getTimestamp() == null) {
                            continue;
                        }
                        ItemRevisionInfo revisionInfo = new ItemRevisionInfo();
                        revisionInfo.setRevisionId(itemRevision.getRevisionId());
                        revisionInfo.setTimestamp(itemRevision.getTimestamp());
                        result.add(revisionInfo);
                    }

                    return result;
                }
            });

            String revisionId = null;
            Long maxTimestamp = 0L;
            for (ItemRevisionInfo revisionInfo : revisionInfoList) {
                if (revisionInfo.getTimestamp() <= timestamp && revisionInfo.getTimestamp() > maxTimestamp) {
                    maxTimestamp = revisionInfo.getTimestamp();
                    revisionId = revisionInfo.getRevisionId();
                }
            }
            ItemRevision revision = get(revisionId);
            if (revision != null) {
                revisions.add(revision);
            }
        }

        return revisions;
    }

    @Override
    public List<ItemRevision> getRevisions(String hostItemId) {
        List<ItemRevision> itemRevisions = queryView("by_hostItemId", hostItemId).get();
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
        String query = "packageName:\"" + Utils.escaple(packageName) + "\" AND -itemId:\"" + Utils.escaple(itemId) + "\"";
        CloudantQueryResult searchResult = searchSync("search", query, 1, null, false);
        return searchResult.getTotalRows() == 0;
    }

    @Override
    public ItemRevision update(ItemRevision revision, ItemRevision oldRevision) {
        ItemRevision updatedRevision = cloudantPutSync(revision, oldRevision);
        CacheFacade.ITEM_REVISION.put(updatedRevision.getRevisionId(), updatedRevision);
        if (Status.APPROVED.is(revision.getStatus())) {
            CacheFacade.ITEM_CONTROL.evict(updatedRevision.getItemId());
        }
        return updatedRevision;
    }

    @Override
    public void delete(String revisionId) {
        cloudantDeleteSync(revisionId);
        CacheFacade.ITEM_REVISION.evict(revisionId);
    }

}
