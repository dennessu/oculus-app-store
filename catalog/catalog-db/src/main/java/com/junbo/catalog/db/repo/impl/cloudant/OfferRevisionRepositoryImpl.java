/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.db.repo.impl.cloudant;

import com.junbo.catalog.db.repo.OfferRevisionRepository;
import com.junbo.catalog.spec.model.offer.OfferRevision;
import com.junbo.catalog.spec.model.offer.OfferRevisionsGetOptions;
import com.junbo.common.cloudant.CloudantClient;
import com.junbo.common.cloudant.model.CloudantViews;
import com.junbo.common.id.OfferId;
import com.junbo.sharding.IdGenerator;
import org.springframework.beans.factory.annotation.Required;

import java.util.*;

/**
 * Offer revision repository.
 */
public class OfferRevisionRepositoryImpl extends CloudantClient<OfferRevision> implements OfferRevisionRepository {
    private IdGenerator idGenerator;

    @Required
    public void setIdGenerator(IdGenerator idGenerator) {
        this.idGenerator = idGenerator;
    }

    public OfferRevision create(OfferRevision offerRevision) {
        if (offerRevision.getRevisionId() == null) {
            offerRevision.setRevisionId(idGenerator.nextId());
        }
        return super.cloudantPost(offerRevision);
    }

    public OfferRevision get(Long revisionId) {
        return super.cloudantGet(revisionId.toString());
    }

    public List<OfferRevision> getRevisions(OfferRevisionsGetOptions options) {
        List<OfferRevision> revisions = new ArrayList<>();
        // TODO: search

        return revisions;
    }

    public List<OfferRevision> getRevisions(Collection<OfferId> offerIds, Long timestamp) {
        Set<Long> longOfferIds = new HashSet<>();
        for (OfferId offerId : offerIds) {
            longOfferIds.add(offerId.getValue());
        }

        return internalGetRevisions(longOfferIds, timestamp);
    }

    private List<OfferRevision> internalGetRevisions(Collection<Long> offerIds, Long timestamp) {
        List<OfferRevision> revisions = new ArrayList<>();
        for (Long offerId : offerIds) {
            List<OfferRevision> itemRevisions = super.queryView("by_offerId", offerId.toString());
            OfferRevision revision = null;
            Long maxTimestamp = 0L;
            for (OfferRevision itemRevision : itemRevisions) {
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
    public OfferRevision update(OfferRevision revision) {
        return super.cloudantPut(revision);
    }

    @Override
    public void delete(Long revisionId) {
        super.cloudantDelete(revisionId.toString());
    }

    private CloudantViews cloudantViews = new CloudantViews() {{
        Map<String, CloudantView> viewMap = new HashMap<>();
        Map<String, CloudantIndex> indexMap = new HashMap<>();

        CloudantViews.CloudantView view = new CloudantViews.CloudantView();
        view.setMap("function(doc) {emit(doc.offerId, doc._id)}");
        view.setResultClass(String.class);
        viewMap.put("by_offerId", view);

        CloudantIndex index = new CloudantIndex();
        index.setResultClass(String.class);
        index.setIndex("function(doc) {" +
                "index(\'offerId\', doc.offerId);" +
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
