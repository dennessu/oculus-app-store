/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.authorization.spec.option.list;

import com.junbo.common.id.RoleId;

import javax.ws.rs.QueryParam;

/**
 * RoleAssignmentListOptions.
 */
public class RoleAssignmentListOptions extends PagingListOptions {

    @QueryParam("roleId")
    private RoleId roleId;

    @QueryParam("assignee")
    private String assignee;

    private String assigneeIdType;

    private String assigneeId;

    public RoleId getRoleId() {
        return roleId;
    }

    public void setRoleId(RoleId roleId) {
        this.roleId = roleId;
    }

    public String getAssignee() {
        return assignee;
    }

    public void setAssignee(String assignee) {
        this.assignee = assignee;
    }

    public String getAssigneeIdType() {
        return assigneeIdType;
    }

    public void setAssigneeIdType(String assigneeIdType) {
        this.assigneeIdType = assigneeIdType;
    }

    public String getAssigneeId() {
        return assigneeId;
    }

    public void setAssigneeId(String assigneeId) {
        this.assigneeId = assigneeId;
    }
}
