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
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * Offer revision repository.
 */
public class OfferRevisionRepositoryImpl extends CloudantClient<OfferRevision> implements OfferRevisionRepository {

    @Override
    public OfferRevision create(OfferRevision offerRevision) {
        return cloudantPostSync(offerRevision);
    }

    @Override
    public OfferRevision get(String revisionId) {
        if (revisionId == null) {
            return null;
        }
        return cloudantGetSync(revisionId);
    }

    @Override
    public List<OfferRevision> getRevisions(OfferRevisionsGetOptions options) {
        List<OfferRevision> offerRevisions = new ArrayList<>();
        if (!CollectionUtils.isEmpty(options.getRevisionIds())) {
            for (String revisionId : options.getRevisionIds()) {
                OfferRevision revision = cloudantGetSync(revisionId.toString());
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
            for (String offerId : options.getOfferIds()) {
                List<OfferRevision> revisions = queryView("by_offerId", offerId.toString()).syncGet();
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
            offerRevisions = queryView("by_status", options.getStatus().toUpperCase(),
                    options.getValidSize(), options.getValidStart(), false).syncGet();
        } else {
            offerRevisions = queryView("by_offerId", null,
                    options.getValidSize(), options.getValidStart(), false).syncGet();
        }

        return offerRevisions;
    }

    @Override
    public List<OfferRevision> getRevisions(Collection<String> offerIds, Long timestamp) {
        List<OfferRevision> revisions = new ArrayList<>();
        for (String offerId : offerIds) {
            List<OfferRevision> itemRevisions = queryView("by_offerId", offerId).syncGet();
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
        return queryView("by_itemId", itemId).syncGet();
    }

    @Override
    public List<OfferRevision> getRevisionsBySubOfferId(String offerId) {
        return queryView("by_subOfferId", offerId).syncGet();
    }

    @Override
    public OfferRevision update(OfferRevision revision, OfferRevision oldRevision) {
        return cloudantPutSync(revision, oldRevision);
    }

    @Override
    public void delete(String revisionId) {
        cloudantDeleteSync(revisionId);
    }

}
