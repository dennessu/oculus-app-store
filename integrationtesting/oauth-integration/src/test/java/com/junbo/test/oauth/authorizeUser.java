/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.oauth;

import com.junbo.common.error.Error;
import com.junbo.common.error.ErrorDetail;
import com.junbo.identity.spec.v1.model.User;
import com.junbo.identity.spec.v1.model.UserLoginName;
import com.junbo.identity.spec.v1.model.UserPersonalInfo;
import com.junbo.identity.spec.v1.model.UserVAT;
import com.junbo.identity.spec.v1.model.migration.OculusInput;
import com.junbo.identity.spec.v1.model.migration.OculusOutput;
import com.junbo.identity.spec.v1.model.migration.UsernameMailBlocker;
import com.junbo.oauth.spec.model.TokenInfo;
import com.junbo.test.common.*;
import com.junbo.test.common.libs.LogHelper;
import com.junbo.test.common.property.Property;
import com.junbo.test.identity.Identity;
import com.junbo.test.identity.IdentityModel;
import org.apache.http.Header;
import org.apache.http.NameValuePair;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.message.BasicNameValuePair;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.InputStreamReader;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author dw
 */
public class authorizeUser {

    @BeforeMethod(alwaysRun = true)
    public void setup() throws Exception {
        HttpclientHelper.CreateHttpClient();
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
        Validator.Validate("validate current view state is login", true,
                currentViewState.contains("\"view\" : \"login\"") || currentViewState.contains("\"view\":\"login\""));

        Oauth.StartLoggingAPISample(Oauth.MessagePostViewRegister);
        String postRegisterViewResponse = Oauth.PostViewRegisterByCid(cid);
        ValidateErrorFreeResponse(postRegisterViewResponse);
        Oauth.StartLoggingAPISample(Oauth.MessageGetViewState);
        currentViewState = Oauth.GetViewStateByCid(cid);
        ValidateErrorFreeResponse(currentViewState);
        Validator.Validate("validate view state after post register view", postRegisterViewResponse, currentViewState);

        Oauth.StartLoggingAPISample(Oauth.MessagePostRegisterUser);
        String userName = RandomHelper.randomAlphabetic(15);
        String email = RandomHelper.randomEmail();
        Oauth.PostRegisterUser(cid, userName, email);

        Oauth.StartLoggingAPISample(Oauth.MessageGetAuthCodeByCidAfterRegisterUser);
        String authCode = Oauth.GetAuthCodeAfterRegisterUser(cid);
        Oauth.StartLoggingAPISample(Oauth.MessageGetAccessTokenByAuthCode);
        String accessToken = Oauth.GetAccessToken(authCode);
        Oauth.StartLoggingAPISample(Oauth.MessageGetTokenInfoByAccessToken);
        TokenInfo tokenInfo = Oauth.GetTokenInfo(accessToken);
        Validator.Validate("validate token->client is correct", Oauth.DefaultClientId, tokenInfo.getClientId());
        Validator.Validate("validate token->scopes is correct", Oauth.DefaultClientScopes, tokenInfo.getScopes());
        User storedUser = Identity.UserGetByUserId(tokenInfo.getSub());
        // https://oculus.atlassian.net/browse/SER-640
        assert storedUser.getNickName().equalsIgnoreCase(userName);
        UserPersonalInfo storedUPI = Identity.UserPersonalInfoGetByUserPersonalInfoId(storedUser.getUsername());
        Validator.Validate("validate token->binded user is correct", userName,
                ((UserLoginName) JsonHelper.JsonNodeToObject(storedUPI.getValue(), UserLoginName.class)).getUserName());
    }

    @Property(environment = "release")
    @Test(groups = "ppe/prod")
    public void authorizeUserRoute() throws Exception {
        if (Oauth.DefaultOauthSecondaryEndpoint == null) return;
        Oauth.StartLoggingAPISample(Oauth.MessageGetLoginCid);
        String cid = Oauth.GetRegistrationCid();

        Oauth.StartLoggingAPISample(Oauth.MessageGetViewState);
        String currentViewState = Oauth.GetViewStateByCid(cid);
        ValidateErrorFreeResponse(currentViewState);
        Validator.Validate("validate current view state is login", true,
                currentViewState.contains("\"view\" : \"login\"") || currentViewState.contains("\"view\":\"login\""));

        Oauth.StartLoggingAPISample(Oauth.MessagePostViewRegister);
        String postRegisterViewResponse = Oauth.PostViewRegisterByCid(cid);
        ValidateErrorFreeResponse(postRegisterViewResponse);
        Oauth.StartLoggingAPISample(Oauth.MessageGetViewState);
        currentViewState = Oauth.GetViewStateByCid(cid);
        ValidateErrorFreeResponse(currentViewState);
        Validator.Validate("validate view state after post register view", postRegisterViewResponse, currentViewState);

        Oauth.StartLoggingAPISample(Oauth.MessagePostRegisterUser);
        String userName = RandomHelper.randomAlphabetic(15);
        String email = RandomHelper.randomEmail();
        Oauth.PostRegisterUser(cid, userName, email);

        Oauth.StartLoggingAPISample(Oauth.MessageGetAuthCodeByCidAfterRegisterUser);
        String authCode = Oauth.GetAuthCodeAfterRegisterUser(cid);
        Oauth.StartLoggingAPISample(Oauth.MessageGetAccessTokenByAuthCode);
        String accessToken = Oauth.GetAccessToken(authCode, Oauth.DefaultOauthSecondaryEndpoint + "/oauth2/token");
        Oauth.StartLoggingAPISample(Oauth.MessageGetTokenInfoByAccessToken);
        TokenInfo tokenInfo = Oauth.GetTokenInfo(accessToken, Oauth.DefaultOauthSecondaryEndpoint + "/oauth2/tokeninfo");
        Validator.Validate("validate token->client is correct", Oauth.DefaultClientId, tokenInfo.getClientId());
        Validator.Validate("validate token->scopes is correct", Oauth.DefaultClientScopes, tokenInfo.getScopes());

    }

