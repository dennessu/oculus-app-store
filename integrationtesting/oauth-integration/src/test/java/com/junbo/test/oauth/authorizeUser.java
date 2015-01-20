/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.oauth;

import com.junbo.common.error.Error;
import com.junbo.common.error.ErrorDetail;
import com.junbo.identity.spec.v1.model.*;
import com.junbo.identity.spec.v1.model.migration.OculusInput;
import com.junbo.identity.spec.v1.model.migration.OculusOutput;
import com.junbo.oauth.spec.model.TokenInfo;
import com.junbo.oauth.spec.model.ViewModel;
import com.junbo.test.common.*;
import com.junbo.test.common.libs.LogHelper;
import com.junbo.test.common.property.Property;
import com.junbo.test.identity.Identity;
import com.junbo.test.identity.IdentityModel;
import org.apache.http.Header;
import org.apache.http.NameValuePair;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.InputStreamReader;
import java.net.URLEncoder;
import java.util.*;

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
        CloseableHttpResponse currentViewResponse = Oauth.GetViewStateByCid(cid);
        Oauth.validateViewModeResponse(currentViewResponse, Oauth.ViewModelType.login.name());

        Oauth.StartLoggingAPISample(Oauth.MessagePostViewRegister);
        CloseableHttpResponse postViewResponse = Oauth.PostViewRegisterByCid(cid);
        Oauth.validateViewModeResponse(postViewResponse, Oauth.ViewModelType.register.name());
        Oauth.StartLoggingAPISample(Oauth.MessageGetViewState);
        currentViewResponse = Oauth.GetViewStateByCid(cid);
        Oauth.validateViewModeResponse(currentViewResponse, Oauth.ViewModelType.register.name());

        Oauth.StartLoggingAPISample(Oauth.MessagePostRegisterUser);
        String userName = RandomHelper.randomAlphabetic(15);
        String email = RandomHelper.randomEmail();
        ViewModel viewModel = new ViewModel();
        viewModel.setModel(new HashMap<String, Object>());
        Oauth.PostRegisterUser(cid, userName, email, viewModel);

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
        CloseableHttpResponse currentViewResponse = Oauth.GetViewStateByCid(cid);
        Oauth.validateViewModeResponse(currentViewResponse, Oauth.ViewModelType.login.name());

        Oauth.StartLoggingAPISample(Oauth.MessagePostViewRegister);
        CloseableHttpResponse postViewResponse = Oauth.PostViewRegisterByCid(cid);
        Oauth.validateViewModeResponse(postViewResponse, Oauth.ViewModelType.register.name());
        Oauth.StartLoggingAPISample(Oauth.MessageGetViewState);
        currentViewResponse = Oauth.GetViewStateByCid(cid);
        Oauth.validateViewModeResponse(currentViewResponse, Oauth.ViewModelType.register.name());

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

        CloseableHttpResponse currentViewResponse = Oauth.GetViewStateByCid(cid);
        Oauth.validateViewModeResponse(currentViewResponse, Oauth.ViewModelType.login.name());

        Oauth.StartLoggingAPISample(Oauth.MessagePostViewRegister);
        CloseableHttpResponse postViewResponse = Oauth.PostViewRegisterByCid(cid);
        Oauth.validateViewModeResponse(postViewResponse, Oauth.ViewModelType.register.name());
        Oauth.StartLoggingAPISample(Oauth.MessageGetViewState);
        currentViewResponse = Oauth.GetViewStateByCid(cid);
        Oauth.validateViewModeResponse(currentViewResponse, Oauth.ViewModelType.register.name());

        String userName = RandomHelper.randomAlphabetic(15);
        String email = RandomHelper.randomEmail();
        Oauth.PostRegisterUser(cid, userName, email);

        /* todo: talk with zhanxin for ls removed.
        Oauth.StartLoggingAPISample(Oauth.MessageGetLoginStateByCidAfterRegisterUser);
        String loginState = Oauth.GetLoginStateAfterRegisterUser(cid);
        Oauth.StartLoggingAPISample(Oauth.MessageGetAuthCodeByLoginState);
        String authCode = Oauth.SSO2GetAuthCode(loginState);
        */
        String authCode = Oauth.GetAuthCodeAfterRegisterUser(cid);
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

        //HttpclientHelper.ResetHttpClient();
        String cid = Oauth.GetLoginCid();
        CloseableHttpResponse currentViewResponse = Oauth.GetViewStateByCid(cid);
        Oauth.validateViewModeResponse(currentViewResponse, Oauth.ViewModelType.login.name());
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
        CloseableHttpResponse currentViewResponse = Oauth.GetViewStateByCid(cid);
        Oauth.validateViewModeResponse(currentViewResponse, Oauth.ViewModelType.login.name());

        Oauth.StartLoggingAPISample(Oauth.MessagePostViewRegister);
        CloseableHttpResponse postViewResponse = Oauth.PostViewRegisterByCid(cid);
        Oauth.validateViewModeResponse(postViewResponse, Oauth.ViewModelType.register.name());
        Oauth.StartLoggingAPISample(Oauth.MessageGetViewState);
        currentViewResponse = Oauth.GetViewStateByCid(cid);
        Oauth.validateViewModeResponse(currentViewResponse, Oauth.ViewModelType.register.name());

        Oauth.StartLoggingAPISample(Oauth.MessagePostRegisterUser);
        String userName = RandomHelper.randomAlphabetic(15);
        String email = RandomHelper.randomEmail();
        Oauth.PostRegisterUser(cid, userName, email);

        HttpclientHelper.ResetHttpClient();
        cid = Oauth.GetLoginCid();
        currentViewResponse = Oauth.GetViewStateByCid(cid);
        Oauth.validateViewModeResponse(currentViewResponse, Oauth.ViewModelType.login.name());
        String loginResponseLink = Oauth.UserLogin(cid, email, null);
        String idToken = Oauth.GetLoginUser(loginResponseLink).get(Oauth.DefaultFNIdToken);
        Oauth.Logout(idToken);
    }

    @Test(groups = "dailies")
    public void loginNonExistUser() throws Exception {
        String cid = Oauth.GetLoginCid();
        CloseableHttpResponse currentViewResponse = Oauth.GetViewStateByCid(cid);
        Oauth.validateViewModeResponse(currentViewResponse, Oauth.ViewModelType.login.name());
        Error error = new Error();
        error.setMessage("Invalid Credential");
        error.setCode("132.103");
        List<ErrorDetail> errorDetails = new ArrayList<>();
        ErrorDetail errorDetail = new ErrorDetail();
        errorDetail.setField("email, password");
        errorDetail.setReason("email and credential doesn't match");
        errorDetails.add(errorDetail);
        error.setDetails(errorDetails);
        Oauth.UserLogin(cid, RandomHelper.randomEmail(), Oauth.DefaultUserPwd, error);
    }

    @Test(groups = "dailies")
    public void loginWithWrongPassword() throws Exception {
        Oauth.StartLoggingAPISample(Oauth.MessageGetLoginCid);
        String cid = Oauth.GetRegistrationCid();

        Oauth.StartLoggingAPISample(Oauth.MessageGetViewState);
        CloseableHttpResponse currentViewResponse = Oauth.GetViewStateByCid(cid);
        Oauth.validateViewModeResponse(currentViewResponse, Oauth.ViewModelType.login.name());

        Oauth.StartLoggingAPISample(Oauth.MessagePostViewRegister);
        CloseableHttpResponse postViewResponse = Oauth.PostViewRegisterByCid(cid);
        Oauth.validateViewModeResponse(postViewResponse, Oauth.ViewModelType.register.name());
        Oauth.StartLoggingAPISample(Oauth.MessageGetViewState);
        currentViewResponse = Oauth.GetViewStateByCid(cid);
        Oauth.validateViewModeResponse(currentViewResponse, Oauth.ViewModelType.register.name());

        Oauth.StartLoggingAPISample(Oauth.MessagePostRegisterUser);
        String userName = RandomHelper.randomAlphabetic(15);
        String email = RandomHelper.randomEmail();
        Oauth.PostRegisterUser(cid, userName, email);

        HttpclientHelper.ResetHttpClient();
        cid = Oauth.GetLoginCid();
        currentViewResponse = Oauth.GetViewStateByCid(cid);
        Oauth.validateViewModeResponse(currentViewResponse, Oauth.ViewModelType.login.name());
        Error error = new Error();
        error.setMessage("Invalid Credential");
        error.setCode("132.103");
        List<ErrorDetail> errorDetails = new ArrayList<>();
        ErrorDetail errorDetail = new ErrorDetail();
        errorDetail.setField("email, password");
        errorDetail.setReason("email and credential doesn't match");
        errorDetails.add(errorDetail);
        error.setDetails(errorDetails);
        Oauth.UserLogin(cid, email, RandomHelper.randomAlphabetic(10), error);
    }

    @Property(environment = "release")
    @Test(groups = "dailies")
    public void registerWithUserPii() throws Exception {
        Oauth.StartLoggingAPISample(Oauth.MessageGetLoginCid);
        String cid = Oauth.GetRegistrationCid();

        Oauth.StartLoggingAPISample(Oauth.MessageGetViewState);
        CloseableHttpResponse currentViewResponse = Oauth.GetViewStateByCid(cid);
        Oauth.validateViewModeResponse(currentViewResponse, Oauth.ViewModelType.login.name());

        Oauth.StartLoggingAPISample(Oauth.MessagePostViewRegister);
        CloseableHttpResponse postViewResponse = Oauth.PostViewRegisterByCid(cid);
        Oauth.validateViewModeResponse(postViewResponse, Oauth.ViewModelType.register.name());
        Oauth.StartLoggingAPISample(Oauth.MessageGetViewState);
        currentViewResponse = Oauth.GetViewStateByCid(cid);
        Oauth.validateViewModeResponse(currentViewResponse, Oauth.ViewModelType.register.name());

        Oauth.StartLoggingAPISample(Oauth.MessagePostRegisterUser);
        //String userName = "allEnvLoginUser";
        //String email = "silkcloudtest+allEnvLoginUser@gmail.com";
        String userName = RandomHelper.randomAlphabetic(15);
        String email = RandomHelper.randomEmail();
        Oauth.PostRegisterUser(cid, userName, email);

        HttpclientHelper.ResetHttpClient();
        cid = Oauth.GetLoginCid();
        currentViewResponse = Oauth.GetViewStateByCid(cid);
        Oauth.validateViewModeResponse(currentViewResponse, Oauth.ViewModelType.login.name());
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
    @Test(groups = "dailies")
    public void registerOvrTestUsers() throws Exception {
        for (int i =1; i <= 150; i++) {
            try {
                HttpclientHelper.CreateHttpClient();
                String username = "davosvr" + String.format("%03d", i);
                String email = "davosvr" + String.format("%03d", i) + "@gmail.com";
                String password = "milkvr" + String.format("%03d", i);
                String pin = String.format("%04d", i);

                Oauth.StartLoggingAPISample(Oauth.MessageGetLoginCid);
                String cid = Oauth.GetRegistrationCid();

                Oauth.StartLoggingAPISample(Oauth.MessageGetViewState);
                CloseableHttpResponse currentViewResponse = Oauth.GetViewStateByCid(cid);
                Oauth.validateViewModeResponse(currentViewResponse, Oauth.ViewModelType.login.name());

                Oauth.StartLoggingAPISample(Oauth.MessagePostViewRegister);
                CloseableHttpResponse postViewResponse = Oauth.PostViewRegisterByCid(cid);
                Oauth.validateViewModeResponse(postViewResponse, Oauth.ViewModelType.register.name());
                Oauth.StartLoggingAPISample(Oauth.MessageGetViewState);
                currentViewResponse = Oauth.GetViewStateByCid(cid);
                Oauth.validateViewModeResponse(currentViewResponse, Oauth.ViewModelType.register.name());

                Oauth.StartLoggingAPISample(Oauth.MessagePostRegisterUser);
                Oauth.PostRegisterUser(cid, username, email, password, pin);

                HttpclientHelper.ResetHttpClient();
                cid = Oauth.GetLoginCid();
                currentViewResponse = Oauth.GetViewStateByCid(cid);
                Oauth.validateViewModeResponse(currentViewResponse, Oauth.ViewModelType.login.name());
                String loginResponseLink = Oauth.UserLogin(cid, email, password);
                String accessToken = Oauth.GetLoginUser(loginResponseLink).get(Oauth.DefaultFNAccessToken);
                TokenInfo tokenInfo = Oauth.GetTokenInfo(accessToken);
                Validator.Validate("validate token->client is correct", Oauth.DefaultClientId, tokenInfo.getClientId());
                Validator.Validate("validate token->scopes is correct", Oauth.DefaultLoginScopes, tokenInfo.getScopes());
                Identity.SetHttpAuthorizationHeader(accessToken);
                User storedUser = Identity.UserGetByUserId(tokenInfo.getSub());
                Identity.UserPersonalInfoPost(storedUser.getId(), IdentityModel.DefaultUserPersonalInfoAddress());
                Identity.UserPersonalInfoPost(storedUser.getId(), IdentityModel.DefaultUserPersonalInfoDob());
                HttpclientHelper.CloseHttpClient();
            } catch (Throwable e) {
                // Ignore
            }
        }
    }

    @Property(environment = "release")
    @Test(groups = "int/ppe/prod/sewer")
    public void loginExistingUser() throws Exception {
        if (Oauth.DefaultOauthEndpoint.contains("http://localhost:8080")) return;
        String cid = Oauth.GetLoginCid();
        CloseableHttpResponse currentViewResponse = Oauth.GetViewStateByCid(cid);
        Oauth.validateViewModeResponse(currentViewResponse, Oauth.ViewModelType.login.name());
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
        CloseableHttpResponse currentViewResponse = Oauth.GetViewStateByCid(cid);
        Oauth.validateViewModeResponse(currentViewResponse, Oauth.ViewModelType.login.name());
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
        CloseableHttpResponse currentViewResponse = Oauth.GetViewStateByCid(cid);
        Oauth.validateViewModeResponse(currentViewResponse, Oauth.ViewModelType.login.name());

        Oauth.StartLoggingAPISample(Oauth.MessagePostViewRegister);
        CloseableHttpResponse postViewResponse = Oauth.PostViewRegisterByCid(cid);
        Oauth.validateViewModeResponse(postViewResponse, Oauth.ViewModelType.register.name());
        Oauth.StartLoggingAPISample(Oauth.MessageGetViewState);
        currentViewResponse = Oauth.GetViewStateByCid(cid);
        Oauth.validateViewModeResponse(currentViewResponse, Oauth.ViewModelType.register.name());

        Oauth.StartLoggingAPISample(Oauth.MessagePostRegisterUser);
        String userName = RandomHelper.randomAlphabetic(15);
        String email = RandomHelper.randomEmail();
        Oauth.PostRegisterUser(cid, userName, email, null, false, false, null);
        Oauth.VerifyEmail(cid, Oauth.DefaultOauthSecondaryEndpoint);

    }

    @Property
    @Test(groups = "ppe/prod")
    public void resetPasswordRoute() throws Exception {
        if (Oauth.DefaultOauthSecondaryEndpoint == null) return;
        Oauth.StartLoggingAPISample(Oauth.MessageGetLoginCid);
        String cid = Oauth.GetRegistrationCid();

        Oauth.StartLoggingAPISample(Oauth.MessageGetViewState);
        CloseableHttpResponse currentViewResponse = Oauth.GetViewStateByCid(cid);
        Oauth.validateViewModeResponse(currentViewResponse, Oauth.ViewModelType.login.name());

        Oauth.StartLoggingAPISample(Oauth.MessagePostViewRegister);
        CloseableHttpResponse postViewResponse = Oauth.PostViewRegisterByCid(cid);
        Oauth.validateViewModeResponse(postViewResponse, Oauth.ViewModelType.register.name());
        Oauth.StartLoggingAPISample(Oauth.MessageGetViewState);
        currentViewResponse = Oauth.GetViewStateByCid(cid);
        Oauth.validateViewModeResponse(currentViewResponse, Oauth.ViewModelType.register.name());

        Oauth.StartLoggingAPISample(Oauth.MessagePostRegisterUser);
        String userName = RandomHelper.randomAlphabetic(15);
        String email = RandomHelper.randomEmail();
        Oauth.PostRegisterUser(cid, userName, email);

        //HttpclientHelper.ResetHttpClient();
        String newPassword = "ASDFqwer1234";
        // set identity authorization header
        Oauth.GetAccessToken(Oauth.GetAuthCodeAfterRegisterUser(cid));
        UserPersonalInfo upi = Identity.UserPersonalInfoGetByUserEmail(email);
        String resetPasswordLink = Oauth.PostResetPassword(
                email, null);
        List<String> resetPwdLinks = Oauth.GetResetPasswordLinks(userName, email, null, false);
        resetPasswordLink = resetPasswordLink.contains("reset") ? resetPasswordLink : resetPwdLinks.get(0);
        LogHelper logHelper = new LogHelper(authorizeUser.class);
        logHelper.logInfo("reset password link: " + resetPasswordLink);
        String resetPasswordCid = Oauth.GetResetPasswordCid(resetPasswordLink);
        Oauth.GetResetPasswordView(resetPasswordCid);
        Oauth.PostResetPasswordWithNewPassword(resetPasswordCid, newPassword);

        HttpclientHelper.ResetHttpClient();
        cid = Oauth.GetLoginCid();
        currentViewResponse = Oauth.GetViewStateByCid(cid);
        Oauth.validateViewModeResponse(currentViewResponse, Oauth.ViewModelType.login.name());
        String loginResponseLink = Oauth.UserLogin(cid, email, newPassword,
                Oauth.DefaultOauthSecondaryEndpoint + "/oauth2/authorize", null);
        String idToken = Oauth.GetLoginUser(loginResponseLink, Oauth.DefaultOauthSecondaryEndpoint)
                .get(Oauth.DefaultFNIdToken);
        Oauth.Logout(idToken);
    }

    @Test(groups = "dailies")
    public void resetPassword() throws Exception {
        Oauth.StartLoggingAPISample(Oauth.MessageGetLoginCid);
        String cid = Oauth.GetRegistrationCid();

        Oauth.StartLoggingAPISample(Oauth.MessageGetViewState);
        CloseableHttpResponse currentViewResponse = Oauth.GetViewStateByCid(cid);
        Oauth.validateViewModeResponse(currentViewResponse, Oauth.ViewModelType.login.name());

        Oauth.StartLoggingAPISample(Oauth.MessagePostViewRegister);
        CloseableHttpResponse postViewResponse = Oauth.PostViewRegisterByCid(cid);
        Oauth.validateViewModeResponse(postViewResponse, Oauth.ViewModelType.register.name());
        Oauth.StartLoggingAPISample(Oauth.MessageGetViewState);
        currentViewResponse = Oauth.GetViewStateByCid(cid);
        Oauth.validateViewModeResponse(currentViewResponse, Oauth.ViewModelType.register.name());

        Oauth.StartLoggingAPISample(Oauth.MessagePostRegisterUser);
        String userName = RandomHelper.randomAlphabetic(15);
        String email = RandomHelper.randomEmail();
        Oauth.PostRegisterUser(cid, userName, email);

        //HttpclientHelper.ResetHttpClient();
        String newPassword = "ASDFqwer1234";
        // set identity authorization header
        Oauth.GetAccessToken(Oauth.GetAuthCodeAfterRegisterUser(cid));
        UserPersonalInfo upi = Identity.UserPersonalInfoGetByUserEmail(email);
        String resetPasswordLink = Oauth.PostResetPassword(email, null);
        String resetPasswordCid = Oauth.GetResetPasswordCid(resetPasswordLink);
        Oauth.GetResetPasswordView(resetPasswordCid);
        Oauth.PostResetPasswordWithNewPassword(resetPasswordCid, newPassword);

        HttpclientHelper.ResetHttpClient();
        cid = Oauth.GetLoginCid();
        currentViewResponse = Oauth.GetViewStateByCid(cid);
        Oauth.validateViewModeResponse(currentViewResponse, Oauth.ViewModelType.login.name());
        String loginResponseLink = Oauth.UserLogin(cid, email, newPassword);
        String idToken = Oauth.GetLoginUser(loginResponseLink).get(Oauth.DefaultFNIdToken);
        Oauth.Logout(idToken);
    }

    @Test(groups = "dailies")
    public void resetPasswordWithoutUserEmail() throws Exception {
        Oauth.StartLoggingAPISample(Oauth.MessageGetLoginCid);
        String cid = Oauth.GetRegistrationCid();

        Oauth.StartLoggingAPISample(Oauth.MessageGetViewState);
        CloseableHttpResponse currentViewResponse = Oauth.GetViewStateByCid(cid);
        Oauth.validateViewModeResponse(currentViewResponse, Oauth.ViewModelType.login.name());

        Oauth.StartLoggingAPISample(Oauth.MessagePostViewRegister);
        CloseableHttpResponse postViewResponse = Oauth.PostViewRegisterByCid(cid);
        Oauth.validateViewModeResponse(postViewResponse, Oauth.ViewModelType.register.name());
        Oauth.StartLoggingAPISample(Oauth.MessageGetViewState);
        currentViewResponse = Oauth.GetViewStateByCid(cid);
        Oauth.validateViewModeResponse(currentViewResponse, Oauth.ViewModelType.register.name());

        Oauth.StartLoggingAPISample(Oauth.MessagePostRegisterUser);
        String userName = RandomHelper.randomAlphabetic(15);
        String email = RandomHelper.randomEmail();
        Oauth.PostRegisterUser(cid, userName, email);

        //HttpclientHelper.ResetHttpClient();
        String newPassword = "ASDFqwer1234";
        // set identity authorization header
        Oauth.GetAccessToken(Oauth.GetAuthCodeAfterRegisterUser(cid));
        UserPersonalInfo upi = Identity.UserPersonalInfoGetByUserEmail(email);
        List<NameValuePair> nvps = new ArrayList<NameValuePair>();
        nvps.add(new BasicNameValuePair(Oauth.DefaultFNUserName, userName));
        nvps.add(new BasicNameValuePair(Oauth.DefaultFNLocale, "en_US"));

        CloseableHttpResponse response = Oauth.OauthPost(Oauth.DefaultResetPasswordURI, nvps);
        try {
            Validator.Validate("validate response code", 400, response.getStatusLine().getStatusCode());
            Error error = JsonHelper.JsonDeserializer(new InputStreamReader(response.getEntity().getContent()),
                    Error.class);
            Validator.Validate("validate error message", "Input Error", error.getMessage());
            Validator.Validate("valdiate details field", "user_email", error.getDetails().get(0).getField());
            Validator.Validate("valdiate details reason", "Field is required", error.getDetails().get(0).getReason());
        } finally {
            response.close();
        }
    }

    @Test(groups = "dailies")
    public void resetPasswordWithNonExistUserEmail() throws Exception {
        Oauth.StartLoggingAPISample(Oauth.MessageGetLoginCid);
        String cid = Oauth.GetRegistrationCid();

        Oauth.StartLoggingAPISample(Oauth.MessageGetViewState);
        CloseableHttpResponse currentViewResponse = Oauth.GetViewStateByCid(cid);
        Oauth.validateViewModeResponse(currentViewResponse, Oauth.ViewModelType.login.name());

        Oauth.StartLoggingAPISample(Oauth.MessagePostViewRegister);
        CloseableHttpResponse postViewResponse = Oauth.PostViewRegisterByCid(cid);
        Oauth.validateViewModeResponse(postViewResponse, Oauth.ViewModelType.register.name());
        Oauth.StartLoggingAPISample(Oauth.MessageGetViewState);
        currentViewResponse = Oauth.GetViewStateByCid(cid);
        Oauth.validateViewModeResponse(currentViewResponse, Oauth.ViewModelType.register.name());

        Oauth.StartLoggingAPISample(Oauth.MessagePostRegisterUser);
        String userName = RandomHelper.randomAlphabetic(15);
        String email = RandomHelper.randomEmail();
        Oauth.PostRegisterUser(cid, userName, email);

        //HttpclientHelper.ResetHttpClient();
        String newPassword = "ASDFqwer1234";
        // set identity authorization header
        Oauth.GetAccessToken(Oauth.GetAuthCodeAfterRegisterUser(cid));
        UserPersonalInfo upi = Identity.UserPersonalInfoGetByUserEmail(email);
        List<NameValuePair> nvps = new ArrayList<NameValuePair>();
        String fakeEmail = RandomHelper.randomEmail();
        nvps.add(new BasicNameValuePair(Oauth.DefaultFNUserEmail, fakeEmail));
        nvps.add(new BasicNameValuePair(Oauth.DefaultFNLocale, "en_US"));

        CloseableHttpResponse response = Oauth.OauthPost(Oauth.DefaultResetPasswordURI, nvps);
        try {
            Validator.Validate("validate response code", 200, response.getStatusLine().getStatusCode());
            String[] fakeEmails = fakeEmail.split("@");
            String expectedResponse = fakeEmails[0].substring(0, 3) + "******" + "@" + fakeEmails[1];
            String responseString = EntityUtils.toString(response.getEntity(), "UTF-8");
            Validator.Validate("validate response content", expectedResponse, responseString);
        } finally {
            response.close();
        }
    }

    @Test(groups = "dailies")
    public void resetPasswordLinkUniqueAvailability() throws Exception {
        Oauth.StartLoggingAPISample(Oauth.MessageGetLoginCid);
        String cid = Oauth.GetRegistrationCid();

        Oauth.StartLoggingAPISample(Oauth.MessageGetViewState);
        CloseableHttpResponse currentViewResponse = Oauth.GetViewStateByCid(cid);
        Oauth.validateViewModeResponse(currentViewResponse, Oauth.ViewModelType.login.name());

        Oauth.StartLoggingAPISample(Oauth.MessagePostViewRegister);
        CloseableHttpResponse postViewResponse = Oauth.PostViewRegisterByCid(cid);
        Oauth.validateViewModeResponse(postViewResponse, Oauth.ViewModelType.register.name());
        Oauth.StartLoggingAPISample(Oauth.MessageGetViewState);
        currentViewResponse = Oauth.GetViewStateByCid(cid);
        Oauth.validateViewModeResponse(currentViewResponse, Oauth.ViewModelType.register.name());

        Oauth.StartLoggingAPISample(Oauth.MessagePostRegisterUser);
        String userName = RandomHelper.randomAlphabetic(15);
        String email = RandomHelper.randomEmail();
        Oauth.PostRegisterUser(cid, userName, email);

        //HttpclientHelper.ResetHttpClient();
        // set identity authorization header
        Oauth.GetAccessToken(Oauth.GetAuthCodeAfterRegisterUser(cid));
        UserPersonalInfo upi = Identity.UserPersonalInfoGetByUserEmail(email);
        Oauth.PostResetPassword(email, null);
        Oauth.PostResetPassword(email, null);
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
        CloseableHttpResponse currentViewResponse = Oauth.GetViewStateByCid(cid);
        Oauth.validateViewModeResponse(currentViewResponse, Oauth.ViewModelType.login.name());

        Oauth.StartLoggingAPISample(Oauth.MessagePostViewRegister);
        CloseableHttpResponse postViewResponse = Oauth.PostViewRegisterByCid(cid);
        Oauth.validateViewModeResponse(postViewResponse, Oauth.ViewModelType.register.name());
        Oauth.StartLoggingAPISample(Oauth.MessageGetViewState);
        currentViewResponse = Oauth.GetViewStateByCid(cid);
        Oauth.validateViewModeResponse(currentViewResponse, Oauth.ViewModelType.register.name());

        Oauth.StartLoggingAPISample(Oauth.MessagePostRegisterUser);
        String userName = RandomHelper.randomAlphabetic(15);
        String email = RandomHelper.randomEmail();
        Oauth.PostRegisterUser(cid, userName, email, null, true, true, null);
    }

    @Test(groups = "dailies")
    public void resetPasswordLinkPrivilege() throws Exception {
        // if (Oauth.DefaultOauthSecondaryEndpoint == null) return;
        Oauth.StartLoggingAPISample(Oauth.MessageGetLoginCid);
        String cid = Oauth.GetRegistrationCid();

        Oauth.StartLoggingAPISample(Oauth.MessageGetViewState);
        CloseableHttpResponse currentViewResponse = Oauth.GetViewStateByCid(cid);
        Oauth.validateViewModeResponse(currentViewResponse, Oauth.ViewModelType.login.name());

        Oauth.StartLoggingAPISample(Oauth.MessagePostViewRegister);
        CloseableHttpResponse postViewResponse = Oauth.PostViewRegisterByCid(cid);
        Oauth.validateViewModeResponse(postViewResponse, Oauth.ViewModelType.register.name());
        Oauth.StartLoggingAPISample(Oauth.MessageGetViewState);
        currentViewResponse = Oauth.GetViewStateByCid(cid);
        Oauth.validateViewModeResponse(currentViewResponse, Oauth.ViewModelType.register.name());

        Oauth.StartLoggingAPISample(Oauth.MessagePostRegisterUser);
        String userName = RandomHelper.randomAlphabetic(15);
        String email = RandomHelper.randomEmail();
        Oauth.PostRegisterUser(cid, userName, email);

        //HttpclientHelper.ResetHttpClient();
        String newPassword = "ASDFqwer1234";
        // set identity authorization header
        Oauth.GetAccessToken(Oauth.GetAuthCodeAfterRegisterUser(cid));
        UserPersonalInfo upi = Identity.UserPersonalInfoGetByUserEmail(email);
        Oauth.PostResetPassword(email, null);
        List<String> resetPwdLinks = Oauth.GetResetPasswordLinks(userName, email, null, true);
    }

    @Test(groups = "dailies", enabled = false)
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

        nvps = new ArrayList<NameValuePair>();
        nvps.add(new BasicNameValuePair("grant_type", "client_credentials"));
        nvps.add(new BasicNameValuePair("client_id", Oauth.DefaultClientId));
        nvps.add(new BasicNameValuePair("client_secret", null));
        nvps.add(new BasicNameValuePair("scope", "identity"));
        response = Oauth.OauthPost(Oauth.DefaultOauthEndpoint + "/oauth2/token", nvps);
        Validator.Validate("response status is ok", response.getStatusLine().getStatusCode(), 400);
        error = JsonHelper.JsonDeserializer(
                new InputStreamReader(response.getEntity().getContent()), Error.class);
        Validator.Validate("error detail field", "client_secret", error.getDetails().get(0).getField());
        Validator.Validate("error detail field", "Query parameter is required", error.getDetails().get(0).getReason());
        response.close();

        nvps = new ArrayList<NameValuePair>();
        nvps.add(new BasicNameValuePair("grant_type", "client_credentials"));
        nvps.add(new BasicNameValuePair("client_id", Oauth.DefaultClientId));
        nvps.add(new BasicNameValuePair("client_secret", ""));
        nvps.add(new BasicNameValuePair("scope", "identity"));
        response = Oauth.OauthPost(Oauth.DefaultOauthEndpoint + "/oauth2/token", nvps);
        Validator.Validate("response status is ok", response.getStatusLine().getStatusCode(), 400);
        error = JsonHelper.JsonDeserializer(
                new InputStreamReader(response.getEntity().getContent()), Error.class);
        Validator.Validate("error detail field", "client_secret", error.getDetails().get(0).getField());
        Validator.Validate("error detail field", "Query parameter is required", error.getDetails().get(0).getReason());
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

    @Test(groups = "dailies")
    public void registerWithoutMissingFields() throws Exception {
        Oauth.StartLoggingAPISample(Oauth.MessageGetLoginCid);
        String cid = Oauth.GetRegistrationCid();

        Oauth.StartLoggingAPISample(Oauth.MessageGetViewState);
        CloseableHttpResponse currentViewResponse = Oauth.GetViewStateByCid(cid);
        Oauth.validateViewModeResponse(currentViewResponse, Oauth.ViewModelType.login.name());

        Oauth.StartLoggingAPISample(Oauth.MessagePostViewRegister);
        CloseableHttpResponse postViewResponse = Oauth.PostViewRegisterByCid(cid);
        Oauth.validateViewModeResponse(postViewResponse, Oauth.ViewModelType.register.name());
        Oauth.StartLoggingAPISample(Oauth.MessageGetViewState);
        currentViewResponse = Oauth.GetViewStateByCid(cid);
        Oauth.validateViewModeResponse(currentViewResponse, Oauth.ViewModelType.register.name());

        Oauth.StartLoggingAPISample(Oauth.MessagePostRegisterUser);
        String userName = RandomHelper.randomAlphabetic(15);
        String email = RandomHelper.randomEmail();
        String firstName = RandomHelper.randomAlphabetic(15);
        String lastName = RandomHelper.randomAlphabetic(15);

        List<NameValuePair> nvps = new ArrayList<NameValuePair>();
        nvps.add(new BasicNameValuePair(Oauth.DefaultFNCid, cid));
        nvps.add(new BasicNameValuePair(Oauth.DefaultFNEvent, "next"));
        //nvps.add(new BasicNameValuePair(Oauth.DefaultFNUserName, userName));
        nvps.add(new BasicNameValuePair(Oauth.DefaultFNPassword, Oauth.DefaultUserPwd));
        nvps.add(new BasicNameValuePair(Oauth.DefaultFNEmail, email));
        nvps.add(new BasicNameValuePair(Oauth.DefaultFNFirstName, firstName));
        nvps.add(new BasicNameValuePair(Oauth.DefaultFNLastName, lastName));
        nvps.add(new BasicNameValuePair(Oauth.DefaultFNGender, "male"));
        nvps.add(new BasicNameValuePair(Oauth.DefaultFNDoB, "1980-01-01"));
        nvps.add(new BasicNameValuePair(Oauth.DefaultFNPin, RandomHelper.randomNumeric(4)));

        Error error = new Error();
        error.setMessage("Input Error");
        List<ErrorDetail> errorDetails = new ArrayList<>();
        // missing username error
        ErrorDetail errorDetail = new ErrorDetail();
        errorDetail.setField("login");
        errorDetail.setReason("Query parameter is required");
        errorDetails.add(errorDetail);
        error.setDetails(errorDetails);
        CloseableHttpResponse response = Oauth.OauthPost(Oauth.DefaultAuthorizeURI, nvps);
        Oauth.validateViewModeResponse(response, "register", error);

        nvps = new ArrayList<NameValuePair>();
        nvps.add(new BasicNameValuePair(Oauth.DefaultFNCid, cid));
        nvps.add(new BasicNameValuePair(Oauth.DefaultFNEvent, "next"));
        nvps.add(new BasicNameValuePair(Oauth.DefaultFNUserName, userName));
        nvps.add(new BasicNameValuePair(Oauth.DefaultFNPassword, Oauth.DefaultUserPwd));
        //nvps.add(new BasicNameValuePair(Oauth.DefaultFNEmail, email));
        nvps.add(new BasicNameValuePair(Oauth.DefaultFNFirstName, firstName));
        nvps.add(new BasicNameValuePair(Oauth.DefaultFNLastName, lastName));
        nvps.add(new BasicNameValuePair(Oauth.DefaultFNGender, "male"));
        nvps.add(new BasicNameValuePair(Oauth.DefaultFNDoB, "1980-01-01"));
        nvps.add(new BasicNameValuePair(Oauth.DefaultFNPin, RandomHelper.randomNumeric(4)));

        error = new Error();
        error.setMessage("Input Error");
        errorDetails = new ArrayList<>();
        // missing email error
        errorDetail = new ErrorDetail();
        errorDetail.setField("email");
        errorDetail.setReason("Field is required");
        errorDetails.add(errorDetail);
        error.setDetails(errorDetails);
        response = Oauth.OauthPost(Oauth.DefaultAuthorizeURI, nvps);
        Oauth.validateViewModeResponse(response, "register", error);

        nvps = new ArrayList<NameValuePair>();
        nvps.add(new BasicNameValuePair(Oauth.DefaultFNCid, cid));
        nvps.add(new BasicNameValuePair(Oauth.DefaultFNEvent, "next"));
        nvps.add(new BasicNameValuePair(Oauth.DefaultFNUserName, userName));
        nvps.add(new BasicNameValuePair(Oauth.DefaultFNPassword, Oauth.DefaultUserPwd));
        nvps.add(new BasicNameValuePair(Oauth.DefaultFNEmail, email));
        //nvps.add(new BasicNameValuePair(Oauth.DefaultFNFirstName, firstName));
        nvps.add(new BasicNameValuePair(Oauth.DefaultFNLastName, lastName));
        nvps.add(new BasicNameValuePair(Oauth.DefaultFNGender, "male"));
        nvps.add(new BasicNameValuePair(Oauth.DefaultFNDoB, "1980-01-01"));
        nvps.add(new BasicNameValuePair(Oauth.DefaultFNPin, RandomHelper.randomNumeric(4)));

        error = new Error();
        error.setMessage("Input Error");
        errorDetails = new ArrayList<>();
        // missing first name error
        errorDetail = new ErrorDetail();
        errorDetail.setField("first_name");
        errorDetail.setReason("Field is required");
        errorDetails.add(errorDetail);
        error.setDetails(errorDetails);
        response = Oauth.OauthPost(Oauth.DefaultAuthorizeURI, nvps);
        Oauth.validateViewModeResponse(response, "register", error);

        nvps = new ArrayList<NameValuePair>();
        nvps.add(new BasicNameValuePair(Oauth.DefaultFNCid, cid));
        nvps.add(new BasicNameValuePair(Oauth.DefaultFNEvent, "next"));
        nvps.add(new BasicNameValuePair(Oauth.DefaultFNUserName, userName));
        nvps.add(new BasicNameValuePair(Oauth.DefaultFNPassword, Oauth.DefaultUserPwd));
        nvps.add(new BasicNameValuePair(Oauth.DefaultFNEmail, email));
        nvps.add(new BasicNameValuePair(Oauth.DefaultFNFirstName, firstName));
        //nvps.add(new BasicNameValuePair(Oauth.DefaultFNLastName, lastName));
        nvps.add(new BasicNameValuePair(Oauth.DefaultFNGender, "male"));
        nvps.add(new BasicNameValuePair(Oauth.DefaultFNDoB, "1980-01-01"));
        nvps.add(new BasicNameValuePair(Oauth.DefaultFNPin, RandomHelper.randomNumeric(4)));

        error = new Error();
        error.setMessage("Input Error");
        errorDetails = new ArrayList<>();
        // missing last name error
        errorDetail = new ErrorDetail();
        errorDetail.setField("last_name");
        errorDetail.setReason("Field is required");
        errorDetails.add(errorDetail);
        error.setDetails(errorDetails);
        response = Oauth.OauthPost(Oauth.DefaultAuthorizeURI, nvps);
        Oauth.validateViewModeResponse(response, "register", error);
    }

    /*
     * test case to cover https://oculus.atlassian.net/browse/SER-800
     */
    @Test(groups = "dailies")
    public void registerWithDuplicateUserName() throws Exception {
        Oauth.StartLoggingAPISample(Oauth.MessageGetLoginCid);
        String cid = Oauth.GetRegistrationCid();

        Oauth.StartLoggingAPISample(Oauth.MessageGetViewState);
        CloseableHttpResponse currentViewResponse = Oauth.GetViewStateByCid(cid);
        Oauth.validateViewModeResponse(currentViewResponse, Oauth.ViewModelType.login.name());

        Oauth.StartLoggingAPISample(Oauth.MessagePostViewRegister);
        CloseableHttpResponse postViewResponse = Oauth.PostViewRegisterByCid(cid);
        Oauth.validateViewModeResponse(postViewResponse, Oauth.ViewModelType.register.name());
        Oauth.StartLoggingAPISample(Oauth.MessageGetViewState);
        currentViewResponse = Oauth.GetViewStateByCid(cid);
        Oauth.validateViewModeResponse(currentViewResponse, Oauth.ViewModelType.register.name());

        Oauth.StartLoggingAPISample(Oauth.MessagePostRegisterUser);
        String userName = RandomHelper.randomAlphabetic(15);
        String email = RandomHelper.randomEmail();
        Oauth.PostRegisterUser(cid, userName, email);

        HttpclientHelper.ResetHttpClient();

        Oauth.StartLoggingAPISample(Oauth.MessageGetLoginCid);
        cid = Oauth.GetRegistrationCid();

        Oauth.StartLoggingAPISample(Oauth.MessageGetViewState);
        currentViewResponse = Oauth.GetViewStateByCid(cid);
        Oauth.validateViewModeResponse(currentViewResponse, Oauth.ViewModelType.login.name());

        Oauth.StartLoggingAPISample(Oauth.MessagePostViewRegister);
        postViewResponse = Oauth.PostViewRegisterByCid(cid);
        Oauth.validateViewModeResponse(postViewResponse, Oauth.ViewModelType.register.name());
        Oauth.StartLoggingAPISample(Oauth.MessageGetViewState);
        currentViewResponse = Oauth.GetViewStateByCid(cid);
        Oauth.validateViewModeResponse(currentViewResponse, Oauth.ViewModelType.register.name());

        Error error = new Error();
        error.setMessage("Input Error");
        error.setCode("132.001");
        List<ErrorDetail> details = new ArrayList<>();
        ErrorDetail detail = new ErrorDetail();
        detail.setField("username");
        detail.setReason("Field value is duplicate");
        details.add(detail);
        error.setDetails(details);
        Oauth.StartLoggingAPISample(Oauth.MessagePostRegisterUser);
        Oauth.PostRegisterUser(cid, userName, email, error);
    }

    @Test(groups = "dailies")
    public void resetPasswordWithMultiReferencedEmail() throws Exception {
        String email = RandomHelper.randomEmail();

        Identity.GetHttpAuthorizationHeader();

        // create user 1
        User user1 = Identity.UserPostDefault(IdentityModel.DefaultUser());
        UserPersonalInfo userPersonalInfoName1 = Identity.UserPersonalInfoPost(user1.getId(),
                IdentityModel.DefaultUserPersonalInfoUsername());
        // create user pii 11 with target email
        UserPersonalInfo userPersonalInfo11 = Identity.UserPersonalInfoPost(user1.getId(),
                IdentityModel.DefaultUserPersonalInfoEmail(email));
        // create user pii 12 with random email to be default for user 1
        UserPersonalInfo userPersonalInfo12 = Identity.UserPersonalInfoPost(user1.getId(),
                IdentityModel.DefaultUserPersonalInfoEmail(RandomHelper.randomEmail()));
        // create user link 11 with target email for user 1, not default
        UserPersonalInfoLink link11 = new UserPersonalInfoLink();
        link11.setValue(userPersonalInfo11.getId());
        link11.setLabel(RandomHelper.randomAlphabetic(15));
        link11.setUserId(user1.getId());
        link11.setIsDefault(false);
        // create user link 12 with random default email for user 1
        UserPersonalInfoLink link12 = new UserPersonalInfoLink();
        link12.setValue(userPersonalInfo12.getId());
        link12.setLabel(RandomHelper.randomAlphabetic(15));
        link12.setUserId(user1.getId());
        link12.setIsDefault(true);

        List<UserPersonalInfoLink> links1 = new ArrayList<>();
        links1.add(link11);
        links1.add(link12);
        user1.setEmails(links1);
        user1.setUsername(userPersonalInfoName1.getId());
        user1.setIsAnonymous(false);
        Identity.UserPut(user1);
        Identity.UserGetByUserId(user1.getId());

        // create user 2
        User user2 = Identity.UserPostDefault(IdentityModel.DefaultUser());
        UserPersonalInfo userPersonalInfoName2 = Identity.UserPersonalInfoPost(user2.getId(),
                IdentityModel.DefaultUserPersonalInfoUsername());
        // create user pii 21 with target email
        UserPersonalInfo userPersonalInfo21 = Identity.UserPersonalInfoPost(user2.getId(),
                IdentityModel.DefaultUserPersonalInfoEmail(email));
        Calendar ca = Calendar.getInstance();
        userPersonalInfo21.setLastValidateTime(ca.getTime());
        Identity.UserPersonalInfoPut(user2.getId(), userPersonalInfo21);
        Identity.UserGetByUserId(user2.getId());

        // create user 3
        User user3 = Identity.UserPostDefault(IdentityModel.DefaultUser());
        UserPersonalInfo userPersonalInfoName3 = Identity.UserPersonalInfoPost(user3.getId(),
                IdentityModel.DefaultUserPersonalInfoUsername());
        // create user pii 31 with target email
        UserPersonalInfo userPersonalInfo31 = Identity.UserPersonalInfoPost(user3.getId(),
                IdentityModel.DefaultUserPersonalInfoEmail(email));
        // create user pii 32 with random email to be default for user 3
        UserPersonalInfo userPersonalInfo32 = Identity.UserPersonalInfoPost(user3.getId(),
                IdentityModel.DefaultUserPersonalInfoEmail(RandomHelper.randomEmail()));
        // create user link 31 with target email for user 3, not default
        UserPersonalInfoLink link31 = new UserPersonalInfoLink();
        link31.setValue(userPersonalInfo31.getId());
        link31.setLabel(RandomHelper.randomAlphabetic(15));
        link31.setUserId(user3.getId());
        link31.setIsDefault(false);
        // create user link 32 with random default email for user 3
        UserPersonalInfoLink link32 = new UserPersonalInfoLink();
        link32.setValue(userPersonalInfo32.getId());
        link32.setLabel(RandomHelper.randomAlphabetic(15));
        link32.setUserId(user3.getId());
        link32.setIsDefault(true);
        List<UserPersonalInfoLink> links3 = new ArrayList<>();
        links3.add(link31);
        links3.add(link32);
        user3.setEmails(links3);
        user3.setUsername(userPersonalInfoName3.getId());
        user3.setIsAnonymous(false);
        Identity.UserPut(user3);
        Identity.UserGetByUserId(user3.getId());

        // create user link 2 with target default email for user 2
        UserPersonalInfoLink link2 = new UserPersonalInfoLink();
        link2.setValue(userPersonalInfo21.getId());
        link2.setLabel(RandomHelper.randomAlphabetic(15));
        link2.setUserId(user2.getId());
        link2.setIsDefault(true);
        List<UserPersonalInfoLink> links2 = new ArrayList<>();
        links2.add(link2);
        user2.setEmails(links2);
        user2.setUsername(userPersonalInfoName2.getId());
        user2.setIsAnonymous(false);
        Identity.UserPut(user2);
        Identity.UserGetByUserId(user2.getId());

        //HttpclientHelper.ResetHttpClient();
        String newPassword = "ASDFqwer1234";
        String resetPasswordLink = Oauth.PostResetPassword(email, null);
        String resetPasswordCid = Oauth.GetResetPasswordCid(resetPasswordLink);
        Oauth.GetResetPasswordView(resetPasswordCid);
        Oauth.PostResetPasswordWithNewPassword(resetPasswordCid, newPassword);

        HttpclientHelper.ResetHttpClient();
        String cid = Oauth.GetLoginCid();
        CloseableHttpResponse currentViewResponse = Oauth.GetViewStateByCid(cid);
        Oauth.validateViewModeResponse(currentViewResponse, Oauth.ViewModelType.login.name());
        String loginResponseLink = Oauth.UserLogin(cid, email, newPassword);
        String idToken = Oauth.GetLoginUser(loginResponseLink).get(Oauth.DefaultFNIdToken);
        Oauth.Logout(idToken);

    }
}
