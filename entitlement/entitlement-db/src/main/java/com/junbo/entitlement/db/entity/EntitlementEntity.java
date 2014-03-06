/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.entitlement.db.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.junbo.entitlement.db.entity.def.EntitlementStatus;
import com.junbo.entitlement.db.entity.def.EntitlementType;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.Date;

/**
 * Entitlement Entity.
 */
@javax.persistence.Entity
@Table(name = "entitlement")
public class EntitlementEntity extends Entity {
    private Long entitlementId;
    private Long userId;
    private Long developerId;
    private EntitlementStatus status;
    private String statusReason;
    private Long entitlementDefinitionId;
    private EntitlementType type;
    private String group;
    private String tag;
    private Date grantTime;
    private Date expirationTime;
    private Long offerId;
    private Boolean consumable;
    private Integer useCount;
    private Boolean managedLifecycle;

    @Id
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

    @Column(name = "managed_lifecycle")
    public Boolean getManagedLifecycle() {
        return managedLifecycle;
    }

    public void setManagedLifecycle(Boolean managedLifecycle) {
        this.managedLifecycle = managedLifecycle;
    }

    @Column(name = "status")
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

    @Column(name = "entitlement_definition_id")
    public Long getEntitlementDefinitionId() {
        return entitlementDefinitionId;
    }

    public void setEntitlementDefinitionId(Long entitlementDefinitionId) {
        this.entitlementDefinitionId = entitlementDefinitionId;
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

    @Column(name = "developer_id")
    public Long getDeveloperId() {
        return developerId;
    }

    public void setDeveloperId(Long developerId) {
        this.developerId = developerId;
    }

    @Column(name = "type")
    public EntitlementType getType() {
        return type;
    }

    public void setType(EntitlementType type) {
        this.type = type;
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

    @JsonIgnore
    @Transient
    @Override
    public Long getId() {
        return this.entitlementId;
    }

    @Override
    public void setId(Long id) {
        this.entitlementId = id;
    }

    @JsonIgnore
    @Transient
    @Override
    public Long getShardMasterId() {
        return userId;
    }
}