    @Test(groups = "bvt")
    public void userSSO() throws Exception {
        Oauth.StartLoggingAPISample(Oauth.MessageGetLoginCid);
        String cid = Oauth.GetRegistrationCid();

        String currentViewState = Oauth.GetViewStateByCid(cid);
        ValidateErrorFreeResponse(currentViewState);
        Validator.Validate("validate current view state is login", true,
                currentViewState.contains("\"view\" : \"login\"") || currentViewState.contains("\"view\":\"login\""));

        String postRegisterViewResponse = Oauth.PostViewRegisterByCid(cid);
        ValidateErrorFreeResponse(postRegisterViewResponse);
        currentViewState = Oauth.GetViewStateByCid(cid);
        ValidateErrorFreeResponse(currentViewState);
        Validator.Validate("validate view state after post register view", postRegisterViewResponse, currentViewState);

        String userName = RandomHelper.randomAlphabetic(15);
        String email = RandomHelper.randomEmail();
        Oauth.PostRegisterUser(cid, userName, email);

        Oauth.StartLoggingAPISample(Oauth.MessageGetLoginStateByCidAfterRegisterUser);
        String loginState = Oauth.GetLoginStateAfterRegisterUser(cid);
        Oauth.StartLoggingAPISample(Oauth.MessageGetAuthCodeByLoginState);
        String authCode = Oauth.SSO2GetAuthCode(loginState);
        String accessToken = Oauth.GetAccessToken(authCode);
        TokenInfo tokenInfo = Oauth.GetTokenInfo(accessToken);
        Validator.Validate("validate token->client is correct", Oauth.DefaultClientId, tokenInfo.getClientId());
        Validator.Validate("validate token->scopes is correct", Oauth.DefaultClientScopes, tokenInfo.getScopes());
        User storedUser = Identity.UserGetByUserId(tokenInfo.getSub());
        UserPersonalInfo storedUPI = Identity.UserPersonalInfoGetByUserPersonalInfoId(storedUser.getUsername());
        Validator.Validate("validate token->binded user is correct", userName,
                ((UserLoginName) JsonHelper.JsonNodeToObject(storedUPI.getValue(), UserLoginName.class)).getUserName());
    }

    @Test(groups = "dailies")
    public void migrateAndLogin() throws Exception {
        OculusInput input = IdentityModel.DefaultOculusInput();
        input.setPassword("1:8UFAbK26VrPLL75jE9P2:PRo4D7r23hrfv3FBxqBv:b87637b9ec5abd43db01d7a299612a49550230a813239fb3e28eec2a88c0df67");
        input.setStatus(IdentityModel.MigrateUserStatus.ACTIVE.name());
        input.setForceResetPassword(false);
        Identity.GetHttpAuthorizationHeaderForMigration();
        OculusOutput output = Identity.ImportMigrationData(input);

        HttpclientHelper.ResetHttpClient();
        String cid = Oauth.GetLoginCid();
        String currentViewState = Oauth.GetViewStateByCid(cid);
        ValidateErrorFreeResponse(currentViewState);
        String loginResponseLink = Oauth.UserLogin(cid, input.getEmail(), "radiant555");
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
        Validator.Validate("validate current view state is login", true,
                currentViewState.contains("\"view\" : \"login\"") || currentViewState.contains("\"view\":\"login\""));

        Oauth.StartLoggingAPISample(Oauth.MessagePostViewRegister);
        String postRegisterViewResponse = Oauth.PostViewRegisterByCid(cid);
        ValidateErrorFreeResponse(postRegisterViewResponse);
        Oauth.StartLoggingAPISample(Oauth.MessageGetViewState);
        currentViewState = Oauth.GetViewStateByCid(cid);
        ValidateErrorFreeResponse(currentViewState);
        Validator.Validate("validate view state after post register view", postRegisterViewResponse, currentViewState);

        Oauth.StartLoggingAPISample(Oauth.MessagePostRegisterUser);
        String userName = RandomHelper.randomAlphabetic(15);
        String email = RandomHelper.randomEmail();
        Oauth.PostRegisterUser(cid, userName, email);

        HttpclientHelper.ResetHttpClient();
        cid = Oauth.GetLoginCid();
        currentViewState = Oauth.GetViewStateByCid(cid);
        ValidateErrorFreeResponse(currentViewState);
        String loginResponseLink = Oauth.UserLogin(cid, email, null);
        String idToken = Oauth.GetLoginUser(loginResponseLink).get(Oauth.DefaultFNIdToken);
        Oauth.Logout(idToken);
    }

