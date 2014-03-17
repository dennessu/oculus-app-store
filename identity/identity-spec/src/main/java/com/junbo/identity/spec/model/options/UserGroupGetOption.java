/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.spec.model.options;

import com.junbo.common.id.GroupId;
import com.junbo.common.id.UserId;

import javax.ws.rs.QueryParam;

/**
 * Created by liangfu on 3/14/14.
 */
public class UserGroupGetOption extends PagingGetOption {
    @QueryParam("userId")
    private UserId userId;

    @QueryParam("groupId")
    private GroupId groupId;

    public UserId getUserId() {
        return userId;
    }

    public void setUserId(UserId userId) {
        this.userId = userId;
    }

    public GroupId getGroupId() {
        return groupId;
    }

    public void setGroupId(GroupId groupId) {
        this.groupId = groupId;
    }
}
