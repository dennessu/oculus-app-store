/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.spec.v1.option.list;

import com.junbo.common.id.UserId;
import com.junbo.common.id.UserTeleId;
import com.junbo.identity.spec.options.list.PagingGetOptions;

import javax.ws.rs.QueryParam;

/**
 * Created by liangfu on 4/22/14.
 */
public class UserTeleAttemptListOptions extends PagingGetOptions {
    @QueryParam("properties")
    private String properties;

    @QueryParam("userId")
    private UserId userId;

    @QueryParam("userTeleId")
    private UserTeleId userTeleId;

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

    public UserTeleId getUserTeleId() {
        return userTeleId;
    }

    public void setUserTeleId(UserTeleId userTeleId) {
        this.userTeleId = userTeleId;
    }
}
