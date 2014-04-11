/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.spec.v1.option.list;

import com.junbo.common.id.UserId;

import javax.ws.rs.QueryParam;

/**
 * Created by liangfu on 4/3/14.
 */
public class UserPiiListOptions {

    @QueryParam("properties")
    private String properties;

    // UserId or email can't be searched at the same time
    @QueryParam("userId")
    private UserId userId;

    @QueryParam("email")
    private String email;

    public String getProperties() {
        return properties;
    }

    public void setProperties(String properties) {
        this.properties = properties;
    }

    public UserId getUserId() {
        return userId;
    }

    public void setUserId(UserId userId) {
        this.userId = userId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
