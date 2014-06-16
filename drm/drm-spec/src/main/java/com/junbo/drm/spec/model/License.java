/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.drm.spec.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.junbo.common.jackson.annotation.DeviceId;
import com.junbo.common.jackson.annotation.LicenseId;
import com.junbo.common.jackson.annotation.UserId;
import com.junbo.common.model.ResourceMeta;
import com.wordnik.swagger.annotations.ApiModelProperty;

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

    @ApiModelProperty(position = 4, required = true, value = "The application id.")
    private String applicationId;

    @ApiModelProperty(position = 5, required = true, value = "The machine hash.")
    private String machineHash;

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

    public String getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(String applicationId) {
        this.applicationId = applicationId;
    }

    public String getMachineHash() {
        return machineHash;
    }

    public void setMachineHash(String machineHash) {
        this.machineHash = machineHash;
    }

}
