/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.common.apihelper.identity;

import com.junbo.authorization.spec.model.Role;
import com.junbo.common.model.Results;

/**
 * @author Jason
 * time 7/18/2014
 * Interface for Role related API, including get/post/put/delete role.
 */
public interface RoleService {

    Role postDefaultRole(String organizationId) throws Exception;
    Role postRole(Role role) throws Exception;
    Role postRole(Role role, int expectedResponseCode) throws Exception;

    Results<Role> getRoles() throws Exception;
    Results<Role> getRoles(int expectedResponseCode) throws Exception;

    Role getRole(String roleId) throws Exception;
    Role getRole(String roleId, int expectedResponseCode) throws Exception;

    Role updateRole(Role role) throws Exception;
    Role updateRole(Role role, int expectedResponseCode) throws Exception;

    void deleteRole(String roleId) throws Exception;
    void deleteRole(String roleId, int expectedResponseCode) throws Exception;
}
