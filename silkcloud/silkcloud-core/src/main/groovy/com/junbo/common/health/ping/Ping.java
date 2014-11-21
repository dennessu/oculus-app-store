/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.common.health.ping;

import com.junbo.common.cloudant.CloudantEntity;
import com.junbo.common.cloudant.json.annotations.CloudantProperty;
import com.junbo.common.model.EntityAdminInfo;

import javax.persistence.*;
import java.util.Date;

/**
 * Ping model.
 */
@Entity
@Table(name = "ping")
public class Ping implements CloudantEntity<String>, EntityAdminInfo {
    @Id
    @Column(name = "id")
    @CloudantProperty("_id")
    private String id;

    @Column(name = "message")
    private String message;

    @Column(name = "version")
    @Version
    private Integer resourceAge;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_time")
    private Date createdTime;

    @Column(name = "created_by")
    private Long createdBy;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "modified_time")
    private Date updatedTime;

    @Column(name = "modified_by")
    private Long updatedBy;

    @Transient
    @CloudantProperty("_rev")
    private String cloudantRev;

    public String getCloudantId() {
        return this.id;
    }

    public void setCloudantId(String cloudantId) {
        this.id = cloudantId;
    }

    public String getCloudantRev() {
        return cloudantRev;
    }

    public void setCloudantRev(String cloudantRev) {
        this.cloudantRev = cloudantRev;
    }

    @Override
    public Boolean isDeleted() {
        return false;
    }

    @Override
    public void setDeleted(Boolean deleted) {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

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

    public Long getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Long createdBy) {
        this.createdBy = createdBy;
    }

    public Date getUpdatedTime() {
        return updatedTime;
    }

    public void setUpdatedTime(Date updatedTime) {
        this.updatedTime = updatedTime;
    }

    public Long getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(Long updatedBy) {
        this.updatedBy = updatedBy;
    }

    @Override
    public String getCreatedByClient() {
        return null;
    }

    @Override
    public void setCreatedByClient(String client) {
    }

    @Override
    public String getUpdatedByClient() {
        return null;
    }

    @Override
    public void setUpdatedByClient(String client) {
    }
}
