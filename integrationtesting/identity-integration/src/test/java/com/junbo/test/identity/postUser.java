/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.identity;

import com.junbo.identity.spec.v1.model.User;
import com.junbo.test.common.HttpclientHelper;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.testng.AssertJUnit.assertEquals;

/**
 * @author dw
 */
public class postUser {

    @BeforeMethod
    public void setup() {
        HttpclientHelper.CreateHttpClient();
    }

    @AfterMethod
    public void dispose() throws Exception {
        HttpclientHelper.CloseHttpClient();
    }

    @Test(groups = "bvt")
    public void postUser() throws Exception {
        Identity.StartLoggingAPISample(Identity.MessageDefaultPostUser);
        User newUser = Identity.DefaultPostUser();
        Identity.StartLoggingAPISample(Identity.MessageGetUserByUserId);
        User storedUser = Identity.GetUserByUserId(newUser.getId());
        assertEquals("validate user name is correct",
                newUser.getUsername(), storedUser.getUsername());
        assertEquals("validate user created time is correct",
                newUser.getCreatedTime(), storedUser.getCreatedTime());
    }

    @Test(groups = "dailies")
    public void postUserWithFullData() throws Exception {
        /*
        String userName = RandomHelper.randomAlphabetic(15);
        User user = new User();
        user.setUsername(userName);
        user.setIsAnonymous(false);
        user.setCanonicalUsername(RandomHelper.randomAlphabetic(15));
        List<UserPersonalInfoLink> addresses = new ArrayList<>();
        addresses.
        user.setAddresses(addresses);
        User posted = (User) HttpclientHelper.SimpleJsonPost(Identity.DefaultIdentityEndPointV1,
                JsonHelper.JsonSerializer(user),
                User.class);
                */
    }

}
