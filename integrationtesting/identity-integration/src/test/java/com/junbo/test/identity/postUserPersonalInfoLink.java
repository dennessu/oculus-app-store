/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.identity;

import com.junbo.identity.spec.v1.model.User;
import com.junbo.identity.spec.v1.model.UserPersonalInfo;
import com.junbo.identity.spec.v1.model.UserPersonalInfoLink;
import com.junbo.test.common.HttpclientHelper;
import com.junbo.test.common.Validator;
import org.testng.annotations.*;

import java.util.ArrayList;
import java.util.List;

/**
 * @author dw
 */
public class postUserPersonalInfoLink {

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
    public void postUserPersonalInfoLink() throws Exception {
        User user = Identity.UserPostDefault();
        UserPersonalInfo upi = IdentityModel.DefaultUserPersonalInfoEmail();
        UserPersonalInfo posted = Identity.UserPersonalInfoPost(user.getId(), upi);
        UserPersonalInfo stored = Identity.UserPersonalInfoGetByUserPersonalInfoId(posted.getId());
        Validator.Validate("validate user personal info type", upi.getType(), stored.getType());
        Validator.Validate("validate user personal info value", upi.getValue(), stored.getValue());
        List<UserPersonalInfoLink> emails = new ArrayList<>();
        UserPersonalInfoLink userPersonalInfoLink = new UserPersonalInfoLink();
        userPersonalInfoLink.setUserId(user.getId());
        userPersonalInfoLink.setIsDefault(true);
        userPersonalInfoLink.setValue(posted.getId());
        emails.add(userPersonalInfoLink);
        user.setEmails(emails);
        User updated = Identity.UserPut(user);
        User storedUser = Identity.UserGetByUserId(user.getId());
        Validator.Validate("validate user", updated, storedUser);
    }
}
