/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.common.apihelper.identity.impl;

import com.junbo.authorization.spec.model.Role;
import com.junbo.authorization.spec.model.RoleTarget;
import com.junbo.common.json.JsonMessageTranscoder;
import com.junbo.common.model.Link;
import com.junbo.common.model.Results;

import com.junbo.langur.core.client.TypeReference;
import com.junbo.test.common.ConfigHelper;
import com.junbo.test.common.apihelper.HttpClientBase;
import com.junbo.test.common.apihelper.identity.RoleService;

/**
 * @author Jason
 * time 4/29/2014
 * Role related API helper, including get/post/put/delete role.
 */
public class RoleServiceImpl extends HttpClientBase implements RoleService {

    private final String roleURL = ConfigHelper.getSetting("defaultIdentityEndPointV1") + "roles";
    private static RoleService instance;

    public static synchronized RoleService instance() {
        if (instance == null) {
            instance = new RoleServiceImpl();
        }
        return instance;
    }

    private RoleServiceImpl() {
    }

    public Role postDefaultRole(String organizationId) throws Exception {
        Role role = new Role();
        RoleTarget target = new RoleTarget();
        Link link = new Link();

        link.setId(organizationId);
        link.setHref("/v1/organizations/" + organizationId);
        target.setTargetType("organizations");
        target.setFilterType("SINGLEINSTANCEFILTER");

        target.setFilterLink(link);

        role.setName("developer");
        role.setTarget(target);
        return this.postRole(role);
    }

    public Role postRole(Role role) throws Exception {
        return postRole(role, 201);
    }

    public Role postRole(Role role, int expectedResponseCode) throws Exception {
        String responseBody = restApiCall(HTTPMethod.POST, roleURL, role, expectedResponseCode);
        return new JsonMessageTranscoder().decode(new TypeReference<Role>() {}, responseBody);
    }

    public Results<Role> getRoles() throws Exception {
        return getRoles(200);
    }

    public Results<Role> getRoles(int expectedResponseCode) throws Exception {
        String responseBody = restApiCall(HTTPMethod.GET, roleURL, expectedResponseCode);
        return new JsonMessageTranscoder().decode(new TypeReference<Results<Role>>() {}, responseBody);
    }

    public Role getRole(String roleId) throws Exception {
        return getRole(roleId, 200);
    }

    public Role getRole(String roleId, int expectedResponseCode) throws Exception {
        String url = roleURL + "/" + roleId;
        String responseBody = restApiCall(HTTPMethod.GET, url, expectedResponseCode);
        return new JsonMessageTranscoder().decode(new TypeReference<Role>() {}, responseBody);
    }

    public Role updateRole(Role role) throws Exception {
        return updateRole(role, 200);
    }

    public Role updateRole(Role role, int expectedResponseCode) throws Exception {
        String putUrl = roleURL + "/" + role.getId().toString();
        String responseBody = restApiCall(HTTPMethod.PUT, putUrl, role, expectedResponseCode);
        return new JsonMessageTranscoder().decode(new TypeReference<Role>() {}, responseBody);
    }

    public void deleteRole(String roleId) throws Exception {
        this.deleteRole(roleId, 204);
    }

    public void deleteRole(String roleId, int expectedResponseCode) throws Exception {
        String url = roleURL + "/" + roleId;
        restApiCall(HTTPMethod.DELETE, url, expectedResponseCode);
    }

}