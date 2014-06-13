/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.spec.model.offer;

import com.junbo.catalog.spec.model.common.PageableGetOptions;

import javax.ws.rs.QueryParam;
import java.util.Set;

/**
 * Items get options.
 */
public class OfferRevisionsGetOptions extends PageableGetOptions {
    @QueryParam("offerId")
    private Set<String> offerIds;
    @QueryParam("revisionId")
    private Set<String> revisionIds;
    @QueryParam("status")
    private String status;
    @QueryParam("timeInMillis")
    private Long timestamp;

    public Set<String> getOfferIds() {
        return offerIds;
    }

    public void setOfferIds(Set<String> offerIds) {
        this.offerIds = offerIds;
    }

    public Set<String> getRevisionIds() {
        return revisionIds;
    }

    public void setRevisionIds(Set<String> revisionIds) {
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
