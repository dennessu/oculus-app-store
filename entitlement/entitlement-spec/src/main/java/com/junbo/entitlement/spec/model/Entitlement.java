/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.entitlement.spec.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.junbo.common.jackson.annotation.EntitlementDefinitionId;
import com.junbo.common.jackson.annotation.EntitlementId;
import com.junbo.common.jackson.annotation.UserId;

import java.util.Date;
import java.util.UUID;

/**
 * Entitlement Model.
 */
@JsonPropertyOrder(value = {"entitlementId", "rev", "userId", "isActive", "isBanned",
        "entitlementDefinitionId", "grantTime", "expirationTime", "useCount"})
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Entitlement{
    @JsonIgnore
    private UUID trackingUuid;
    @JsonIgnore
    private String type;
    @JsonProperty("self")
    @EntitlementId
    private Long entitlementId;
    private Integer rev;
    @UserId
    @JsonProperty("user")
    private Long userId;
    private Boolean isActive;
    @JsonProperty("isSuspended")
    private Boolean isBanned;
    @EntitlementDefinitionId
    @JsonProperty("entitlementDefinition")
    private Long entitlementDefinitionId;
    private Date grantTime;
    private Date expirationTime;
    private Integer useCount;

    public UUID getTrackingUuid() {
        return trackingUuid;
    }

    public void setTrackingUuid(UUID trackingUuid) {
        this.trackingUuid = trackingUuid;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Long getEntitlementId() {
        return entitlementId;
    }

    public void setEntitlementId(Long entitlementId) {
        this.entitlementId = entitlementId;
    }

    public Integer getRev() {
        return rev;
    }

    public void setRev(Integer rev) {
        this.rev = rev;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
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

    public Integer getUseCount() {
        return useCount;
    }

    public void setUseCount(Integer useCount) {
        this.useCount = useCount;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public Boolean getIsBanned() {
        return isBanned;
    }

    public void setIsBanned(Boolean isBanned) {
        this.isBanned = isBanned;
    }
}
