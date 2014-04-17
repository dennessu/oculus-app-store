/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.spec.model.offer;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.junbo.catalog.spec.model.common.BaseEntityModel;
import com.junbo.catalog.spec.model.common.Link;
import com.junbo.common.jackson.annotation.*;
import com.wordnik.swagger.annotations.ApiModelProperty;

import java.util.List;

/**
 * Offer model.
 */
public class Offer extends BaseEntityModel {
    @OfferId
    @JsonProperty("self")
    @ApiModelProperty(position = 1, required = true, value = "[Client Immutable] The id of offer resource.")
    private Long offerId;

    @OfferRevisionId
    @JsonProperty("currentRevision")
    @ApiModelProperty(position = 20, required = true, value = "The id of current revision.")
    private Long currentRevisionId;

    @ApiModelProperty(position = 21, required = true, value = "Offer revisions")
    private Link revisions;

    @UserId
    @JsonProperty("publisher")
    @ApiModelProperty(position = 22, required = true, value = "Publisher of the offer.")
    private Long ownerId;

    @ItemId
    @JsonProperty("iapItem")
    @ApiModelProperty(position = 23, required = false, value = "The item in which the IAP offer will be sold.")
    private Long iapItemId;

    @AttributeId
    @ApiModelProperty(position = 24, required = true, value = "Categories of the offer.")
    private List<Long> categories;

    public Long getOfferId() {
        return offerId;
    }

    public void setOfferId(Long offerId) {
        this.offerId = offerId;
    }

    public Long getCurrentRevisionId() {
        return currentRevisionId;
    }

    public void setCurrentRevisionId(Long currentRevisionId) {
        this.currentRevisionId = currentRevisionId;
    }

    public Link getRevisions() {
        return revisions;
    }

    public void setRevisions(Link revisions) {
        this.revisions = revisions;
    }

    public Long getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(Long ownerId) {
        this.ownerId = ownerId;
    }

    public Long getIapItemId() {
        return iapItemId;
    }

    public void setIapItemId(Long iapItemId) {
        this.iapItemId = iapItemId;
    }

    public List<Long> getCategories() {
        return categories;
    }

    public void setCategories(List<Long> categories) {
        this.categories = categories;
    }
}
