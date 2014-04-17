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
import java.util.List;

/**
 * Items get options.
 */
public class OfferRevisionsGetOptions extends PageableGetOptions {
    @QueryParam("offerId")
    private List<OfferId> offerIds;
    @QueryParam("revisionId")
    private List<OfferRevisionId> revisionIds;
    @QueryParam("status")
    private String status;
    //@QueryParam("timestamp")
   // private Long timestamp;

    public List<OfferId> getOfferIds() {
        return offerIds;
    }

    public void setOfferIds(List<OfferId> offerIds) {
        this.offerIds = offerIds;
    }

    public List<OfferRevisionId> getRevisionIds() {
        return revisionIds;
    }

    public void setRevisionIds(List<OfferRevisionId> revisionIds) {
        this.revisionIds = revisionIds;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

   /* public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }
    */
}
