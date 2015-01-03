/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.store;

import com.junbo.common.error.Error;
import com.junbo.common.id.OfferId;
import com.junbo.common.model.Results;
import com.junbo.common.util.IdFormatter;
import com.junbo.identity.spec.v1.model.Country;
import com.junbo.store.spec.model.ChallengeAnswer;
import com.junbo.store.spec.model.browse.document.Tos;
import com.junbo.store.spec.model.identity.*;
import com.junbo.store.spec.model.login.*;
import com.junbo.test.common.Entities.enums.ComponentType;
import com.junbo.test.common.RandomHelper;
import com.junbo.test.common.Validator;
import com.junbo.test.common.apihelper.identity.CountryService;
import com.junbo.test.common.apihelper.identity.impl.CountryServiceImpl;
import com.junbo.test.common.apihelper.oauth.OAuthService;
import com.junbo.test.common.apihelper.oauth.enums.GrantType;
import com.junbo.test.common.apihelper.oauth.impl.OAuthServiceImpl;
import com.junbo.test.common.blueprint.Master;
import com.junbo.test.common.libs.IdConverter;
import com.junbo.test.common.libs.RandomFactory;
import com.junbo.test.common.property.Component;
import com.junbo.test.common.property.Priority;
import com.junbo.test.common.property.Property;
import com.junbo.test.common.property.Status;
import com.junbo.test.store.apihelper.TestContext;
import org.apache.commons.lang3.time.DateUtils;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.net.URLEncoder;
import java.util.*;

/**
 * Created by liangfu on 8/29/14.
 */
public class LoginResourceTesting extends BaseTestClass {

    public static String CREDENTIAL_STRENGTH_INVALID = "INVALID";
    public static String CREDENTIAL_STRENGTH_WEAK = "WEAK";
    public static String CREDENTIAL_STRENGTH_FAIR = "FAIR";
    public static String CREDENTIAL_STRENGTH_STRONG = "STRONG";
    public static Integer CREDENTIAL_ATTEMPT_COUNT = 3;

    OAuthService oAuthClient = OAuthServiceImpl.getInstance();
    CountryService countryService = CountryServiceImpl.instance();

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
        String invalidUsername = "---123Test";
        UserNameCheckResponse userNameCheckResponse = null;
        Error error = testDataProvider.CheckUserNameWithError(invalidUsername, RandomHelper.randomEmail(), 400, "130.001");
        assert error != null;
        assert error.getDetails().get(0).getField().equalsIgnoreCase("username");
        assert error.getDetails().get(0).getReason().equalsIgnoreCase("Field value is invalid.");

        CreateUserRequest createUserRequest = testDataProvider.CreateUserRequest();
        userNameCheckResponse = testDataProvider.CheckUserName(createUserRequest.getUsername(), RandomHelper.randomEmail());
        Validator.Validate("Validate valid username", userNameCheckResponse.getIsAvailable(), true);

        AuthTokenResponse authTokenResponse = testDataProvider.CreateUser(createUserRequest, false);
        Validator.Validate("validate authtoken response correct", createUserRequest.getUsername(), authTokenResponse.getUsername());

        error = testDataProvider.CheckUserNameWithError(invalidUsername, RandomHelper.randomEmail(), 400, "130.001");
        assert error != null;
        assert error.getDetails().get(0).getField().equalsIgnoreCase("username");
        assert error.getDetails().get(0).getReason().equalsIgnoreCase("Field value is invalid.");

        userNameCheckResponse = testDataProvider.CheckUserName(createUserRequest.getUsername(), RandomHelper.randomEmail());
        Validator.Validate("Validate duplicate username", userNameCheckResponse.getIsAvailable(), false);

        userNameCheckResponse = testDataProvider.CheckUserName(RandomHelper.randomAlphabetic(15), RandomHelper.randomEmail());
        Validator.Validate("Validate random character username", userNameCheckResponse.getIsAvailable(), true);

        error = testDataProvider.CheckUserNameWithError(null, RandomHelper.randomEmail(), 400, "130.001");
        Validator.Validate("Validate null username error response", true, error != null);

        error = testDataProvider.CheckUserNameWithError("", RandomHelper.randomEmail(), 400, "130.001");
        Validator.Validate("Validate empty username error response", true, error != null);

        error = testDataProvider.CheckUserNameWithError(RandomHelper.randomAlphabetic(10) + "  ", RandomHelper.randomEmail(), 400, "130.001");
        Validator.Validate("Validate consecutive space error response", true, error != null);

        error = testDataProvider.CheckUserNameWithError(RandomHelper.randomAlphabetic(10) + "___", RandomHelper.randomEmail(), 400, "130.001");
        Validator.Validate("Validate consecutive underscores response", true, error != null);

        error = testDataProvider.CheckUserNameWithError(RandomHelper.randomAlphabetic(10) + "----", RandomHelper.randomEmail(), 400, "130.001");
        Validator.Validate("Validate consecutive dash response", true, error != null);

