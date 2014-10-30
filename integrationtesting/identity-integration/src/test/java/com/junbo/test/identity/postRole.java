/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.identity;

import com.junbo.authorization.spec.model.Role;
import com.junbo.authorization.spec.model.RoleTarget;
import com.junbo.common.model.Link;
import com.junbo.identity.spec.v1.model.User;
import com.junbo.test.common.HttpclientHelper;
import com.junbo.test.common.RandomHelper;
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

        User user = Identity.UserPostDefault();
        posted.setName(RandomHelper.randomName());
        RoleTarget target = new RoleTarget();
        target.setTargetType(RandomHelper.randomAlphabetic(10));
        target.setFilterType(RandomHelper.randomAlphabetic(10));
        Link link = new Link();
        link.setId(Identity.GetHexLongId(user.getId().getValue()));
        link.setHref("/v1/users/" + Identity.GetHexLongId(user.getId().getValue()));
        target.setFilterLink(link);
        posted.setTarget(target);
        Role put = Authorization.rolePut(posted);
        Validator.Validate("validate role id is the same", posted.getId(), put.getId());
        Validator.Validate("validate role name is updated", posted.getName(), put.getName());
        Validator.Validate("validate role target is updated", posted.getTarget(), put.getTarget());
    }
}
