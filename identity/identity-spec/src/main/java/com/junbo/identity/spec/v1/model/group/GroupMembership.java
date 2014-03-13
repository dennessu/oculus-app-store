/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.spec.v1.model.group;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.junbo.common.id.GroupId;
import com.junbo.common.id.GroupMembershipId;
import com.junbo.common.id.UserId;

/**
 * Created by kg on 3/12/14.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GroupMembership {

    @JsonProperty("self")
    private GroupMembershipId groupMembershipId;

    @JsonProperty("group")
    private GroupId groupId;

    @JsonProperty("user")
    private UserId userId;

    private Boolean active;

    public GroupMembershipId getGroupMembershipId() {
        return groupMembershipId;
    }

    public void setGroupMembershipId(GroupMembershipId groupMembershipId) {
        this.groupMembershipId = groupMembershipId;
    }

    public GroupId getGroupId() {
        return groupId;
    }

    public void setGroupId(GroupId groupId) {
        this.groupId = groupId;
    }

    public UserId getUserId() {
        return userId;
    }

    public void setUserId(UserId userId) {
        this.userId = userId;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }
}
