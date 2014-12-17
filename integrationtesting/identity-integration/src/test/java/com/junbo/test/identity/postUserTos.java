/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.identity;

import com.junbo.common.model.Results;
import com.junbo.identity.spec.v1.model.Tos;
import com.junbo.identity.spec.v1.model.User;
import com.junbo.identity.spec.v1.model.UserTosAgreement;
import com.junbo.test.common.HttpclientHelper;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * Created by liangfu on 11/12/14.
 */
public class postUserTos {

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
    public void testPostUserTos() throws Exception {
        Tos tos1 = Identity.TosPostDefault(IdentityModel.DefaultTos());
        Tos tos2 = Identity.TosPostDefault(IdentityModel.DefaultTos());
        User user = Identity.UserPostDefault();

        Identity.UserTosAgreementPost(user.getId(), tos1.getId());
        Identity.UserTosAgreementPost(user.getId(), tos2.getId());

        Results<UserTosAgreement> results = Identity.UserTosAgreementSearch(user.getId(), null, null);
        assert results.getTotal() == 2;
        assert results.getItems().size() == 2;

        results = Identity.UserTosAgreementSearch(user.getId(), null, 1);
        assert results.getTotal() == 2;
        assert results.getItems().size() == 1;

        results = Identity.UserTosAgreementSearch(user.getId(), null, 0);
        assert results.getTotal() == 2;
        assert results.getItems().size() == 0;

        results = Identity.UserTosAgreementSearch(null, tos1.getId(), null);
        assert results.getTotal() == 1;
        assert results.getItems().size() == 1;

        results = Identity.UserTosAgreementSearch(null, tos1.getId(), 2);
        assert results.getTotal() == 1;
        assert results.getItems().size() == 1;

        results = Identity.UserTosAgreementSearch(null, tos1.getId(), 0);
        assert results.getTotal() == 1;
        assert results.getItems().size() == 0;

        results = Identity.UserTosAgreementSearch(user.getId(), tos2.getId(), null);
        assert results.getTotal() == 1;
        assert results.getItems().size() == 1;

        results = Identity.UserTosAgreementSearch(user.getId(), tos2.getId(), 2);
        assert results.getTotal() == 1;
        assert results.getItems().size() == 1;

        results = Identity.UserTosAgreementSearch(user.getId(), tos2.getId(), 0);
        assert results.getTotal() == 1;
        assert results.getItems().size() == 0;
    }
}
