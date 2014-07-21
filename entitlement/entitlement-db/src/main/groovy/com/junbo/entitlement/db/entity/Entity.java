/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.entitlement.db.entity;

import com.junbo.common.cloudant.CloudantEntity;
import com.junbo.common.cloudant.json.annotations.CloudantDeserialize;
import com.junbo.common.cloudant.json.annotations.CloudantProperty;
import com.junbo.common.cloudant.json.annotations.CloudantSerialize;
import com.junbo.entitlement.db.entity.def.DateDeserializer;
import com.junbo.entitlement.db.entity.def.DateSerializer;
import com.junbo.entitlement.db.entity.def.Shardable;

import javax.persistence.*;
import java.util.Date;

/**
 * Base Entity.
 */
@MappedSuperclass
public abstract class Entity implements CloudantEntity<String>, Shardable {

    @CloudantProperty("_id")
    private String id;

    private Boolean isDeleted;

    @CloudantSerialize(DateSerializer.class)
    @CloudantDeserialize(DateDeserializer.class)
    private Date createdTime;

    private Long createdBy;

    @CloudantSerialize(DateSerializer.class)
    @CloudantDeserialize(DateDeserializer.class)
    private Date updatedTime;

    private Long updatedBy;

    @CloudantProperty("_rev")
    private String rev;
    
    private String createdByClient;

    private String updatedByClient;

    @Id
    @Column(name = "id")
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    @Column(name = "created_by_client")
    public String getCreatedByClient() {
        return this.createdByClient;
    }

    public void setCreatedByClient(String client) {
        this.createdByClient = client;
    }

    @Column(name = "updated_by_client")
    public String getUpdatedByClient() {
        return this.updatedByClient;
    }

    @Override
    public void setUpdatedByClient(String client) {
        this.updatedByClient = client;
    }

    @Version
    @Column(name = "rev")
    public Integer getResourceAge() {
        return Integer.parseInt(rev);
    }

    public void setResourceAge(Integer rev) {
        if (rev != null) {
            this.rev = rev.toString();
        } else {
            this.rev = null;
        }
    }

    @Transient
    public String getRev() {
        return rev;
    }

    public void setRev(String rev) {
        this.rev = rev;
    }

    @Transient
    public String getCloudantId() {
        return id;
    }

    public void setCloudantId(String id) {
        this.id = id;
    }

    @Transient
    public String getCloudantRev() {
        return getRev();
    }

    @Transient
    public void setCloudantRev(String rev) {
        setRev(rev);
    }

    @Transient
    @Override
    public Long getShardMasterId() {
        return Long.parseLong(getId());
    }
}
