/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.store;

import com.junbo.identity.spec.v1.model.User;
import com.junbo.store.spec.model.identity.UserProfileGetResponse;
import com.junbo.store.spec.model.login.AuthTokenResponse;
import com.junbo.store.spec.model.login.CreateUserRequest;
import com.junbo.store.spec.model.login.UserNameCheckResponse;
import com.junbo.test.common.Entities.enums.ComponentType;
import com.junbo.test.common.RandomHelper;
import com.junbo.test.common.Validator;
import com.junbo.test.common.apihelper.oauth.OAuthService;
import com.junbo.test.common.apihelper.oauth.enums.GrantType;
import com.junbo.test.common.apihelper.oauth.impl.OAuthServiceImpl;
import com.junbo.test.common.libs.IdConverter;
import com.junbo.test.common.property.Component;
import com.junbo.test.common.property.Priority;
import com.junbo.test.common.property.Property;
import com.junbo.test.common.property.Status;
import com.ning.http.util.DateUtil;
import org.apache.commons.lang3.time.DateUtils;
import org.testng.annotations.Test;

import javax.validation.Valid;
import java.util.Date;
import java.util.List;

/**
 * Created by liangfu on 8/29/14.
 */
public class LoginResourceTesting extends BaseTestClass {

    OAuthService oAuthClient = OAuthServiceImpl.getInstance();
    @Property(
            priority = Priority.Dailies,
            features = "Store",
            component = Component.STORE,
            owner = "ZhaoYunlong",
            status = Status.Enable,
            steps = {
                    "Check username"
            }
    )
    @Test
    public void testCheckUsername() throws Exception {
        String invalidUsername = "123Test";
        UserNameCheckResponse userNameCheckResponse = testDataProvider.CheckUserName(invalidUsername);
        Validator.Validate("Validate invalid username", userNameCheckResponse.getIsAvailable(), false);

        CreateUserRequest createUserRequest = testDataProvider.CreateUserRequest();
        userNameCheckResponse = testDataProvider.CheckUserName(createUserRequest.getUsername());
        Validator.Validate("Validate valid username", userNameCheckResponse.getIsAvailable(), true);

        AuthTokenResponse authTokenResponse = testDataProvider.CreateUser(createUserRequest, false);
        Validator.Validate("validate authtoken response correct", createUserRequest.getUsername(), authTokenResponse.getUsername());

        userNameCheckResponse = testDataProvider.CheckUserName(invalidUsername);
        Validator.Validate("Validate invalid username", userNameCheckResponse.getIsAvailable(), false);

        userNameCheckResponse = testDataProvider.CheckUserName(createUserRequest.getUsername());
        Validator.Validate("Validate duplicate username", userNameCheckResponse.getIsAvailable(), false);

        userNameCheckResponse = testDataProvider.CheckUserName(RandomHelper.randomAlphabetic(15));
        Validator.Validate("Validate random character username", userNameCheckResponse.getIsAvailable(), true);
    }


