/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.sharding.dualwrite.data
import com.junbo.common.cloudant.CloudantEntity
import com.junbo.common.cloudant.json.annotations.CloudantProperty
import com.junbo.common.hibernate.StringJsonUserType
import groovy.transform.CompileStatic
import org.hibernate.annotations.Type
import org.hibernate.annotations.TypeDef

import javax.persistence.*
/**
 * The pending action.
 */
@Entity
@Table(name = "pending_action")
@TypeDef(name = "json-string", typeClass = StringJsonUserType.class)
@CompileStatic
public class PendingActionEntity implements CloudantEntity<Long> {

    @Id
    @Column(name = "id")
    @CloudantProperty("_id")
    private Long id;

    @Column(name = "saved_entity_type")
    private String savedEntityType;

    @Column(name = "saved_entity")
    @Type(type = "json-string")
    private String savedEntity;

    @Column(name = "deleted_key")
    private Long deletedKey;

    @Column(name = "changed_entity_id")
    private Long changedEntityId;

    @Column(name = "retry_count")
    private Integer retryCount;

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

    @Column(name = "deleted")
    private boolean deleted;

    @Transient
    @CloudantProperty("_rev")
    private String cloudantRev;

    public String getCloudantId() {
        return this.id.toString();
    }

    public void setCloudantId(String cloudantId) {
        this.id = Long.parseLong(cloudantId);
    }

    public String getCloudantRev() {
        return cloudantRev;
    }

    public void setCloudantRev(String cloudantRev) {
        this.cloudantRev = cloudantRev;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSavedEntityType() {
        return savedEntityType;
    }

    public void setSavedEntityType(String savedEntityType) {
        this.savedEntityType = savedEntityType;
    }

    public String getSavedEntity() {
        return savedEntity;
    }

    public void setSavedEntity(String savedEntity) {
        this.savedEntity = savedEntity;
    }

    public Long getDeletedKey() {
        return deletedKey;
    }

    public void setDeletedKey(Long deletedKey) {
        this.deletedKey = deletedKey;
    }

    public Long getChangedEntityId() {
        return changedEntityId;
    }

    public void setChangedEntityId(Long changedEntityId) {
        this.changedEntityId = changedEntityId;
    }

    public Integer getResourceAge() {
        return resourceAge;
    }

    public void setResourceAge(Integer resourceAge) {
        this.resourceAge = resourceAge;
    }

    Integer getRetryCount() {
        return retryCount
    }

    void setRetryCount(Integer retryCount) {
        this.retryCount = retryCount
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

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    @Override
    String getCreatedByClient() {
        return null
    }

    @Override
    void setCreatedByClient(String client) {
    }

    @Override
    String getUpdatedByClient() {
        return null
    }

    @Override
    void setUpdatedByClient(String client) {
    }
}
