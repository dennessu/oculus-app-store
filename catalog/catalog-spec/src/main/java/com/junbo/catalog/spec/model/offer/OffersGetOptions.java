/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.spec.model.offer;

import com.junbo.catalog.spec.model.common.PageableGetOptions;
import com.junbo.common.id.ItemId;
import com.junbo.common.id.OfferAttributeId;
import com.junbo.common.id.OfferId;
import com.junbo.common.id.UserId;

import javax.ws.rs.QueryParam;
import java.util.Set;

/**
 * Offers get options.
 */
public class OffersGetOptions extends PageableGetOptions {
    @QueryParam("offerId")
    private Set<OfferId> offerIds;
    @QueryParam("published")
    private Boolean published;
    @QueryParam("category")
    private OfferAttributeId category;
    @QueryParam("itemId")
    private ItemId itemId;
    @QueryParam("publisherId")
    private UserId ownerId;
    @QueryParam("q")
    private String query;

    public Set<OfferId> getOfferIds() {
        return offerIds;
    }

    public void setOfferIds(Set<OfferId> offerIds) {
        this.offerIds = offerIds;
    }

    public Boolean getPublished() {
        return published;
    }

    public void setPublished(Boolean published) {
        this.published = published;
    }

    public OfferAttributeId getCategory() {
        return category;
    }

    public void setCategory(OfferAttributeId category) {
        this.category = category;
    }

    public ItemId getItemId() {
        return itemId;
    }

    public void setItemId(ItemId itemId) {
        this.itemId = itemId;
    }

    public UserId getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(UserId ownerId) {
        this.ownerId = ownerId;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }
}
