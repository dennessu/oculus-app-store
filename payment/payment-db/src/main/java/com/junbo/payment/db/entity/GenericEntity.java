/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.payment.db.entity;

import com.junbo.common.model.EntityAdminInfoString;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;


/**
 * generic entity.
 */
@MappedSuperclass
public abstract class GenericEntity implements EntityAdminInfoString, Serializable {
    @Column(name = "created_time", updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdTime;

    @Column(name = "created_by", updatable = false)
    private String createdBy;

    @Column(name = "updated_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedTime;

    @Column(name = "updated_by")
    private String updatedBy;

    @Column(name = "resource_age")
    @Version
    private Integer resourceAge;

    @Override
    public Date getCreatedTime() {
        return createdTime;
    }

    @Override
    public void setCreatedTime(Date createdTime) {
        this.createdTime = createdTime;
    }

    @Override
    public String getCreatedBy() {
        return createdBy;
    }

    @Override
    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    @Override
    public Date getUpdatedTime() {
        return updatedTime;
    }

    @Override
    public void setUpdatedTime(Date updatedTime) {
        this.updatedTime = updatedTime;
    }

    @Override
    public String getUpdatedBy() {
        return updatedBy;
    }

    @Override
    public void setUpdatedBy(String updatedBy) {
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

    public Integer getResourceAge() {
        return resourceAge;
    }

    public void setResourceAge(Integer resourceAge) {
        this.resourceAge = resourceAge;
    }

    @Transient
    public abstract Long getId();

    public abstract void setId(Long id);

    @Transient
    public abstract Long getShardMasterId();
}
