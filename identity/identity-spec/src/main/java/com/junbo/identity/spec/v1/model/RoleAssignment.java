/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.spec.v1.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.junbo.common.id.RoleAssignmentId;
import com.junbo.common.id.RoleId;
import com.junbo.common.util.Identifiable;
import com.junbo.identity.spec.model.users.ResourceMeta;

/**
 * RoleAssignment.
 */
public class RoleAssignment extends ResourceMeta implements Identifiable<RoleAssignmentId> {
    @JsonProperty("self")
    private RoleAssignmentId id;

    private RoleId roleId;

    private String assigneeType;

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

    public String getAssigneeType() {
        return assigneeType;
    }

    public void setAssigneeType(String assigneeType) {
        this.assigneeType = assigneeType;
        support.setPropertyAssigned("assigneeType");
    }

    public Long getAssigneeId() {
        return assigneeId;
    }

    public void setAssigneeId(Long assigneeId) {
        this.assigneeId = assigneeId;
        support.setPropertyAssigned("assigneeId");
    }
}
