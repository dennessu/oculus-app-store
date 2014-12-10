/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.catalog.spec.model.offer;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.junbo.catalog.spec.model.common.BaseEntityModel;
import com.junbo.common.id.OrganizationId;
import com.junbo.common.jackson.annotation.*;
import com.junbo.common.model.Link;
import com.wordnik.swagger.annotations.ApiModelProperty;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * Offer model.
 */
public class Offer extends BaseEntityModel {
    @OfferId
    @JsonProperty("self")
    @ApiModelProperty(position = 1, required = true, value = "[Client Immutable] The id of offer resource.")
    private String offerId;

    @OfferRevisionId
    @JsonProperty("currentRevision")
    @ApiModelProperty(position = 20, required = true, value = "The id of current revision.")
    private String currentRevisionId;

    @ApiModelProperty(position = 21, required = true, value = "Offer revisions")
    @HateoasLink("/offer-revisions?offerId={offerId}")
    private Link revisions;

    @JsonProperty("publisher")
    @ApiModelProperty(position = 22, required = true, value = "Organization owner of the offer")
    private OrganizationId ownerId;

    @ApiModelProperty(position = 23, required = true, value = "Whether the offer is published")
    @JsonProperty("isPublished")
    private Boolean published;

    @OfferAttributeId
    @ApiModelProperty(position = 25, required = true, value = "Categories of the offer.")
    private List<String> categories;

    @ApiModelProperty(position = 26, required = true, value = "Environment", allowableValues = "DEV, STAGING, PROD")
    private String environment;

    @Deprecated
    @JsonProperty("publisherRevenueRatio")
    @ApiModelProperty(position = 27, required = true, value = "Revenue ratio to developer")
    private BigDecimal developerRatio;

    // current revision used for index & search, periodically updated by backend job
    @JsonIgnore
    private OfferRevision activeRevision;

    @JsonIgnore
    private Map<String, RevisionInfo> approvedRevisions;

    public String getOfferId() {
        return offerId;
    }

    public void setOfferId(String offerId) {
        this.offerId = offerId;
    }

    public String getCurrentRevisionId() {
        return currentRevisionId;
    }

    public void setCurrentRevisionId(String currentRevisionId) {
        this.currentRevisionId = currentRevisionId;
    }

    public Link getRevisions() {
        return revisions;
    }

    public void setRevisions(Link revisions) {
        this.revisions = revisions;
    }

    public OrganizationId getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(OrganizationId ownerId) {
        this.ownerId = ownerId;
    }

    public Boolean getPublished() {
        return published;
    }

    public void setPublished(Boolean published) {
        this.published = published;
    }

    public List<String> getCategories() {
        return categories;
    }

    public void setCategories(List<String> categories) {
        this.categories = categories;
    }

    public String getEnvironment() {
        return environment;
    }

    public void setEnvironment(String environment) {
        this.environment = environment;
    }

    public BigDecimal getDeveloperRatio() {
        return developerRatio;
    }

    public void setDeveloperRatio(BigDecimal developerRatio) {
        this.developerRatio = developerRatio;
    }

    public OfferRevision getActiveRevision() {
        return activeRevision;
    }

    public void setActiveRevision(OfferRevision activeRevision) {
        this.activeRevision = activeRevision;
    }

    public Map<String, RevisionInfo> getApprovedRevisions() {
        return approvedRevisions;
    }

    public void setApprovedRevisions(Map<String, RevisionInfo> approvedRevisions) {
        this.approvedRevisions = approvedRevisions;
    }

    @Override
    @JsonIgnore
    public String getId() {
        return offerId;
    }

    @Override
    public void setId(String id) {
        this.offerId = id;
    }
}
