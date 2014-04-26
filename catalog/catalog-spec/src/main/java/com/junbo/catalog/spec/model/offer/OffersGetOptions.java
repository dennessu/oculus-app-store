/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.spec.model.offer;

import com.junbo.catalog.spec.model.common.PageableGetOptions;
import com.junbo.common.id.ItemId;
import com.junbo.common.id.OfferId;
import com.junbo.common.jackson.annotation.AttributeId;

import javax.ws.rs.QueryParam;
import java.util.List;

/**
 * Offers get options.
 */
public class OffersGetOptions extends PageableGetOptions {
    @QueryParam("offerId")
    private List<OfferId> offerIds;
    @QueryParam("published")
    private Boolean published;
    @AttributeId
    @QueryParam("category")
    private Long category;
    @QueryParam("itemId")
    private ItemId itemId;

    public List<OfferId> getOfferIds() {
        return offerIds;
    }

    public void setOfferIds(List<OfferId> offerIds) {
        this.offerIds = offerIds;
    }

    public Boolean getPublished() {
        return published;
    }

    public void setPublished(Boolean published) {
        this.published = published;
    }

    public Long getCategory() {
        return category;
    }

    public void setCategory(Long category) {
        this.category = category;
    }

    public ItemId getItemId() {
        return itemId;
    }

    public void setItemId(ItemId itemId) {
        this.itemId = itemId;
    }
}
