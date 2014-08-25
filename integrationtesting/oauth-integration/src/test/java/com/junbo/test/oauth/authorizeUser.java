/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.oauth;

import com.junbo.identity.spec.v1.model.User;
import com.junbo.identity.spec.v1.model.UserLoginName;
import com.junbo.identity.spec.v1.model.UserPersonalInfo;
import com.junbo.identity.spec.v1.model.UserVAT;
import com.junbo.identity.spec.v1.model.migration.OculusInput;
import com.junbo.identity.spec.v1.model.migration.OculusOutput;
import com.junbo.oauth.spec.model.TokenInfo;
import com.junbo.test.common.HttpclientHelper;
import com.junbo.test.common.JsonHelper;
import com.junbo.test.common.RandomHelper;
import com.junbo.test.common.Validator;
import com.junbo.test.common.property.Property;
import com.junbo.test.identity.Identity;
import com.junbo.test.identity.IdentityModel;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.Map;

import static org.testng.AssertJUnit.assertEquals;

/**
 * @author dw
 */
public class authorizeUser {

    @BeforeMethod(alwaysRun = true)
    public void setup() throws Exception {
        HttpclientHelper.CreateHttpClient();
        com.junbo.test.oauth.Identity.GetHttpAuthorizationHeaderForMigration();
    }

    @AfterMethod(alwaysRun = true)
    public void dispose() throws Exception {
        HttpclientHelper.CloseHttpClient();
    }

    @Test(groups = "bvt")
    public void authorizeUser() throws Exception {
        Oauth.StartLoggingAPISample(Oauth.MessageGetLoginCid);
        String cid = Oauth.GetRegistrationCid();

        Oauth.StartLoggingAPISample(Oauth.MessageGetViewState);
        String currentViewState = Oauth.GetViewStateByCid(cid);
        ValidateErrorFreeResponse(currentViewState);
        assertEquals("validate current view state is login", true, currentViewState.contains("\"view\" : \"login\""));

        Oauth.StartLoggingAPISample(Oauth.MessagePostViewRegister);
        String postRegisterViewResponse = Oauth.PostViewRegisterByCid(cid);
        ValidateErrorFreeResponse(postRegisterViewResponse);
        Oauth.StartLoggingAPISample(Oauth.MessageGetViewState);
        currentViewState = Oauth.GetViewStateByCid(cid);
        ValidateErrorFreeResponse(currentViewState);
        assertEquals("validate view state after post register view", postRegisterViewResponse, currentViewState);

        Oauth.StartLoggingAPISample(Oauth.MessagePostRegisterUser);
        String userName = RandomHelper.randomAlphabetic(15);
        String email = RandomHelper.randomEmail();
        String postRegisterUserResponse = Oauth.PostRegisterUser(cid, userName, email);
        ValidateErrorFreeResponse(postRegisterUserResponse);

        Oauth.StartLoggingAPISample(Oauth.MessageGetAuthCodeByCidAfterRegisterUser);
        String authCode = Oauth.GetAuthCodeAfterRegisterUser(cid);
        Oauth.StartLoggingAPISample(Oauth.MessageGetAccessTokenByAuthCode);
        String accessToken = Oauth.GetAccessToken(authCode);
        Oauth.StartLoggingAPISample(Oauth.MessageGetTokenInfoByAccessToken);
        TokenInfo tokenInfo = Oauth.GetTokenInfo(accessToken);
        assertEquals("validate token->client is correct", Oauth.DefaultClientId, tokenInfo.getClientId());
        assertEquals("validate token->scopes is correct", Oauth.DefaultClientScopes, tokenInfo.getScopes());
        User storedUser = Identity.UserGetByUserId(tokenInfo.getSub());
        UserPersonalInfo storedUPI = Identity.UserPersonalInfoGetByUserPersonalInfoId(storedUser.getUsername());
        assertEquals("validate token->binded user is correct", userName,
                ((UserLoginName) JsonHelper.JsonNodeToObject(storedUPI.getValue(), UserLoginName.class)).getUserName());
    }