        error = testDataProvider.CheckUserNameWithError(RandomHelper.randomAlphabetic(10) + "...", RandomHelper.randomEmail(), 400, "130.001");
        Validator.Validate("Validate consecutive period response", true, error != null);
    }

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
    public void testGetSupportCountries() throws Exception {
        GetSupportedCountriesResponse response = testDataProvider.GetSupportedCountries();
        Results<Country> countryResults = countryService.getAllCountries();

        assert response.getSupportedCountries().size() == countryResults.getItems().size();
        for(String countryCode : response.getSupportedCountries()) {
            boolean found = false;
            for (Country country : countryResults.getItems()) {
                if (country.getCountryCode().equals(countryCode)) {
                    found = true;
                    break;
                }
            }

            Validator.Validate("validate countryCode " + countryCode, true, found);
        }
    }

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
    public void testCheckUsernameWithInvalidMail() throws Exception {
        CreateUserRequest createUserRequest = testDataProvider.CreateUserRequest();
        String username = createUserRequest.getUsername();
        String invalidEmail = RandomHelper.randomAlphabetic(10);

        Error error = testDataProvider.CheckUserNameWithError(username, invalidEmail, 400, "130.001");
        assert error != null;
        assert error.getDetails().get(0).getField().equalsIgnoreCase("email");
        assert error.getDetails().get(0).getReason().equalsIgnoreCase("Field value is invalid.");

        testDataProvider.CreateUser(createUserRequest, false);
        UserNameCheckResponse response = testDataProvider.CheckUserName(username, RandomHelper.randomEmail());
        assert response.getIsAvailable() == false;

        response = testDataProvider.CheckUserName(RandomHelper.randomAlphabetic(15), createUserRequest.getEmail());
        assert response.getIsAvailable() == false;

        response = testDataProvider.CheckUserName(RandomHelper.randomAlphabetic(15), RandomHelper.randomEmail());
        assert response.getIsAvailable();
    }

    @Property(
            priority = Priority.Dailies,
            features = "Store",
            component = Component.STORE,
            owner = "ZhaoYunlong",
            status = Status.Disable,
            steps = {
                    "Check username"
            }
    )
    @Test
    public void testCheckUsernameMailBlocker() throws Exception {
        CreateUserRequest createUserRequest = testDataProvider.CreateUserRequest();
        String email = createUserRequest.getEmail();
        String username = createUserRequest.getUsername();
        testDataProvider.PrepareUsernameEmailBlocker(username, email);

        UserNameCheckResponse response = testDataProvider.CheckUserName(username, RandomHelper.randomEmail());
        assert response.getIsAvailable() == false;

        response = testDataProvider.CheckUserName(username.toLowerCase(), RandomHelper.randomEmail());
        assert response.getIsAvailable() == false;

        response = testDataProvider.CheckUserName(username.toUpperCase(), RandomHelper.randomEmail());
        assert response.getIsAvailable() == false;

        response = testDataProvider.CheckUserName(username, email);
        assert response.getIsAvailable() == true;

        response = testDataProvider.CheckUserName(RandomHelper.randomAlphabetic(15), email);
        assert response.getIsAvailable() == true;

        response = testDataProvider.CheckUserName(RandomHelper.randomAlphabetic(15), email.toLowerCase());
        assert response.getIsAvailable() == true;

        response = testDataProvider.CheckUserName(RandomHelper.randomAlphabetic(15), email.toUpperCase());
        assert response.getIsAvailable() == true;
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
        EmailCheckResponse emailCheckResponse = null;
        Error error = testDataProvider.CheckEmailWithError(invalidEmail, 400, "130.001");
        assert error != null;
        assert error.getDetails().get(0).getField().equalsIgnoreCase("email");
        assert error.getDetails().get(0).getReason().contains("Field value is invalid.");

        invalidEmail = "###1212@silkcloud.com";
        error = testDataProvider.CheckEmailWithError(invalidEmail, 400, "130.001");
        assert error != null;
        assert error.getDetails().get(0).getField().equalsIgnoreCase("email");
        assert error.getDetails().get(0).getReason().contains("Field value is invalid.");

        CreateUserRequest createUserRequest = testDataProvider.CreateUserRequest();
        emailCheckResponse = testDataProvider.CheckEmail(createUserRequest.getEmail());
        Validator.Validate("Validate valid username", emailCheckResponse.getIsAvailable(), true);

        AuthTokenResponse authTokenResponse = testDataProvider.CreateUser(createUserRequest, false);
        Validator.Validate("validate authtoken response username correct", createUserRequest.getUsername(), authTokenResponse.getUsername());
        UserProfileGetResponse userProfileGetResponse = testDataProvider.getUserProfile(412);
        assert userProfileGetResponse == null;

        oAuthClient.postAccessToken(GrantType.CLIENT_CREDENTIALS, ComponentType.SMOKETEST);
        List<String> links = oAuthClient.getEmailVerifyLink(IdConverter.idToHexString(authTokenResponse.getUserId()), createUserRequest.getEmail());
        assert links != null;
        for (String link : links) {
            ConfirmEmailResponse response = testDataProvider.confirmEmail(link);
            assert response.getIsSuccess();
            assert createUserRequest.getEmail().equalsIgnoreCase(response.getEmail());
        }
        userProfileGetResponse = testDataProvider.getUserProfile();
        assert userProfileGetResponse != null;
        Validator.Validate("validate authtoken response name correct", createUserRequest.getUsername(), userProfileGetResponse.getUserProfile().getUsername());
        Validator.Validate("validate authtoken response email correct", createUserRequest.getEmail(), userProfileGetResponse.getUserProfile().getEmail().getValue());

        error = testDataProvider.CheckEmailWithError(invalidEmail, 400, "130.001");
        assert error != null;
        assert error.getDetails().get(0).getField().equalsIgnoreCase("email");
        assert error.getDetails().get(0).getReason().contains("Field value is invalid.");

        emailCheckResponse = testDataProvider.CheckEmail(createUserRequest.getEmail());
        Validator.Validate("Validate duplicate email", emailCheckResponse.getIsAvailable(), false);

        emailCheckResponse = testDataProvider.CheckEmail(RandomHelper.randomEmail());
        Validator.Validate("Validate random character email", emailCheckResponse.getIsAvailable(), true);

        error = testDataProvider.CheckEmailWithError(null, 400, "130.001");
        assert error != null;

        error = testDataProvider.CheckEmailWithError("", 400, "130.001");
        assert error != null;
    }

    @Property(
            priority = Priority.Dailies,
            features = "Store",
            component = Component.STORE,
            owner = "Zhaoyunlong",
            status = Status.Disable
    )
    @Test
    public void testCreateUserBlock() throws Exception {
        CreateUserRequest createUserRequest = testDataProvider.CreateUserRequest();
        String email = createUserRequest.getEmail();
        String username = createUserRequest.getUsername();
        testDataProvider.PrepareUsernameEmailBlocker(username, email);

        createUserRequest.setEmail(RandomHelper.randomEmail());
        Error error = testDataProvider.CreateUserWithError(createUserRequest, true, 400, "130.001");
        assert error != null;
        String errorMessage = "username and email are occupied";
        String field = "username";
        assert error.getDetails().get(0).getField().equalsIgnoreCase(field);
        assert error.getDetails().get(0).getReason().contains(errorMessage);

        createUserRequest.setUsername(username.toUpperCase());
        error = testDataProvider.CreateUserWithError(createUserRequest, true, 400, "130.001");
        assert error != null;
        assert error.getDetails().get(0).getField().equalsIgnoreCase(field);
        assert error.getDetails().get(0).getReason().contains(errorMessage);

        createUserRequest.setUsername(username.toLowerCase());
        error = testDataProvider.CreateUserWithError(createUserRequest, true, 400, "130.001");
        assert error != null;
        assert error.getDetails().get(0).getField().equalsIgnoreCase(field);
        assert error.getDetails().get(0).getReason().contains(errorMessage);

        createUserRequest.setUsername(RandomHelper.randomAlphabetic(15));
        createUserRequest.setEmail(email);
        AuthTokenResponse authTokenResponse = testDataProvider.CreateUser(createUserRequest, true);
        assert authTokenResponse != null;
        assert authTokenResponse.getUsername().equalsIgnoreCase(createUserRequest.getUsername());

        createUserRequest = testDataProvider.CreateUserRequest();
        email = createUserRequest.getEmail();
        username = createUserRequest.getUsername();
        testDataProvider.PrepareUsernameEmailBlocker(username, email);

        authTokenResponse = testDataProvider.CreateUser(createUserRequest, true);
        assert authTokenResponse != null;
        assert authTokenResponse.getUsername().equalsIgnoreCase(createUserRequest.getUsername());
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
        createUserRequest.setNickName(createUserRequest.getUsername());
        String invalidUsername = "---123yunlong";
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
        error = testDataProvider.CreateUserWithError(createUserRequest, true, 412, "131.140");
        assert error != null;
        assert error.getDetails().get(0).getField().contains("dob");

        createUserRequest.setDob(DateUtils.addYears(new Date(), -11));
        error = testDataProvider.CreateUserWithError(createUserRequest, true, 412, "131.140");
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
        validationHelper.verifyEmailInAuthResponse(createUserResponse, createUserRequest.getEmail(), false);

        // validate create with same username failure
        createUserRequest.setEmail(RandomFactory.getRandomEmailAddress());
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
            priority = Priority.BVT,
            features = "Store sign in",
            component = Component.STORE,
            owner = "fzhang",
            status = Status.Enable,
            description = "Test when user register, it'll auto purchase the initial items"
    )
    @Test
    public void testCreateUserPurchaseInitialItem() throws Exception {
        testDataProvider.resetEmulatorData();
        // config two free apps and 1 paid app, the paid app will be ignored
        testDataProvider.setupCmsOffers(initialAppsCmsPage, Collections.singletonList(initialAppsCmsSlot),
                Collections.singletonList(Arrays.asList(new OfferId(testDataProvider.getOfferIdByName(offer_digital_free)),
                        new OfferId(testDataProvider.getOfferIdByName(offer_digital_free)),
                        new OfferId(testDataProvider.getOfferIdByName(offer_digital_oculus_free1)),
                        new OfferId(testDataProvider.getOfferIdByName(offer_digital_oculus_free1)),
                        new OfferId(testDataProvider.getOfferIdByName(offer_digital_free_same_item_offer1)),
                        new OfferId(testDataProvider.getOfferIdByName(offer_digital_free_same_item_offer1)),
                        new OfferId(testDataProvider.getOfferIdByName(offer_digital_free_same_item_offer2)),
                        new OfferId(testDataProvider.getOfferIdByName(offer_digital_free_same_item_offer2)),
                        new OfferId(testDataProvider.getOfferIdByName(offer_digital_normal1)))));

        CreateUserRequest createUserRequest = testDataProvider.CreateUserRequest();
        testDataProvider.CreateUser(createUserRequest, true);
        try {
            validationHelper.verifyItemsInLibrary(testDataProvider.getLibrary(), Arrays.asList(item_digital_free, item_digital_oculus_free1,
                    item_digital_free_same_item));
        } finally {
            testDataProvider.resetEmulatorData();
        }
    }


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
    public void testCreateUserWithInvalidLocale() throws Exception {
        CreateUserRequest createUserRequest = testDataProvider.CreateUserRequest();
        TestContext.getData().putHeader("Accept-Language", "es-US");
        AuthTokenResponse response = testDataProvider.CreateUser(createUserRequest, true);
        assert response != null;
        assert response.getUsername().equalsIgnoreCase(createUserRequest.getUsername());

        createUserRequest = testDataProvider.CreateUserRequest();
        TestContext.getData().putHeader("Accept-Language", "es");
        response = testDataProvider.CreateUser(createUserRequest, true);
        assert response != null;
        assert response.getUsername().equalsIgnoreCase(createUserRequest.getUsername());

        createUserRequest = testDataProvider.CreateUserRequest();
        createUserRequest.setPreferredLocale("es_US");
        response = testDataProvider.CreateUser(createUserRequest, true);
        assert response != null;
        assert response.getUsername().equalsIgnoreCase(createUserRequest.getUsername());

        createUserRequest = testDataProvider.CreateUserRequest();
        createUserRequest.setPreferredLocale("es-US");
        response = testDataProvider.CreateUser(createUserRequest, true);
        assert response != null;
        assert response.getUsername().equalsIgnoreCase(createUserRequest.getUsername());

        createUserRequest = testDataProvider.CreateUserRequest();
        createUserRequest.setPreferredLocale("es");
        response = testDataProvider.CreateUser(createUserRequest, true);
        assert response != null;
        assert response.getUsername().equalsIgnoreCase(createUserRequest.getUsername());
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
        validationHelper.verifyEmailInAuthResponse(authTokenResponse, createUserRequest.getEmail(), false);

        assert authTokenResponse.getUsername() != null;
        AuthTokenResponse signInResponse = testDataProvider.SignIn(createUserRequest.getEmail(), createUserRequest.getPassword());
        Validator.Validate("validate createdToken equals to signIn token", authTokenResponse.getUsername(), signInResponse.getUsername());
        validationHelper.verifyEmailInAuthResponse(signInResponse, createUserRequest.getEmail(), true);

        signInResponse = testDataProvider.SignIn(createUserRequest.getEmail(), RandomHelper.randomAlphabetic(15), 412);
        assert signInResponse == null;

        signInResponse = testDataProvider.SignIn(createUserRequest.getEmail(), createUserRequest.getPassword());
        Validator.Validate("validate createdToken equals to signIn token", authTokenResponse.getUsername(), signInResponse.getUsername());
        validationHelper.verifyEmailInAuthResponse(signInResponse, createUserRequest.getEmail(), true);

        signInResponse = testDataProvider.SignIn(createUserRequest.getEmail(), createUserRequest.getPassword());
        Validator.Validate("validate createdToken equals to signIn token through email login", authTokenResponse.getUsername(), signInResponse.getUsername());

        signInResponse = testDataProvider.SignIn(createUserRequest.getEmail(), RandomHelper.randomAlphabetic(15), 412);
        assert signInResponse == null;

        signInResponse = testDataProvider.SignIn(createUserRequest.getEmail(), createUserRequest.getPassword());
        Validator.Validate("validate createdToken equals to signIn token through email login", authTokenResponse.getUsername(), signInResponse.getUsername());
        validationHelper.verifyEmailInAuthResponse(signInResponse, createUserRequest.getEmail(), true);

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

        signInResponse = testDataProvider.SignIn(createUserRequest.getEmail(), newPassword);
        Validator.Validate("validate signIn token equals to the current user", createUserRequest.getUsername(), signInResponse.getUsername());
        validationHelper.verifyEmailInAuthResponse(signInResponse, createUserRequest.getEmail(), true);

        signInResponse = testDataProvider.SignIn(createUserRequest.getEmail(), newPassword);
        Validator.Validate("validate signIn token equals to current user with username login", createUserRequest.getUsername(), signInResponse.getUsername());
        validationHelper.verifyEmailInAuthResponse(signInResponse, createUserRequest.getEmail(), true);
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
        validationHelper.verifyEmailInAuthResponse(authTokenResponse, createUserRequest.getEmail(), false);
        assert authTokenResponse != null;

        Error error = testDataProvider.SignInWithError(createUserRequest.getEmail(), "PIN", "1234", 400, "130.001");
        assert error != null;
        assert error.getDetails().get(0).getField().contains("userCredential.type");
        assert error.getDetails().get(0).getReason().contains("Field value is invalid. type must be PASSWORD");

        error = testDataProvider.SignInWithError(RandomHelper.randomAlphabetic(15), "PASSWORD", createUserRequest.getPassword(), 400, "130.001");
        assert error != null;
        assert error.getDetails().get(0).getField().contains("email");
        assert error.getDetails().get(0).getReason().contains("email is incorrect format");

        error = testDataProvider.SignInWithError(createUserRequest.getEmail(), "PASSWORD", RandomHelper.randomAlphabetic(10), 412, "132.103");
        assert error != null;
        assert error.getDetails().get(0).getField().contains("password");

        error = testDataProvider.SignInWithError(RandomHelper.randomAlphabetic(10) + "@gmail.com", "PASSWORD", createUserRequest.getPassword(), 412, "132.103");
        assert error != null;
        assert error.getDetails().get(0).getField().contains("email");
        assert error.getDetails().get(0).getReason().contains("email and credential doesn't match");

        error = testDataProvider.SignInWithError(createUserRequest.getEmail(), "PASSWORD", null, 400, "130.001");
        assert error != null;
        assert error.getDetails().get(0).getField().contains("userCredential.value");
        assert error.getDetails().get(0).getReason().contains("Field is required");

        authTokenResponse = testDataProvider.SignIn(createUserRequest.getEmail(), createUserRequest.getPassword());
        assert authTokenResponse != null;
        assert authTokenResponse.getUsername().equals(createUserRequest.getUsername());
        validationHelper.verifyEmailInAuthResponse(authTokenResponse, createUserRequest.getEmail(), true);
    }

    @Property(
            priority = Priority.Dailies,
            features = "Store",
            component = Component.STORE,
            owner = "ZhaoYunlong",
            status = Status.Enable,
            steps = {
                    "Check login max login attempt"
            }
    )
    @Test
    public void testLoginReachMaxAttempt() throws Exception {
        int allowMaxTime = 3;
        CreateUserRequest createUserRequest = testDataProvider.CreateUserRequest();
        testDataProvider.CreateUser(createUserRequest, true);
        for (int i  = 0;i < allowMaxTime;++i) {
            testDataProvider.SignInWithError(createUserRequest.getEmail(), "PASSWORD", RandomHelper.randomAlphabetic(10), 412, "132.103");
        }
        testDataProvider.SignInWithError(createUserRequest.getEmail(), "PASSWORD", RandomHelper.randomAlphabetic(10), 429, "131.139");
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
        validationHelper.verifyEmailInAuthResponse(authTokenResponse, createUserRequest.getEmail(), false);

        AuthTokenResponse response = testDataProvider.getToken(authTokenResponse.getRefreshToken());
        Validator.Validate("Validate refreshToken works", response.getUsername(), authTokenResponse.getUsername());
        validationHelper.verifyEmailInAuthResponse(response, createUserRequest.getEmail(), true);

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

        // within 5 minutes the refresh token will still be valid
        AuthTokenResponse response1 = testDataProvider.getToken(authTokenResponse.getRefreshToken());
        assert response1.getRefreshToken().equals(response.getRefreshToken());
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
        validationHelper.verifyEmailInAuthResponse(authTokenResponse, createUserRequest.getEmail(), false);

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
        assert links.size() == 1;
        String link = links.get(0);
        ConfirmEmailResponse confirmEmailResponse = testDataProvider.confirmEmail(link);
        assert confirmEmailResponse.getIsSuccess();
        assert newEmail.equalsIgnoreCase(confirmEmailResponse.getEmail());

        UserProfileGetResponse userProfileGetResponse = testDataProvider.getUserProfile();
        assert userProfileGetResponse.getUserProfile().getEmail().getValue().equalsIgnoreCase(newEmail);

        AuthTokenResponse response = testDataProvider.SignIn(newEmail, createUserRequest.getPassword());
        validationHelper.verifyEmailInAuthResponse(response, newEmail, true);
        assert response.getUsername().equalsIgnoreCase(createUserRequest.getUsername());
    }

    @Property(
            priority = Priority.Dailies,
            features = "Store",
            component = Component.STORE,
            owner = "ZhaoYunlong",
            status = Status.Enable,
            steps = {
                    "Check update email works if country & locale is missing"
            }
    )
    @Test
    public void testUpdateEmailUserCountryLocaleMissing() throws Exception {
        CreateUserRequest createUserRequest = testDataProvider.CreateUserRequest();
        AuthTokenResponse authTokenResponse = testDataProvider.CreateUser(createUserRequest, true);
        validationHelper.verifyEmailInAuthResponse(authTokenResponse, createUserRequest.getEmail(), false);

        // clear country & locale field of user
        Master.getInstance().setCurrentUid(IdFormatter.encodeId(authTokenResponse.getUserId()));
        Master.getInstance().addUserAccessToken(IdFormatter.encodeId(authTokenResponse.getUserId()),
                testDataProvider.getUserAccessToken(URLEncoder.encode(createUserRequest.getEmail(), "UTF-8"), createUserRequest.getPassword()));
        oAuthTokenService.postAccessToken(GrantType.CLIENT_CREDENTIALS, ComponentType.IDENTITY);
        testDataProvider.clearUserPreferLocalAndCountry(authTokenResponse.getUserId());

        testDataProvider.SignIn(createUserRequest.getEmail(), createUserRequest.getPassword());
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
        assert links.size() == 1;
        String link = links.get(0);
        ConfirmEmailResponse confirmEmailResponse = testDataProvider.confirmEmail(link);
        assert confirmEmailResponse.getIsSuccess();
        assert newEmail.equalsIgnoreCase(confirmEmailResponse.getEmail());

        UserProfileGetResponse userProfileGetResponse = testDataProvider.getUserProfile();
        assert userProfileGetResponse.getUserProfile().getEmail().getValue().equalsIgnoreCase(newEmail);

        AuthTokenResponse response = testDataProvider.SignIn(newEmail, createUserRequest.getPassword());
        validationHelper.verifyEmailInAuthResponse(response, newEmail, true);
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
        ConfirmEmailResponse confirmEmailResponse = testDataProvider.confirmEmail(links.get(0));
        assert confirmEmailResponse.getIsSuccess();
        assert newEmail.equalsIgnoreCase(confirmEmailResponse.getEmail());
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
        confirmEmailResponse = testDataProvider.confirmEmail(links.get(0));
        assert confirmEmailResponse.getIsSuccess();
        assert createUserRequest.getEmail().equalsIgnoreCase(confirmEmailResponse.getEmail());

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
        confirmEmailResponse = testDataProvider.confirmEmail(links.get(0));
        assert newEmail.equalsIgnoreCase(confirmEmailResponse.getEmail());
        assert confirmEmailResponse.getIsSuccess();

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
        confirmEmailResponse = testDataProvider.confirmEmail(links.get(0));
        assert confirmEmailResponse.getIsSuccess();
        assert newEmail.equalsIgnoreCase(confirmEmailResponse.getEmail());

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
        assert links.size() == 0;

        for (int i = 0; i < CREDENTIAL_ATTEMPT_COUNT; i++) {
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
        String oldNickName = createUserRequest.getUsername();
        storeUserProfile.setNickName(newNickName);
        userProfileUpdateRequest.setUserProfile(storeUserProfile);
        UserProfileUpdateResponse response = testDataProvider.updateUserProfile(userProfileUpdateRequest);
        //https://oculus.atlassian.net/browse/SER-693?filter=-1
        assert response.getUserProfile().getNickName().equalsIgnoreCase(oldNickName);

        UserProfileGetResponse userProfileGetResponse = testDataProvider.getUserProfile();
        assert userProfileGetResponse.getUserProfile().getNickName().equalsIgnoreCase(oldNickName);

        String avatar = RandomHelper.randomAlphabetic(100);
        storeUserProfile.setAvatar(avatar);
        userProfileUpdateRequest.setUserProfile(storeUserProfile);
        response = testDataProvider.updateUserProfile(userProfileUpdateRequest);
        assert response.getUserProfile().getAvatar().equalsIgnoreCase(avatar);

        userProfileGetResponse = testDataProvider.getUserProfile();
        assert userProfileGetResponse.getUserProfile().getAvatar().equalsIgnoreCase(avatar);
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
        String link = links.get(0);
        ConfirmEmailResponse confirmEmailResponse = testDataProvider.confirmEmail(link);
        assert confirmEmailResponse.getIsSuccess();
        assert createUserRequest.getEmail().equalsIgnoreCase(confirmEmailResponse.getEmail());

        confirmEmailResponse = testDataProvider.confirmEmail(link);
        assert !confirmEmailResponse.getIsSuccess();
        assert confirmEmailResponse.getEmail() == null;

        link = links.get(1);
        confirmEmailResponse = testDataProvider.confirmEmail(link);
        assert !confirmEmailResponse.getIsSuccess();
        assert confirmEmailResponse.getEmail() == null;

        response = testDataProvider.verifyEmail(new VerifyEmailRequest());
        assert response != null;
        assert response.getEmailSent();
        oAuthClient.postAccessToken(GrantType.CLIENT_CREDENTIALS, ComponentType.SMOKETEST);
        links = oAuthClient.getEmailVerifyLink(IdConverter.idToHexString(authTokenResponse.getUserId()), createUserRequest.getEmail());
        assert links != null;
        assert links.size() == 1;

        // clear country & locale field of user
        Master.getInstance().setCurrentUid(IdFormatter.encodeId(authTokenResponse.getUserId()));
        Master.getInstance().addUserAccessToken(IdFormatter.encodeId(authTokenResponse.getUserId()),
                testDataProvider.getUserAccessToken(URLEncoder.encode(createUserRequest.getEmail(), "UTF-8"), createUserRequest.getPassword()));
        oAuthTokenService.postAccessToken(GrantType.CLIENT_CREDENTIALS, ComponentType.IDENTITY);
        testDataProvider.clearUserPreferLocalAndCountry(authTokenResponse.getUserId());

        testDataProvider.SignIn(createUserRequest.getEmail(), createUserRequest.getPassword());
        response = testDataProvider.verifyEmail(new VerifyEmailRequest());
        assert response != null;
        assert response.getEmailSent();
        oAuthClient.postAccessToken(GrantType.CLIENT_CREDENTIALS, ComponentType.SMOKETEST);
        links = oAuthClient.getEmailVerifyLink(IdConverter.idToHexString(authTokenResponse.getUserId()), createUserRequest.getEmail());
        assert links != null;
        assert links.size() == 2;
    }

    @Property(
            priority = Priority.Dailies,
            features = "Store",
            component = Component.STORE,
            owner = "ZhaoYunlong",
            status = Status.Enable,
            steps = {
                    "Check tos updated"
            }
    )
    @Test
    public void testLoginTosUpdated() throws Exception {
        CreateUserRequest createUserRequest = testDataProvider.CreateUserRequest();
        testDataProvider.CreateUser(createUserRequest, true);

        // Try to login, it should have no challenge
        AuthTokenResponse response = testDataProvider.SignIn(createUserRequest.getEmail(), createUserRequest.getPassword());
        assert response != null;
        assert response.getChallenge() == null;

        Thread.sleep(2000);
        // update tos to draft status
        testDataProvider.UpdateTos("end user tos", "DRAFT");
        response = testDataProvider.SignIn(createUserRequest.getEmail(), createUserRequest.getPassword());
        assert response != null;
        assert response.getChallenge() == null;

        Thread.sleep(2000);
        testDataProvider.UpdateTos("end user tos", "APPROVED");
        response = testDataProvider.SignIn(createUserRequest.getEmail(), createUserRequest.getPassword());
        assert response != null;
        assert response.getChallenge() != null;
        assert response.getChallenge().getTos() != null;

        Thread.sleep(2000);
        testDataProvider.acceptTos(response.getChallenge().getTos().getTosId());
        response = testDataProvider.SignIn(createUserRequest.getEmail(), createUserRequest.getPassword());
        assert response != null;
        assert response.getChallenge() == null;
    }

    @Property(
            priority = Priority.BVT,
            features = "Store sign in",
            component = Component.STORE,
            owner = "ZhaoYunlong",
            environment = "release",
            status = Status.Enable,
            description = "Test user sign in with multi endpoint",
            steps = {
                    "1. Create user in east dc",
                    "2. Sign in in west dc",

            }
    )
    @Test(groups = "int/ppe/prod/sewer")
    public void testSignInWithMultiEndpoint() throws Exception {
        try {
            Master.getInstance().setEndPointType(Master.EndPointType.Secondary);
            CreateUserRequest createUserRequest = testDataProvider.CreateUserRequest();
            AuthTokenResponse authTokenResponse = testDataProvider.CreateUser(createUserRequest, true);
            String userName = authTokenResponse.getUsername();
            Master.getInstance().setEndPointType(Master.EndPointType.Primary);
            AuthTokenResponse signInResponse = testDataProvider.signIn(createUserRequest.getEmail());

            validationHelper.verifySignInResponse(authTokenResponse, signInResponse);
        }
        catch (Exception ex){}
        finally {
            Master.getInstance().setEndPointType(Master.EndPointType.Primary);
        }

    }

    @Property(
            priority = Priority.BVT,
            features = "Store sign in",
            component = Component.STORE,
            owner = "fzhang",
            status = Status.Enable,
            description = "Test when user sign-in, it'll auto purchase the initial items if the user doesn't own those items",
            steps = {
                    "1. create user",
                    "2. No initial apps configured, user sign in and verify no items are in user's library",
                    "3. two free initial apps configured",
                    "4. user sign in failed, verify no items in the library",
                    "5. user sign in and verify those items are in user's library",
                    "6. user sign in again, check those items are in user's library",
                    "7. one more free initial apps configured",
                    "8. user sign in again, check 3 items are in user's library",
                    "9. user sign in again, error occurs in getting initial apps, the auth response still returns success"

            }
    )
    @Test
    public void testSignInPurchaseInitialItem() throws Exception {
        testDataProvider.resetEmulatorData();
        CreateUserRequest createUserRequest = testDataProvider.CreateUserRequest();
        testDataProvider.CreateUser(createUserRequest, true);

        AuthTokenResponse response = testDataProvider.SignIn(createUserRequest.getEmail(), createUserRequest.getPassword());
        assert response != null;
        assert response.getChallenge() == null;
        Assert.assertEquals(testDataProvider.getLibrary().getItems().size(), 0);

        // config two free apps and 1 paid app, the paid app will be ignored
        testDataProvider.setupCmsOffers(initialAppsCmsPage, Collections.singletonList(initialAppsCmsSlot),
                Collections.singletonList(Arrays.asList(new OfferId(testDataProvider.getOfferIdByName(offer_digital_free)),
                        new OfferId(testDataProvider.getOfferIdByName(offer_digital_oculus_free1)),
                        new OfferId(testDataProvider.getOfferIdByName(offer_digital_normal1)))));

        try {
            testDataProvider.SignIn(createUserRequest.getEmail(), createUserRequest.getPassword() + "123", 412);
            Assert.assertEquals(testDataProvider.getLibrary().getItems().size(), 0);

            response = testDataProvider.SignIn(createUserRequest.getEmail(), createUserRequest.getPassword());
            assert response != null;
            assert response.getChallenge() == null;
            validationHelper.verifyItemsInLibrary(testDataProvider.getLibrary(), Arrays.asList(item_digital_free, item_digital_oculus_free1));

            response = testDataProvider.SignIn(createUserRequest.getEmail(), createUserRequest.getPassword());
            assert response != null;
            assert response.getChallenge() == null;
            validationHelper.verifyItemsInLibrary(testDataProvider.getLibrary(), Arrays.asList(item_digital_free, item_digital_oculus_free1));

            testDataProvider.setupCmsOffers(initialAppsCmsPage, Collections.singletonList(initialAppsCmsSlot),
                    Collections.singletonList(Arrays.asList(new OfferId(testDataProvider.getOfferIdByName(offer_digital_free)),
                            new OfferId(testDataProvider.getOfferIdByName(offer_digital_oculus_free2)),
                            new OfferId(testDataProvider.getOfferIdByName(offer_digital_oculus_free2)),
                            new OfferId(testDataProvider.getOfferIdByName(offer_digital_free_same_item_offer1)),
                            new OfferId(testDataProvider.getOfferIdByName(offer_digital_free_same_item_offer1)),
                            new OfferId(testDataProvider.getOfferIdByName(offer_digital_free_same_item_offer2)),
                            new OfferId(testDataProvider.getOfferIdByName(offer_digital_free_same_item_offer2)),
                            new OfferId(testDataProvider.getOfferIdByName(offer_digital_normal1)))));

            response = testDataProvider.SignIn(createUserRequest.getEmail(), createUserRequest.getPassword());
            assert response != null;
            assert response.getChallenge() == null;
            validationHelper.verifyItemsInLibrary(testDataProvider.getLibrary(), Arrays.asList(item_digital_free,
                    item_digital_oculus_free1, item_digital_oculus_free2, item_digital_free_same_item));

            TestContext.getData().putHeader("X_QA_CASEY_ERROR", "search");
            response = testDataProvider.SignIn(createUserRequest.getEmail(), createUserRequest.getPassword());
            assert response != null;
            assert response.getChallenge() == null;
        } finally {
            testDataProvider.resetEmulatorData();
        }
    }

    @Property(
            priority = Priority.Dailies,
            features = "Store",
            component = Component.STORE,
            owner = "ZhaoYunlong",
            status = Status.Enable,
            steps = {
                    "Accept tos twice should fail"
            }
    )
    @Test
    public void testAcceptTosAlreadyAccepted() throws Exception {
        CreateUserRequest createUserRequest = testDataProvider.CreateUserRequest();
        testDataProvider.CreateUser(createUserRequest, true);
        testDataProvider.acceptTos(createUserRequest.getTosAgreed(), 409);
        Assert.assertTrue(Master.getInstance().getApiErrorMsg().contains("131.002"));
    }

    @Property(
            priority = Priority.Dailies,
            features = "Store",
            component = Component.STORE,
            owner = "ZhaoYunlong",
            status = Status.Enable,
            steps = {
                    "check lookup tos"
            }
    )
    @Test
    public void testLookupTos() throws Exception {
        Tos tos = testDataProvider.lookupTos("TOS", "end user tos", 200);
        Assert.assertEquals(tos.getTitle(), "end user tos");

        // test locale fallback
        TestContext.getData().putHeader("Accept-Language", "zh-CN");
        tos = testDataProvider.lookupTos("TOS", "end user tos", 200);
        Assert.assertEquals(tos.getTitle(), "end user tos");

        // test tos version update
        Thread.sleep(2000);

        // update tos to draft status
        testDataProvider.UpdateTos("end user tos", "DRAFT");
        Tos newTos = testDataProvider.lookupTos("TOS", "end user tos", 200);
        Assert.assertEquals(newTos.getTosId(), tos.getTosId());

        Thread.sleep(2000);
        testDataProvider.UpdateTos("end user tos", "APPROVED");
        newTos = testDataProvider.lookupTos("TOS", "end user tos", 200);
        Assert.assertNotEquals(newTos.getTosId(), tos.getTosId());
        Assert.assertTrue(Double.valueOf(newTos.getVersion()) > Double.valueOf(tos.getVersion()));
    }

    @Test
    public void testLookupTosInvalid() throws Exception {
        testDataProvider.lookupTos(null, "end user tos", 400);
        testDataProvider.lookupTos("TOS", "", 400);

        testDataProvider.lookupTos("TOS", "e1nd user tos", 404);
        Assert.assertTrue(Master.getInstance().getApiErrorMsg().contains("130.128"));

        testDataProvider.lookupTos("TOS1", "end user tos", 404);
        Assert.assertTrue(Master.getInstance().getApiErrorMsg().contains("130.128"));

        TestContext.getData().putHeader("oculus-geoip-country-code", "GU");
        testDataProvider.lookupTos("TOS", "end user tos", 404);
        Assert.assertTrue(Master.getInstance().getApiErrorMsg().contains("130.128"));
    }
}
