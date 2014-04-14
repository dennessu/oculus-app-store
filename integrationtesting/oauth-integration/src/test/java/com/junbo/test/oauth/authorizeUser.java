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

    @BeforeMethod
    public void setup() {
        HttpclientHelper.CreateHttpClient();
    }

    @AfterMethod
    public void dispose() throws Exception {
        HttpclientHelper.CloseHttpClient();
    }

    @Test(groups = "bvt")
    public void authorizeUser() throws Exception {
        User user = Identity.DefaultPostUser();
        Oauth.StartLoggingAPISample(Oauth.MessageGetLoginCid);
        String cid = Oauth.GetLoginCid();
        Oauth.StartLoggingAPISample(Oauth.MessageGetAuthCodeByCidAndUserName);
        String authCode = Oauth.GetAuthCode(cid, user.getUserName());
        Oauth.StartLoggingAPISample(Oauth.MessageGetAccessTokenByAuthCode);
        String accessToken = Oauth.GetAccessToken(authCode);
        Oauth.StartLoggingAPISample(Oauth.MessageGetTokenInfoByAccessToken);
        TokenInfo tokenInfo = Oauth.GetTokenInfo(accessToken);
        assertEquals("validate token->client is correct", Oauth.DefaultClientId, tokenInfo.getClientId());
        assertEquals("validate token->scopes is correct", Oauth.DefaultClientScopes, tokenInfo.getScopes());
        User storedUser = Identity.GetUserByUserId(tokenInfo.getSub());
        assertEquals("validate token->binded user is correct", user.getUserName(), storedUser.getUserName());
    }

    @Test(groups = "bvt")
    public void userSSO() throws Exception {
        User user = Identity.DefaultPostUser();
        String cid = Oauth.GetLoginCid();
        String loginState = Oauth.GetLoginState(cid, user.getUserName());
        String authCode = Oauth.SSO2GetAuthCode(loginState);
        String accessToken = Oauth.GetAccessToken(authCode);
        TokenInfo tokenInfo = Oauth.GetTokenInfo(accessToken);
        assertEquals("validate token->client is correct", Oauth.DefaultClientId, tokenInfo.getClientId());
        assertEquals("validate token->scopes is correct", Oauth.DefaultClientScopes, tokenInfo.getScopes());
        User storedUser = Identity.GetUserByUserId(tokenInfo.getSub());
        assertEquals("validate token->binded user is correct", user.getUserName(), storedUser.getUserName());
    }

}
