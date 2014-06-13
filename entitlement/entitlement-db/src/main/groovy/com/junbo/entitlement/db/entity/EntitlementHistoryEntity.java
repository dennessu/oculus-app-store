/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.entitlement.db.entity;

import com.fasterxml.jackson.databind.JsonNode;
import com.junbo.common.cloudant.CloudantEntity;
import com.junbo.common.id.EntitlementId;
import com.junbo.entitlement.db.entity.def.MapJsonUserType;
import com.junbo.entitlement.db.entity.def.Shardable;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;

import javax.persistence.Column;
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
public class EntitlementHistoryEntity extends Entity implements Shardable, CloudantEntity<EntitlementId> {
    private String action;
    private Long entitlementId;
    private Long userId;
    private String itemId;
    private Boolean isBanned;
    private Date grantTime;
    private Date expirationTime;
    private Integer useCount;
    private Map<String, JsonNode> futureExpansion;

    public EntitlementHistoryEntity() {
    }

    public EntitlementHistoryEntity(String action, EntitlementEntity entitlementEntity) {
        this.action = action;
        this.entitlementId = entitlementEntity.getpId();
        this.setResourceAge(entitlementEntity.getResourceAge());
        this.itemId = entitlementEntity.getItemId();
        this.userId = entitlementEntity.getUserId();
        this.isBanned = entitlementEntity.getIsBanned();
        this.futureExpansion = entitlementEntity.getFutureExpansion();
        this.grantTime = entitlementEntity.getGrantTime();
        this.expirationTime = entitlementEntity.getExpirationTime();
        this.useCount = entitlementEntity.getUseCount();
        this.setIsDeleted(entitlementEntity.getIsDeleted());
        this.setCreatedBy(entitlementEntity.getCreatedBy());
        this.setCreatedTime(entitlementEntity.getCreatedTime());
        this.setUpdatedTime(entitlementEntity.getUpdatedTime());
        this.setUpdatedBy(entitlementEntity.getUpdatedBy());
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

    @Column(name = "item_id")
    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
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

    @Transient
    @Override
    public Long getShardMasterId() {
        return entitlementId;
    }

    @Override
    @Transient
    public EntitlementId getId() {
        return new EntitlementId(this.getpId());
    }

    @Override
    @Transient
    public void setId(EntitlementId id) {
        this.setpId(id.getValue());
    }
}
