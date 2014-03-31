/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.identity;

import com.junbo.identity.spec.model.user.User;

import com.junbo.test.common.HttpclientHelper;
import org.testng.annotations.*;

import static org.testng.AssertJUnit.*;

/**
 * @author dw
 */
public class postUser {

    @BeforeTest
    public void setup() {
        HttpclientHelper.CreateHttpClient();
    }

    @AfterTest
    public void dispose() throws Exception {
        HttpclientHelper.CloseHttpClient();
    }

    @Test(groups = "bvt")
    public void postUser() throws Exception {
        User newUser = Identity.DefaultPostUser();
        User storedUser = Identity.GetUserByUserId(newUser.getId());
        assertEquals("validate user name is correct",
                newUser.getUserName(), storedUser.getUserName());
        assertEquals("validate user created time is correct",
                newUser.getCreatedTime(), storedUser.getCreatedTime());
    }

}
