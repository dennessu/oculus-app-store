/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.authorization.spec.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.junbo.common.id.RoleAssignmentId;
import com.junbo.common.id.RoleId;
import com.junbo.common.model.Link;
import com.junbo.common.model.PropertyAssignedAwareResourceMeta;
import com.junbo.common.util.Identifiable;
import com.wordnik.swagger.annotations.ApiModelProperty;

/**
 * RoleAssignment.
 */
public class RoleAssignment extends PropertyAssignedAwareResourceMeta implements Identifiable<RoleAssignmentId> {
    @ApiModelProperty(position = 1, required = true,
            value = "[Nullable]The id of the role assignment resource.")
    @JsonProperty("self")
    private RoleAssignmentId id;

    @ApiModelProperty(position = 2, required = true, value = "The role resource.")
    private RoleId roleId;

    @ApiModelProperty(position = 3, required = true, value = "The assignee. User or group.")
    private Link assignee;

    @JsonIgnore
    private String assigneeType;

    @JsonIgnore
    private Long assigneeId;

    @Override
    public RoleAssignmentId getId() {
        return id;
    }

    public void setId(RoleAssignmentId id) {
        this.id = id;
        support.setPropertyAssigned("self");
        support.setPropertyAssigned("id");
    }

    public RoleId getRoleId() {
        return roleId;
    }

    public void setRoleId(RoleId roleId) {
        this.roleId = roleId;
        support.setPropertyAssigned("roleId");
    }

    public Link getAssignee() {
        return assignee;
    }

    public void setAssignee(Link assignee) {
        this.assignee = assignee;
    }

    public String getAssigneeType() {
        return assigneeType;
    }

    public void setAssigneeType(String assigneeType) {
        this.assigneeType = assigneeType;
    }

    public Long getAssigneeId() {
        return assigneeId;
    }

    public void setAssigneeId(Long assigneeId) {
        this.assigneeId = assigneeId;
    }
}
