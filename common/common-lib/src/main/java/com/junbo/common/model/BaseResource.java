/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.common.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.wordnik.swagger.annotations.ApiModelProperty;

import java.util.Date;

/**
 * The base class for all resource with system properties.
 */
public abstract class BaseResource {
    @ApiModelProperty(position = 1000, required = true,
            value = "[Readonly] The revision of the resource. Used for optimistic locking.")
    @JsonProperty("rev")
    private Long resourceAge;

    @ApiModelProperty(position = 1001, required = true,
            value = "[Readonly] The created datetime of the resource.")
    private Date createdTime;

    @ApiModelProperty(position = 1002, required = true,
            value = "[Readonly] The updated datetime of the resource.")
    private Date updatedTime;

    /**
     * The user and OAuth client who created the resource. Stored in format [userId]:[clientId].
     */
    @JsonIgnore
    private String createdBy;

    /**
     * The user and OAuth client who updated the resource. Stored in format [userId]:[clientId].
     */
    @JsonIgnore
    private String updatedBy;

    public Long getResourceAge() {
        return resourceAge;
    }

    public void setResourceAge(Long resourceAge) {
        this.resourceAge = resourceAge;
    }

    public Date getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(Date createdTime) {
        this.createdTime = createdTime;
    }

    public Date getUpdatedTime() {
        return updatedTime;
    }

    public void setUpdatedTime(Date updatedTime) {
        this.updatedTime = updatedTime;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }
}
