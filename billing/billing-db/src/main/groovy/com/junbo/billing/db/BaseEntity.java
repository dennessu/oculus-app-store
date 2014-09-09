/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.billing.db;

import com.junbo.common.model.EntityAdminInfoString;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;
import javax.persistence.Version;
import java.io.Serializable;
import java.util.Date;

/**
 * Created by xmchen on 14-1-17.
 */
@MappedSuperclass
public class BaseEntity implements EntityAdminInfoString, Serializable {
    @Column(name = "created_date", updatable = false)
    protected Date createdTime;

    @Column(name = "created_by", updatable = false)
    protected String createdBy;

    @Column(name = "updated_date")
    protected Date updatedTime;

    @Column(name = "updated_by")
    protected String updatedBy;

    @Column(name = "resource_age")
    @Version
    private Integer resourceAge;

    public Date getCreatedTime() {
        return createdTime;
    }
    public void setCreatedTime(Date createdTime) {
        this.createdTime = createdTime;
    }

    public String getCreatedBy() {
        return createdBy;
    }
    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public Date getUpdatedTime() {
        return updatedTime;
    }

    public void setUpdatedTime(Date updatedTime) {
        this.updatedTime = updatedTime;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    public Integer getResourceAge() {
        return resourceAge;
    }

    public void setResourceAge(Integer resourceAge) {
        this.resourceAge = resourceAge;
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
}