    @Property(environment = "release")
    @Test(groups = "dailies")
    public void registerWithUserPii() throws Exception {
        Oauth.StartLoggingAPISample(Oauth.MessageGetLoginCid);
        String cid = Oauth.GetRegistrationCid();

        Oauth.StartLoggingAPISample(Oauth.MessageGetViewState);
        String currentViewState = Oauth.GetViewStateByCid(cid);
        ValidateErrorFreeResponse(currentViewState);
        Validator.Validate("validate current view state is login", true,
                currentViewState.contains("\"view\" : \"login\"") || currentViewState.contains("\"view\":\"login\""));

        Oauth.StartLoggingAPISample(Oauth.MessagePostViewRegister);
        String postRegisterViewResponse = Oauth.PostViewRegisterByCid(cid);
        ValidateErrorFreeResponse(postRegisterViewResponse);
        Oauth.StartLoggingAPISample(Oauth.MessageGetViewState);
        currentViewState = Oauth.GetViewStateByCid(cid);
        ValidateErrorFreeResponse(currentViewState);
        Validator.Validate("validate view state after post register view", postRegisterViewResponse, currentViewState);

        Oauth.StartLoggingAPISample(Oauth.MessagePostRegisterUser);
        //String userName = "allEnvLoginUser";
        //String email = "silkcloudtest+allEnvLoginUser@gmail.com";
        String userName = RandomHelper.randomAlphabetic(15);
        String email = RandomHelper.randomEmail();
        Oauth.PostRegisterUser(cid, userName, email);

        HttpclientHelper.ResetHttpClient();
        cid = Oauth.GetLoginCid();
        currentViewState = Oauth.GetViewStateByCid(cid);
        ValidateErrorFreeResponse(currentViewState);
        String loginResponseLink = Oauth.UserLogin(cid, email, null);
        String accessToken = Oauth.GetLoginUser(loginResponseLink).get(Oauth.DefaultFNAccessToken);
        TokenInfo tokenInfo = Oauth.GetTokenInfo(accessToken);
        Validator.Validate("validate token->client is correct", Oauth.DefaultClientId, tokenInfo.getClientId());
        Validator.Validate("validate token->scopes is correct", Oauth.DefaultLoginScopes, tokenInfo.getScopes());
        Identity.SetHttpAuthorizationHeader(accessToken);
        User storedUser = Identity.UserGetByUserId(tokenInfo.getSub());
        Identity.UserPersonalInfoPost(storedUser.getId(), IdentityModel.DefaultUserPersonalInfoAddress());
        Identity.UserPersonalInfoPost(storedUser.getId(), IdentityModel.DefaultUserPersonalInfoDob());
        Map<String, UserVAT> vatMap = new HashMap<>();
        vatMap.put(IdentityModel.DefaultUserVat().getVatNumber().substring(0, 2), IdentityModel.DefaultUserVat());
        storedUser = Identity.UserGetByUserId(tokenInfo.getSub());
        storedUser.setNickName(RandomHelper.randomName());
        storedUser.setVat(vatMap);
        Identity.UserPut(storedUser);
    }

    @Property(environment = "release")
    @Test(groups = "int/ppe/prod/sewer")
    public void loginExistingUser() throws Exception {
        if (Oauth.DefaultOauthEndpoint.contains("http://localhost:8080")) return;
        String cid = Oauth.GetLoginCid();
        String currentViewState = Oauth.GetViewStateByCid(cid);
        ValidateErrorFreeResponse(currentViewState);
        String loginResponseLink = Oauth.UserLogin(cid, "silkcloudtest+allEnvLoginUser@gmail.com", Oauth.DefaultUserPwd);
        String accessToken = Oauth.GetLoginUser(loginResponseLink).get(Oauth.DefaultFNAccessToken);
        TokenInfo tokenInfo = Oauth.GetTokenInfo(accessToken);
        Validator.Validate("validate token->client is correct", Oauth.DefaultClientId, tokenInfo.getClientId());
        Validator.Validate("validate token->scopes is correct", Oauth.DefaultLoginScopes, tokenInfo.getScopes());
        //Oauth.UserLogin(cid, RandomHelper.randomAlphabetic(10), "Welcome123");
        //String idToken = Oauth.GetLoginUser(loginResponseLink);
        //Oauth.Logout(idToken);
    }

