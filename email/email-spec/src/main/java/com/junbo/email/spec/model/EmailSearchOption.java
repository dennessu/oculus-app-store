/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.email.spec.model;

import com.junbo.common.id.UserId;

/**
 * Created by haomin on 2/15/15.
 */
public class EmailSearchOption {
    @javax.ws.rs.QueryParam("userId")
    private UserId userId;

    public UserId getUserId() {
        return userId;
    }

    public void setUserId(UserId userId) {
        this.userId = userId;
    }
}