    @Property(
            priority = Priority.Dailies,
            features = "Store",
            component = Component.STORE,
            owner = "ZhaoYunlong",
            status = Status.Enable,
            steps = {
                    "Check email"
            }
    )
    @Test
    public void testCheckEmail() throws Exception {
        String invalidEmail = "123Test";
        UserNameCheckResponse userNameCheckResponse = testDataProvider.CheckEmail(invalidEmail);
        Validator.Validate("Validate invalid email", userNameCheckResponse.getIsAvailable(), false);

        invalidEmail = "###1212@silkcloud.com";
        userNameCheckResponse = testDataProvider.CheckEmail(invalidEmail);
        Validator.Validate("Validate invalid email", userNameCheckResponse.getIsAvailable(), false);

        CreateUserRequest createUserRequest = testDataProvider.CreateUserRequest();
        userNameCheckResponse = testDataProvider.CheckEmail(createUserRequest.getEmail());
        Validator.Validate("Validate valid username", userNameCheckResponse.getIsAvailable(), true);

        AuthTokenResponse authTokenResponse = testDataProvider.CreateUser(createUserRequest, false);
        Validator.Validate("validate authtoken response username correct", createUserRequest.getUsername(), authTokenResponse.getUsername());
        UserProfileGetResponse userProfileGetResponse = testDataProvider.getUserProfile(412);
        assert userProfileGetResponse == null;

        oAuthClient.postAccessToken(GrantType.CLIENT_CREDENTIALS, ComponentType.SMOKETEST);
        List<String> links = oAuthClient.getEmailVerifyLink(IdConverter.idToHexString(authTokenResponse.getUserId()), createUserRequest.getEmail());
        assert links != null;
        for(String link : links) {
            oAuthClient.accessEmailVerifyLink(link);
        }
        userProfileGetResponse = testDataProvider.getUserProfile();
        assert userProfileGetResponse != null;
        Validator.Validate("validate authtoken response name correct", createUserRequest.getUsername(), userProfileGetResponse.getUserProfile().getUsername());
        Validator.Validate("validate authtoken response email correct", createUserRequest.getEmail(), userProfileGetResponse.getUserProfile().getEmail().getValue());

        userNameCheckResponse = testDataProvider.CheckEmail(invalidEmail);
        Validator.Validate("Validate invalid email", userNameCheckResponse.getIsAvailable(), false);

        userNameCheckResponse = testDataProvider.CheckEmail(createUserRequest.getEmail());
        Validator.Validate("Validate duplicate email", userNameCheckResponse.getIsAvailable(), false);

        userNameCheckResponse = testDataProvider.CheckEmail(RandomHelper.randomEmail());
        Validator.Validate("Validate random character email", userNameCheckResponse.getIsAvailable(), true);
    }

    @Property(
            priority = Priority.Dailies,
            features = "Store",
            component = Component.STORE,
            owner = "ZhaoYunlong",
            status = Status.Enable,
            steps = {
                    "Check create user successful"
            }
    )
    @Test
    public void testCreateUser() throws Exception {
        CreateUserRequest createUserRequest = testDataProvider.CreateUserRequest();
        String invalidUsername = "123yunlong";
        String oldUsername = createUserRequest.getUsername();
        createUserRequest.setUsername(invalidUsername);
        AuthTokenResponse createUserResponse = testDataProvider.CreateUser(createUserRequest, true, 400);
        assert createUserResponse == null;

        createUserRequest.setUsername(oldUsername);
        String oldEmail = createUserRequest.getEmail();
        createUserRequest.setEmail("##1234@silkcloud.com");
        createUserResponse = testDataProvider.CreateUser(createUserRequest, true, 400);
        assert createUserResponse == null;

        createUserRequest.setEmail(oldEmail);
        String oldNickName = createUserRequest.getNickName();
        // nick name should not be the same as username
        createUserRequest.setNickName(createUserRequest.getUsername());
        createUserResponse = testDataProvider.CreateUser(createUserRequest, true, 400);
        assert createUserResponse == null;

        createUserRequest.setNickName(oldNickName);
        String oldPassword = createUserRequest.getPassword();
        createUserRequest.setPassword(createUserRequest.getUsername() + "gggg");
        createUserResponse = testDataProvider.CreateUser(createUserRequest, true, 400);
        assert createUserResponse == null;

        createUserRequest.setPassword(oldPassword);
        String oldPin = createUserRequest.getPin();
        createUserRequest.setPin("abcd");
        createUserResponse = testDataProvider.CreateUser(createUserRequest, true, 400);
        assert createUserResponse == null;

        createUserRequest.setPin(oldPin);
        Date oldDob = createUserRequest.getDob();
        createUserRequest.setDob(DateUtils.addYears(new Date(), 100));
        createUserResponse = testDataProvider.CreateUser(createUserRequest, true, 400);
        assert createUserResponse == null;

        createUserRequest.setDob(oldDob);
        createUserResponse = testDataProvider.CreateUser(createUserRequest, true, 200);
        Validator.Validate("Validate username created successfully", createUserRequest.getUsername(), createUserResponse.getUsername());

        UserProfileGetResponse userProfileGetResponse = testDataProvider.getUserProfile();
        assert userProfileGetResponse != null;

        Validator.Validate("Validate username in userProfile", createUserRequest.getUsername(), userProfileGetResponse.getUserProfile().getUsername());
        Validator.Validate("Validate nickName in userProfile", createUserRequest.getNickName(), userProfileGetResponse.getUserProfile().getNickName());
    }
}
