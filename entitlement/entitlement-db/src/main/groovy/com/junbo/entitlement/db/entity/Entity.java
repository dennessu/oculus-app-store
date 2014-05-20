/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.entitlement.db.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.junbo.entitlement.db.entity.def.Shardable;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;
import java.util.Date;

/**
 * Base Entity.
 */
@MappedSuperclass
public abstract class Entity implements Shardable {
    @JsonIgnore
    private Long pId;
    private Boolean isDeleted;
    private Date createdTime;
    private Long createdBy;
    private Date updatedTime;
    private Long updatedBy;
    private Integer rev;
    private String cloudantRev;

    @Id
    @Column(name = "id")
    public Long getpId() {
        return pId;
    }

    public void setpId(Long pId) {
        this.pId = pId;
    }

    @Column(name = "is_deleted")
    public Boolean getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(Boolean isDeleted) {
        this.isDeleted = isDeleted;
    }

    @Column(name = "created_time")
    public Date getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(Date createdTime) {
        this.createdTime = createdTime;
    }

    @Column(name = "created_by")
    public Long getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Long createdBy) {
        this.createdBy = createdBy;
    }

    @Column(name = "modified_time")
    public Date getUpdatedTime() {
        return updatedTime;
    }

    public void setUpdatedTime(Date updatedTime) {
        this.updatedTime = updatedTime;
    }

    @Column(name = "modified_by")
    public Long getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(Long updatedBy) {
        this.updatedBy = updatedBy;
    }

    @Column(name = "rev")
    public Integer getRev() {
        return rev;
    }

    public void setRev(Integer rev) {
        this.rev = rev;
    }

    @Transient
    public String getCloudantRev() {
        return cloudantRev;
    }

    public void setCloudantRev(String cloudantRev) {
        this.cloudantRev = cloudantRev;
    }

    @Transient
    public Integer getResourceAge() {
        return rev;
    }

    public void setResourceAge(Integer resourceAge) {
        this.rev = resourceAge;
    }

    @Transient
    public String getCloudantId() {
        return pId.toString();
    }

    public void setCloudantId(String id) {
        this.pId = Long.parseLong(id);
    }
}
