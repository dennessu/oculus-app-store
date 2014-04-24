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
import com.junbo.common.jackson.annotation.UserId;
import com.wordnik.swagger.annotations.ApiModelProperty;

import java.util.Date;
import java.util.UUID;

/**
 * Entitlement Model.
 */
@JsonPropertyOrder(value = {"entitlementId", "rev", "userId", "isActive", "isBanned",
        "entitlementDefinitionId", "grantTime", "expirationTime", "useCount"})
public class Entitlement {
    @ApiModelProperty(position = 1, required = true, value = "[Client Immutable] entitlement id")
    @JsonProperty("self")
    @EntitlementId
    private Long entitlementId;
    @ApiModelProperty(position = 2, required = true, value = "[Client Immutable]")
    private String rev;
    @ApiModelProperty(position = 3, required = true, value = "the user this entitlement belongs to")
    @UserId
    @JsonProperty("user")
    private Long userId;
    @ApiModelProperty(position = 4, required = true, value = "[Client Immutable]")
    private Boolean isActive;
    @ApiModelProperty(position = 5, required = true, value = "[Client Immutable]")
    @JsonProperty("isSuspended")
    private Boolean isBanned;
    @ApiModelProperty(position = 6, required = true, value = "grant time")
    private Date grantTime;
    @ApiModelProperty(position = 7, required = true, value = "null represents for never expires")
    private Date expirationTime;
    @ApiModelProperty(position = 8, required = true,
            value = "must be a non-negative number" +
                    " if the entitlementDefinition's isConsumable is true," +
                    " otherwise must be null")
    private Integer useCount;
    @ApiModelProperty(position = 9, required = true, value = "the entitlementDefinition associated with entitlement")
    @EntitlementDefinitionId
    @JsonProperty("entitlementDefinition")
    private Long entitlementDefinitionId;

    @JsonIgnore
    private UUID trackingUuid;
    @JsonIgnore
    private String type;

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

    public String getRev() {
        return rev;
    }

    public void setRev(String rev) {
        this.rev = rev;
    }

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
}
