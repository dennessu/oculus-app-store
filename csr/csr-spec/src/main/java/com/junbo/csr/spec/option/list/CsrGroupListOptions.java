/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.csr.spec.option.list;

import com.junbo.common.id.UserId;

import javax.ws.rs.QueryParam;

/**
 * Created by haomin on 14-7-14.
 */
public class CsrGroupListOptions {
    @QueryParam("groupName")
    private String groupName;

    @QueryParam("userId")
    private UserId userId;

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public UserId getUserId() {
        return userId;
    }

    public void setUserId(UserId userId) {
        this.userId = userId;
    }
}