    @Test(groups = "bvt")
    public void userSSO() throws Exception {
        Oauth.StartLoggingAPISample(Oauth.MessageGetLoginCid);
        String cid = Oauth.GetRegistrationCid();

        String currentViewState = Oauth.GetViewStateByCid(cid);
        ValidateErrorFreeResponse(currentViewState);
        assertEquals("validate current view state is login", true, currentViewState.contains("\"view\" : \"login\""));

        String postRegisterViewResponse = Oauth.PostViewRegisterByCid(cid);
        ValidateErrorFreeResponse(postRegisterViewResponse);
        currentViewState = Oauth.GetViewStateByCid(cid);
        ValidateErrorFreeResponse(currentViewState);
        assertEquals("validate view state after post register view", postRegisterViewResponse, currentViewState);

        String userName = RandomHelper.randomAlphabetic(15);
        String email = RandomHelper.randomEmail();
        String postRegisterUserResponse = Oauth.PostRegisterUser(cid, userName, email);
        ValidateErrorFreeResponse(postRegisterUserResponse);

        Oauth.StartLoggingAPISample(Oauth.MessageGetLoginStateByCidAfterRegisterUser);
        String loginState = Oauth.GetLoginStateAfterRegisterUser(cid);
        Oauth.StartLoggingAPISample(Oauth.MessageGetAuthCodeByLoginState);
        String authCode = Oauth.SSO2GetAuthCode(loginState);
        String accessToken = Oauth.GetAccessToken(authCode);
        TokenInfo tokenInfo = Oauth.GetTokenInfo(accessToken);
        assertEquals("validate token->client is correct", Oauth.DefaultClientId, tokenInfo.getClientId());
        assertEquals("validate token->scopes is correct", Oauth.DefaultClientScopes, tokenInfo.getScopes());
        User storedUser = Identity.UserGetByUserId(tokenInfo.getSub());
        UserPersonalInfo storedUPI = Identity.UserPersonalInfoGetByUserPersonalInfoId(storedUser.getUsername());
        assertEquals("validate token->binded user is correct", userName,
                ((UserLoginName) JsonHelper.JsonNodeToObject(storedUPI.getValue(), UserLoginName.class)).getUserName());
    }

    @Test(groups = "dailies")
    public void migrateAndLogin() throws Exception {
        OculusInput input = com.junbo.test.oauth.IdentityModel.DefaultOculusInput(
                "1:8UFAbK26VrPLL75jE9P2:PRo4D7r23hrfv3FBxqBv:b87637b9ec5abd43db01d7a299612a49550230a813239fb3e28eec2a88c0df67");
        OculusOutput output = com.junbo.test.oauth.Identity.ImportMigrationData(input);

        HttpclientHelper.ResetHttpClient();
        String cid = Oauth.GetLoginCid();
        String currentViewState = Oauth.GetViewStateByCid(cid);
        ValidateErrorFreeResponse(currentViewState);
        String loginResponseLink = Oauth.UserLogin(cid, input.getUsername(), "radiant555");
        String access_Token = Oauth.GetLoginAccessToken(loginResponseLink);
        TokenInfo tokenInfo = Oauth.GetTokenInfo(access_Token);
        Validator.Validate("Validate UserId same", output.getUserId(), tokenInfo.getSub());
    }

    @Test(groups = "dailies")
    public void login() throws Exception {
        Oauth.StartLoggingAPISample(Oauth.MessageGetLoginCid);
        String cid = Oauth.GetRegistrationCid();

        Oauth.StartLoggingAPISample(Oauth.MessageGetViewState);
        String currentViewState = Oauth.GetViewStateByCid(cid);
        ValidateErrorFreeResponse(currentViewState);
        assertEquals("validate current view state is login", true, currentViewState.contains("\"view\" : \"login\""));

        Oauth.StartLoggingAPISample(Oauth.MessagePostViewRegister);
        String postRegisterViewResponse = Oauth.PostViewRegisterByCid(cid);
        ValidateErrorFreeResponse(postRegisterViewResponse);
        Oauth.StartLoggingAPISample(Oauth.MessageGetViewState);
        currentViewState = Oauth.GetViewStateByCid(cid);
        ValidateErrorFreeResponse(currentViewState);
        assertEquals("validate view state after post register view", postRegisterViewResponse, currentViewState);

        Oauth.StartLoggingAPISample(Oauth.MessagePostRegisterUser);
        String userName = RandomHelper.randomAlphabetic(15);
        String email = RandomHelper.randomEmail();
        String postRegisterUserResponse = Oauth.PostRegisterUser(cid, userName, email);
        ValidateErrorFreeResponse(postRegisterUserResponse);

        HttpclientHelper.ResetHttpClient();
        cid = Oauth.GetLoginCid();
        currentViewState = Oauth.GetViewStateByCid(cid);
        ValidateErrorFreeResponse(currentViewState);
        String loginResponseLink = Oauth.UserLogin(cid, userName, null);
        String idToken = Oauth.GetLoginUserIdToken(loginResponseLink);
        Oauth.Logout(idToken);
    }

