/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.testing.customerscenario.user;

import com.junbo.testing.customerscenario.util.BaseTestClass;

import com.junbo.testing.common.libs.LogHelper;
import com.junbo.testing.common.apihelper.user.GetUser;
import com.junbo.testing.common.apihelper.user.PostUser;
import com.junbo.testing.common.property.*;

import org.testng.Assert;
import org.testng.annotations.Test;

/**
 @author Jason
 * Time: 3/7/2014
 * For holding Post User API test cases
 */
public class TestPostUser extends BaseTestClass {

    private LogHelper logger = new LogHelper(TestPostUser.class);

    private final String serverURL = "http://10.0.0.111:8081/rest/users";

    @property(
            priority = Priority.BVT,
            features = "CustomerScenarios",
            component = Component.Identity,
            owner = "JasonFu",
            status = Status.Enable,
            description = "Test post user",
            steps = {
                    "1. Post a user and get its user ID",
                    "2. Try to get the user with the returned ID",
                    "3. Try to get the user with the returned username"
            }
    )
    @Test
    public void testPostUser() throws Exception {

        String apiResponse = PostUser.CreateUser(serverURL);

        logger.logInfo("The Response Body is: " + apiResponse);

        String[] results = apiResponse.split(",");
        String userId = null;
        String userName = null;
        for (String s : results) {
            if (s.contains("id")) {
                userId = s.replace("\"id\":\"", "").replace("\"", "").replace("}", "").trim();
            }
            if (s.contains("userName")) {
                userName = s.replace("\"userName\":\"", "").replace("\"", "").trim();
            }
        }

        Assert.assertNotNull(userId);
        Assert.assertNotNull(userName);

        //Get the user with ID
        apiResponse = GetUser.GetUserById(serverURL, userId);
        Assert.assertTrue(apiResponse.contains(userId), "Can't get user by user ID");

        //Get the user with userName
        apiResponse = GetUser.GetUserByUserName(serverURL, userName);
        Assert.assertTrue(apiResponse.contains(userName),  "Can't get user by username");

    }

}
