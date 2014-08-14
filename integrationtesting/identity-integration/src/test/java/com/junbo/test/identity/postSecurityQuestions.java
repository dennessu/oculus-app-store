/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.identity;

import com.junbo.identity.spec.v1.model.User;
import com.junbo.identity.spec.v1.model.UserSecurityQuestion;
import com.junbo.test.common.HttpclientHelper;
import com.junbo.test.common.Validator;
import org.testng.annotations.*;

/**
 * @author dw
 */
public class postSecurityQuestions {

    @BeforeClass
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
    public void postUserSecurityQuestion() throws Exception {
        User user = Identity.UserPostDefault();

        UserSecurityQuestion usq = IdentityModel.DefaultUserSecurityQuestion();
        UserSecurityQuestion posted = Identity.UserSecurityQuestionPost(user.getId(), usq);
        UserSecurityQuestion result = Identity.UserSecurityQuestionGetById(user.getId(), posted.getId());
        Validator.Validate("validate security question", usq.getSecurityQuestion(), result.getSecurityQuestion());
        Validator.Validate("validate security answer", null, result.getAnswer());
    }
}
