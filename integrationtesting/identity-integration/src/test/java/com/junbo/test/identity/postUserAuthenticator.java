/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.identity;

import com.junbo.common.model.Results;
import com.junbo.identity.spec.v1.model.User;
import com.junbo.identity.spec.v1.model.UserAuthenticator;
import com.junbo.test.common.HttpclientHelper;
import com.junbo.test.common.RandomHelper;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by liangfu on 10/31/14.
 */
public class postUserAuthenticator {
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
    public void testPostUserAuthenticator() throws Exception {
        User user = Identity.UserPostDefault();

        UserAuthenticator userAuthenticator = Identity.UserAuthenticatorPost(user.getId());
        assert userAuthenticator != null;

        userAuthenticator.setExternalId(RandomHelper.randomAlphabetic(15));
        userAuthenticator = Identity.UserAuthenticatorPut(userAuthenticator);

        UserAuthenticator newUserAuthenticator = Identity.UserAuthenticatorGet(userAuthenticator.getId());
        assert newUserAuthenticator != null;
        assert userAuthenticator.getExternalId().equals(newUserAuthenticator.getExternalId());
    }

    @Test(groups = "dailies")
    public void testUserAuthenticatorSearch() throws Exception {
        User user = Identity.UserPostDefault();

        List<String> externalRefs = new ArrayList<>();
        for (int i=0; i<10; i++) {
            UserAuthenticator userAuthenticator = Identity.UserAuthenticatorPost(user.getId());
            externalRefs.add(userAuthenticator.getExternalId());
        }

        // validate search
        Results<UserAuthenticator> results = Identity.UserAuthenticatorSearch(user.getId(), null, null, null);
        assert results != null;
        assert results.getTotal() == 10;
        assert results.getItems().size() == 10;

        results = Identity.UserAuthenticatorSearch(user.getId(), null, null, 5);
        assert results != null;
        assert results.getTotal() == 10;
        assert results.getItems().size() == 5;

        results = Identity.UserAuthenticatorSearch(user.getId(), "GOOGLE", null, null);
        assert results != null;
        assert results.getTotal() == 10;
        assert results.getItems().size() == 10;

        for (String externalRef : externalRefs) {
            results = Identity.UserAuthenticatorSearch(user.getId(), "GOOGLE", externalRef, null);
            assert results != null;
            assert results.getTotal() == 1;
            assert results.getItems().size() == 1;

            results = Identity.UserAuthenticatorSearch(user.getId(), "GOOGLE", externalRef, 0);
            assert results != null;
            assert results.getTotal() == 1;
            assert results.getItems().size() == 0;

            results = Identity.UserAuthenticatorSearch(null, "GOOGLE", externalRef, null);
            assert results != null;
            assert results.getTotal() == 1;
            assert results.getItems().size() == 1;

            results = Identity.UserAuthenticatorSearch(null, "GOOGLE", externalRef, 0);
            assert results != null;
            assert results.getTotal() == 1;
            assert results.getItems().size() == 0;

            results = Identity.UserAuthenticatorSearch(user.getId(), null, externalRef, null);
            assert results != null;
            assert results.getTotal() == 1;
            assert results.getItems().size() == 1;

            results = Identity.UserAuthenticatorSearch(user.getId(), null, externalRef, 0);
            assert results != null;
            assert results.getTotal() == 1;
            assert results.getItems().size() == 0;

            results = Identity.UserAuthenticatorSearch(null, null, externalRef, null);
            assert results != null;
            assert results.getTotal() == 1;
            assert results.getItems().size() == 1;

            results = Identity.UserAuthenticatorSearch(null, null, externalRef, 0);
            assert results != null;
            assert results.getTotal() == 1;
            assert results.getItems().size() == 0;
        }
    }
}
