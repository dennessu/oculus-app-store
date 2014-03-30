/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.spec.model.offer;

import com.junbo.catalog.spec.model.common.PageableGetOptions;
import com.junbo.catalog.spec.model.common.Status;
import com.junbo.common.id.OfferId;

import javax.ws.rs.QueryParam;
import java.util.List;

/**
 * Offers get options.
 */
public class OffersGetOptions extends PageableGetOptions {
    @QueryParam("id")
    private List<OfferId> offerIds;
    @QueryParam("status")
    private String status;

    public static OffersGetOptions getDefault() {
        return new OffersGetOptions().setStatus(Status.RELEASED);
    }

    public static OffersGetOptions getDesign() {
        return new OffersGetOptions().setStatus(Status.DESIGN);
    }

    public List<OfferId> getOfferIds() {
        return offerIds;
    }

    public OffersGetOptions setOfferIds(List<OfferId> offerIds) {
        this.offerIds = offerIds;
        return this;
    }

    public String getStatus() {
        return status;
    }

    public OffersGetOptions setStatus(String status) {
        this.status = status;
        return this;
    }
}
