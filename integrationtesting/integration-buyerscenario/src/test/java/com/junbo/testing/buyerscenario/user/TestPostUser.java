/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.testing.buyerscenario.user;

import com.junbo.testing.buyerscenario.util.BaseTestClass;
import com.junbo.testing.common.apihelper.identity.impl.UserServiceImpl;
import com.junbo.testing.common.blueprint.User;
import com.junbo.testing.common.libs.LogHelper;
import com.junbo.testing.common.property.*;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.testng.Assert;
import org.testng.annotations.Test;

/**
 @author Jason
 * Time: 3/7/2014
 * For holding Post User API test cases
 */
public class TestPostUser extends BaseTestClass {

    private LogHelper logger = new LogHelper(TestPostUser.class);

    private final String serverURL = "http://localhost:8080/rest/users";

    @Property(
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

        UserServiceImpl userServiceAPI = new UserServiceImpl(serverURL);
        String apiResponse = userServiceAPI.PostUser();

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
        apiResponse = userServiceAPI.GetUserByUserId(userId);
        Assert.assertTrue(apiResponse.contains(userId), "Can't get user by user ID");

        //Get the user with userName
        apiResponse = userServiceAPI.GetUserByUserName(userName);
        Assert.assertTrue(apiResponse.contains(userName),  "Can't get user by username");
    }

}