    @Property(environment = "release")
    @Test(groups = "ppe/prod")
    public void accessTokenRoute() throws Exception {
        if (Oauth.DefaultOauthEndpoint.contains("http://localhost:8080")) return;
        String secondaryDcEndpoint = ConfigHelper.getSetting("secondaryDcEndpoint");
        if (secondaryDcEndpoint == null) return;
        String cid = Oauth.GetLoginCid();
        String currentViewState = Oauth.GetViewStateByCid(cid);
        ValidateErrorFreeResponse(currentViewState);
        String loginResponseLink = Oauth.UserLogin(cid, "silkcloudtest+allEnvLoginUser@gmail.com", Oauth.DefaultUserPwd);
        String accessToken = Oauth.GetLoginUser(loginResponseLink).get(Oauth.DefaultFNAccessToken);
        TokenInfo tokenInfo = Oauth.GetTokenInfo(accessToken, secondaryDcEndpoint + "/oauth2/tokeninfo");
        Validator.Validate("validate getting token from another dc", true, tokenInfo != null);
    }

    @Property(environment = "release")
    @Test(groups = "ppe/prod")
    public void verifyEmailRoute() throws Exception {
        if (Oauth.DefaultOauthSecondaryEndpoint == null) return;
        Oauth.StartLoggingAPISample(Oauth.MessageGetLoginCid);
        String cid = Oauth.GetRegistrationCid();

        Oauth.StartLoggingAPISample(Oauth.MessageGetViewState);
        String currentViewState = Oauth.GetViewStateByCid(cid);
        ValidateErrorFreeResponse(currentViewState);
        Validator.Validate("validate current view state is login", true,
                currentViewState.contains("\"view\" : \"login\"") || currentViewState.contains("\"view\":\"login\""));

        Oauth.StartLoggingAPISample(Oauth.MessagePostViewRegister);
        String postRegisterViewResponse = Oauth.PostViewRegisterByCid(cid);
        ValidateErrorFreeResponse(postRegisterViewResponse);
        Oauth.StartLoggingAPISample(Oauth.MessageGetViewState);
        currentViewState = Oauth.GetViewStateByCid(cid);
        ValidateErrorFreeResponse(currentViewState);
        Validator.Validate("validate view state after post register view", postRegisterViewResponse, currentViewState);

        Oauth.StartLoggingAPISample(Oauth.MessagePostRegisterUser);
        String userName = RandomHelper.randomAlphabetic(15);
        String email = RandomHelper.randomEmail();
        Oauth.PostRegisterUser(cid, userName, email, null, false, false);
        Oauth.VerifyEmail(cid, Oauth.DefaultOauthSecondaryEndpoint);

    }

    @Property
    @Test(groups = "ppe/prod")
    public void resetPasswordRoute() throws Exception {
        if (Oauth.DefaultOauthSecondaryEndpoint == null) return;
        Oauth.StartLoggingAPISample(Oauth.MessageGetLoginCid);
        String cid = Oauth.GetRegistrationCid();

        Oauth.StartLoggingAPISample(Oauth.MessageGetViewState);
        String currentViewState = Oauth.GetViewStateByCid(cid);
        ValidateErrorFreeResponse(currentViewState);
        Validator.Validate("validate current view state is login", true,
                currentViewState.contains("\"view\" : \"login\"") || currentViewState.contains("\"view\":\"login\""));

        Oauth.StartLoggingAPISample(Oauth.MessagePostViewRegister);
        String postRegisterViewResponse = Oauth.PostViewRegisterByCid(cid);
        ValidateErrorFreeResponse(postRegisterViewResponse);
        Oauth.StartLoggingAPISample(Oauth.MessageGetViewState);
        currentViewState = Oauth.GetViewStateByCid(cid);
        ValidateErrorFreeResponse(currentViewState);
        Validator.Validate("validate view state after post register view", postRegisterViewResponse, currentViewState);

        Oauth.StartLoggingAPISample(Oauth.MessagePostRegisterUser);
        String userName = RandomHelper.randomAlphabetic(15);
        String email = RandomHelper.randomEmail();
        Oauth.PostRegisterUser(cid, userName, email);

        HttpclientHelper.ResetHttpClient();
        String newPassword = "ASDFqwer1234";
        // set identity authorization header
        Oauth.GetAccessToken(Oauth.GetAuthCodeAfterRegisterUser(cid));
        UserPersonalInfo upi = Identity.UserPersonalInfoGetByUserEmail(email);
        String resetPasswordLink = Oauth.PostResetPassword(
                Identity.GetHexLongId(upi.getUserId().getValue()), userName, null);
        List<String> resetPwdLinks = Oauth.GetResetPasswordLinks(userName, email, null, false);
        resetPasswordLink = resetPasswordLink.contains("reset") ? resetPasswordLink : resetPwdLinks.get(0);
        LogHelper logHelper = new LogHelper(authorizeUser.class);
        logHelper.logInfo("reset password link: " + resetPasswordLink);
        String resetPasswordCid = Oauth.GetResetPasswordCid(resetPasswordLink);
        Oauth.GetResetPasswordView(resetPasswordCid);
        Oauth.PostResetPasswordWithNewPassword(resetPasswordCid, newPassword);

        HttpclientHelper.ResetHttpClient();
        cid = Oauth.GetLoginCid();
        currentViewState = Oauth.GetViewStateByCid(cid);
        ValidateErrorFreeResponse(currentViewState);
        String loginResponseLink = Oauth.UserLogin(cid, email, newPassword, Oauth.DefaultOauthSecondaryEndpoint + "/oauth2/authorize");
        String idToken = Oauth.GetLoginUser(loginResponseLink, Oauth.DefaultOauthSecondaryEndpoint).get(Oauth.DefaultFNIdToken);
        Oauth.Logout(idToken);

    }

