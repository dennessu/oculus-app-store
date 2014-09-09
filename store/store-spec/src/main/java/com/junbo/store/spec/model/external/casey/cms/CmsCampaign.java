/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.store.spec.model.external.casey.cms;

import com.junbo.store.spec.model.external.casey.BaseCaseyModel;
import com.junbo.store.spec.model.external.casey.CaseyLink;

import java.util.List;

/**
 * The CmsCampaign class.
 */
public class CmsCampaign extends BaseCaseyModel {

    private CaseyLink self;

    private String status;

    private String label;

    private String startTime;

    private String endTime;

    private List<CaseyLink> eligibleCountries;

    private List<Placement> placements;

    public CaseyLink getSelf() {
        return self;
    }

    public void setSelf(CaseyLink self) {
        this.self = self;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public List<CaseyLink> getEligibleCountries() {
        return eligibleCountries;
    }

    public void setEligibleCountries(List<CaseyLink> eligibleCountries) {
        this.eligibleCountries = eligibleCountries;
    }

    public List<Placement> getPlacements() {
        return placements;
    }

    public void setPlacements(List<Placement> placements) {
        this.placements = placements;
    }
}
