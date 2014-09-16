/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.store;

import com.junbo.common.error.Error;
import com.junbo.store.spec.model.ChallengeAnswer;
import com.junbo.store.spec.model.identity.*;
import com.junbo.store.spec.model.login.AuthTokenResponse;
import com.junbo.store.spec.model.login.CreateUserRequest;
import com.junbo.store.spec.model.login.UserCredentialRateResponse;
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
import org.apache.commons.lang3.time.DateUtils;
import org.testng.annotations.Test;

import java.util.Date;
import java.util.List;

/**
 * Created by liangfu on 8/29/14.
 */
public class LoginResourceTesting extends BaseTestClass {

    public static String CREDENTIAL_STRENGTH_INVALID = "INVALID";
    public static String CREDENTIAL_STRENGTH_WEAK = "WEAK";
    public static String CREDENTIAL_STRENGTH_FAIR = "FAIR";
    public static String CREDENTIAL_STRENGTH_STRONG = "STRONG";

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
        UserNameCheckResponse userNameCheckResponse = null;
        Error error = testDataProvider.CheckUserNameWithError(invalidUsername, 400, "130.001");
        assert error != null;
        assert error.getDetails().get(0).getField().equalsIgnoreCase("username");
        assert error.getDetails().get(0).getReason().equalsIgnoreCase("Field value is invalid.");

        CreateUserRequest createUserRequest = testDataProvider.CreateUserRequest();
        userNameCheckResponse = testDataProvider.CheckUserName(createUserRequest.getUsername());
        Validator.Validate("Validate valid username", userNameCheckResponse.getIsAvailable(), true);

        AuthTokenResponse authTokenResponse = testDataProvider.CreateUser(createUserRequest, false);
        Validator.Validate("validate authtoken response correct", createUserRequest.getUsername(), authTokenResponse.getUsername());

        error = testDataProvider.CheckUserNameWithError(invalidUsername, 400, "130.001");
        assert error != null;
        assert error.getDetails().get(0).getField().equalsIgnoreCase("username");
        assert error.getDetails().get(0).getReason().equalsIgnoreCase("Field value is invalid.");

        userNameCheckResponse = testDataProvider.CheckUserName(createUserRequest.getUsername());
        Validator.Validate("Validate duplicate username", userNameCheckResponse.getIsAvailable(), false);

        userNameCheckResponse = testDataProvider.CheckUserName(RandomHelper.randomAlphabetic(15));
        Validator.Validate("Validate random character username", userNameCheckResponse.getIsAvailable(), true);

        error = testDataProvider.CheckUserNameWithError(null, 400, "130.001");
        Validator.Validate("Validate null username error response", true, error != null);

        error = testDataProvider.CheckUserNameWithError("", 400, "130.001");
        Validator.Validate("Validate empty username error response", true, error != null);
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
        UserNameCheckResponse userNameCheckResponse =null;
        Error error = testDataProvider.CheckEmailWithError(invalidEmail, 400, "130.001");
        assert error != null;
        assert error.getDetails().get(0).getField().equalsIgnoreCase("email");
        assert error.getDetails().get(0).getReason().contains("Field value is invalid.");

        invalidEmail = "###1212@silkcloud.com";
        error = testDataProvider.CheckEmailWithError(invalidEmail, 400, "130.001");assert error != null;
        assert error.getDetails().get(0).getField().equalsIgnoreCase("email");
        assert error.getDetails().get(0).getReason().contains("Field value is invalid.");

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

        error = testDataProvider.CheckEmailWithError(invalidEmail, 400, "130.001");
        assert error != null;
        assert error.getDetails().get(0).getField().equalsIgnoreCase("email");
        assert error.getDetails().get(0).getReason().contains("Field value is invalid.");

        userNameCheckResponse = testDataProvider.CheckEmail(createUserRequest.getEmail());
        Validator.Validate("Validate duplicate email", userNameCheckResponse.getIsAvailable(), false);

        userNameCheckResponse = testDataProvider.CheckEmail(RandomHelper.randomEmail());
        Validator.Validate("Validate random character email", userNameCheckResponse.getIsAvailable(), true);

        error = testDataProvider.CheckEmailWithError(null, 400, "130.001");
        assert error != null;

        error = testDataProvider.CheckEmailWithError("", 400, "130.001");
        assert error != null;
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
        AuthTokenResponse createUserResponse = null;
        CreateUserRequest createUserRequest = testDataProvider.CreateUserRequest();
        String invalidUsername = "123yunlong";
        String oldUsername = createUserRequest.getUsername();
        createUserRequest.setUsername(invalidUsername);
        Error error = testDataProvider.CreateUserWithError(createUserRequest, true, 400, "130.001");
        assert error != null;
        assert error.getDetails().get(0).getField().contains("username");

        createUserRequest.setUsername(oldUsername);
        String oldEmail = createUserRequest.getEmail();
        createUserRequest.setEmail("##1234@silkcloud.com");
        error = testDataProvider.CreateUserWithError(createUserRequest, true, 400, "130.001");
        assert error != null;
        assert error.getDetails().get(0).getField().contains("email");

