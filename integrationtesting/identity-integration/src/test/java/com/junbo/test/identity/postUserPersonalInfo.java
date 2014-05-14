/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.identity;

import com.junbo.identity.spec.v1.model.User;
import com.junbo.identity.spec.v1.model.UserPersonalInfo;
import com.junbo.test.common.HttpclientHelper;
import com.junbo.test.common.JsonHelper;
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
    public void postUserPersonalInfo() throws Exception {
        User user = Identity.UserPostDefault();
        UserPersonalInfo posted = Identity.UserPersonalInfoPostDefault(user.getId());
        UserPersonalInfo stored = Identity.UserPersonalInfoGetByUserPersonalInfoId(posted.getId());
        assertEquals("validate user personal info id is correct",
                posted.getId(), stored.getId());
        assertEquals("validate user personal info type is correct",
                posted.getType(), stored.getType());
        assertEquals("validate user personal info user id is correct",
                posted.getUserId(), stored.getUserId());
        assertEquals("validate user personal info value is correct",
                JsonHelper.ObjectToJsonNode(posted.getValue()),
                JsonHelper.ObjectToJsonNode(stored.getValue()));
    }
}
