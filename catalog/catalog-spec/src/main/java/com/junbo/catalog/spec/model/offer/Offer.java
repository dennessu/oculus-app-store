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

import java.util.List;
import java.util.Map;

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
    private List<Long> categories;

    @ApiModelProperty(position = 26, required = true, value = "Environment", allowableValues = "DEV, STAGING, PROD")
    private String environment;

    // current revision used for index & search, periodically updated by backend job
    @JsonIgnore
    private OfferRevision activeRevision;

    @JsonIgnore
    private Map<Long, RevisionInfo> approvedRevisions;

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

    public List<Long> getCategories() {
        return categories;
    }

    public void setCategories(List<Long> categories) {
        this.categories = categories;
    }

    public String getEnvironment() {
        return environment;
    }

    public void setEnvironment(String environment) {
        this.environment = environment;
    }

    public OfferRevision getActiveRevision() {
        return activeRevision;
    }

    public void setActiveRevision(OfferRevision activeRevision) {
        this.activeRevision = activeRevision;
    }

    public Map<Long, RevisionInfo> getApprovedRevisions() {
        return approvedRevisions;
    }

    public void setApprovedRevisions(Map<Long, RevisionInfo> approvedRevisions) {
        this.approvedRevisions = approvedRevisions;
    }

    @Override
    @JsonIgnore
    public Long getId() {
        return offerId;
    }

    @Override
    public void setId(Long id) {
        this.offerId = id;
    }
}
