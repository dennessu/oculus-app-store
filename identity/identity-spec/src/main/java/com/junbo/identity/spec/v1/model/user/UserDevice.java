/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.spec.v1.model.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.junbo.common.id.UserDeviceId;
import com.junbo.common.id.UserId;

/**
 * Created by kg on 3/13/14.
 */
public class UserDevice {

    @JsonProperty("self")
    private UserDeviceId id;

    @JsonProperty("user")
    private UserId userId;

    private String deviceId;

    private String os;

    private String type;

    private String name;

    public UserDeviceId getId() {
        return id;
    }

    public void setId(UserDeviceId id) {
        this.id = id;
    }

    public UserId getUserId() {
        return userId;
    }

    public void setUserId(UserId userId) {
        this.userId = userId;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getOs() {
        return os;
    }

    public void setOs(String os) {
        this.os = os;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
