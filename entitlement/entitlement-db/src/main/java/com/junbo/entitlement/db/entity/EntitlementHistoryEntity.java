/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.entitlement.db.entity;

import com.junbo.entitlement.db.entity.def.*;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

/**
 * EntitlementHistory Entity.
 */
@javax.persistence.Entity
@Table(name = "entitlement_history")
@TypeDefs(@TypeDef(name="json-string", typeClass=ListJsonUserType.class))
public class EntitlementHistoryEntity implements Shardable {
    public static final String CREATE_ACTION = "CREATE";
    public static final String UPDATE_ACTION = "UPDATE";

    private Long entitlementHistoryId;
    private String action;
    private Long entitlementId;
    private Integer rev;
    private Long userId;
    private List<String> inAppContext;
    private String group;
    private String tag;
    private EntitlementType type;
    private Long entitlementDefinitionId;
    private EntitlementStatus status;
    private String statusReason;
    private Date grantTime;
    private Date expirationTime;
    private Integer useCount;
    private Date createdTime;
    private String createdBy;
    private Date modifiedTime;
    private String modifiedBy;

    public EntitlementHistoryEntity() {
    }

    public EntitlementHistoryEntity(String action, EntitlementEntity entitlementEntity) {
        this.action = action;
        this.entitlementId = entitlementEntity.getEntitlementId();
        this.rev = entitlementEntity.getRev();
        this.entitlementDefinitionId = entitlementEntity.getEntitlementDefinitionId();
        this.inAppContext = entitlementEntity.getInAppContext();
        this.group = entitlementEntity.getGroup();
        this.tag = entitlementEntity.getTag();
        this.type = entitlementEntity.getType();
        this.userId = entitlementEntity.getUserId();
        this.status = entitlementEntity.getStatus();
        this.statusReason = entitlementEntity.getStatusReason();
        this.entitlementDefinitionId = entitlementEntity.getEntitlementDefinitionId();
        this.grantTime = entitlementEntity.getGrantTime();
        this.expirationTime = entitlementEntity.getExpirationTime();
        this.useCount = entitlementEntity.getUseCount();
        this.setCreatedBy(entitlementEntity.getCreatedBy());
        this.setCreatedTime(entitlementEntity.getCreatedTime());
        this.setModifiedBy(entitlementEntity.getModifiedBy());
        this.setModifiedTime(entitlementEntity.getModifiedTime());
    }

    @Id
    @Column(name = "entitlement_history_id")
    public Long getEntitlementHistoryId() {
        return entitlementHistoryId;
    }

    public void setEntitlementHistoryId(Long entitlementHistoryId) {
        this.entitlementHistoryId = entitlementHistoryId;
    }

    @Column(name = "action")
    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    @Column(name = "entitlement_id")
    public Long getEntitlementId() {
        return entitlementId;
    }

    public void setEntitlementId(Long entitlementId) {
        this.entitlementId = entitlementId;
    }

    @Column(name = "rev")
    public Integer getRev() {
        return rev;
    }

    public void setRev(Integer rev) {
        this.rev = rev;
    }

    @Column(name = "user_id")
    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    @Column(name = "entitlement_definition_id")
    public Long getEntitlementDefinitionId() {
        return entitlementDefinitionId;
    }

    public void setEntitlementDefinitionId(Long entitlementDefinitionId) {
        this.entitlementDefinitionId = entitlementDefinitionId;
    }

    @Column(name = "status")
    @Type(type = IdentifiableType.TYPE)
    public EntitlementStatus getStatus() {
        return status;
    }

    public void setStatus(EntitlementStatus status) {
        this.status = status;
    }

    @Column(name = "status_reason")
    public String getStatusReason() {
        return statusReason;
    }

    public void setStatusReason(String statusReason) {
        this.statusReason = statusReason;
    }

    @Column(name = "grant_time")
    public Date getGrantTime() {
        return grantTime;
    }

    public void setGrantTime(Date grantTime) {
        this.grantTime = grantTime;
    }

    @Column(name = "expiration_time")
    public Date getExpirationTime() {
        return expirationTime;
    }

    public void setExpirationTime(Date expirationTime) {
        this.expirationTime = expirationTime;
    }

    @Column(name = "use_count")
    public Integer getUseCount() {
        return useCount;
    }

    public void setUseCount(Integer useCount) {
        this.useCount = useCount;
    }

    @Column(name = "created_time")
    public Date getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(Date createdTime) {
        this.createdTime = createdTime;
    }

    @Column(name = "created_by")
    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    @Column(name = "modified_time")
    public Date getModifiedTime() {
        return modifiedTime;
    }

    public void setModifiedTime(Date modifiedTime) {
        this.modifiedTime = modifiedTime;
    }

    @Column(name = "modified_by")
    public String getModifiedBy() {
        return modifiedBy;
    }

    public void setModifiedBy(String modifiedBy) {
        this.modifiedBy = modifiedBy;
    }

    @Column(name = "in_app_context")
    @Type(type = "json-string")
    public List<String> getInAppContext() {
        return inAppContext;
    }

    public void setInAppContext(List<String> inAppContext) {
        this.inAppContext = inAppContext;
    }

    @Column(name = "entitlement_group")
    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    @Column(name = "tag")
    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    @Column(name = "type")
    @Type(type = IdentifiableType.TYPE)
    public EntitlementType getType() {
        return type;
    }

    public void setType(EntitlementType type) {
        this.type = type;
    }

    @Transient
    @Override
    public Long getShardMasterId() {
        return entitlementId;
    }
}
