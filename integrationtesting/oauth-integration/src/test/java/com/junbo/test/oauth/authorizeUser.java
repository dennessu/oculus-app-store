/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.oauth;

import com.junbo.identity.spec.model.user.User;
import com.junbo.oauth.spec.model.TokenInfo;
import com.junbo.test.common.HttpclientHelper;
import com.junbo.test.identity.Identity;
import org.testng.annotations.*;

import static org.testng.AssertJUnit.*;

/**
 * @author dw
 */
public class authorizeUser {

    @BeforeTest
    public void setup() {
        HttpclientHelper.CreateHttpClient();
    }

    @AfterTest
    public void dispose() throws Exception {
        HttpclientHelper.CloseHttpClient();
    }

    @Test(groups = "bvt")
    public void authorizeUser() throws Exception {
        String cid = Oauth.GetLoginCid();
        User user = Identity.DefaultPostUser();
        String authCode = Oauth.GetAuthCode(cid, user.getUserName());
        String accessToken = Oauth.GetAccessToken(authCode);
        TokenInfo tokenInfo = Oauth.GetTokenInfo(accessToken);
        assertEquals("validate token->client is correct", Oauth.DefaultClientId, tokenInfo.getClientId());
        assertEquals("validate token->scopes is correct", Oauth.DefaultClientScopes, tokenInfo.getScopes());
        User storedUser = Identity.GetUserByUserId(tokenInfo.getSub());
        assertEquals("validate token->binded user is correct", user.getUserName(), storedUser.getUserName());
    }

}