        createUserRequest.setUsername(null);
        error = testDataProvider.CreateUserWithError(createUserRequest, true, 400, "130.001");
        assert error != null;
        assert error.getDetails().get(0).getField().contains("username");
        createUserRequest.setUsername(oldUsername);

        createUserRequest.setEmail(oldEmail);
        String oldNickName = createUserRequest.getNickName();
        // nick name should not be the same as username
        createUserRequest.setNickName(createUserRequest.getUsername());
        error = testDataProvider.CreateUserWithError(createUserRequest, true, 400, "130.001");
        assert error != null;
        assert error.getDetails().get(0).getField().contains("username");

        createUserRequest.setNickName(oldNickName);
        String oldPassword = createUserRequest.getPassword();
        createUserRequest.setPassword(createUserRequest.getUsername() + "gggg");
        error = testDataProvider.CreateUserWithError(createUserRequest, true, 400, "130.001");
        assert error != null;
        assert error.getDetails().get(0).getField().contains("password");

        createUserRequest.setPassword(oldPassword);
        String oldPin = createUserRequest.getPin();
        createUserRequest.setPin("abcd");
        error = testDataProvider.CreateUserWithError(createUserRequest, true, 400, "130.001");
        assert error != null;
        assert error.getDetails().get(0).getField().contains("pin");

        createUserRequest.setPin(oldPin);
        Date oldDob = createUserRequest.getDob();
        createUserRequest.setDob(DateUtils.addYears(new Date(), 100));
        error = testDataProvider.CreateUserWithError(createUserRequest, true, 400, "130.001");
        assert error != null;
        assert error.getDetails().get(0).getField().contains("dob");

        createUserRequest.setDob(DateUtils.addYears(new Date(), -11));
        error = testDataProvider.CreateUserWithError(createUserRequest, true, 400, "130.001");
        assert error != null;


        createUserRequest.setDob(oldDob);
        oldPassword = createUserRequest.getPassword();
        createUserRequest.setPassword(null);
        error = testDataProvider.CreateUserWithError(createUserRequest, true, 400, "130.001");
        assert error != null;
        assert error.getDetails().get(0).getField().contains("password");
        createUserRequest.setPassword(oldPassword);

        createUserResponse = testDataProvider.CreateUser(createUserRequest, true, 200);
        Validator.Validate("Validate username created successfully", createUserRequest.getUsername(), createUserResponse.getUsername());

        // validate create with same username failure
        error = testDataProvider.CreateUserWithError(createUserRequest, true, 409, "131.002");
        assert error != null;
        assert error.getDetails().get(0).getField().contains("username");
        assert error.getDetails().get(0).getReason().contains("Field value is duplicate");

        UserProfileGetResponse userProfileGetResponse = testDataProvider.getUserProfile();
        assert userProfileGetResponse != null;

