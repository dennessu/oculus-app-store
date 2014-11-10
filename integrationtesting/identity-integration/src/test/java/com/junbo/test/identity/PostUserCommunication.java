/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.identity;

import com.junbo.common.model.Results;
import com.junbo.identity.spec.v1.model.Communication;
import com.junbo.identity.spec.v1.model.User;
import com.junbo.identity.spec.v1.model.UserCommunication;
import com.junbo.test.common.HttpclientHelper;
import com.junbo.test.common.RandomHelper;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * Created by liangfu on 11/10/14.
 */
public class PostUserCommunication {


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
    public void postUserCommunication() throws Exception {
        String username = RandomHelper.randomAlphabetic(15);
        String email = RandomHelper.randomEmail();
        User user = Identity.UserPostDefaultWithMail(username, email);
        Communication communication1 = Identity.CommunicationDefault(IdentityModel.DefaultCommunication());
        Communication communication2 = Identity.CommunicationDefault(IdentityModel.DefaultCommunication());

        UserCommunication userCommunication = Identity.UserCommunicationPost(user.getId(), communication1.getId());
        Results<UserCommunication> results = Identity.UserCommunicationSearch(user.getId(), null, null);

        assert results != null;
        assert results.getItems().size() == 1;
        assert results.getTotal() == 1;

        results = Identity.UserCommunicationSearch(user.getId(), null, 0);
        assert results != null;
        assert results.getItems().size() == 0;
        assert results.getTotal() == 1;

        results = Identity.UserCommunicationSearch(user.getId(), communication2.getId(), null);
        assert results != null;
        assert results.getItems().size() == 0;
        assert results.getTotal() == 0;

        results = Identity.UserCommunicationSearch(user.getId(), communication1.getId(), null);
        assert results != null;
        assert results.getItems().size() == 1;
        assert results.getTotal() == 1;

        results = Identity.UserCommunicationSearch(user.getId(), communication1.getId(), 0);
        assert results != null;
        assert results.getItems().size() == 0;
        assert results.getTotal() == 1;
    }
}
