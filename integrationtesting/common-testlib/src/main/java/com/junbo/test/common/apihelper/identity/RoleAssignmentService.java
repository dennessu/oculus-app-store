/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.common.apihelper.identity;

import com.junbo.common.id.RoleId;
import com.junbo.common.model.Results;
import com.junbo.authorization.spec.model.RoleAssignment;

/**
 * @author Jason
 * time 4/29/2014
 * Interface for User/RoleAssignment related API, including get/post/put/delete roleAssignment.
 */
public interface RoleAssignmentService {

    RoleAssignment postDefaultRoleAssignment(RoleId roleId, String userId) throws Exception;
    RoleAssignment postRoleAssignment(RoleAssignment roleAssignment) throws Exception;
    RoleAssignment postRoleAssignment(RoleAssignment roleAssignment, int expectedResponseCode) throws Exception;

    Results<RoleAssignment> getRoleAssignments() throws Exception;
    Results<RoleAssignment> getRoleAssignments(int expectedResponseCode) throws Exception;

    RoleAssignment getRoleAssignment(String roleAssignmentId) throws Exception;
    RoleAssignment getRoleAssignment(String roleAssignmentId, int expectedResponseCode) throws Exception;

    RoleAssignment updateRoleAssignment(RoleAssignment roleAssignment) throws Exception;
    RoleAssignment updateRoleAssignment(RoleAssignment roleAssignment, int expectedResponseCode) throws Exception;

    void deleteRoleAssignment(String roleAssignmentId) throws Exception;
    void deleteRoleAssignment(String roleAssignmentId, int expectedResponseCode) throws Exception;
}