        Validator.Validate("Validate username in userProfile", createUserRequest.getUsername(), userProfileGetResponse.getUserProfile().getUsername());
        Validator.Validate("Validate nickName in userProfile", createUserRequest.getNickName(), userProfileGetResponse.getUserProfile().getNickName());
    }

    @Property(
            priority = Priority.Dailies,
            features = "Store",
            component = Component.STORE,
            owner = "ZhaoYunlong",
            status = Status.Enable,
            steps = {
                    "Check rate credential"
            }
    )
    @Test
    public void testRateCredential() throws Exception {
        String password = "123456";
        UserCredentialRateResponse response = testDataProvider.RateUserCredential(password);
        Validator.Validate("validate invalid password", response.getStrength(), CREDENTIAL_STRENGTH_INVALID);

        password = "12345678";
        response = testDataProvider.RateUserCredential(password);
        Validator.Validate("validate weak character password", response.getStrength(), CREDENTIAL_STRENGTH_WEAK);

        password = "ABCDEFGHIJK";
        response = testDataProvider.RateUserCredential(password);
        Validator.Validate("validate weak character password", response.getStrength(), CREDENTIAL_STRENGTH_WEAK);

        password = "abcdefghijklmn";
        response = testDataProvider.RateUserCredential(password);
        Validator.Validate("validate weak character password", response.getStrength(), CREDENTIAL_STRENGTH_WEAK);

        password = "abcdEFGH#";
        response = testDataProvider.RateUserCredential(password);
        Validator.Validate("validate fair character password", response.getStrength(), CREDENTIAL_STRENGTH_FAIR);

        password = RandomHelper.randomAlphabetic(1000) + RandomHelper.randomNumeric(1000);
        response = testDataProvider.RateUserCredential(password);
        Validator.Validate("validate strong character password", response.getStrength(), CREDENTIAL_STRENGTH_STRONG);

        password = "abcdEFG#$1223";
        response = testDataProvider.RateUserCredential(password);
        Validator.Validate("validate strong character password", response.getStrength(), CREDENTIAL_STRENGTH_STRONG);

        String username = "abcdefg";
        response = testDataProvider.RateUserCredential(password, username);
        Validator.Validate("validate invalid character password", response.getStrength(), CREDENTIAL_STRENGTH_INVALID);

        password = null;
        Error error = testDataProvider.RateUserCredentialWithError(password, RandomHelper.randomAlphabetic(10), 400, "130.001");
        assert error != null;
        assert error.getDetails().get(0).getField().contains("userCredential.value");
        assert error.getDetails().get(0).getReason().contains("Field is required");

        password = "";
        error = testDataProvider.RateUserCredentialWithError(password, RandomHelper.randomAlphabetic(10), 400, "130.001");
        assert error != null;
        assert error.getDetails().get(0).getField().contains("userCredential.value");
        assert error.getDetails().get(0).getReason().contains("Field is required");
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
    public void testLogin() throws Exception {
        CreateUserRequest createUserRequest = testDataProvider.CreateUserRequest();
        AuthTokenResponse authTokenResponse = testDataProvider.CreateUser(createUserRequest, true);

        assert authTokenResponse.getUsername() != null;
        AuthTokenResponse signInResponse = testDataProvider.SignIn(createUserRequest.getUsername(), createUserRequest.getPassword());
        Validator.Validate("validate createdToken equals to signIn token", authTokenResponse.getUsername(), signInResponse.getUsername());

        signInResponse = testDataProvider.SignIn(createUserRequest.getUsername(), RandomHelper.randomAlphabetic(15), 412);
        assert signInResponse == null;

        signInResponse = testDataProvider.SignIn(createUserRequest.getUsername(), createUserRequest.getPassword());
        Validator.Validate("validate createdToken equals to signIn token", authTokenResponse.getUsername(), signInResponse.getUsername());

        signInResponse = testDataProvider.SignIn(createUserRequest.getEmail(), createUserRequest.getPassword());
        Validator.Validate("validate createdToken equals to signIn token through email login", authTokenResponse.getUsername(), signInResponse.getUsername());

        signInResponse = testDataProvider.SignIn(createUserRequest.getEmail(), RandomHelper.randomAlphabetic(15), 412);
        assert signInResponse == null;

        signInResponse = testDataProvider.SignIn(createUserRequest.getEmail(), createUserRequest.getPassword());
        Validator.Validate("validate createdToken equals to signIn token through email login", authTokenResponse.getUsername(), signInResponse.getUsername());

        UserProfileUpdateRequest userProfileUpdateRequest = new UserProfileUpdateRequest();
        StoreUserProfile storeUserProfile = new StoreUserProfile();
        String newPassword = RandomHelper.randomAlphabetic(6) + RandomHelper.randomNumeric(5);
        storeUserProfile.setPassword(newPassword);
        userProfileUpdateRequest.setUserProfile(storeUserProfile);

        UserProfileUpdateResponse userProfileUpdateResponse = testDataProvider.updateUserProfile(userProfileUpdateRequest);
        assert userProfileUpdateResponse.getChallenge() != null;
        assert userProfileUpdateResponse.getChallenge().getType().equalsIgnoreCase("PASSWORD");

        ChallengeAnswer challengeAnswer = new ChallengeAnswer();
        challengeAnswer.setType("PASSWORD");
        challengeAnswer.setPassword(createUserRequest.getPassword());
        userProfileUpdateRequest.setChallengeAnswer(challengeAnswer);
        userProfileUpdateResponse = testDataProvider.updateUserProfile(userProfileUpdateRequest);
        assert userProfileUpdateResponse != null;

        signInResponse = testDataProvider.SignIn(createUserRequest.getUsername(), newPassword);
        Validator.Validate("validate signIn token equals to the current user", createUserRequest.getUsername(), signInResponse.getUsername());

        signInResponse = testDataProvider.SignIn(createUserRequest.getEmail(), newPassword);
        Validator.Validate("validate signIn token equals to current user with username login", createUserRequest.getUsername(), signInResponse.getUsername());
    }

    @Property(
            priority = Priority.Dailies,
            features = "Store",
            component = Component.STORE,
            owner = "ZhaoYunlong",
            status = Status.Enable,
            steps = {
                    "Check login negative case"
            }
    )
    @Test
    public void testLoginInvalid() throws Exception {
        CreateUserRequest createUserRequest = testDataProvider.CreateUserRequest();
        AuthTokenResponse authTokenResponse = testDataProvider.CreateUser(createUserRequest, true);
        assert authTokenResponse !=  null;

        Error error = testDataProvider.SignInWithError(createUserRequest.getUsername(), "PIN", "1234", 400, "130.001");
        assert error != null;
        assert error.getDetails().get(0).getField().contains("userCredential.type");
        assert error.getDetails().get(0).getReason().contains("Field value is invalid. type must be PASSWORD");

        error = testDataProvider.SignInWithError(createUserRequest.getUsername(), "PASSWORD", RandomHelper.randomAlphabetic(10), 412, "132.103");
        assert error != null;
        assert error.getDetails().get(0).getField().contains("password");

        error = testDataProvider.SignInWithError(RandomHelper.randomAlphabetic(10), "PASSWORD", createUserRequest.getPassword(), 412, "132.103");
        assert error != null;
        assert error.getDetails().get(0).getField().contains("username");

        error = testDataProvider.SignInWithError(createUserRequest.getUsername(), "PASSWORD", null, 400, "130.001");
        assert error != null;
        assert error.getDetails().get(0).getField().contains("userCredential.value");
        assert error.getDetails().get(0).getReason().contains("Field is required");

        authTokenResponse = testDataProvider.SignIn(createUserRequest.getUsername(), createUserRequest.getPassword());
        assert authTokenResponse != null;
        assert authTokenResponse.getUsername().equals(createUserRequest.getUsername());
    }

    @Property(
            priority = Priority.Dailies,
            features = "Store",
            component = Component.STORE,
            owner = "ZhaoYunlong",
            status = Status.Enable,
            steps = {
                    "Check refresh token works"
            }
    )
    @Test
    public void testRefreshToken() throws Exception {
        CreateUserRequest createUserRequest = testDataProvider.CreateUserRequest();
        AuthTokenResponse authTokenResponse = testDataProvider.CreateUser(createUserRequest, true);

        AuthTokenResponse response = testDataProvider.getToken(authTokenResponse.getRefreshToken());
        Validator.Validate("Validate refreshToken works", response.getUsername(), authTokenResponse.getUsername());

        Error error = testDataProvider.getTokenWithError(authTokenResponse.getAccessToken(), 400, "132.001");
        assert error != null;
        assert error.getDetails().get(0).getField().contains("refresh_token");
        assert error.getDetails().get(0).getReason().contains("Field value is invalid.");

        error = testDataProvider.getTokenWithError(null, 400, "130.001");
        assert error != null;
        assert error.getDetails().get(0).getField().contains("refreshToken");
        assert error.getDetails().get(0).getReason().contains("Field is required");

        error = testDataProvider.getTokenWithError("", 400, "130.001");
        assert error != null;
        assert error.getDetails().get(0).getField().contains("refreshToken");
        assert error.getDetails().get(0).getReason().contains("Field is required");

        error = testDataProvider.getTokenWithError("       ", 400, "130.001");
        assert error != null;
        assert error.getDetails().get(0).getField().contains("refreshToken");
        assert error.getDetails().get(0).getReason().contains("Field is required");

        error = testDataProvider.getTokenWithError(authTokenResponse.getRefreshToken(), 400, "132.001");
        assert error != null;
        assert error.getDetails().get(0).getField().contains("refresh_token");
        assert error.getDetails().get(0).getReason().contains("Field value is invalid.");
    }

    @Property(
            priority = Priority.Dailies,
            features = "Store",
            component = Component.STORE,
            owner = "ZhaoYunlong",
            status = Status.Enable,
            steps = {
                    "Check update email works"
            }
    )
    @Test
    public void testUpdateEmail() throws Exception {
        CreateUserRequest createUserRequest = testDataProvider.CreateUserRequest();
        AuthTokenResponse authTokenResponse = testDataProvider.CreateUser(createUserRequest, true);

        UserProfileUpdateRequest userProfileUpdateRequest = new UserProfileUpdateRequest();
        StoreUserProfile storeUserProfile = new StoreUserProfile();
        StoreUserEmail storeUserEmail = new StoreUserEmail();
        String newEmail = RandomHelper.randomEmail();
        storeUserEmail.setValue(newEmail);
        storeUserProfile.setEmail(storeUserEmail);
        userProfileUpdateRequest.setUserProfile(storeUserProfile);

        UserProfileUpdateResponse userProfileUpdateResponse = testDataProvider.updateUserProfile(userProfileUpdateRequest);
        assert userProfileUpdateResponse.getChallenge() != null;
        assert userProfileUpdateResponse.getChallenge().getType().equalsIgnoreCase("PASSWORD");

        ChallengeAnswer answer = new ChallengeAnswer();
        answer.setType(userProfileUpdateResponse.getChallenge().getType());
        answer.setPassword(createUserRequest.getPassword());
        userProfileUpdateRequest.setChallengeAnswer(answer);
        userProfileUpdateResponse = testDataProvider.updateUserProfile(userProfileUpdateRequest);
        assert userProfileUpdateResponse != null;
        assert userProfileUpdateResponse.getUserProfile().getEmail().getValue().equalsIgnoreCase(createUserRequest.getEmail());

        oAuthClient.postAccessToken(GrantType.CLIENT_CREDENTIALS, ComponentType.SMOKETEST);
        List<String> links = oAuthClient.getEmailVerifyLink(IdConverter.idToHexString(authTokenResponse.getUserId()), newEmail);
        assert links != null;
        for(String link : links) {
            oAuthClient.accessEmailVerifyLink(link);
        }

        UserProfileGetResponse userProfileGetResponse = testDataProvider.getUserProfile();
        assert userProfileGetResponse.getUserProfile().getEmail().getValue().equalsIgnoreCase(newEmail);

        AuthTokenResponse response = testDataProvider.SignIn(newEmail, createUserRequest.getPassword());
        assert response.getUsername().equalsIgnoreCase(createUserRequest.getUsername());
    }

    @Property(
            priority = Priority.Dailies,
            features = "Store",
            component = Component.STORE,
            owner = "ZhaoYunlong",
            status = Status.Enable,
            steps = {
                    "Check update email in invalid situation, all those steps should be done after password input",
                    "1. check if the update link is not clicked, email cannot be updated",
                    "2. check if the update link is not clicked, update again, it will generate two emails",
                    "3. check if the update link is clicked, the primary email is updated",
                    "4. check if there is no default validated email, the primary email can be updated with new email, should throw exception",
                    "5. check if the password isn't correct, link mail isn't sent and there is no "
            }
    )
    @Test
    public void testUpdateEmailInvalidCases() throws Exception {
        CreateUserRequest createUserRequest = testDataProvider.CreateUserRequest();
        AuthTokenResponse authTokenResponse = testDataProvider.CreateUser(createUserRequest, true);

        // Without password, there should be new mail sent
        UserProfileUpdateRequest userProfileUpdateRequest = new UserProfileUpdateRequest();
        StoreUserProfile storeUserProfile = new StoreUserProfile();
        StoreUserEmail storeUserEmail = new StoreUserEmail();
        String newEmail = RandomHelper.randomEmail();
        storeUserEmail.setValue(newEmail);
        storeUserProfile.setEmail(storeUserEmail);
        userProfileUpdateRequest.setUserProfile(storeUserProfile);
        UserProfileUpdateResponse userProfileUpdateResponse = testDataProvider.updateUserProfile(userProfileUpdateRequest);
        assert userProfileUpdateResponse.getChallenge() != null;
        assert userProfileUpdateResponse.getChallenge().getType().equalsIgnoreCase("PASSWORD");

        oAuthClient.postAccessToken(GrantType.CLIENT_CREDENTIALS, ComponentType.SMOKETEST);
        List<String> links = oAuthClient.getEmailVerifyLink(IdConverter.idToHexString(authTokenResponse.getUserId()), newEmail);
        assert links != null;
        assert links.size() == 0;

        // Scenario 1:
        ChallengeAnswer answer = new ChallengeAnswer();
        answer.setType(userProfileUpdateResponse.getChallenge().getType());
        answer.setPassword(createUserRequest.getPassword());
        userProfileUpdateRequest.setChallengeAnswer(answer);
        userProfileUpdateResponse = testDataProvider.updateUserProfile(userProfileUpdateRequest);
        assert userProfileUpdateResponse != null;
        assert userProfileUpdateResponse.getUserProfile().getEmail().getValue().equalsIgnoreCase(createUserRequest.getEmail());

        links = oAuthClient.getEmailVerifyLink(IdConverter.idToHexString(authTokenResponse.getUserId()), newEmail);
        assert links != null;
        assert links.size() == 1;

        UserProfileGetResponse userProfileGetResponse = testDataProvider.getUserProfile();
        assert userProfileGetResponse != null;
        assert userProfileGetResponse.getUserProfile().getEmail().getValue().equalsIgnoreCase(createUserRequest.getEmail());

        // Scenario 2:
        userProfileUpdateResponse = testDataProvider.updateUserProfile(userProfileUpdateRequest);
        assert userProfileUpdateResponse != null;
        assert userProfileUpdateResponse.getUserProfile().getEmail().getValue().equalsIgnoreCase(createUserRequest.getEmail());

        links = oAuthClient.getEmailVerifyLink(IdConverter.idToHexString(authTokenResponse.getUserId()), newEmail);
        assert links != null;
        assert links.size() == 2;

        userProfileGetResponse = testDataProvider.getUserProfile();
        assert userProfileGetResponse != null;
        assert userProfileGetResponse.getUserProfile().getEmail().getValue().equalsIgnoreCase(createUserRequest.getEmail());

        // Scenario 3:
        oAuthClient.accessEmailVerifyLink(links.get(0));
        userProfileGetResponse = testDataProvider.getUserProfile();
        assert userProfileGetResponse != null;
        assert userProfileGetResponse.getUserProfile().getEmail().getValue().equalsIgnoreCase(newEmail);

        // Scenario 4:
        createUserRequest = testDataProvider.CreateUserRequest();
        authTokenResponse = testDataProvider.CreateUser(createUserRequest, false);

        newEmail = RandomHelper.randomEmail();
        userProfileUpdateRequest.getUserProfile().getEmail().setValue(newEmail);
        userProfileUpdateRequest.setChallengeAnswer(null);
        userProfileUpdateResponse = testDataProvider.updateUserProfile(userProfileUpdateRequest, 412);

        links = oAuthClient.getEmailVerifyLink(IdConverter.idToHexString(authTokenResponse.getUserId()), createUserRequest.getEmail());
        assert links != null;
        assert links.size() == 1;
        oAuthClient.accessEmailVerifyLink(links.get(0));

        userProfileUpdateResponse = testDataProvider.updateUserProfile(userProfileUpdateRequest);

        answer = new ChallengeAnswer();
        answer.setType(userProfileUpdateResponse.getChallenge().getType());
        answer.setPassword(createUserRequest.getPassword());
        userProfileUpdateRequest.setChallengeAnswer(answer);
        userProfileUpdateResponse = testDataProvider.updateUserProfile(userProfileUpdateRequest);

        assert userProfileUpdateResponse != null;
        assert userProfileUpdateResponse.getUserProfile().getEmail().getValue().equalsIgnoreCase(createUserRequest.getEmail());

        links = oAuthClient.getEmailVerifyLink(IdConverter.idToHexString(authTokenResponse.getUserId()), newEmail);
        assert links != null;
        assert links.size() == 1;
        oAuthClient.accessEmailVerifyLink(links.get(0));

        userProfileGetResponse = testDataProvider.getUserProfile();
        assert userProfileGetResponse != null;
        assert userProfileGetResponse.getUserProfile().getEmail().getValue().equalsIgnoreCase(newEmail);

        // Scenario 5:
        createUserRequest = testDataProvider.CreateUserRequest();
        authTokenResponse = testDataProvider.CreateUser(createUserRequest, true);

        newEmail = RandomHelper.randomEmail();
        userProfileUpdateRequest.getUserProfile().getEmail().setValue(newEmail);
        userProfileUpdateRequest.setChallengeAnswer(null);
        String newHeadLine = RandomHelper.randomNumeric(100);
        userProfileUpdateRequest.getUserProfile().setHeadline(newHeadLine);
        userProfileUpdateResponse = testDataProvider.updateUserProfile(userProfileUpdateRequest, 200);

        answer = new ChallengeAnswer();
        answer.setType(userProfileUpdateResponse.getChallenge().getType());
        answer.setPassword(RandomHelper.randomAlphabetic(100));
        userProfileUpdateRequest.setChallengeAnswer(answer);
        Error error = testDataProvider.updateUserProfileWithError(userProfileUpdateRequest, 400, "130.108");
        assert error != null;
        assert error.getMessage().equalsIgnoreCase("Invalid Challenge Answer.");

        links = oAuthClient.getEmailVerifyLink(IdConverter.idToHexString(authTokenResponse.getUserId()), newEmail);
        assert links != null;
        assert links.size() == 0;

        userProfileGetResponse = testDataProvider.getUserProfile();
        assert userProfileGetResponse != null;
        assert userProfileGetResponse.getUserProfile().getEmail().getValue().equalsIgnoreCase(createUserRequest.getEmail());
        assert userProfileGetResponse.getUserProfile().getHeadline() == null;

        answer = new ChallengeAnswer();
        answer.setType(userProfileUpdateResponse.getChallenge().getType());
        answer.setPassword(createUserRequest.getPassword());
        userProfileUpdateRequest.setChallengeAnswer(answer);
        userProfileUpdateResponse = testDataProvider.updateUserProfile(userProfileUpdateRequest);
        assert userProfileUpdateResponse != null;
        assert userProfileUpdateResponse.getUserProfile().getHeadline().equalsIgnoreCase(newHeadLine);
        assert userProfileUpdateResponse.getUserProfile().getEmail().getValue().equalsIgnoreCase(createUserRequest.getEmail());

        userProfileGetResponse = testDataProvider.getUserProfile();
        assert userProfileGetResponse != null;
        assert userProfileGetResponse.getUserProfile().getHeadline().equalsIgnoreCase(newHeadLine);
        assert userProfileGetResponse.getUserProfile().getEmail().getValue().equalsIgnoreCase(createUserRequest.getEmail());

        links = oAuthClient.getEmailVerifyLink(IdConverter.idToHexString(authTokenResponse.getUserId()), newEmail);
        assert links != null;
        assert links.size() == 1;
        oAuthClient.accessEmailVerifyLink(links.get(0));

        userProfileGetResponse = testDataProvider.getUserProfile();
        assert userProfileGetResponse != null;
        assert userProfileGetResponse.getUserProfile().getHeadline().equalsIgnoreCase(newHeadLine);
        assert userProfileGetResponse.getUserProfile().getEmail().getValue().equalsIgnoreCase(newEmail);
        assert userProfileGetResponse.getUserProfile().getEmail().getIsValidated();
    }

    @Property(
            priority = Priority.Dailies,
            features = "Store",
            component = Component.STORE,
            owner = "ZhaoYunlong",
            status = Status.Enable,
            steps = {
                    "Check update email works with invalid password"
            }
    )
    @Test
    public void testUpdateEmailWithInvalidPassword() throws Exception {
        CreateUserRequest createUserRequest = testDataProvider.CreateUserRequest();
        AuthTokenResponse authTokenResponse = testDataProvider.CreateUser(createUserRequest, true);

        UserProfileUpdateRequest userProfileUpdateRequest = new UserProfileUpdateRequest();
        StoreUserProfile storeUserProfile = new StoreUserProfile();
        StoreUserEmail storeUserEmail = new StoreUserEmail();
        String newEmail = RandomHelper.randomEmail();
        storeUserEmail.setValue(newEmail);
        storeUserProfile.setEmail(storeUserEmail);
        userProfileUpdateRequest.setUserProfile(storeUserProfile);
        UserProfileUpdateResponse userProfileUpdateResponse = testDataProvider.updateUserProfile(userProfileUpdateRequest);
        assert userProfileUpdateResponse.getChallenge() != null;
        assert userProfileUpdateResponse.getChallenge().getType().equalsIgnoreCase("PASSWORD");

        oAuthClient.postAccessToken(GrantType.CLIENT_CREDENTIALS, ComponentType.SMOKETEST);
        List<String> links = oAuthClient.getEmailVerifyLink(IdConverter.idToHexString(authTokenResponse.getUserId()), newEmail);
        assert links != null;
        for(String link : links) {
            oAuthClient.accessEmailVerifyLink(link);
        }

        for (int i = 0; i < 5; i++) {
            ChallengeAnswer answer = new ChallengeAnswer();
            answer.setType(userProfileUpdateResponse.getChallenge().getType());
            answer.setPassword(RandomHelper.randomAlphabetic(10));
            userProfileUpdateRequest.setChallengeAnswer(answer);
            com.junbo.common.error.Error appError = testDataProvider.updateUserProfile(userProfileUpdateRequest, 400, "130.108");
            assert appError != null;
        }

        // check maximum retry count
        ChallengeAnswer answer = new ChallengeAnswer();
        answer.setType(userProfileUpdateResponse.getChallenge().getType());
        answer.setPassword(createUserRequest.getPassword());
        com.junbo.common.error.Error appError = testDataProvider.updateUserProfile(userProfileUpdateRequest, 400, "130.108");
        assert appError != null;
    }

    @Property(
            priority = Priority.Dailies,
            features = "Store",
            component = Component.STORE,
            owner = "ZhaoYunlong",
            status = Status.Enable,
            steps = {
                    "Check update email works"
            }
    )
    @Test
    public void testUpdatePin() throws Exception {
        CreateUserRequest createUserRequest = testDataProvider.CreateUserRequest();
        AuthTokenResponse authTokenResponse = testDataProvider.CreateUser(createUserRequest, true);
        assert authTokenResponse.getUsername().equalsIgnoreCase(createUserRequest.getUsername());

        UserProfileUpdateRequest userProfileUpdateRequest = new UserProfileUpdateRequest();
        StoreUserProfile storeUserProfile = new StoreUserProfile();
        String newPin = "5678";
        storeUserProfile.setPin(newPin);
        userProfileUpdateRequest.setUserProfile(storeUserProfile);

        UserProfileUpdateResponse userProfileUpdateResponse = testDataProvider.updateUserProfile(userProfileUpdateRequest);
        assert userProfileUpdateResponse.getChallenge() != null;
        assert userProfileUpdateResponse.getChallenge().getType().equalsIgnoreCase("PASSWORD");

        ChallengeAnswer challengeAnswer = new ChallengeAnswer();
        challengeAnswer.setType("PASSWORD");
        challengeAnswer.setPassword(createUserRequest.getPassword());
        userProfileUpdateRequest.setChallengeAnswer(challengeAnswer);
        userProfileUpdateResponse = testDataProvider.updateUserProfile(userProfileUpdateRequest);
        assert userProfileUpdateResponse != null;
        assert userProfileUpdateResponse.getUserProfile().getUsername().equalsIgnoreCase(createUserRequest.getUsername());
    }

    @Property(
            priority = Priority.Dailies,
            features = "Store",
            component = Component.STORE,
            owner = "ZhaoYunlong",
            status = Status.Enable,
            steps = {
                    "Check update other fields except email, pin and password"
            }
    )
    @Test
    public void testUpdateNoncriticalFields() throws Exception {
        CreateUserRequest createUserRequest = testDataProvider.CreateUserRequest();
        AuthTokenResponse authTokenResponse = testDataProvider.CreateUser(createUserRequest, true);
        assert authTokenResponse.getUsername().equalsIgnoreCase(createUserRequest.getUsername());

        UserProfileUpdateRequest userProfileUpdateRequest = new UserProfileUpdateRequest();
        StoreUserProfile storeUserProfile = new StoreUserProfile();
        String newNickName = RandomHelper.randomAlphabetic(10);
        storeUserProfile.setNickName(newNickName);
        userProfileUpdateRequest.setUserProfile(storeUserProfile);
        UserProfileUpdateResponse response = testDataProvider.updateUserProfile(userProfileUpdateRequest);
        assert response.getUserProfile().getNickName().equalsIgnoreCase(newNickName);

        UserProfileGetResponse userProfileGetResponse = testDataProvider.getUserProfile();
        assert userProfileGetResponse.getUserProfile().getNickName().equalsIgnoreCase(newNickName);
    }

    @Property(
            priority = Priority.Dailies,
            features = "Store",
            component = Component.STORE,
            owner = "ZhaoYunlong",
            status = Status.Enable,
            steps = {
                    "Check update headline and avatar"
            }
    )
    @Test
    public void testHeadLineAndAvator() throws Exception {
        CreateUserRequest createUserRequest = testDataProvider.CreateUserRequest();
        AuthTokenResponse authTokenResponse = testDataProvider.CreateUser(createUserRequest, true);
        assert authTokenResponse.getUsername().equalsIgnoreCase(createUserRequest.getUsername());

        UserProfileUpdateRequest userProfileUpdateRequest = new UserProfileUpdateRequest();
        StoreUserProfile storeUserProfile = new StoreUserProfile();
        storeUserProfile.setHeadline(RandomHelper.randomAlphabetic(15));
        userProfileUpdateRequest.setUserProfile(storeUserProfile);
        UserProfileUpdateResponse userProfileUpdateResponse = testDataProvider.updateUserProfile(userProfileUpdateRequest);
        assert userProfileUpdateResponse.getUserProfile().getHeadline().equalsIgnoreCase(storeUserProfile.getHeadline());

        String newHeadLine = RandomHelper.randomNumeric(10);
        storeUserProfile.setHeadline(newHeadLine);
        userProfileUpdateResponse = testDataProvider.updateUserProfile(userProfileUpdateRequest);
        assert userProfileUpdateResponse.getUserProfile().getHeadline().equalsIgnoreCase(newHeadLine);

        storeUserProfile.setHeadline(null);
        storeUserProfile.setAvatar(RandomHelper.randomAlphabetic(15));
        userProfileUpdateResponse = testDataProvider.updateUserProfile(userProfileUpdateRequest);
        assert userProfileUpdateResponse.getUserProfile().getAvatar().equalsIgnoreCase(storeUserProfile.getAvatar());
        assert userProfileUpdateResponse.getUserProfile().getHeadline().equalsIgnoreCase(newHeadLine);

        String newAvatar = RandomHelper.randomNumeric(20);
        storeUserProfile.setAvatar(newAvatar);
        userProfileUpdateResponse = testDataProvider.updateUserProfile(userProfileUpdateRequest);
        assert userProfileUpdateResponse.getUserProfile().getAvatar().equalsIgnoreCase(storeUserProfile.getAvatar());
        assert userProfileUpdateResponse.getUserProfile().getHeadline().equalsIgnoreCase(newHeadLine);
    }

    @Property(
            priority = Priority.Dailies,
            features = "Store",
            component = Component.STORE,
            owner = "ZhaoYunlong",
            status = Status.Enable,
            steps = {
                    "Check multiple mail send"
            }
    )
    @Test
    public void testMultipleVerificationMail() throws Exception {
        CreateUserRequest createUserRequest = testDataProvider.CreateUserRequest();
        AuthTokenResponse authTokenResponse = testDataProvider.CreateUser(createUserRequest, false);

        oAuthClient.postAccessToken(GrantType.CLIENT_CREDENTIALS, ComponentType.SMOKETEST);
        List<String> links = oAuthClient.getEmailVerifyLink(IdConverter.idToHexString(authTokenResponse.getUserId()), createUserRequest.getEmail());
        assert links != null;
        assert links.size() == 1;

        VerifyEmailResponse response = testDataProvider.verifyEmail(new VerifyEmailRequest());
        assert response != null;
        assert response.getEmailSent();
        oAuthClient.postAccessToken(GrantType.CLIENT_CREDENTIALS, ComponentType.SMOKETEST);
        links = oAuthClient.getEmailVerifyLink(IdConverter.idToHexString(authTokenResponse.getUserId()), createUserRequest.getEmail());
        assert links != null;
        assert links.size() == 2;
        for(String link : links) {
            try {
                oAuthClient.accessEmailVerifyLink(link);
            } catch (Exception e) {
                // do nothing here
            }
        }

        response = testDataProvider.verifyEmail(new VerifyEmailRequest());
        assert response != null;
        assert response.getEmailSent();
        oAuthClient.postAccessToken(GrantType.CLIENT_CREDENTIALS, ComponentType.SMOKETEST);
        links = oAuthClient.getEmailVerifyLink(IdConverter.idToHexString(authTokenResponse.getUserId()), createUserRequest.getEmail());
        assert links != null;
        assert links.size() == 1;
    }
}
