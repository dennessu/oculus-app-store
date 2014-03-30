/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.spec.model.offer;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.junbo.catalog.spec.model.common.BaseEntityModel;
import com.junbo.common.jackson.annotation.AttributeId;
import com.junbo.common.jackson.annotation.OfferId;
import com.junbo.common.jackson.annotation.OfferRevisionId;

import java.util.List;

/**
 * Offer model.
 */
public class Offer extends BaseEntityModel {
    @OfferId
    @JsonProperty("self")
    private Long offerId;

    @OfferRevisionId
    @JsonProperty("currentRevision")
    private Long currentRevisionId;

    @AttributeId
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

    public List<Long> getCategories() {
        return categories;
    }

    public void setCategories(List<Long> categories) {
        this.categories = categories;
    }
}
