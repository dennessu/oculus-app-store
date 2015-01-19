/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.db.repo.impl.cloudant;

import com.junbo.catalog.common.cache.CacheFacade;
import com.junbo.catalog.common.util.Callable;
import com.junbo.catalog.common.util.Utils;
import com.junbo.catalog.db.repo.OfferRevisionRepository;
import com.junbo.catalog.spec.enums.Status;
import com.junbo.catalog.spec.model.offer.OfferRevision;
import com.junbo.catalog.spec.model.offer.OfferRevisionsGetOptions;
import com.junbo.catalog.spec.model.offer.RevisionInfo;
import com.junbo.common.cloudant.CloudantClient;
import com.junbo.common.cloudant.model.CloudantQueryResult;
import com.junbo.common.cloudant.model.CloudantSearchResult;
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
        OfferRevision createdRevision = cloudantPostSync(offerRevision);
        CacheFacade.OFFER_REVISION.put(createdRevision.getRevisionId(), createdRevision);
        return createdRevision;
    }

    @Override
    public OfferRevision get(final String revisionId) {
        if (revisionId == null) {
            return null;
        }
        return CacheFacade.OFFER_REVISION.get(revisionId, new Callable<OfferRevision>() {
            @Override
            public OfferRevision execute() {
                return cloudantGetSync(revisionId);
            }
        });
    }

    @Override
    public List<OfferRevision> getRevisions(OfferRevisionsGetOptions options) {
        List<OfferRevision> offerRevisions = new ArrayList<>();
        if (!CollectionUtils.isEmpty(options.getRevisionIds())) {
            for (String revisionId : options.getRevisionIds()) {
                OfferRevision revision = get(revisionId);
                if (revision==null
                        || !StringUtils.isEmpty(options.getStatus()) && !options.getStatus().equalsIgnoreCase(revision.getStatus())
                        || options.getPublisherId() != null && !options.getPublisherId().equals(revision.getOwnerId())) {
                    continue;
                } else {
                    offerRevisions.add(revision);
                }
            }
            options.setTotal(Long.valueOf(offerRevisions.size()));
        } else if (!CollectionUtils.isEmpty(options.getOfferIds())) {
            for (String offerId : options.getOfferIds()) {
                List<OfferRevision> revisions = queryView("by_offerId", offerId.toString()).get();
                if (!StringUtils.isEmpty(options.getStatus()) || options.getPublisherId() != null) {
                    Iterator<OfferRevision> iterator = revisions.iterator();
                    while (iterator.hasNext()) {
                        OfferRevision revision = iterator.next();
                        if (!StringUtils.isEmpty(options.getStatus()) && !options.getStatus().equalsIgnoreCase(revision.getStatus())
                                || options.getPublisherId() != null && !options.getPublisherId().equals(revision.getOwnerId())) {
                            iterator.remove();
                        }
                    }
                }
                offerRevisions.addAll(revisions);
            }
            options.setTotal(Long.valueOf(offerRevisions.size()));
        } else if (options.getPublisherId() != null || !StringUtils.isEmpty(options.getStatus())) {
            StringBuilder sb = new StringBuilder();
            if (options.getPublisherId() != null) {
                sb.append("ownerId:\"").append(options.getPublisherId().getValue()).append("\"");
            }
            if (!StringUtils.isEmpty(options.getStatus())) {
                if (sb.length() > 0) {
                    sb.append(" AND ");
                }
                sb.append("status:\"").append(options.getStatus()).append("\"");
            }
            CloudantSearchResult<OfferRevision> searchResult = searchSync("search", sb.toString(), options.getValidSize(), options.getCursor());
            offerRevisions = searchResult.getResults();
            options.setNextCursor(searchResult.getBookmark());
            options.setTotal(searchResult.getTotal());
        } else {
            CloudantQueryResult queryResult = queryViewSync("by_offerId", null, options.getValidSize(), options.getValidStart(), false, true);
            offerRevisions = Utils.getDocs(queryResult.getRows());
            options.setTotal(queryResult.getTotalRows());
        }

        return offerRevisions;
    }

    @Override
    public List<OfferRevision> getRevisions(Collection<String> offerIds, Long timestamp) {
        List<OfferRevision> revisions = new ArrayList<>();
        for (final String offerId : offerIds) {
            List<RevisionInfo> revisionInfoList = CacheFacade.OFFER_CONTROL.get(offerId, new Callable<List<RevisionInfo>>() {
                @Override
                public List<RevisionInfo> execute() {
                    List<RevisionInfo> result = new ArrayList<>();
                    List<OfferRevision> itemRevisions = queryView("by_offerId", offerId).get();
                    for (OfferRevision revision : itemRevisions) {
                        if (revision.getTimestamp() == null) {
                            continue;
                        }
                        RevisionInfo revisionInfo = new RevisionInfo();
                        revisionInfo.setRevisionId(revision.getRevisionId());
                        revisionInfo.setStartTime(revision.getStartTime().getTime());
                        if (revision.getEndTime() == null) {
                            revisionInfo.setEndTime(Utils.maxDate().getTime());
                        } else {
                            revisionInfo.setEndTime(revision.getEndTime().getTime());
                        }
                        revisionInfo.setApprovedTime(revision.getTimestamp());
                        result.add(revisionInfo);
                    }
                    return result;
                }
            });

            String revisionId = null;
            Long maxTimestamp = 0L;
            for (RevisionInfo revisionInfo : revisionInfoList) {
                if (revisionInfo.getApprovedTime() > maxTimestamp && (timestamp>=revisionInfo.getStartTime() && timestamp<revisionInfo.getEndTime())) {
                    maxTimestamp = revisionInfo.getApprovedTime();
                    revisionId = revisionInfo.getRevisionId();
                }
            }
            OfferRevision revision = get(revisionId);
            if (revision != null) {
                revisions.add(revision);
            }
        }

        return revisions;
    }

    @Override
    public List<OfferRevision> getRevisions(String itemId) {
        return queryView("by_itemId", itemId).get();
    }

    @Override
    public List<OfferRevision> getRevisionsBySubOfferId(String offerId) {
        return queryView("by_subOfferId", offerId).get();
    }

    @Override
    public OfferRevision update(OfferRevision revision, OfferRevision oldRevision) {
        OfferRevision updatedRevision = cloudantPutSync(revision, oldRevision);
        CacheFacade.OFFER_REVISION.put(updatedRevision.getRevisionId(), updatedRevision);
        if (Status.APPROVED.is(revision.getStatus()) || Status.OBSOLETE.is(revision.getStatus())) {
            CacheFacade.OFFER_CONTROL.evict(updatedRevision.getOfferId());
        }
        return updatedRevision;
    }

    @Override
    public void delete(String revisionId) {
        cloudantDeleteSync(revisionId);
        CacheFacade.OFFER_REVISION.evict(revisionId);
    }

}
