/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.spec.model.promotion;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.junbo.catalog.spec.model.common.BaseEntityModel;
import com.junbo.common.jackson.annotation.*;

/**
 * Promotion model.
 */
public class Promotion extends BaseEntityModel {
    @PromotionId
    @JsonProperty("self")
    private String promotionId;

    private PromotionType type;

    @PromotionRevisionId
    @JsonProperty("currentRevision")
    private String currentRevisionId;

    @UserId
    @JsonProperty("publisher")
    private Long ownerId;

    public String getPromotionId() {
        return promotionId;
    }

    public void setPromotionId(String promotionId) {
        this.promotionId = promotionId;
    }

    public PromotionType getType() {
        return type;
    }

    public void setType(PromotionType type) {
        this.type = type;
    }

    public String getCurrentRevisionId() {
        return currentRevisionId;
    }

    public void setCurrentRevisionId(String currentRevisionId) {
        this.currentRevisionId = currentRevisionId;
    }

    public Long getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(Long ownerId) {
        this.ownerId = ownerId;
    }

    @Override
    @JsonIgnore
    public String getId() {
        return promotionId;
    }

    @Override
    public void setId(String id) {
        this.promotionId = id;
    }
}
