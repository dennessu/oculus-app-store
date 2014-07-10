/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.entitlement.spec.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.junbo.common.jackson.annotation.EntitlementId;
import com.junbo.common.jackson.annotation.ItemId;
import com.junbo.common.jackson.annotation.UserId;
import com.junbo.common.model.ResourceMeta;
import com.wordnik.swagger.annotations.ApiModelProperty;

import java.util.Date;
import java.util.Map;
import java.util.UUID;

/**
 * Entitlement Model.
 */
@JsonPropertyOrder(value = {"entitlementId", "userId", "itemId", "binaries", "isActive", "isBanned",
        "grantTime", "expirationTime", "useCount", "type", "futureExpansion",
        "rev", "createdTime", "updatedTime", "adminInfo"})
public class Entitlement extends ResourceMeta<String> {

    @ApiModelProperty(position = 1, required = true, value = "[Client Immutable] Link to this entitlement resource")
    @JsonProperty("self")
    @EntitlementId
    private String id;

    @ApiModelProperty(position = 2, required = true, value = "Link to the User that is granted access by this entitlement")
    @UserId
    @JsonProperty("user")
    private Long userId;

    @ApiModelProperty(position = 5, required = true, value = "[Client Immutable] True if/only if the entitlement is active;" +
            " false when the entitlement is out of date, useCount is 0, or someone manually set isSuspended to true")
    private Boolean isActive;

    @ApiModelProperty(position = 6, required = true, value = "True if/only if this entitlement is suspended, e.g., by CSR or other authorized agent")
    @JsonProperty("isSuspended")
    private Boolean isBanned;

    @ApiModelProperty(position = 7, required = true, value = "the timestamp when this entitlement was granted; must be ISO 8601")
    private Date grantTime;

    @ApiModelProperty(position = 8, required = true, value = "the timestamp when this entitlement expires (must be ISO 8601), or null to mean it never expires")
    private Date expirationTime;

    @ApiModelProperty(position = 9, required = true,
            value = "a non-negative number if this is a consumable entitlement; otherwise null")
    private Integer useCount;

    @ApiModelProperty(position = 3, required = true, value = "Link to the Item that is granted to the user by this entitlement")
    @ItemId
    @JsonProperty("item")
    private String itemId;
    @ApiModelProperty(position = 4, required = true, value = "[Client Immutable] A mapping between platform and download url string.")
    private Map<String, String> binaries;

    @ApiModelProperty(position = 10, required = true, value = "enumeration; values are \"DOWNLOAD\" and \"RUN\"")
    private String type;

    @JsonIgnore
    private UUID trackingUuid;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
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

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public Map<String, String> getBinaries() {
        return binaries;
    }

    public void setBinaries(Map<String, String> binaries) {
        this.binaries = binaries;
    }
}