    @Property(environment = "release")
    @Test(groups = "dailies")
    public void RegisterWithoutEmailVerification() throws Exception {
        Oauth.StartLoggingAPISample(Oauth.MessageGetLoginCid);
        String cid = Oauth.GetRegistrationCid();

        Oauth.StartLoggingAPISample(Oauth.MessageGetViewState);
        String currentViewState = Oauth.GetViewStateByCid(cid);
        ValidateErrorFreeResponse(currentViewState);
        assertEquals("validate current view state is login", true, currentViewState.contains("\"view\" : \"login\""));

        Oauth.StartLoggingAPISample(Oauth.MessagePostViewRegister);
        String postRegisterViewResponse = Oauth.PostViewRegisterByCid(cid);
        ValidateErrorFreeResponse(postRegisterViewResponse);
        Oauth.StartLoggingAPISample(Oauth.MessageGetViewState);
        currentViewState = Oauth.GetViewStateByCid(cid);
        ValidateErrorFreeResponse(currentViewState);
        assertEquals("validate view state after post register view", postRegisterViewResponse, currentViewState);

        Oauth.StartLoggingAPISample(Oauth.MessagePostRegisterUser);
        //String userName = "allEnvLoginUser";
        //String email = "silkcloudtest+allEnvLoginUser@gmail.com";
        String userName = RandomHelper.randomAlphabetic(15);
        String email = RandomHelper.randomEmail();
        String postRegisterUserResponse = Oauth.PostRegisterUser(cid, userName, email, true);
        ValidateErrorFreeResponse(postRegisterUserResponse);

        String loginState = Oauth.GetLoginStateAfterRegisterUser(cid);
        String authCode = Oauth.SSO2GetAuthCode(loginState);
        String accessToken = Oauth.GetAccessToken(authCode);
        TokenInfo tokenInfo = Oauth.GetTokenInfo(accessToken);
        assertEquals("validate token->client is correct", Oauth.DefaultClientId, tokenInfo.getClientId());
        assertEquals("validate token->scopes is correct", Oauth.DefaultClientScopes, tokenInfo.getScopes());
        User storedUser = Identity.UserGetByUserId(tokenInfo.getSub());
        Identity.UserPersonalInfoPost(storedUser.getId(), IdentityModel.DefaultUserPersonalInfoAddress());
        Identity.UserPersonalInfoPost(storedUser.getId(), IdentityModel.DefaultUserPersonalInfoDob());
        Map<String, UserVAT> vatMap = new HashMap<>();
        vatMap.put("validVAT", IdentityModel.DefaultUserVat());
        storedUser.setVat(vatMap);
        Identity.UserPut(storedUser);
    }

    @Property(environment = "release")
    @Test(groups = "dailies")
    public void loginExistingUser() throws Exception {
        String cid = Oauth.GetLoginCid();
        String currentViewState = Oauth.GetViewStateByCid(cid);
        ValidateErrorFreeResponse(currentViewState);
        //String loginResponseLink =
        Oauth.UserLogin(cid, "allEnvLoginUser", Oauth.DefaultUserPwd);
        //Oauth.UserLogin(cid, RandomHelper.randomAlphabetic(10), "Welcome123");
        //String idToken = Oauth.GetLoginUserIdToken(loginResponseLink);
        //Oauth.Logout(idToken);
    }

    @Test(groups = "dailies")
    public void resetPassword() throws Exception {
        Oauth.StartLoggingAPISample(Oauth.MessageGetLoginCid);
        String cid = Oauth.GetRegistrationCid();

        Oauth.StartLoggingAPISample(Oauth.MessageGetViewState);
        String currentViewState = Oauth.GetViewStateByCid(cid);
        ValidateErrorFreeResponse(currentViewState);
        assertEquals("validate current view state is login", true, currentViewState.contains("\"view\" : \"login\""));

        Oauth.StartLoggingAPISample(Oauth.MessagePostViewRegister);
        String postRegisterViewResponse = Oauth.PostViewRegisterByCid(cid);
        ValidateErrorFreeResponse(postRegisterViewResponse);
        Oauth.StartLoggingAPISample(Oauth.MessageGetViewState);
        currentViewState = Oauth.GetViewStateByCid(cid);
        ValidateErrorFreeResponse(currentViewState);
        assertEquals("validate view state after post register view", postRegisterViewResponse, currentViewState);

        Oauth.StartLoggingAPISample(Oauth.MessagePostRegisterUser);
        String userName = RandomHelper.randomAlphabetic(15);
        String email = RandomHelper.randomEmail();
        String postRegisterUserResponse = Oauth.PostRegisterUser(cid, userName, email);
        ValidateErrorFreeResponse(postRegisterUserResponse);

        HttpclientHelper.ResetHttpClient();
        String newPassword = "ASDFqwer1234";
        // set identity authorization header
        Oauth.GetAccessToken(Oauth.GetAuthCodeAfterRegisterUser(cid));
        UserPersonalInfo upi = Identity.UserPersonalInfoGetByUserEmail(email);
        String resetPasswordLink = Oauth.PostResetPassword(
                Identity.GetHexLongId(upi.getUserId().getValue()), userName, null);
        String resetPasswordCid = Oauth.GetResetPasswordCid(resetPasswordLink);
        Oauth.GetResetPasswordView(resetPasswordCid);
        Oauth.PostResetPasswordWithNewPassword(resetPasswordCid, newPassword);

        HttpclientHelper.ResetHttpClient();
        cid = Oauth.GetLoginCid();
        currentViewState = Oauth.GetViewStateByCid(cid);
        ValidateErrorFreeResponse(currentViewState);
        String loginResponseLink = Oauth.UserLogin(cid, userName, newPassword);
        String idToken = Oauth.GetLoginUserIdToken(loginResponseLink);
        Oauth.Logout(idToken);
    }

    private static void ValidateErrorFreeResponse(String responseString) throws Exception {
        assertEquals("validate no errors in response \r\n" + responseString,
                true, responseString.contains("\"errors\" : [ ]"));
    }
}
