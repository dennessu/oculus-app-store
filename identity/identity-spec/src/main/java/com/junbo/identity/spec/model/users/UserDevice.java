/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.spec.model.users;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.junbo.common.id.UserDeviceId;
import com.junbo.common.id.UserId;
import com.junbo.common.util.Identifiable;

/**
 * Created by kg on 3/13/14.
 */
public class UserDevice extends ResourceMeta implements Identifiable<UserDeviceId> {

    @JsonProperty("self")
    private UserDeviceId id;

    private String deviceId;

    private String os;

    private String type;

    private String name;

    // Not returned
    @JsonIgnore
    private UserId userId;

    public UserDeviceId getId() {
        return id;
    }

    public void setId(UserDeviceId id) {
        this.id = id;
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

    public UserId getUserId() {
        return userId;
    }

    public void setUserId(UserId userId) {
        this.userId = userId;
    }
}
