/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.spec.model.promotion;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.junbo.catalog.spec.model.common.BaseEntityModel;
import com.junbo.common.jackson.annotation.*;

/**
 * Promotion model.
 */
public class Promotion extends BaseEntityModel {
    @PromotionId
    @JsonProperty("self")
    private Long promotionId;

    private PromotionType type;

    @PromotionRevisionId
    @JsonProperty("currentRevision")
    private Long currentRevisionId;

    @UserId
    @JsonProperty("publisher")
    private Long ownerId;

    public Long getPromotionId() {
        return promotionId;
    }

    public void setPromotionId(Long promotionId) {
        this.promotionId = promotionId;
    }

    public PromotionType getType() {
        return type;
    }

    public void setType(PromotionType type) {
        this.type = type;
    }

    public Long getCurrentRevisionId() {
        return currentRevisionId;
    }

    public void setCurrentRevisionId(Long currentRevisionId) {
        this.currentRevisionId = currentRevisionId;
    }

    public Long getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(Long ownerId) {
        this.ownerId = ownerId;
    }

    @Override
    public Long getId() {
        return promotionId;
    }

    @Override
    public void setId(Long id) {
        this.promotionId = id;
    }
}
