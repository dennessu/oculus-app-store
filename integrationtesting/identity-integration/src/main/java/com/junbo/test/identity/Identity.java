/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.identity;

import com.junbo.common.id.UserId;
import com.junbo.common.util.IdFormatter;
import com.junbo.identity.spec.model.user.User;
import com.junbo.test.common.GsonHelper;
import com.junbo.test.common.HttpclientHelper;
import com.junbo.test.common.RandomHelper;

/**
 * @author dw
 */
public class Identity {

    public static final String DefaultIdentityURI = "http://localhost:8081/rest/users";
    public static final String DefaultUserPwd = "1234qwerASDF";
    public static final String DefaultUserStatus = "ACTIVE";

    private Identity() {

    }

    public static User DefaultPostUser() throws Exception {
        String userName = RandomHelper.randomAlphabetic(15);
        User user = new User();
        user.setUserName(userName);
        user.setPassword(DefaultUserPwd);
        user.setStatus(DefaultUserStatus);
        User posted = (User) HttpclientHelper.SimpleJsonPost(DefaultIdentityURI,
                GsonHelper.GsonSerializer(user),
                User.class);
        return posted;
    }

    public static User GetUserByUserId(UserId userId) throws Exception {
        User got = (User) HttpclientHelper.SimpleGet(DefaultIdentityURI + "/" +
                IdFormatter.encodeId(userId), User.class);
        return got;
    }
}
