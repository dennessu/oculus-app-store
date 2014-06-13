/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.spec.model.promotion;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.junbo.catalog.spec.model.common.BaseRevisionModel;
import com.junbo.catalog.spec.model.promotion.criterion.Criterion;
import com.junbo.common.jackson.annotation.CurrencyId;
import com.junbo.common.jackson.annotation.PromotionId;
import com.junbo.common.jackson.annotation.PromotionRevisionId;
import com.junbo.common.jackson.annotation.UserId;

import java.util.Date;
import java.util.List;

/**
 * PromotionRevision model.
 */
public class PromotionRevision extends BaseRevisionModel {
    @PromotionRevisionId
    @JsonProperty("self")
    private String revisionId;

    @PromotionId
    @JsonProperty("promotion")
    private String promotionId;

    @UserId
    @JsonProperty("publisher")
    private Long ownerId;

    private PromotionType type;

    @CurrencyId
    private String currency;

    private Date startDate;
    private Date endDate;

    private List<Criterion> criteria;

    private Benefit benefit;

    public String getRevisionId() {
        return revisionId;
    }

    public void setRevisionId(String revisionId) {
        this.revisionId = revisionId;
    }

    public String getPromotionId() {
        return promotionId;
    }

    public void setPromotionId(String promotionId) {
        this.promotionId = promotionId;
    }

    public Long getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(Long ownerId) {
        this.ownerId = ownerId;
    }

    public PromotionType getType() {
        return type;
    }

    public void setType(PromotionType type) {
        this.type = type;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public List<Criterion> getCriteria() {
        return criteria;
    }

    public void setCriteria(List<Criterion> criteria) {
        this.criteria = criteria;
    }

    public Benefit getBenefit() {
        return benefit;
    }

    public void setBenefit(Benefit benefit) {
        this.benefit = benefit;
    }

    public boolean isEffective(Date current) {
        return startDate.before(current) && endDate.after(current);
    }

    @Override
    @JsonIgnore
    public String getId() {
        return revisionId;
    }

    @Override
    public void setId(String id) {
        this.revisionId = id;
    }

    @Override
    @JsonIgnore
    public String getEntityId() {
        return promotionId;
    }
}
