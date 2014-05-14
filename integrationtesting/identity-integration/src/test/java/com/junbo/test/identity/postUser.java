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
public class postUser {

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
        User posted = Identity.UserPostDefault();
        User stored = Identity.UserGetByUserId(posted.getId());
        assertEquals("validate user name is correct",
                posted.getUsername(), stored.getUsername());
        assertEquals("validate user created time is correct",
                posted.getCreatedTime(), stored.getCreatedTime());
    }

    @Test(groups = "dailies")
    public void postUserWithFullData() throws Exception {

    }

}
