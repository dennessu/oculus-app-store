/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.spec.v1.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.junbo.common.id.DeviceId;
import com.junbo.common.id.UserDeviceId;
import com.junbo.common.id.UserId;
import com.junbo.common.util.Identifiable;
import com.junbo.identity.spec.model.users.ResourceMeta;
import com.wordnik.swagger.annotations.ApiModelProperty;

/**
 * Created by liangfu on 4/3/14.
 */
public class UserDevice extends ResourceMeta implements Identifiable<UserDeviceId> {

    @ApiModelProperty(position = 1, required = true, value = "The id of the user device pair resource.")
    @JsonProperty("self")
    private UserDeviceId id;

    @ApiModelProperty(position = 2, required = true, value = "The user resource.")
    @JsonProperty("user")
    private UserId userId;

    @ApiModelProperty(position = 3, required = true, value = "The device resource.")
    @JsonProperty("device")
    private DeviceId deviceId;

    public UserDeviceId getId() {
        return id;
    }

    public void setId(UserDeviceId id) {
        this.id = id;
        support.setPropertyAssigned("id");
        support.setPropertyAssigned("self");
    }

    public UserId getUserId() {
        return userId;
    }

    public void setUserId(UserId userId) {
        this.userId = userId;
        support.setPropertyAssigned("userId");
        support.setPropertyAssigned("user");
    }

    public DeviceId getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(DeviceId deviceId) {
        this.deviceId = deviceId;
        support.setPropertyAssigned("deviceId");
        support.setPropertyAssigned("device");
    }
}
