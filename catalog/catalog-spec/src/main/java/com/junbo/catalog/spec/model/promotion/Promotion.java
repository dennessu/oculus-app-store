/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.spec.model.promotion;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.junbo.catalog.spec.model.common.VersionedModel;
import com.junbo.common.jackson.annotation.PromotionId;

import java.util.Date;
import java.util.List;

/**
 * Promotion model.
 */
public class Promotion extends VersionedModel {
    @PromotionId
    @JsonProperty("self")
    private Long id;
    private PromotionType type;
    private String currency;
    private Date startDate;
    private Date endDate;

    private List<Criterion> criteria;

    private Benefit benefit;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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
    public String getEntityType() {
        return "Promotion";
    }
}
