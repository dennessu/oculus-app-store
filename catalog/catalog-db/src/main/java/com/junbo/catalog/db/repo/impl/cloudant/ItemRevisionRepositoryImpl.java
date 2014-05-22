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
import com.junbo.sharding.IdGenerator;
import org.springframework.beans.factory.annotation.Required;

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

    public Long create(ItemRevision itemRevision) {
        if (itemRevision.getRevisionId() == null) {
            itemRevision.setRevisionId(idGenerator.nextId());
        }
        ItemRevision result = super.cloudantPost(itemRevision);
        return result.getRevisionId();
    }

    public ItemRevision get(Long revisionId) {
        return super.cloudantGet(revisionId.toString());
    }

    public List<ItemRevision> getRevisions(ItemRevisionsGetOptions options) {
        return null;
    }

    public List<ItemRevision> getRevisions(Collection<ItemId> itemIds, Long timestamp) {
        Set<Long> longItemIds = new HashSet<>();
        for (ItemId itemId : itemIds) {
            longItemIds.add(itemId.getValue());
        }

        return internalGetRevisions(longItemIds, timestamp);
    }

    @Override
    public List<ItemRevision> getRevisions(Long hostItemId) {
        List<ItemRevision> itemRevisions = super.queryView("by_hostItemId", hostItemId.toString());
        Set<Long> itemIds = new HashSet<>();
        Set<Long> itemRevisionIds = new HashSet<>();
        for (ItemRevision itemRevision : itemRevisions) {
            itemIds.add(itemRevision.getItemId());
            itemRevisionIds.add(itemRevision.getRevisionId());
        }

        itemRevisions = internalGetRevisions(itemIds, Utils.currentTimestamp());
        Iterator<ItemRevision> iterator = itemRevisions.iterator();
        while(iterator.hasNext()) {
            ItemRevision revision = iterator.next();
            if (!itemRevisionIds.contains(revision.getRevisionId())) {
                iterator.remove();
            }
        }

        return itemRevisions;
    }

    private List<ItemRevision> internalGetRevisions(Collection<Long> itemIds, Long timestamp) {
        List<ItemRevision> revisions = new ArrayList<>();
        for (Long itemId : itemIds) {
            List<ItemRevision> itemRevisions = super.queryView("by_itemId", itemId.toString());
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
            revisions.add(revision);
        }

        return revisions;
    }

    @Override
    public Long update(ItemRevision revision) {
        ItemRevision result = super.cloudantPut(revision);
        return result.getRevisionId();
    }

    @Override
    public void delete(Long revisionId) {
        super.cloudantDelete(revisionId.toString());
    }

    private CloudantViews cloudantViews = new CloudantViews() {{
        Map<String, CloudantView> viewMap = new HashMap<String, CloudantViews.CloudantView>();

        CloudantViews.CloudantView view = new CloudantViews.CloudantView();
        view.setMap("function(doc) {emit(doc.itemId, doc._id)}");
        view.setResultClass(String.class);
        viewMap.put("by_itemId", view);

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

        setViews(viewMap);
    }};

    @Override
    protected CloudantViews getCloudantViews() {
        return cloudantViews;
    }
}