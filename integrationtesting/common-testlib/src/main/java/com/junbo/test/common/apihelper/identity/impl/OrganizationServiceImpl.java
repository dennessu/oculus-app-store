/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.common.apihelper.identity.impl;

import com.junbo.authorization.spec.model.Role;
import com.junbo.test.common.apihelper.identity.OrganizationService;
import com.junbo.test.common.apihelper.identity.RoleAssignmentService;
import com.junbo.test.common.apihelper.identity.RoleService;
import com.junbo.test.common.apihelper.identity.UserService;
import com.junbo.test.common.apihelper.HttpClientBase;
import com.junbo.identity.spec.v1.model.Organization;
import com.junbo.common.json.JsonMessageTranscoder;
import com.junbo.langur.core.client.TypeReference;
import com.junbo.test.common.libs.RandomFactory;
import com.junbo.test.common.libs.IdConverter;
import com.junbo.test.common.ConfigHelper;
import com.junbo.common.id.UserId;

/**
 * @author Jason
 *         time 6/10/2014
 *         Organization related API helper, including get/post/put/delete organization.
 */
public class OrganizationServiceImpl extends HttpClientBase implements OrganizationService {

    private final String organizationUrl = ConfigHelper.getSetting("defaultIdentityEndPointV1") + "organizations";
    private static OrganizationService instance;

    public static synchronized OrganizationService instance() {
        if (instance == null) {
            instance = new OrganizationServiceImpl();
        }
        return instance;
    }

    private OrganizationServiceImpl() {
    }

    public Organization postDefaultOrganization() throws Exception {
        UserService us = UserServiceImpl.instance();
        String userId = us.PostUser();
        return this.postDefaultOrganization(userId);
    }

    public Organization postDefaultOrganization(String userId) throws Exception {
        Organization organization = new Organization();
        organization.setOwnerId(new UserId(IdConverter.hexStringToId(UserId.class, userId)));
        organization.setName(RandomFactory.getRandomStringOfAlphabet(10));
        organization.setIsValidated(true);
        Organization organizationPost = this.postOrganization(organization);

        //Add role to the organization
        RoleService roleService = RoleServiceImpl.instance();
        Role role = roleService.postDefaultRole(IdConverter.idToHexString(organizationPost.getId()));

        //Add role assignment
        RoleAssignmentService roleAssignmentService = RoleAssignmentServiceImpl.instance();
        roleAssignmentService.postDefaultRoleAssignment(role.getId(), userId);

        return organizationPost;
    }

    public Organization postOrganization(Organization organization) throws Exception {
        return this.postOrganization(organization, 201);
    }

    public Organization postOrganization(Organization organization, int expectedResponseCode) throws Exception {
        String responseBody = restApiCall(HTTPMethod.POST, organizationUrl, organization, expectedResponseCode);
        return new JsonMessageTranscoder().decode(new TypeReference<Organization>() {
        }, responseBody);
    }

}
