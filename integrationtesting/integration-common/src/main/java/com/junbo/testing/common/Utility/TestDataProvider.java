/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.testing.common.Utility;

import com.junbo.identity.spec.model.user.User;
import com.junbo.testing.common.apihelper.catalog.ItemService;
import com.junbo.testing.common.apihelper.catalog.impl.ItemServiceImpl;
import com.junbo.testing.common.apihelper.catalog.impl.OfferServiceImpl;
import com.junbo.testing.common.apihelper.identity.UserService;
import com.junbo.testing.common.apihelper.identity.impl.UserServiceImpl;
import com.junbo.testing.common.libs.EnumHelper.UserStatus;

/**
 * Created by Yunlong on 3/20/14.
 */
public class TestDataProvider {
    private UserService identityClient = UserServiceImpl.instance();
    private ItemService ItemClient = ItemServiceImpl.instance();
    private OfferServiceImpl offerServiceImpl = new OfferServiceImpl();

    public TestDataProvider() {
    }

    public void CreateUser(String email, String password, UserStatus status) throws Exception {
        User userToPost = new User();
        userToPost.setUserName(email);
        userToPost.setPassword(password);
        userToPost.setStatus(status.getStatus());

        identityClient.PostUser(userToPost);
    }


    public String GetUserByUid(String userId) throws  Exception{
        return identityClient.GetUserByUserId(userId);
    }

    public void PostItem(){}

    public void PostOffer(){}

}
