/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.spec.model.offer;

import com.junbo.catalog.spec.model.common.PageableGetOptions;
import com.junbo.common.id.OfferId;
import com.junbo.common.jackson.annotation.AttributeId;

import javax.ws.rs.QueryParam;
import java.util.List;

/**
 * Offers get options.
 */
public class OffersGetOptions extends PageableGetOptions {
    @QueryParam("id")
    private List<OfferId> offerIds;
    @QueryParam("curated")
    private Boolean curated;
    @AttributeId
    @QueryParam("category")
    private Long category;

    public List<OfferId> getOfferIds() {
        return offerIds;
    }

    public void setOfferIds(List<OfferId> offerIds) {
        this.offerIds = offerIds;
    }

    public Boolean getCurated() {
        return curated;
    }

    public void setCurated(Boolean curated) {
        this.curated = curated;
    }

    public Long getCategory() {
        return category;
    }

    public void setCategory(Long category) {
        this.category = category;
    }
}