    @Test(groups = "dailies")
    public void resetPassword() throws Exception {
        Oauth.StartLoggingAPISample(Oauth.MessageGetLoginCid);
        String cid = Oauth.GetRegistrationCid();

        Oauth.StartLoggingAPISample(Oauth.MessageGetViewState);
        String currentViewState = Oauth.GetViewStateByCid(cid);
        ValidateErrorFreeResponse(currentViewState);
        Validator.Validate("validate current view state is login", true,
                currentViewState.contains("\"view\" : \"login\"") || currentViewState.contains("\"view\":\"login\""));

        Oauth.StartLoggingAPISample(Oauth.MessagePostViewRegister);
        String postRegisterViewResponse = Oauth.PostViewRegisterByCid(cid);
        ValidateErrorFreeResponse(postRegisterViewResponse);
        Oauth.StartLoggingAPISample(Oauth.MessageGetViewState);
        currentViewState = Oauth.GetViewStateByCid(cid);
        ValidateErrorFreeResponse(currentViewState);
        Validator.Validate("validate view state after post register view", postRegisterViewResponse, currentViewState);

        Oauth.StartLoggingAPISample(Oauth.MessagePostRegisterUser);
        String userName = RandomHelper.randomAlphabetic(15);
        String email = RandomHelper.randomEmail();
        Oauth.PostRegisterUser(cid, userName, email);

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
        String loginResponseLink = Oauth.UserLogin(cid, email, newPassword);
        String idToken = Oauth.GetLoginUser(loginResponseLink).get(Oauth.DefaultFNIdToken);
        Oauth.Logout(idToken);
    }

    @Test(groups = "dailies")
    public void resetPasswordLinkUniqueAvailability() throws Exception {
        Oauth.StartLoggingAPISample(Oauth.MessageGetLoginCid);
        String cid = Oauth.GetRegistrationCid();

        Oauth.StartLoggingAPISample(Oauth.MessageGetViewState);
        String currentViewState = Oauth.GetViewStateByCid(cid);
        ValidateErrorFreeResponse(currentViewState);
        Validator.Validate("validate current view state is login", true,
                currentViewState.contains("\"view\" : \"login\"") || currentViewState.contains("\"view\":\"login\""));

        Oauth.StartLoggingAPISample(Oauth.MessagePostViewRegister);
        String postRegisterViewResponse = Oauth.PostViewRegisterByCid(cid);
        ValidateErrorFreeResponse(postRegisterViewResponse);
        Oauth.StartLoggingAPISample(Oauth.MessageGetViewState);
        currentViewState = Oauth.GetViewStateByCid(cid);
        ValidateErrorFreeResponse(currentViewState);
        Validator.Validate("validate view state after post register view", postRegisterViewResponse, currentViewState);

        Oauth.StartLoggingAPISample(Oauth.MessagePostRegisterUser);
        String userName = RandomHelper.randomAlphabetic(15);
        String email = RandomHelper.randomEmail();
        Oauth.PostRegisterUser(cid, userName, email);

        HttpclientHelper.ResetHttpClient();
        // set identity authorization header
        Oauth.GetAccessToken(Oauth.GetAuthCodeAfterRegisterUser(cid), Oauth.DefaultTokenURI);
        UserPersonalInfo upi = Identity.UserPersonalInfoGetByUserEmail(email);
        Oauth.PostResetPassword(Identity.GetHexLongId(upi.getUserId().getValue()), userName, null);
        Oauth.PostResetPassword(Identity.GetHexLongId(upi.getUserId().getValue()), userName, null);
        Oauth.PostAccessToken("smoketest", "smoketest");
        List<String> resetPwdLinks = Oauth.GetResetPasswordLinks(userName, email, null, false);
        String resetPasswordCid = Oauth.GetResetPasswordCid(resetPwdLinks.get(1));
        String newPassword = "ASDFqwer1234";
        // set identity authorization header
        Oauth.GetResetPasswordView(resetPasswordCid);
        Oauth.PostResetPasswordWithNewPassword(resetPasswordCid, newPassword);
    }

