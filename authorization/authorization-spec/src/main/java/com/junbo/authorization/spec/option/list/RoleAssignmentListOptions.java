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
}
