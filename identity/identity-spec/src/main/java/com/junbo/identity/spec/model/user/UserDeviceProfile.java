/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.spec.model.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.junbo.common.id.DeviceId;
import com.junbo.common.id.UserDeviceProfileId;
import com.junbo.common.id.UserId;
import com.junbo.identity.spec.model.common.CommonStamp;

import java.util.Date;

/**
 * Created by liangfu on 2/13/14.
 */
public class UserDeviceProfile extends CommonStamp {
    @JsonProperty("self")
    private UserDeviceProfileId id;

    @JsonProperty("user")
    private UserId userId;
    private String type;

    @JsonProperty("device")
    private DeviceId deviceId;
    private Date lastUsedDate;

    public UserDeviceProfileId getId() {
        return id;
    }

    public void setId(UserDeviceProfileId id) {
        this.id = id;
    }

    public UserId getUserId() {
        return userId;
    }

    public void setUserId(UserId userId) {
        this.userId = userId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public DeviceId getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(DeviceId deviceId) {
        this.deviceId = deviceId;
    }

    public Date getLastUsedDate() {
        return lastUsedDate;
    }

    public void setLastUsedDate(Date lastUsedDate) {
        this.lastUsedDate = lastUsedDate;
    }
}
