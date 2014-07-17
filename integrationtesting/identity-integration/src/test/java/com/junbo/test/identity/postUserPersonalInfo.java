/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.identity;

import com.junbo.identity.spec.v1.model.User;
import com.junbo.identity.spec.v1.model.UserPersonalInfo;
import com.junbo.test.common.HttpclientHelper;
import com.junbo.test.common.Validator;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

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
        UserPersonalInfo upi = IdentityModel.DefaultUserPersonalInfoAddress();
        UserPersonalInfo posted = Identity.UserPersonalInfoPost(user.getId(), upi);
        UserPersonalInfo stored = Identity.UserPersonalInfoGetByUserPersonalInfoId(posted.getId());
        Validator.Validate("validate user personal info type", upi.getType(), stored.getType());
        Validator.Validate("validate user personal info value", upi.getValue(), stored.getValue());
    }
}
