/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.buyerscenario;

import com.junbo.test.common.apihelper.identity.impl.UserServiceImpl;
import com.junbo.test.common.apihelper.identity.UserService;
import com.junbo.test.common.Utility.TestClass;
import com.junbo.test.common.blueprint.Master;
import com.junbo.test.common.libs.LogHelper;
import com.junbo.test.common.property.*;

import org.testng.annotations.Test;
import org.testng.Assert;
import java.util.List;

/**
  * @author Jason
  * Time: 3/7/2014
  * For holding test cases of User portal
*/
public class UserPortal extends TestClass {

    private LogHelper logger = new LogHelper(UserPortal.class);

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

        UserService us = UserServiceImpl.instance();
        String userPostId = us.PostUser();

        Assert.assertNotNull(Master.getInstance().getUser(userPostId));
        Assert.assertNotNull(Master.getInstance().getUser(userPostId).getUsername());

        //Get the user with ID
        userPostId = us.GetUserByUserId(userPostId);
        Assert.assertNotNull(userPostId, "Can't get user by user ID");

        //Get the user with userName
        List<String> userGetList = us.GetUserByUserName(Master.getInstance().getUser(userPostId).getUsername());
        Assert.assertNotNull(userGetList, "Can't get user by user Name");
    }
}
