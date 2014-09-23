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
import org.testng.annotations.*;

import java.util.ArrayList;
import java.util.List;

/**
 * @author dw
 */
public class postUserTFA {

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
    public void postUserTFA() throws Exception {
        User user = Identity.UserPostDefault();
        UserTFA userTFA = IdentityModel.DefaultUserTFA();
        UserPersonalInfo upiEmail = null, upiPhone = null;

        List<UserPersonalInfoLink> upiLinkEmail = new ArrayList<>();
        UserPersonalInfoLink userPersonalInfoLinkEmail = new UserPersonalInfoLink();
        userPersonalInfoLinkEmail.setUserId(user.getId());
        userPersonalInfoLinkEmail.setIsDefault(true);
        upiEmail = Identity.UserPersonalInfoPost(user.getId(), IdentityModel.DefaultUserPersonalInfoEmail());
        userPersonalInfoLinkEmail.setValue(upiEmail.getId());
        upiLinkEmail.add(userPersonalInfoLinkEmail);
        user.setEmails(upiLinkEmail);
        user = Identity.UserPut(user);

        List<UserPersonalInfoLink> upiLinkPhone = new ArrayList<>();
        UserPersonalInfoLink userPersonalInfoLinkPhone = new UserPersonalInfoLink();
        userPersonalInfoLinkPhone.setUserId(user.getId());
        userPersonalInfoLinkPhone.setIsDefault(true);
        upiPhone = Identity.UserPersonalInfoPost(user.getId(), IdentityModel.DefaultUserPersonalInfoPhone());
        userPersonalInfoLinkPhone.setValue(upiPhone.getId());
        upiLinkPhone.add(userPersonalInfoLinkPhone);
        user.setPhones(upiLinkPhone);
        user = Identity.UserPut(user);

        List<String> array = new ArrayList<>();
        array.add(IdentityModel.TFAVerifyType.CALL.name());
        array.add(IdentityModel.TFAVerifyType.EMAIL.name());
        array.add(IdentityModel.TFAVerifyType.SMS.name());
        for (int i = 0; i < array.size(); i++) {
            userTFA.setVerifyType(array.get(i));
            if (array.get(i).equals(IdentityModel.TFAVerifyType.EMAIL.name())) {
                userTFA.setPersonalInfo(upiEmail.getId());
            } else {
                userTFA.setPersonalInfo(upiPhone.getId());
            }
            Identity.UserTFAPost(user.getId(), userTFA);
        }
    }
}
