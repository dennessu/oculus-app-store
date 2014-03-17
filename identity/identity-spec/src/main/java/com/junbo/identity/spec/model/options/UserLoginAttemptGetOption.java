/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.spec.model.options;

import com.junbo.common.id.UserId;

import javax.ws.rs.QueryParam;

/**
 * Created by liangfu on 3/14/14.
 */
public class UserLoginAttemptGetOption extends PagingGetOption {
    @QueryParam("type")
    private String type;

    @QueryParam("ipAddress")
    private String ipAddress;

    @QueryParam("userId")
    private UserId userId;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public UserId getUserId() {
        return userId;
    }

    public void setUserId(UserId userId) {
        this.userId = userId;
    }
}
