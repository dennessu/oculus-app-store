/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.common.apihelper.identity.impl;

import com.junbo.common.id.RoleId;
import com.junbo.common.json.JsonMessageTranscoder;
import com.junbo.common.model.Link;
import com.junbo.common.model.Results;
import com.junbo.authorization.spec.model.RoleAssignment;
import com.junbo.langur.core.client.TypeReference;
import com.junbo.test.common.ConfigHelper;
import com.junbo.test.common.Entities.enums.ComponentType;
import com.junbo.test.common.apihelper.HttpClientBase;
import com.junbo.test.common.apihelper.identity.RoleAssignmentService;

/**
 * @author Jason
 *         time 4/29/2014
 *         RoleAssignment related API helper, including get/post/put/delete roleAssignment.
 */
public class RoleAssignmentServiceImpl extends HttpClientBase implements RoleAssignmentService {

    private final String roleAssignmentURL = ConfigHelper.getSetting("defaultIdentityEndPointV1") + "/role-assignments";
    private static RoleAssignmentService instance;
    private boolean isServiceScope = true;

    public static synchronized RoleAssignmentService instance() {
        if (instance == null) {
            instance = new RoleAssignmentServiceImpl();
        }
        return instance;
    }

    private RoleAssignmentServiceImpl() {
        componentType = ComponentType.IDENTITY;
    }

    public RoleAssignment postDefaultRoleAssignment(RoleId roleId, String userId) throws Exception {

        RoleAssignment roleAssignment = new RoleAssignment();
        Link assignee = new Link();
        assignee.setId(userId);
        assignee.setHref("/v1/users/" + userId);

        roleAssignment.setRoleId(roleId);
        roleAssignment.setAssignee(assignee);

        return this.postRoleAssignment(roleAssignment);
    }

    public RoleAssignment postRoleAssignment(RoleAssignment roleAssignment) throws Exception {
        return postRoleAssignment(roleAssignment, 201);
    }

    public RoleAssignment postRoleAssignment(RoleAssignment roleAssignment, int expectedResponseCode) throws Exception {
        String responseBody = restApiCall(HTTPMethod.POST, roleAssignmentURL, roleAssignment,
                expectedResponseCode, isServiceScope);
        return new JsonMessageTranscoder().decode(new TypeReference<RoleAssignment>() {
        }, responseBody);
    }

    public Results<RoleAssignment> getRoleAssignments() throws Exception {
        return getRoleAssignments(200);
    }

    public Results<RoleAssignment> getRoleAssignments(int expectedResponseCode) throws Exception {
        String responseBody = restApiCall(HTTPMethod.GET, roleAssignmentURL, expectedResponseCode, isServiceScope);
        return new JsonMessageTranscoder().decode(new TypeReference<Results<RoleAssignment>>() {
        }, responseBody);
    }

    public RoleAssignment getRoleAssignment(String roleAssignmentId) throws Exception {
        return getRoleAssignment(roleAssignmentId, 200);
    }

    public RoleAssignment getRoleAssignment(String roleAssignmentId, int expectedResponseCode) throws Exception {
        String url = roleAssignmentURL + "/" + roleAssignmentId;
        String responseBody = restApiCall(HTTPMethod.GET, url, expectedResponseCode, isServiceScope);
        return new JsonMessageTranscoder().decode(new TypeReference<RoleAssignment>() {
        }, responseBody);
    }

    public RoleAssignment updateRoleAssignment(RoleAssignment roleAssignment) throws Exception {
        return updateRoleAssignment(roleAssignment, 200);
    }

    public RoleAssignment updateRoleAssignment(RoleAssignment roleAssignment, int expectedResponseCode)
            throws Exception {
        String putUrl = roleAssignmentURL + "/" + roleAssignment.getId().toString();
        String responseBody = restApiCall(HTTPMethod.PUT, putUrl, roleAssignment, expectedResponseCode, isServiceScope);
        return new JsonMessageTranscoder().decode(new TypeReference<RoleAssignment>() {
        }, responseBody);
    }

    public void deleteRoleAssignment(String roleAssignmentId) throws Exception {
        this.deleteRoleAssignment(roleAssignmentId, 204);
    }

    public void deleteRoleAssignment(String roleAssignmentId, int expectedResponseCode) throws Exception {
        String url = roleAssignmentURL + "/" + roleAssignmentId;
        restApiCall(HTTPMethod.DELETE, url, expectedResponseCode, isServiceScope);
    }

}
