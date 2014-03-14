/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.spec.v1.model.users;

import com.junbo.common.id.GroupId;
import com.junbo.common.id.UserGroupId;
import com.junbo.common.id.UserId;
import com.junbo.common.util.Identifiable;

/**
 * Created by kg on 3/12/14.
 */
public class UserGroup extends ResourceMeta implements Identifiable<UserGroupId> {

    private UserGroupId id;

    private UserId userId;

    private GroupId groupId;

    public UserGroupId getId() {
        return id;
    }

    public void setId(UserGroupId id) {
        this.id = id;
    }

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
