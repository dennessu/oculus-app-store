/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.store.spec.model.identity;

import com.junbo.common.id.UserId;

import javax.ws.rs.QueryParam;

/**
 * The UserProfileGetRequest class.
 */
public class UserProfileGetRequest {

    @QueryParam("userId")
    private UserId userId;

    public UserId getUserId() {
        return userId;
    }

    public void setUserId(UserId userId) {
        this.userId = userId;
    }
}
