/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.common.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.junbo.common.cloudant.CloudantEntity;
import com.junbo.common.json.PropertyAssignedAware;
import com.junbo.common.json.PropertyAssignedAwareSupport;
import com.wordnik.swagger.annotations.ApiModelProperty;

import java.util.Date;

/**
 * The base class for all resource with system properties.
 */
public abstract class ResourceMeta implements CloudantEntity, PropertyAssignedAware {

    protected final PropertyAssignedAwareSupport support = new PropertyAssignedAwareSupport();

    @ApiModelProperty(position = 1000, required = true,
            value = "[Readonly] The revision of the resource. Used for optimistic locking.")
    @JsonProperty("rev")
    private String resourceAge;

    @ApiModelProperty(position = 1001, required = true,
            value = "[Readonly] The created datetime of the resource.")
    private Date createdTime;

    @ApiModelProperty(position = 1002, required = true,
            value = "[Readonly] The updated datetime of the resource.")
    private Date updatedTime;

    @JsonIgnore
    private String createdBy;

    @JsonIgnore
    private String updatedBy;

    @JsonIgnore
    private String createdByClient;

    @JsonIgnore
    private String updatedByClient;

    @JsonIgnore
    private String cloudantId;

    @JsonIgnore
    private String cloudantRev;

    public String getResourceAge() {
        return resourceAge;
    }

    public void setResourceAge(String resourceAge) {
        this.resourceAge = resourceAge;
        support.setPropertyAssigned("resourceAge");
        support.setPropertyAssigned("rev");
    }

    public Date getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(Date createdTime) {
        this.createdTime = createdTime;
        support.setPropertyAssigned("createdTime");
    }

    public Date getUpdatedTime() {
        return updatedTime;
    }

    public void setUpdatedTime(Date updatedTime) {
        this.updatedTime = updatedTime;
        support.setPropertyAssigned("updatedTime");
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
        support.setPropertyAssigned("createdBy");
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
        support.setPropertyAssigned("updatedBy");
    }

    public String getCreatedByClient() {
        return createdByClient;
    }

    public void setCreatedByClient(String createdByClient) {
        this.createdByClient = createdByClient;
        support.setPropertyAssigned("createdByClient");
    }

    public String getUpdatedByClient() {
        return updatedByClient;
    }

    public void setUpdatedByClient(String updatedByClient) {
        this.updatedByClient = updatedByClient;
        support.setPropertyAssigned("updatedByClient");
    }

    public String getCloudantId() {
        return cloudantId;
    }

    public void setCloudantId(String cloudantId) {
        this.cloudantId = cloudantId;
    }

    public String getCloudantRev() {
        return cloudantRev;
    }

    public void setCloudantRev(String cloudantRev) {
        this.cloudantRev = cloudantRev;
    }

    @Override
    public boolean isPropertyAssigned(String propertyName) {
        return support.isPropertyAssigned(propertyName);
    }
}