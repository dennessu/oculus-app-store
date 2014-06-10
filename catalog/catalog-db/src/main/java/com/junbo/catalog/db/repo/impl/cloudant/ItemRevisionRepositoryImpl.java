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
import com.junbo.common.cloudant.model.CloudantViews;
import com.junbo.common.id.ItemId;
import com.junbo.common.id.ItemRevisionId;
import com.junbo.sharding.IdGenerator;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.*;

/**
 * Offer revision repository.
 */
public class ItemRevisionRepositoryImpl extends CloudantClient<ItemRevision> implements ItemRevisionRepository {
    private IdGenerator idGenerator;

    @Required
    public void setIdGenerator(IdGenerator idGenerator) {
        this.idGenerator = idGenerator;
    }

    @Override
    public ItemRevision create(ItemRevision itemRevision) {
        if (itemRevision.getRevisionId() == null) {
            itemRevision.setRevisionId(idGenerator.nextId());
        }
        return super.cloudantPost(itemRevision).get();
    }

    @Override
    public ItemRevision get(Long revisionId) {
        return super.cloudantGet(revisionId.toString()).get();
    }

    @Override
    public List<ItemRevision> getRevisions(ItemRevisionsGetOptions options) {
        List<ItemRevision> itemRevisions = new ArrayList<>();
        if (!CollectionUtils.isEmpty(options.getRevisionIds())) {
            for (ItemRevisionId revisionId : options.getRevisionIds()) {
                ItemRevision revision = super.cloudantGet(revisionId.toString()).get();
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
            for (ItemId itemId : options.getItemIds()) {
                List<ItemRevision> revisions = super.queryView("by_itemId", itemId.toString()).get();
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
            itemRevisions = super.queryView("by_status", options.getStatus().toUpperCase(),
                    options.getValidSize(), options.getValidStart(), false).get();
        } else {
            itemRevisions = super.queryView("by_itemId", null, options.getValidSize(), options.getValidStart(), false).get();
        }

        return itemRevisions;
    }

    @Override
    public List<ItemRevision> getRevisions(Collection<Long> itemIds, Long timestamp) {
        List<ItemRevision> revisions = new ArrayList<>();
        for (Long itemId : itemIds) {
            List<ItemRevision> itemRevisions = super.queryView("by_itemId", itemId.toString()).get();
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
    public List<ItemRevision> getRevisions(Long hostItemId) {
        List<ItemRevision> itemRevisions = super.queryView("by_hostItemId", hostItemId.toString()).get();
        Set<Long> itemIds = new HashSet<>();
        Set<Long> itemRevisionIds = new HashSet<>();
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
    public ItemRevision update(ItemRevision revision) {
        return super.cloudantPut(revision).get();
    }

    @Override
    public void delete(Long revisionId) {
        super.cloudantDelete(revisionId.toString()).get();
    }

    private CloudantViews cloudantViews = new CloudantViews() {{
        Map<String, CloudantView> viewMap = new HashMap<>();
        Map<String, CloudantIndex> indexMap = new HashMap<>();

        CloudantViews.CloudantView view = new CloudantViews.CloudantView();
        view.setMap("function(doc) {emit(doc.itemId, doc._id)}");
        view.setResultClass(String.class);
        viewMap.put("by_itemId", view);

        view = new CloudantViews.CloudantView();
        view.setMap("function(doc) {if (doc.status){ emit(doc.status, doc._id); }}");
        view.setResultClass(String.class);
        viewMap.put("by_status", view);

        view = new CloudantViews.CloudantView();
        view.setMap(
            "function(doc) {" +
                "if (doc.iapHostItemIds) {" +
                    "for (var idx in doc.iapHostItemIds) {" +
                        "emit(doc.iapHostItemIds[idx], doc._id);" +
                    "}" +
                "}" +
            "}");
        view.setResultClass(String.class);
        viewMap.put("by_hostItemId", view);

        CloudantIndex index = new CloudantIndex();
        index.setResultClass(String.class);
        index.setIndex("function(doc) {" +
                "index(\'itemId\', doc.itemId);" +
                "index(\'revisionId\', doc.revisionId);" +
                "index(\'status\', doc.status);" +
                "index(\'timeInMillis\', doc.timestamp);" +
            "}");
        indexMap.put("search", index);

        setIndexes(indexMap);
        setViews(viewMap);
    }};

    @Override
    protected CloudantViews getCloudantViews() {
        return cloudantViews;
    }
}