    @Test(groups = "dailies")
    public void verifyEmailTwiceEnsureTheSecondFail() throws Exception {
        Oauth.StartLoggingAPISample(Oauth.MessageGetLoginCid);
        String cid = Oauth.GetRegistrationCid();

        Oauth.StartLoggingAPISample(Oauth.MessageGetViewState);
        String currentViewState = Oauth.GetViewStateByCid(cid);
        ValidateErrorFreeResponse(currentViewState);
        Validator.Validate("validate current view state is login", true,
                currentViewState.contains("\"view\" : \"login\"") || currentViewState.contains("\"view\":\"login\""));

        Oauth.StartLoggingAPISample(Oauth.MessagePostViewRegister);
        String postRegisterViewResponse = Oauth.PostViewRegisterByCid(cid);
        ValidateErrorFreeResponse(postRegisterViewResponse);
        Oauth.StartLoggingAPISample(Oauth.MessageGetViewState);
        currentViewState = Oauth.GetViewStateByCid(cid);
        ValidateErrorFreeResponse(currentViewState);
        Validator.Validate("validate view state after post register view", postRegisterViewResponse, currentViewState);

        Oauth.StartLoggingAPISample(Oauth.MessagePostRegisterUser);
        String userName = RandomHelper.randomAlphabetic(15);
        String email = RandomHelper.randomEmail();
        Oauth.PostRegisterUser(cid, userName, email, null, true, true);
    }

    @Test(groups = "dailies")
    public void resetPasswordLinkPrivilege() throws Exception {
        // if (Oauth.DefaultOauthSecondaryEndpoint == null) return;
        Oauth.StartLoggingAPISample(Oauth.MessageGetLoginCid);
        String cid = Oauth.GetRegistrationCid();

        Oauth.StartLoggingAPISample(Oauth.MessageGetViewState);
        String currentViewState = Oauth.GetViewStateByCid(cid);
        ValidateErrorFreeResponse(currentViewState);
        Validator.Validate("validate current view state is login", true,
                currentViewState.contains("\"view\" : \"login\"") || currentViewState.contains("\"view\":\"login\""));

        Oauth.StartLoggingAPISample(Oauth.MessagePostViewRegister);
        String postRegisterViewResponse = Oauth.PostViewRegisterByCid(cid);
        ValidateErrorFreeResponse(postRegisterViewResponse);
        Oauth.StartLoggingAPISample(Oauth.MessageGetViewState);
        currentViewState = Oauth.GetViewStateByCid(cid);
        ValidateErrorFreeResponse(currentViewState);
        Validator.Validate("validate view state after post register view", postRegisterViewResponse, currentViewState);

        Oauth.StartLoggingAPISample(Oauth.MessagePostRegisterUser);
        String userName = RandomHelper.randomAlphabetic(15);
        String email = RandomHelper.randomEmail();
        Oauth.PostRegisterUser(cid, userName, email);

        HttpclientHelper.ResetHttpClient();
        String newPassword = "ASDFqwer1234";
        // set identity authorization header
        Oauth.GetAccessToken(Oauth.GetAuthCodeAfterRegisterUser(cid), Oauth.DefaultTokenURI, "client");
        UserPersonalInfo upi = Identity.UserPersonalInfoGetByUserEmail(email);
        Oauth.PostResetPassword(Identity.GetHexLongId(upi.getUserId().getValue()), userName, null);
        List<String> resetPwdLinks = Oauth.GetResetPasswordLinks(userName, email, null, true);
    }

    @Test(groups = "dailies")
    public void registerBlockedUsernameOtherEmail() throws Exception {
        String userName = RandomHelper.randomAlphabetic(15);
        String email = RandomHelper.randomEmail();

        UsernameMailBlocker usernameMailBlocker = new UsernameMailBlocker();
        usernameMailBlocker.setUsername(userName);
        usernameMailBlocker.setEmail(email);
        Identity.GetHttpAuthorizationHeaderForMigration();
        Identity.UsernameMailBlockerPost(usernameMailBlocker);

        HttpclientHelper.ResetHttpClient();

        Oauth.StartLoggingAPISample(Oauth.MessageGetLoginCid);
        String cid = Oauth.GetRegistrationCid();

        Oauth.StartLoggingAPISample(Oauth.MessageGetViewState);
        String currentViewState = Oauth.GetViewStateByCid(cid);
        ValidateErrorFreeResponse(currentViewState);
        Validator.Validate("validate current view state is login", true,
                currentViewState.contains("\"view\" : \"login\"") || currentViewState.contains("\"view\":\"login\""));

        Oauth.StartLoggingAPISample(Oauth.MessagePostViewRegister);
        String postRegisterViewResponse = Oauth.PostViewRegisterByCid(cid);
        ValidateErrorFreeResponse(postRegisterViewResponse);
        Oauth.StartLoggingAPISample(Oauth.MessageGetViewState);
        currentViewState = Oauth.GetViewStateByCid(cid);
        ValidateErrorFreeResponse(currentViewState);
        Validator.Validate("validate view state after post register view", postRegisterViewResponse, currentViewState);

        Oauth.StartLoggingAPISample(Oauth.MessagePostRegisterUser);
        List<ErrorDetail> errorDetails = new ArrayList<>();
        ErrorDetail errorDetail = new ErrorDetail();
        errorDetail.setField("username");
        errorDetail.setReason("Field value is invalid. username and email are occupied");
        errorDetails.add(0, errorDetail);
        Error error = new Error();
        error.setMessage("Input Error");
        error.setDetails(errorDetails);
        Oauth.PostRegisterUser(cid, userName, RandomHelper.randomEmail(), error);
    }

