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
import com.junbo.common.id.OfferRevisionId;
import com.junbo.sharding.IdGenerator;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

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

    @Override
    public OfferRevision create(OfferRevision offerRevision) {
        if (offerRevision.getRevisionId() == null) {
            offerRevision.setRevisionId(idGenerator.nextId());
        }
        return super.cloudantPost(offerRevision);
    }

    @Override
    public OfferRevision get(Long revisionId) {
        return super.cloudantGet(revisionId.toString());
    }

    @Override
    public List<OfferRevision> getRevisions(OfferRevisionsGetOptions options) {
        List<OfferRevision> offerRevisions = new ArrayList<>();
        if (!CollectionUtils.isEmpty(options.getRevisionIds())) {
            for (OfferRevisionId revisionId : options.getRevisionIds()) {
                OfferRevision revision = super.cloudantGet(revisionId.toString());
                if (revision==null) {
                    continue;
                } else if (!StringUtils.isEmpty(options.getStatus())
                        && !options.getStatus().equalsIgnoreCase(revision.getStatus())) {
                    continue;
                } else {
                    offerRevisions.add(revision);
                }
            }
        } else if (!CollectionUtils.isEmpty(options.getOfferIds())) {
            for (OfferId offerId : options.getOfferIds()) {
                List<OfferRevision> revisions = super.queryView("by_offerId", offerId.toString());
                if (StringUtils.isEmpty(options.getStatus())) {
                    continue;
                }
                Iterator<OfferRevision> iterator = revisions.iterator();
                while (iterator.hasNext()) {
                    OfferRevision revision = iterator.next();
                    if (!options.getStatus().equalsIgnoreCase(revision.getStatus())) {
                        iterator.remove();
                    }
                }
                offerRevisions.addAll(revisions);
            }
        } else if (!StringUtils.isEmpty(options.getStatus())){
            offerRevisions = super.queryView("by_status", options.getStatus().toUpperCase(),
                    options.getValidSize(), options.getValidStart(), false);
        } else {
            offerRevisions = super.queryView("by_offerId", null,
                    options.getValidSize(), options.getValidStart(), false);
        }

        return offerRevisions;
    }

    @Override
    public List<OfferRevision> getRevisions(Collection<Long> offerIds, Long timestamp) {
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
            if (revision != null) {
                revisions.add(revision);
            }
        }

        return revisions;
    }

    @Override
    public List<OfferRevision> getRevisions(Long itemId) {
        return super.queryView("by_itemId", itemId.toString());
    }

    @Override
    public List<OfferRevision> getRevisionsBySubOfferId(Long offerId) {
        return super.queryView("by_subOfferId", offerId.toString());
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
        view.setMap("function(doc) {emit(doc.offerId, doc._id);}");
        view.setResultClass(String.class);
        viewMap.put("by_offerId", view);

        view = new CloudantViews.CloudantView();
        view.setMap("function(doc) {if (doc.status){ emit(doc.status, doc._id); }}");
        view.setResultClass(String.class);
        viewMap.put("by_status", view);

        view = new CloudantViews.CloudantView();
        view.setMap("function(doc) {" +
                "if (doc.items) {" +
                    "for (var idx in doc.items) {" +
                        "emit(doc.items[idx].itemId, doc._id);" +
                    "}" +
                "}" +
            "}");
        view.setResultClass(String.class);
        viewMap.put("by_itemId", view);

        view = new CloudantViews.CloudantView();
        view.setMap("function(doc) {" +
                "if (doc.subOffers) {" +
                    "for (var idx in doc.subOffers) {" +
                        "emit(doc.subOffers[idx], doc._id);" +
                    "}" +
                "}" +
            "}");
        view.setResultClass(String.class);
        viewMap.put("by_subOfferId", view);

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
