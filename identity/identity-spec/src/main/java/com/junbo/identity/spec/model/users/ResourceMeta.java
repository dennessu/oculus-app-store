/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.spec.model.users;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.Date;

/**
 * Resource meta.
 * Per resource will have those attributes.
 */
public abstract class ResourceMeta {
    @JsonIgnore
    private Integer resourceAge;

    private Date createdTime;

    private Date updatedTime;

    @JsonIgnore
    private String createdBy;

    @JsonIgnore
    private String updatedBy;

    public Integer getResourceAge() {
        return resourceAge;
    }

    public void setResourceAge(Integer resourceAge) {
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
