/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.spec.v1.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.junbo.common.id.GroupId;
import com.junbo.common.id.UserGroupId;
import com.junbo.common.id.UserId;
import com.junbo.common.util.Identifiable;
import com.junbo.common.model.ResourceMeta;
import com.wordnik.swagger.annotations.ApiModelProperty;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by liangfu on 4/3/14.
 */
public class UserGroup extends ResourceMeta implements Identifiable<UserGroupId> {

    @ApiModelProperty(position = 1, required = true,
            value = "[Nullable]The id of the user group membership resource.")
    @JsonProperty("self")
    private UserGroupId id;

    @ApiModelProperty(position = 2, required = true, value = "The user resource.")
    @JsonProperty("user")
    private UserId userId;

    @ApiModelProperty(position = 3, required = true, value = "The group resource.")
    @JsonProperty("group")
    private GroupId groupId;

    @ApiModelProperty(position = 4, required = true, value = "The future expansion of user group.")
    private Map<String, JsonNode> futureExpansion = new HashMap<>();

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
        support.setPropertyAssigned("groupId");
        support.setPropertyAssigned("group");
    }

    public Map<String, JsonNode> getFutureExpansion() {
        return futureExpansion;
    }

    public void setFutureExpansion(Map<String, JsonNode> futureExpansion) {
        this.futureExpansion = futureExpansion;
        support.setPropertyAssigned("futureExpansion");
    }
}
