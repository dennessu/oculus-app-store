/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.spec.model.users;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.junbo.common.id.GroupId;
import com.junbo.common.id.UserGroupId;
import com.junbo.common.id.UserId;
import com.junbo.common.util.Identifiable;

/**
 * Created by kg on 3/12/14.
 */
public class UserGroup extends ResourceMeta implements Identifiable<UserGroupId> {

    @JsonProperty("self")
    private UserGroupId id;

    @JsonProperty("user")
    private UserId userId;

    @JsonProperty("group")
    private GroupId groupId;

    public UserGroupId getId() {
        return id;
    }

    public void setId(UserGroupId id) {
        this.id = id;
        support.setPropertyAssigned("self");
        support.setPropertyAssigned("id");
    }

    public UserId getUserId() {
        return userId;
    }

    public void setUserId(UserId userId) {
        this.userId = userId;
        support.setPropertyAssigned("userId");
        support.setPropertyAssigned("user");
    }

    public GroupId getGroupId() {
        return groupId;
    }

    public void setGroupId(GroupId groupId) {
        this.groupId = groupId;
        support.setPropertyAssigned("group");
        support.setPropertyAssigned("groupId");
    }
}
