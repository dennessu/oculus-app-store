/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.identity;

import com.junbo.identity.spec.v1.model.User;
import com.junbo.identity.spec.v1.model.UserPersonalInfo;
import com.junbo.identity.spec.v1.model.UserPersonalInfoLink;
import com.junbo.identity.spec.v1.model.UserTFA;
import com.junbo.test.common.HttpclientHelper;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * @author dw
 */
public class postUserTFA {

    @BeforeSuite
    public void run() throws Exception {
        HttpclientHelper.CreateHttpClient();
        Identity.GetHttpAuthorizationHeader();
        HttpclientHelper.CloseHttpClient();
    }

    @BeforeMethod
    public void setup() throws Exception {
        HttpclientHelper.CreateHttpClient();
    }

    @AfterMethod
    public void dispose() throws Exception {
        HttpclientHelper.CloseHttpClient();
    }

    @Test(groups = "bvt")
    public void postUserTFA() throws Exception {
        User user = Identity.UserPostDefault();
        User storedUser = Identity.UserGetByUserId(user.getId());
        UserTFA userTFA = IdentityModel.DefaultUserTFA();
        UserPersonalInfo upi = null;

        List<UserPersonalInfoLink> upiLink = new ArrayList<>();
        UserPersonalInfoLink userPersonalInfoLink = new UserPersonalInfoLink();
        userPersonalInfoLink.setUserId(user.getId());
        userPersonalInfoLink.setIsDefault(true);
        if (userTFA.getVerifyType().equals(IdentityModel.TFAVerifyType.MAIL.name())) {
            upi = Identity.UserPersonalInfoPost(user.getId(), IdentityModel.DefaultUserPersonalInfoEmail());
            userPersonalInfoLink.setValue(upi.getId());
            upiLink.add(userPersonalInfoLink);
            storedUser.setEmails(upiLink);
        } else {
            upi = Identity.UserPersonalInfoPost(user.getId(), IdentityModel.DefaultUserPersonalInfoPhone());
            userPersonalInfoLink.setValue(upi.getId());
            upiLink.add(userPersonalInfoLink);
            storedUser.setPhones(upiLink);
        }
        Identity.UserPut(storedUser);
        userTFA.setPersonalInfo(upi.getId());

        Identity.UserTFAPost(user.getId(), userTFA);
    }
}