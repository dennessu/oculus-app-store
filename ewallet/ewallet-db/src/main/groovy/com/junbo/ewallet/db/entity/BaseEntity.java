/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.ewallet.db.entity;

import com.junbo.common.model.EntityAdminInfoString;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * Base entity.
 */
@MappedSuperclass
public abstract class BaseEntity implements EntityAdminInfoString, Serializable {
    @Id
    @Column(name = "id")
    private Long id;

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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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
    public abstract Long getShardMasterId();
}
