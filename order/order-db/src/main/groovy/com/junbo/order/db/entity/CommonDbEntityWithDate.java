/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.order.db.entity;

import com.junbo.common.model.EntityAdminInfo;
import com.junbo.order.db.ValidationMessages;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

/**
 * Created by chriszhu on 1/24/14.
 */

@MappedSuperclass
public abstract class CommonDbEntityWithDate implements EntityAdminInfo, Serializable, Shardable {
    protected Date createdTime;
    protected Long createdBy;
    protected Date updatedTime;
    protected Long updatedBy;
    protected Integer resourceAge;

    @Column(name = "CREATED_TIME")
    @NotNull(message = ValidationMessages.MISSING_VALUE)
    public Date getCreatedTime() {
        return createdTime;
    }
    public void setCreatedTime(Date createdTime) {
        this.createdTime = createdTime;
    }

    @Column(name = "CREATED_BY")
    @NotNull(message = ValidationMessages.MISSING_VALUE)
    public Long getCreatedBy() {
        return createdBy;
    }
    public void setCreatedBy(Long createdBy) {
        this.createdBy = createdBy;
    }

    @Column(name = "UPDATED_TIME")
    @NotNull(message = ValidationMessages.MISSING_VALUE)
    public Date getUpdatedTime() {
        return updatedTime;
    }
    public void setUpdatedTime(Date updatedTime) {
        this.updatedTime = updatedTime;
    }

    @Column(name = "UPDATED_BY")
    public Long getUpdatedBy() {
        return updatedBy;
    }
    public void setUpdatedBy(Long updatedBy) {
        this.updatedBy = updatedBy;
    }

    @Override
    @Transient
    public String getCreatedByClient() {
        return null;
    }

    @Override
    @Transient
    public void setCreatedByClient(String createdByClient) {

    }

    @Override
    @Transient
    public String getUpdatedByClient() {
        return null;
    }

    @Override
    @Transient
    public void setUpdatedByClient(String updatedByClient) {

    }

    @Column(name = "RESOURCE_AGE")
    @Version
    public Integer getResourceAge() {
        return resourceAge;
    }

    public void setResourceAge(Integer resourceAge) {
        this.resourceAge = resourceAge;
    }

}
