/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.spec.model.offer;

import com.junbo.catalog.spec.model.common.PageableGetOptions;
import com.junbo.common.id.*;

import javax.ws.rs.QueryParam;
import java.util.Set;

/**
 * Offers get options.
 */
public class OffersGetOptions extends PageableGetOptions {
    @QueryParam("offerId")
    private Set<String> offerIds;
    @QueryParam("published")
    private Boolean published;
    @QueryParam("categoryId")
    private String category;
    @QueryParam("itemId")
    private String itemId;
    @QueryParam("publisherId")
    private OrganizationId ownerId;
    @QueryParam("q")
    private String query;
    @QueryParam("country")
    private String country;

    public Set<String> getOfferIds() {
        return offerIds;
    }

    public void setOfferIds(Set<String> offerIds) {
        this.offerIds = offerIds;
    }

    public Boolean getPublished() {
        return published;
    }

    public void setPublished(Boolean published) {
        this.published = published;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public OrganizationId getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(OrganizationId ownerId) {
        this.ownerId = ownerId;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }
}
