/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.spec.model.offer;

import com.junbo.catalog.spec.model.common.PageableGetOptions;
import com.junbo.common.id.OfferId;
import com.junbo.common.id.OfferRevisionId;

import javax.ws.rs.QueryParam;
import java.util.Set;

/**
 * Items get options.
 */
public class OfferRevisionsGetOptions extends PageableGetOptions {
    @QueryParam("offerId")
    private Set<OfferId> offerIds;
    @QueryParam("revisionId")
    private Set<OfferRevisionId> revisionIds;
    @QueryParam("status")
    private String status;
    @QueryParam("timeInMillis")
    private Long timestamp;

    public Set<OfferId> getOfferIds() {
        return offerIds;
    }

    public void setOfferIds(Set<OfferId> offerIds) {
        this.offerIds = offerIds;
    }

    public Set<OfferRevisionId> getRevisionIds() {
        return revisionIds;
    }

    public void setRevisionIds(Set<OfferRevisionId> revisionIds) {
        this.revisionIds = revisionIds;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }
}
