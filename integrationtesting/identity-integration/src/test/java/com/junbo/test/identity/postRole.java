/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.identity;

import com.junbo.authorization.spec.model.Role;
import com.junbo.authorization.spec.model.RoleTarget;
import com.junbo.common.model.Link;
import com.junbo.identity.spec.v1.model.Organization;
import com.junbo.test.common.HttpclientHelper;
import com.junbo.test.common.Validator;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * @author dw
 */
public class postRole {

    @BeforeClass(alwaysRun = true)
    public void run() throws Exception {
        HttpclientHelper.CreateHttpClient();
        Identity.GetHttpAuthorizationHeader();
        HttpclientHelper.CloseHttpClient();
    }

    @BeforeMethod(alwaysRun = true)
    public void setup() throws Exception {
        HttpclientHelper.CreateHttpClient();
    }

    @AfterMethod(alwaysRun = true)
    public void dispose() throws Exception {
        HttpclientHelper.CloseHttpClient();
    }

    @Test(groups = "bvt")
    public void postRole() throws Exception {
        Role posted = Authorization.rolePostDefault();
        Role gotById = Authorization.roleGetByRoleId(posted.getId());
        Role gotByMultiFields = Authorization.roleGetByMultiFields(posted);
        Validator.Validate("validate role is the same", gotById, gotByMultiFields);

        Organization organization = Identity.OrganizationPostDefault(null);
        RoleTarget target = new RoleTarget();
        target.setTargetType(AuthorizationModel.RandomRoleTargetType());
        target.setFilterType(AuthorizationModel.RandomRoleFilterType());
        Link link = new Link();
        link.setId(Identity.GetHexLongId(organization.getId().getValue()));
        link.setHref("/v1/organizations/" + Identity.GetHexLongId(organization.getId().getValue()));
        target.setFilterLink(link);
        posted.setTarget(target);
        Role put = Authorization.rolePut(posted);
        Validator.Validate("validate role id is the same", posted.getId(), put.getId());
        Validator.Validate("validate role target is updated", posted.getTarget(), put.getTarget());
    }
}
