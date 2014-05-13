/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.identity;

import com.junbo.identity.spec.v1.model.User;
import com.junbo.test.common.HttpclientHelper;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.testng.AssertJUnit.assertEquals;

/**
 * @author dw
 */
public class postUserPersonalInfo {

    @BeforeMethod
    public void setup() {
        HttpclientHelper.CreateHttpClient();
    }

    @AfterMethod
    public void dispose() throws Exception {
        HttpclientHelper.CloseHttpClient();
    }

    @Test(groups = "bvt")
    public void postUser() throws Exception {
        User newUser = Identity.DefaultPostUser();
        User storedUser = Identity.GetUserByUserId(newUser.getId());
        assertEquals("validate user name is correct",
                newUser.getUsername(), storedUser.getUsername());
        assertEquals("validate user created time is correct",
                newUser.getCreatedTime(), storedUser.getCreatedTime());
    }
}
