/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.identity;

import com.junbo.common.id.UserId;
import com.junbo.common.util.IdFormatter;
import com.junbo.identity.spec.v1.model.User;
import com.junbo.test.common.*;

/**
 * @author dw
 */
public class Identity {

    public static final String DefaultIdentityURI = ConfigHelper.getSetting("defaultIdentityURI");
    public static final String DefaultUserPwd = "1234qwerASDF";
    public static final String DefaultUserStatus = "ACTIVE";
    public static final String DefaultUserEmail = "leoltd@163.com";

    private Identity() {

    }

    public static User DefaultPostUser() throws Exception {
        String userName = RandomHelper.randomAlphabetic(15);
        User user = new User();
        user.setUsername(userName);
        user.setIsAnonymous(false);
        User posted = (User) HttpclientHelper.SimpleJsonPost(DefaultIdentityURI,
                JsonHelper.JsonSerializer(user),
                User.class);
        return posted;
    }

    public static User GetUserByUserId(UserId userId) throws Exception {
        User got = (User) HttpclientHelper.SimpleGet(DefaultIdentityURI + "/"
                + IdFormatter.encodeId(userId), User.class);
        return got;
    }

    // ****** start API sample logging ******
    public static final String MessageDefaultPostUser = "[Include In Sample][1] Description: Post_User_Default";
    public static final String MessageGetUserByUserId = "[Include In Sample][1] Description: Get_User_By_UserId";

    public static void StartLoggingAPISample(String message) {
        System.out.println(message);
    }
}
