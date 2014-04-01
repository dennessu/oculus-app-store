/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.data.entity.common;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by liangfu on 3/14/14.
 */
@MappedSuperclass
public abstract class ResourceMetaEntity {
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

    @Column(name = "version")
    @Version
    private Integer resourceAge;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_time")
    private Date createdTime;
    @Column(name = "created_by")
    private String createdBy;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "modified_time")
    private Date updatedTime;
    @Column(name = "modified_by")
    private String updatedBy;
}
