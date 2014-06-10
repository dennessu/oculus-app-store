/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.ewallet.db.entity;

import com.junbo.common.cloudant.CloudantEntity;
import com.junbo.common.cloudant.json.annotations.CloudantDeserialize;
import com.junbo.common.cloudant.json.annotations.CloudantProperty;
import com.junbo.common.cloudant.json.annotations.CloudantSerialize;
import com.junbo.ewallet.db.entity.def.DateDeserializer;
import com.junbo.ewallet.db.entity.def.DateSerializer;
import com.junbo.ewallet.db.entity.def.Shardable;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;
import java.util.Date;

/**
 * Base entity for transaction.
 */
@MappedSuperclass
public abstract class EntityWithCreated<K> implements Shardable, CloudantEntity<K> {
    @CloudantProperty("_id")
    private Long pId;
    private Long createdBy;
    @CloudantSerialize(DateSerializer.class)
    @CloudantDeserialize(DateDeserializer.class)
    private Date createdTime;
    @CloudantProperty("_rev")
    private String cloudantRev;
    private Integer rev;

    @Id
    @Column(name = "id")
    public Long getpId() {
        return pId;
    }

    public void setpId(Long pId) {
        this.pId = pId;
    }

    @Column(name = "created_by")
    public Long getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Long createdBy) {
        this.createdBy = createdBy;
    }

    @Column(name = "created_time")
    public Date getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(Date createdTime) {
        this.createdTime = createdTime;
    }

    @Transient
    public String getCloudantRev() {
        return cloudantRev;
    }

    public void setCloudantRev(String cloudantRev) {
        this.cloudantRev = cloudantRev;
    }

    @Transient
    public Integer getRev() {
        return rev;
    }

    public void setRev(Integer rev) {
        this.rev = rev;
    }

    @Transient
    public String getCloudantId() {
        return pId.toString();
    }

    public void setCloudantId(String id) {
        this.pId = Long.parseLong(id);
    }

    @Transient
    public Integer getResourceAge() {
        return 0;
    }

    public void setResourceAge(Integer resourceAge) {
    }

    @Transient
    public Long getUpdatedBy() {
        return null;
    }

    public void setUpdatedBy(Long updatedBy) {
    }

    @Transient
    public Date getUpdatedTime() {
        return null;
    }

    public void setUpdatedTime(Date updatedTime) {
    }
}
