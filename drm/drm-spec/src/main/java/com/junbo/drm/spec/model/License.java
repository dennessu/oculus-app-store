/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.drm.spec.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.junbo.common.jackson.annotation.DeviceId;
import com.junbo.common.jackson.annotation.ItemId;
import com.junbo.common.jackson.annotation.LicenseId;
import com.junbo.common.jackson.annotation.UserId;
import com.junbo.common.model.ResourceMeta;
import com.wordnik.swagger.annotations.ApiModelProperty;

import java.util.Date;

/**
 * drm.
 */
public class License extends ResourceMeta<String> {
    @ApiModelProperty(position = 1, required = false, value = "[Client Immutable] The id of license resource.")
    @JsonProperty("self")
    @LicenseId
    private String id;

    @ApiModelProperty(position = 2, required = true, value = "[Client Immutable] The id of user resource.")
    @UserId
    private Long userId;

    @ApiModelProperty(position = 3, required = true, value = "[Client Immutable] The id of device resource.")
    @DeviceId
    private String deviceId;

    @ApiModelProperty(position = 4, required = true, value = "[Client Immutable] The id of item resource.")
    @ItemId
    private String itemId;

    @ApiModelProperty(position = 5, required = true, value = "The license’s machine hash, calculated based on the machine the user using.")
    private String machineHash;

    @ApiModelProperty(position = 6, required = true, value = "The expiration time of the license.")
    private Date expirationTime;

    @ApiModelProperty(position = 7, required = true, value = "The use countThe use count of the license, suggesting how many times the user can use this license.")
    private Integer useCount;

    @ApiModelProperty(position = 8, required = true, value = "The revision.")
    private String rev;

    public String getId() {
        return id;
    }

    public void setId(String licenseId) {
        this.id = licenseId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getMachineHash() {
        return machineHash;
    }

    public void setMachineHash(String machineHash) {
        this.machineHash = machineHash;
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
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

    public String getRev() {
        return rev;
    }

    public void setRev(String rev) {
        this.rev = rev;
    }


}
