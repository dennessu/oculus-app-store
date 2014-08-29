/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.store;

import com.junbo.store.spec.model.login.AuthTokenResponse;
import com.junbo.store.spec.model.login.CreateUserRequest;
import com.junbo.store.spec.model.login.UserNameCheckResponse;
import com.junbo.test.common.RandomHelper;
import com.junbo.test.common.Validator;
import com.junbo.test.common.property.Component;
import com.junbo.test.common.property.Priority;
import com.junbo.test.common.property.Property;
import com.junbo.test.common.property.Status;
import org.testng.annotations.Test;

/**
 * Created by liangfu on 8/29/14.
 */
public class LoginResourceTesting extends BaseTestClass {

    @Property(
            priority = Priority.Dailies,
            features = "Store",
            component = Component.STORE,
            owner = "ZhaoYunlong",
            status = Status.Enable,
            steps = {
                    "Check username with "
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
}
