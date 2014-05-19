/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.entitlement.db.entity;

import com.fasterxml.jackson.databind.JsonNode;
import com.junbo.entitlement.db.entity.def.MapJsonUserType;
import com.junbo.entitlement.db.entity.def.Shardable;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.Date;
import java.util.Map;

/**
 * EntitlementHistory Entity.
 */
@javax.persistence.Entity
@Table(name = "entitlement_history")
@TypeDefs(@TypeDef(name = "json-map", typeClass = MapJsonUserType.class))
public class EntitlementHistoryEntity implements Shardable {
    private Long entitlementHistoryId;
    private String action;
    private Long entitlementId;
    private Integer rev;
    private Long userId;
    private Long itemId;
    private Boolean isBanned;
    private Date grantTime;
    private Date expirationTime;
    private Integer useCount;
    private Boolean isDeleted;
    private Date createdTime;
    private String createdBy;
    private Date modifiedTime;
    private String modifiedBy;
    private Map<String, JsonNode> futureExpansion;

    public EntitlementHistoryEntity() {
    }

    public EntitlementHistoryEntity(String action, EntitlementEntity entitlementEntity) {
        this.action = action;
        this.entitlementId = entitlementEntity.getEntitlementId();
        this.rev = entitlementEntity.getRev();
        this.itemId = entitlementEntity.getItemId();
        this.userId = entitlementEntity.getUserId();
        this.isBanned = entitlementEntity.getIsBanned();
        this.futureExpansion = entitlementEntity.getFutureExpansion();
        this.grantTime = entitlementEntity.getGrantTime();
        this.expirationTime = entitlementEntity.getExpirationTime();
        this.useCount = entitlementEntity.getUseCount();
        this.isDeleted = entitlementEntity.getIsDeleted();
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

    @Column(name = "item_id")
    public Long getItemId() {
        return itemId;
    }

    public void setItemId(Long itemId) {
        this.itemId = itemId;
    }

    @Column(name = "future_expansion")
    @Type(type = "json-map")
    public Map<String, JsonNode> getFutureExpansion() {
        return futureExpansion;
    }

    public void setFutureExpansion(Map<String, JsonNode> futureExpansion) {
        this.futureExpansion = futureExpansion;
    }

    @Column(name = "is_banned")
    public Boolean getIsBanned() {
        return isBanned;
    }

    public void setIsBanned(Boolean isBanned) {
        this.isBanned = isBanned;
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

    @Column(name = "is_deleted")
    public Boolean getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(Boolean isDeleted) {
        this.isDeleted = isDeleted;
    }

    @Transient
    @Override
    public Long getShardMasterId() {
        return entitlementId;
    }
}