    @Test(groups = "dailies")
    public void registerBlockedEmailOtherUsername() throws Exception {
        String userName = RandomHelper.randomAlphabetic(15);
        String email = RandomHelper.randomEmail();

        UsernameMailBlocker usernameMailBlocker = new UsernameMailBlocker();
        usernameMailBlocker.setUsername(userName);
        usernameMailBlocker.setEmail(email);
        Identity.GetHttpAuthorizationHeaderForMigration();
        Identity.UsernameMailBlockerPost(usernameMailBlocker);

        HttpclientHelper.ResetHttpClient();

        Oauth.StartLoggingAPISample(Oauth.MessageGetLoginCid);
        String cid = Oauth.GetRegistrationCid();

        Oauth.StartLoggingAPISample(Oauth.MessageGetViewState);
        String currentViewState = Oauth.GetViewStateByCid(cid);
        ValidateErrorFreeResponse(currentViewState);
        Validator.Validate("validate current view state is login", true,
                currentViewState.contains("\"view\" : \"login\"") || currentViewState.contains("\"view\":\"login\""));

        Oauth.StartLoggingAPISample(Oauth.MessagePostViewRegister);
        String postRegisterViewResponse = Oauth.PostViewRegisterByCid(cid);
        ValidateErrorFreeResponse(postRegisterViewResponse);
        Oauth.StartLoggingAPISample(Oauth.MessageGetViewState);
        currentViewState = Oauth.GetViewStateByCid(cid);
        ValidateErrorFreeResponse(currentViewState);
        Validator.Validate("validate view state after post register view", postRegisterViewResponse, currentViewState);

        Oauth.StartLoggingAPISample(Oauth.MessagePostRegisterUser);
        Oauth.PostRegisterUser(cid, RandomHelper.randomAlphabetic(15), email);
    }

    @Test(groups = "dailies")
    public void registerBlockedUsernameEmail() throws Exception {
        String userName = RandomHelper.randomAlphabetic(15);
        String email = RandomHelper.randomEmail();

        UsernameMailBlocker usernameMailBlocker = new UsernameMailBlocker();
        usernameMailBlocker.setUsername(userName);
        usernameMailBlocker.setEmail(email);
        Identity.GetHttpAuthorizationHeaderForMigration();
        Identity.UsernameMailBlockerPost(usernameMailBlocker);

        HttpclientHelper.ResetHttpClient();

        Oauth.StartLoggingAPISample(Oauth.MessageGetLoginCid);
        String cid = Oauth.GetRegistrationCid();

        Oauth.StartLoggingAPISample(Oauth.MessageGetViewState);
        String currentViewState = Oauth.GetViewStateByCid(cid);
        ValidateErrorFreeResponse(currentViewState);
        Validator.Validate("validate current view state is login", true,
                currentViewState.contains("\"view\" : \"login\"") || currentViewState.contains("\"view\":\"login\""));

        Oauth.StartLoggingAPISample(Oauth.MessagePostViewRegister);
        String postRegisterViewResponse = Oauth.PostViewRegisterByCid(cid);
        ValidateErrorFreeResponse(postRegisterViewResponse);
        Oauth.StartLoggingAPISample(Oauth.MessageGetViewState);
        currentViewState = Oauth.GetViewStateByCid(cid);
        ValidateErrorFreeResponse(currentViewState);
        Validator.Validate("validate view state after post register view", postRegisterViewResponse, currentViewState);

        Oauth.StartLoggingAPISample(Oauth.MessagePostRegisterUser);
        Oauth.PostRegisterUser(cid, userName, email);
    }

    @Test(groups = "dailies")
    public void oculusInternalHeaderFunction() throws Exception {
        CloseableHttpResponse response = Oauth.OauthGet(Oauth.DefaultAuthorizeURI
                + "?client_id="
                + Oauth.DefaultClientId
                + "&response_type=code&scope=identity&redirect_uri="
                + Oauth.DefaultRedirectURI, null);
        Validator.Validate("response status is ok", 302, response.getStatusLine().getStatusCode());
        response.close();

        response = Oauth.OauthGet(Oauth.DefaultAuthorizeURI
                + "?client_id="
                + Oauth.DefaultClientId
                + "&response_type=code&scope=identity&redirect_uri="
                + Oauth.DefaultRedirectURI, null, false, false);
        Validator.Validate("response status is not ok", 403, response.getStatusLine().getStatusCode());
        Error error = JsonHelper.JsonDeserializer(new InputStreamReader(response.getEntity().getContent()), Error.class);
        Validator.Validate("failure reason is internal client",
                "This client is for internal use only", error.getDetails().get(0).getReason());
        response.close();

        response = Oauth.OauthGet(Oauth.DefaultAuthorizeURI
                + "?client_id="
                + Oauth.DefaultClientIdExt
                + "&response_type=code&scope=storeapi&redirect_uri="
                + Oauth.DefaultRedirectURI, null);
        Validator.Validate("response status is ok", 302, response.getStatusLine().getStatusCode());
        response.close();

        response = Oauth.OauthGet(Oauth.DefaultAuthorizeURI
                + "?client_id="
                + Oauth.DefaultClientIdExt
                + "&response_type=code&scope=storeapi&redirect_uri="
                + Oauth.DefaultRedirectURI, null, false, false);
        Validator.Validate("response status is ok", 302, response.getStatusLine().getStatusCode());
        response.close();
    }

