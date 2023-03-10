/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.db.entity;

import com.junbo.common.hibernate.StringJsonUserType;
import com.junbo.catalog.spec.model.promotion.PromotionType;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;

import javax.persistence.*;

/**
 * Promotion DB entity.
 */
@Entity
@Table(name = "promotion")
@TypeDefs(@TypeDef(name="json-string", typeClass=StringJsonUserType.class))
public class PromotionEntity extends BaseEntity {
    private String promotionId;
    private PromotionType type;
    private String currentRevisionId;
    private Long ownerId;
    private String payload;

    @Id
    @Column(name = "promotion_id")
    public String getPromotionId() {
        return promotionId;
    }

    public void setPromotionId(String promotionId) {
        this.promotionId = promotionId;
    }

    @Column(name = "type")
    public PromotionType getType() {
        return type;
    }

    public void setType(PromotionType type) {
        this.type = type;
    }

    @Column(name = "current_revision_id")
    public String getCurrentRevisionId() {
        return currentRevisionId;
    }

    public void setCurrentRevisionId(String currentRevisionId) {
        this.currentRevisionId = currentRevisionId;
    }

    @Column(name = "owner_id")
    public Long getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(Long ownerId) {
        this.ownerId = ownerId;
    }

    @Column(name = "payload")
    @Type(type = "json-string")
    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }

    @Override
    @Transient
    public String getId() {
        return promotionId;
    }

    @Override
    public void setId(String id) {
        this.promotionId = id;
    }
}
