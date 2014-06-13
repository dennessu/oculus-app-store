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
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.*;

/**
 * Offer revision repository.
 */
public class OfferRevisionRepositoryImpl extends CloudantClient<OfferRevision> implements OfferRevisionRepository {

    @Override
    public OfferRevision create(OfferRevision offerRevision) {
        return super.cloudantPost(offerRevision).get();
    }

    @Override
    public OfferRevision get(String revisionId) {
        if (revisionId == null) {
            return null;
        }
        return super.cloudantGet(revisionId).get();
    }

    @Override
    public List<OfferRevision> getRevisions(OfferRevisionsGetOptions options) {
        List<OfferRevision> offerRevisions = new ArrayList<>();
        if (!CollectionUtils.isEmpty(options.getRevisionIds())) {
            for (OfferRevisionId revisionId : options.getRevisionIds()) {
                OfferRevision revision = super.cloudantGet(revisionId.toString()).get();
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
                List<OfferRevision> revisions = super.queryView("by_offerId", offerId.toString()).get();
                if (!StringUtils.isEmpty(options.getStatus())) {
                    Iterator<OfferRevision> iterator = revisions.iterator();
                    while (iterator.hasNext()) {
                        OfferRevision revision = iterator.next();
                        if (!options.getStatus().equalsIgnoreCase(revision.getStatus())) {
                            iterator.remove();
                        }
                    }
                }
                offerRevisions.addAll(revisions);
            }
        } else if (!StringUtils.isEmpty(options.getStatus())){
            offerRevisions = super.queryView("by_status", options.getStatus().toUpperCase(),
                    options.getValidSize(), options.getValidStart(), false).get();
        } else {
            offerRevisions = super.queryView("by_offerId", null,
                    options.getValidSize(), options.getValidStart(), false).get();
        }

        return offerRevisions;
    }

    @Override
    public List<OfferRevision> getRevisions(Collection<String> offerIds, Long timestamp) {
        List<OfferRevision> revisions = new ArrayList<>();
        for (String offerId : offerIds) {
            List<OfferRevision> itemRevisions = super.queryView("by_offerId", offerId).get();
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
    public List<OfferRevision> getRevisions(String itemId) {
        return super.queryView("by_itemId", itemId).get();
    }

    @Override
    public List<OfferRevision> getRevisionsBySubOfferId(String offerId) {
        return super.queryView("by_subOfferId", offerId).get();
    }

    @Override
    public OfferRevision update(OfferRevision revision) {
        return super.cloudantPut(revision).get();
    }

    @Override
    public void delete(String revisionId) {
        super.cloudantDelete(revisionId).get();
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
