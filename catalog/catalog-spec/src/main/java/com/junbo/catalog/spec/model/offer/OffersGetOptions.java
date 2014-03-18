/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.spec.model.offer;

import com.junbo.catalog.spec.model.common.EntitiesGetOptions;
import com.junbo.common.id.OfferId;

import javax.ws.rs.QueryParam;
import java.util.List;

/**
 * Offers get options.
 */
public class OffersGetOptions extends EntitiesGetOptions {
    @QueryParam("id")
    private List<OfferId> offerIds;

    public static OffersGetOptions getDefault() {
        return setDefaults(new OffersGetOptions());
    }

    public List<OfferId> getOfferIds() {
        return offerIds;
    }

    public void setOfferIds(List<OfferId> offerIds) {
        this.offerIds = offerIds;
    }

    @Override
    public List<OfferId> getEntityIds() {
        return offerIds;
    }
}
