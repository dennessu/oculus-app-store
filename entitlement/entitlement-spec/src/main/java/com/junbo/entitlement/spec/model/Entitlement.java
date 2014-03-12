/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.entitlement.spec.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.junbo.common.jackson.annotation.EntitlementDefinitionId;
import com.junbo.common.jackson.annotation.EntitlementId;
import com.junbo.common.jackson.annotation.OfferId;
import com.junbo.common.jackson.annotation.UserId;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import java.util.Date;
import java.util.UUID;

/**
 * Entitlement Model.
 */
@JsonPropertyOrder(value = {"entitlementId", "userId", "developerId", "offerId", "status", "statusReason",
        "type", "group", "tag", "entitlementDefinitionId", "grantTime", "expirationTime",
        "consumable", "useCount", "managedLifecycle"})
public class Entitlement{
    private UUID trackingUuid;
    @Null
    @JsonProperty("self")
    @EntitlementId
    private Long entitlementId;
    @NotNull
    @UserId
    private Long userId;
    @UserId
    private Long developerId;
    private String type;
    private String status;
    private String statusReason;
    @NotNull
    @EntitlementDefinitionId
    private Long entitlementDefinitionId;
    private Date grantTime;
    private Date expirationTime;
    private Long period;
    @OfferId
    private Long offerId;
    private String group;
    private String tag;
    private Boolean consumable;
    private Integer useCount;
    private Boolean managedLifecycle;

    @JsonIgnore
    public UUID getTrackingUuid() {
        return trackingUuid;
    }

    @JsonProperty
    public void setTrackingUuid(UUID trackingUuid) {
        this.trackingUuid = trackingUuid;
    }

    public Long getEntitlementId() {
        return entitlementId;
    }

    public void setEntitlementId(Long entitlementId) {
        this.entitlementId = entitlementId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatusReason() {
        return statusReason;
    }

    public void setStatusReason(String statusReason) {
        this.statusReason = statusReason;
    }

    public Long getEntitlementDefinitionId() {
        return entitlementDefinitionId;
    }

    public void setEntitlementDefinitionId(Long entitlementDefinitionId) {
        this.entitlementDefinitionId = entitlementDefinitionId;
    }

    public Date getGrantTime() {
        return grantTime;
    }

    public void setGrantTime(Date grantTime) {
        this.grantTime = grantTime;
    }

    public Date getExpirationTime() {
        return expirationTime;
    }

    public void setExpirationTime(Date expirationTime) {
        this.expirationTime = expirationTime;
    }

    public Long getOfferId() {
        return offerId;
    }

    public void setOfferId(Long offerId) {
        this.offerId = offerId;
    }

    public Boolean getConsumable() {
        return consumable;
    }

    public void setConsumable(Boolean consumable) {
        this.consumable = consumable;
    }

    public Integer getUseCount() {
        return useCount;
    }

    public void setUseCount(Integer useCount) {
        this.useCount = useCount;
    }

    public Boolean getManagedLifecycle() {
        return managedLifecycle;
    }

    public void setManagedLifecycle(Boolean managedLifecycle) {
        this.managedLifecycle = managedLifecycle;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public Long getDeveloperId() {
        return developerId;
    }

    public void setDeveloperId(Long developerId) {
        this.developerId = developerId;
    }

    @JsonProperty
    public void setPeriod(Long period) {
        this.period = period;
    }

    @JsonIgnore
    public Long getPeriod() {
        return period;
    }
}
