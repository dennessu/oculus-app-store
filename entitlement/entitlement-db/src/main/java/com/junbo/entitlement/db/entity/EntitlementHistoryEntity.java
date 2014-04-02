/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.entitlement.db.entity;

import com.junbo.entitlement.db.entity.def.EntitlementStatus;
import com.junbo.entitlement.db.entity.def.EntitlementType;
import com.junbo.entitlement.db.entity.def.IdentifiableType;
import com.junbo.entitlement.db.entity.def.Shardable;
import org.hibernate.annotations.Type;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.Date;

/**
 * EntitlementHistory Entity.
 */
@javax.persistence.Entity
@Table(name = "entitlement_history")
public class EntitlementHistoryEntity implements Shardable {
    public static final String CREATE_ACTION = "CREATE";
    public static final String UPDATE_ACTION = "UPDATE";

    private Long entitlementHistoryId;
    private String action;
    private Long entitlementId;
    private Long userId;
    private String ownerId;
    private String group;
    private String tag;
    private EntitlementType type;
    private Long entitlementDefinitionId;
    private EntitlementStatus status;
    private String statusReason;
    private Date grantTime;
    private Date expirationTime;
    private Long offerId;
    private Boolean consumable;
    private Integer useCount;
    private Boolean managedLifecycle;
    private Date createdTime;
    private String createdBy;
    private Date modifiedTime;
    private String modifiedBy;

    public EntitlementHistoryEntity() {
    }

    public EntitlementHistoryEntity(String action, EntitlementEntity entitlementEntity) {
        this.action = action;
        this.entitlementId = entitlementEntity.getEntitlementId();
        this.entitlementDefinitionId = entitlementEntity.getEntitlementDefinitionId();
        this.ownerId = entitlementEntity.getOwnerId();
        this.group = entitlementEntity.getGroup();
        this.tag = entitlementEntity.getTag();
        this.type = entitlementEntity.getType();
        this.userId = entitlementEntity.getUserId();
        this.status = entitlementEntity.getStatus();
        this.statusReason = entitlementEntity.getStatusReason();
        this.entitlementDefinitionId = entitlementEntity.getEntitlementDefinitionId();
        this.grantTime = entitlementEntity.getGrantTime();
        this.expirationTime = entitlementEntity.getExpirationTime();
        this.offerId = entitlementEntity.getOfferId();
        this.consumable = entitlementEntity.getConsumable();
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

    @Column(name = "offer_id")
    public Long getOfferId() {
        return offerId;
    }

    public void setOfferId(Long itemId) {
        this.offerId = itemId;
    }

    @Column(name = "consumable")
    public Boolean getConsumable() {
        return consumable;
    }

    public void setConsumable(Boolean consumable) {
        this.consumable = consumable;
    }

    @Column(name = "use_count")
    public Integer getUseCount() {
        return useCount;
    }

    public void setUseCount(Integer useCount) {
        this.useCount = useCount;
    }

    @Column(name = "managed_lifecycle")
    public Boolean getManagedLifecycle() {
        return managedLifecycle;
    }

    public void setManagedLifecycle(Boolean managedLifecycle) {
        this.managedLifecycle = managedLifecycle;
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

    @Column(name = "owner_id")
    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
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