    @Test(groups = "dailies")
    public void clientAndSecretMisMatch() throws Exception {
        List<NameValuePair> nvps = new ArrayList<NameValuePair>();
        nvps.add(new BasicNameValuePair("grant_type", "client_credentials"));
        nvps.add(new BasicNameValuePair("client_id", Oauth.DefaultClientId));
        nvps.add(new BasicNameValuePair("client_secret", Oauth.DefaultClientSecret));
        nvps.add(new BasicNameValuePair("scope", "identity"));
        CloseableHttpResponse response = Oauth.OauthPost(Oauth.DefaultOauthEndpoint + "/oauth2/token", nvps);
        Validator.Validate("response status is ok", response.getStatusLine().getStatusCode(), 200);
        response.close();

        nvps = new ArrayList<NameValuePair>();
        nvps.add(new BasicNameValuePair("grant_type", "client_credentials"));
        nvps.add(new BasicNameValuePair("client_id", Oauth.DefaultClientId));
        nvps.add(new BasicNameValuePair("client_secret", RandomHelper.randomAlphabetic(15)));
        nvps.add(new BasicNameValuePair("scope", "identity"));
        response = Oauth.OauthPost(Oauth.DefaultOauthEndpoint + "/oauth2/token", nvps);
        Validator.Validate("response status is ok", response.getStatusLine().getStatusCode(), 400);
        com.junbo.common.error.Error error = JsonHelper.JsonDeserializer(
                new InputStreamReader(response.getEntity().getContent()), Error.class);
        Validator.Validate("error detail field", "client_secret", error.getDetails().get(0).getField());
        Validator.Validate("error detail field", "Field value is invalid. *****", error.getDetails().get(0).getReason());
        response.close();

        String randomClient = RandomHelper.randomAlphabetic(15);
        nvps = new ArrayList<NameValuePair>();
        nvps.add(new BasicNameValuePair("grant_type", "client_credentials"));
        nvps.add(new BasicNameValuePair("client_id", randomClient));
        nvps.add(new BasicNameValuePair("client_secret", RandomHelper.randomAlphabetic(15)));
        nvps.add(new BasicNameValuePair("scope", "identity"));
        response = Oauth.OauthPost(Oauth.DefaultOauthEndpoint + "/oauth2/token", nvps);
        Validator.Validate("response status is ok", response.getStatusLine().getStatusCode(), 400);
        error = JsonHelper.JsonDeserializer(new InputStreamReader(response.getEntity().getContent()), Error.class);
        Validator.Validate("error detail field", "client_id", error.getDetails().get(0).getField());
        Validator.Validate("error detail field",
                "Field value is invalid. " + randomClient,
                error.getDetails().get(0).getReason());
        response.close();
    }

    @Test(groups = "dailies")
    public void coverSER687() throws Exception {
        String redirectURI = "http://localhost";
        String requestURI = Oauth.DefaultAuthorizeURI + "?"
                + Oauth.DefaultFNClientId + "=" + Oauth.DefaultClientId + "&"
                + Oauth.DefaultFNResponseType + "=" + URLEncoder.encode("token id_token", "UTF-8") + "&"
                + "state=state&"
                + "nonce=12345&"
                + Oauth.DefaultFNRedirectURI + "=" + redirectURI + "&"
                + Oauth.DefaultFNCountry + "=" + "US" + "&"
                + Oauth.DefaultFNLocale + "=" + "en_US" + "&"
                + "prompt=none";

        CloseableHttpResponse response = Oauth.OauthGet(requestURI, null);
        Validator.Validate("validate response code", 302, response.getStatusLine().getStatusCode());
        for (Header h : response.getAllHeaders()) {
            if (h.getName().equals("Location")) {
                Validator.Validate("validate redirect uri", true, h.getValue().contains(redirectURI));
                Validator.Validate("validate error status", true, h.getValue().contains("error=login_required"));
                return;
            }
        }
        throw new Exception("response header location not validated");
    }

    private static void ValidateErrorFreeResponse(String responseString) throws Exception {
        Validator.Validate("validate no errors in response \r\n" + responseString,
                true, responseString.contains("\"errors\" : [ ]") || responseString.contains("\"errors\":[]"));
    }
}
