/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.integration.customerscenarios.user;

import com.junbo.integration.customerscenarios.helper.LogHelper;
import com.junbo.integration.customerscenarios.util.BaseTestClass;
import com.junbo.integration.customerscenarios.helper.AsyncHttpClientHelper;
import com.junbo.integration.customerscenarios.helper.RandomFactory;

import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Created by Jason on 3/7/14.
 */
public class TestPostUser extends BaseTestClass {

    private LogHelper logger = new LogHelper(TestPostUser.class);

    private final String serverURL = "http://10.0.0.111:8081/rest/users";

    @Test(
            description = "Test Post User",
            enabled = true
    )
    public void testPostUser() throws Exception {

        AsyncHttpClientHelper clientHelper = new AsyncHttpClientHelper();
        StringBuilder postRequestBody = new StringBuilder();
        postRequestBody.append("{\"");
        postRequestBody.append(UserPara.userName.toString());
        postRequestBody.append("\":\"");
        postRequestBody.append(RandomFactory.getRandomEmailAddress());
        postRequestBody.append("\",\"");
        postRequestBody.append(UserPara.password.toString());
        postRequestBody.append("\":\"");
        postRequestBody.append(weakPassword);
        postRequestBody.append("\",\"");
        postRequestBody.append(UserPara.passwordStrength.toString());
        postRequestBody.append("\":\"");
        postRequestBody.append(PasswordStrength.WEAK.toString());
        postRequestBody.append("\",\"");
        postRequestBody.append(UserPara.status.toString());
        postRequestBody.append("\":\"");
        postRequestBody.append(UserStatus.ACTIVE.toString());
        postRequestBody.append("\"}");

        String response = clientHelper.UserPost(serverURL,
                postRequestBody.toString());
        logger.logInfo("The Response Body is: " + response);

        String[] results = response.split(",");
        String entityId = null;
        for (String s : results) {
            if (s.contains("id")) {
                entityId = s.replace("{\"id\":\"", "").replace("\"", "").trim();
            }
        }

        Assert.assertNotNull(entityId);
    }
}
