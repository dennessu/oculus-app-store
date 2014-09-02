/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.store;


import com.junbo.common.id.EntitlementId;
import com.junbo.common.id.PaymentInstrumentId;
import com.junbo.store.spec.model.EntitlementsGetResponse;
import com.junbo.store.spec.model.billing.InstrumentUpdateResponse;
import com.junbo.store.spec.model.iap.IAPEntitlementConsumeResponse;
import com.junbo.store.spec.model.identity.UserProfileGetResponse;
import com.junbo.store.spec.model.login.AuthTokenResponse;
import com.junbo.store.spec.model.login.CreateUserRequest;
import com.junbo.store.spec.model.login.UserCredentialRateResponse;
import com.junbo.store.spec.model.login.UserNameCheckResponse;
import com.junbo.store.spec.model.purchase.CommitPurchaseResponse;
import com.junbo.store.spec.model.purchase.MakeFreePurchaseResponse;
import com.junbo.store.spec.model.purchase.PreparePurchaseResponse;
import com.junbo.test.common.Entities.enums.ComponentType;
import com.junbo.test.common.RandomHelper;
import com.junbo.test.common.Validator;
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
import org.testng.annotations.Test;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by weiyu_000 on 8/6/14.
 */
public class StoreTesting extends BaseTestClass {

    OAuthService oAuthClient = OAuthServiceImpl.getInstance();

    @Property(
            priority = Priority.BVT,
            features = "Store checkout",
            component = Component.Order,
            owner = "ZhaoYunlong",
            status = Status.Enable,
            description = "Test iap offer checkout",
            steps = {
                    "1. Create user",
                    "2. Add credit card into billing profile",
                    "3. Post prepare purchase",
                    "4. Verify price response",
                    "5. Select payment instrument for purchase",
                    "6. Commit purchase",
                    "7. Verify purchase response",
                    "8. Consume iap entitlement",
                    "9. Get entitlement",
                    "10. Verify entitlement response"
            }
    )
    @Test
    public void testIAPCheckoutByCreditCard() throws Exception {
        CreateUserRequest createUserRequest = testDataProvider.CreateUserRequest();
        AuthTokenResponse authTokenResponse = testDataProvider.CreateUser(createUserRequest, true);
        String uid = IdConverter.idToHexString(authTokenResponse.getUserId());
        //add new credit card to user

        InstrumentUpdateResponse instrumentUpdateResponse = testDataProvider.CreateCreditCard(uid);
        //verify decrypted credit card info
        validationHelper.verifyAddNewCreditCard(instrumentUpdateResponse);

        //get payment id in billing profile
        PaymentInstrumentId paymentId = instrumentUpdateResponse.getBillingProfile().getInstruments().get(0).getSelf();

        String offerId = testDataProvider.getOfferIdByName(offer_digital_normal1);
        //post order without set payment instrument
        PreparePurchaseResponse preparePurchaseResponse = testDataProvider.preparePurchase(null, offerId, null, null, null);

        assert preparePurchaseResponse.getChallenge() != null;
        assert preparePurchaseResponse.getChallenge().getType().equalsIgnoreCase("PIN");

        preparePurchaseResponse = testDataProvider.preparePurchase(preparePurchaseResponse.getPurchaseToken(),
                offerId, paymentId, "1234", null);

        assert preparePurchaseResponse.getChallenge() != null;
        assert preparePurchaseResponse.getChallenge().getType().equalsIgnoreCase("TOS_ACCEPTANCE");
        assert preparePurchaseResponse.getChallenge().getTos() != null;

        preparePurchaseResponse = testDataProvider.preparePurchase(preparePurchaseResponse.getPurchaseToken(), offerId, paymentId, null,
                preparePurchaseResponse.getChallenge().getTos().getTosId());

        //verify formatted price
        validationHelper.verifyPreparePurchase(preparePurchaseResponse);

        String purchaseToken = preparePurchaseResponse.getPurchaseToken(); //get order id

        CommitPurchaseResponse commitPurchaseResponse = testDataProvider.commitPurchase(uid, purchaseToken);
        validationHelper.verifyCommitPurchase(commitPurchaseResponse, offerId);

        // todo:    Need to call entitlement
        //EntitlementId entitlementId = commitPurchaseResponse.getEntitlements().get(0).getSelf();
        //IAPEntitlementConsumeResponse iapEntitlementConsumeResponse = testDataProvider.iapConsumeEntitlement(entitlementId, offer_iap_normal);

    }

    @Property(
            priority = Priority.Dailies,
            features = "Store checkout",
            component = Component.STORE,
            owner = "ZhaoYunlong",
            environment = "release",
            status = Status.Enable,
            description = "Test email verification",
            steps = {
                    "Create user with no email verification",
                    "Call smoke API to check one record only",
                    "Call checkUserName with the created username, it should return error",
                    "Call checkUserName with one random username, it should return success",
                    "Call rateUserCredential to check it doesn't fail",
                    "Call signIn to get one new AccessToken. Get and compare with the created return access_token",

                    "Call EmailVerify with access_token, it should have two records",
                    "Call all other store apis, it should return all error due to no valid token",
                    "Call get userProfile with and without access_token"
            }
    )
    @Test
    public void testPrivilege() throws Exception {
        CreateUserRequest createUserRequest = testDataProvider.CreateUserRequest();
        AuthTokenResponse authTokenResponse = testDataProvider.CreateUser(createUserRequest, false);

        Validator.Validate("validate authtoken response correct", createUserRequest.getUsername(), authTokenResponse.getUsername());

        oAuthClient.postAccessToken(GrantType.CLIENT_CREDENTIALS, ComponentType.SMOKETEST);
        List<String> links = oAuthClient.getEmailVerifyLink(IdConverter.idToHexString(authTokenResponse.getUserId()), createUserRequest.getEmail());
        Validator.Validate("validate email was sent", 1, links.size());

        UserNameCheckResponse userNameCheckResponse = testDataProvider.CheckUserName(createUserRequest.getUsername());
        Validator.Validate("validate username is not available", false, userNameCheckResponse.getIsAvailable());

        userNameCheckResponse = testDataProvider.CheckUserName(RandomHelper.randomAlphabetic(10));
        Validator.Validate("validate username is available", true, userNameCheckResponse.getIsAvailable());

        String password = "1234";
        UserCredentialRateResponse userCredentialRateResponse = testDataProvider.RateUserCredential(password);
        Validator.Validate("validate password invalid", "INVALID", userCredentialRateResponse.getStrength());

        password = "11111111";
        userCredentialRateResponse = testDataProvider.RateUserCredential(password);
        Validator.Validate("validate repeated character password invalid", "INVALID", userCredentialRateResponse.getStrength());

        password = "12345678";
        userCredentialRateResponse = testDataProvider.RateUserCredential(password);
        Validator.Validate("validate password weak", "WEAK", userCredentialRateResponse.getStrength());

        password = "#Bugsfor$";
        userCredentialRateResponse = testDataProvider.RateUserCredential(password);
        Validator.Validate("validate password fair", "FAIR", userCredentialRateResponse.getStrength());

        password = "#Bugsfor$1234";
        userCredentialRateResponse = testDataProvider.RateUserCredential(password);
        Validator.Validate("validate password strong", "STRONG", userCredentialRateResponse.getStrength());

        AuthTokenResponse newAuthTokenResponse = testDataProvider.SignIn(createUserRequest.getUsername(), createUserRequest.getPassword());
        Validator.Validate("validate token valid", authTokenResponse.getUsername(), newAuthTokenResponse.getUsername());

        // todo:    Add other conditions
    }

    @Property(
            priority = Priority.BVT,
            features = "Store checkout",
            component = Component.Order,
            owner = "ZhaoYunlong",
            environment = "release",
            status = Status.Enable,
            description = "Test iap offer checkout",
            steps = {
                    "1. Create user",
                    "2. Sign in",
                    "3. Get user profile",
                    "4. Verify response",
                    "5. Make free purchase",
                    "6. Verify purchase response",
                    "8. Get entitlement",
                    "9. Verify entitlement response",
                    "10. Refresh token",
            }
    )
    @Test
    public void testMakeFreePurchase() throws Exception {
        CreateUserRequest createUserRequest = testDataProvider.CreateUserRequest();
        AuthTokenResponse authTokenResponse = testDataProvider.CreateUser(createUserRequest, true);
        String userName = authTokenResponse.getUsername();

        AuthTokenResponse signInResponse = testDataProvider.signIn(userName);

        validationHelper.verifySignInResponse(authTokenResponse, signInResponse);

        UserProfileGetResponse userProfileResponse = testDataProvider.getUserProfile();

        validationHelper.verifyUserProfile(userProfileResponse, authTokenResponse);

        String offerId;
        if (offer_iap_free.toLowerCase().contains("test")) {
            offerId = testDataProvider.getOfferIdByName(offer_digital_free);
        } else {
            offerId = offer_digital_free;
        }

        MakeFreePurchaseResponse freePurchaseResponse = testDataProvider.makeFreePurchase(offerId, null);

        //String purchaseToken = IdConverter.idToHexString(freePurchaseResponse.getOrder()); //get order id

        freePurchaseResponse = testDataProvider.makeFreePurchase(offerId, freePurchaseResponse.getChallenge().getTos().getTosId());

        EntitlementsGetResponse entitlementsResponse = testDataProvider.getEntitlement();
        Master.getInstance().setCurrentUid(null);

        AuthTokenResponse tokenResponse = testDataProvider.getToken(signInResponse.getRefreshToken());

        validationHelper.verifySignInResponse(signInResponse, tokenResponse);

    }

    @Property(
            priority = Priority.BVT,
            features = "Store checkout",
            component = Component.Order,
            owner = "ZhaoYunlong",
            status = Status.Enable,
            description = "Test iap offer checkout",
            steps = {
                    "1. Create user",
                    "2. Add wallet into billing profile",
                    "3. Post prepare purchase",
                    "4. Verify price response",
                    "5. Select payment instrument for purchase",
                    "6. Commit purchase",
                    "7. Verify purchase response",
                    "8. Consume iap entitlement",
                    "9. Get entitlement",
                    "10. Verify entitlement response"
            }
    )
    @Test
    public void testIAPCheckoutByWallet() throws Exception {
        CreateUserRequest createUserRequest = testDataProvider.CreateUserRequest();
        AuthTokenResponse authTokenResponse = testDataProvider.CreateUser(createUserRequest, true);
        String uid = IdConverter.idToHexString(authTokenResponse.getUserId());
        //add new credit card to user

        InstrumentUpdateResponse instrumentUpdateResponse = testDataProvider.CreateStoredValue();
        testDataProvider.CreditStoredValue(uid, new BigDecimal(100));

        //get payment id in billing profile
        PaymentInstrumentId paymentId = instrumentUpdateResponse.getBillingProfile().getInstruments().get(0).getSelf();

        String offerId = testDataProvider.getOfferIdByName(offer_iap_normal);
        //post order without set payment instrument
               PreparePurchaseResponse preparePurchaseResponse = testDataProvider.preparePurchase(null, offerId, null, null, null, true, 200);

        assert preparePurchaseResponse.getChallenge() != null;

        preparePurchaseResponse = testDataProvider.preparePurchase(preparePurchaseResponse.getPurchaseToken(),
                offerId, paymentId, "1234", null, true, 200);

        assert preparePurchaseResponse.getChallenge().getTos() != null;

        preparePurchaseResponse = testDataProvider.preparePurchase(preparePurchaseResponse.getPurchaseToken(), offerId, paymentId, null,
                preparePurchaseResponse.getChallenge().getTos().getTosId(), true, 200);

        //verify formatted price
        validationHelper.verifyPreparePurchase(preparePurchaseResponse);

        String purchaseToken = preparePurchaseResponse.getPurchaseToken(); //get order id

        CommitPurchaseResponse commitPurchaseResponse = testDataProvider.commitPurchase(uid, purchaseToken);
        validationHelper.verifyCommitPurchase(commitPurchaseResponse, offerId);

        EntitlementId entitlementId = commitPurchaseResponse.getEntitlements().get(0).getSelf();
        IAPEntitlementConsumeResponse iapEntitlementConsumeResponse = testDataProvider.iapConsumeEntitlement(entitlementId, offerId);

        //TODO validation

    }

    @Property(
            priority = Priority.Dailies,
            features = "Store checkout",
            component = Component.Order,
            owner = "ZhaoYunlong",
            status = Status.Enable,
            description = "Test store login",
            steps = {
                    "1. Create user",
                    "2. Validate user",
                    "3. Check user name with created user name",
                    "4. post sign in",
            }
    )
    @Test
    public void testStoreLoginResource() throws Exception {
        String userName = RandomFactory.getRandomStringOfAlphabet(6);
        String password = "Test1234";
        CreateUserRequest createUserRequest = testDataProvider.CreateUserRequest(userName);
        AuthTokenResponse authTokenResponse = testDataProvider.CreateUser(createUserRequest, true);

        assert authTokenResponse.getUsername().equals(userName);
        assert authTokenResponse.getAccessToken() != null;

        UserNameCheckResponse userNameCheckResponse = testDataProvider.CheckUserName(userName);
        assert !userNameCheckResponse.getIsAvailable();

        authTokenResponse = testDataProvider.SignIn(userName, password);
        assert authTokenResponse.getUsername().equals(userName);
        assert authTokenResponse.getAccessToken() != null;
    }

    @Property(
            priority = Priority.Dailies,
            features = "Store checkout",
            component = Component.Order,
            owner = "ZhaoYunlong",
            status = Status.Enable,
            description = "Test gate user whose email not validate",
            steps = {
                    "1. Create user",
                    "2. Sign in",
                    "3. Attempt to make free purchase",
                    "4. Verify response code is 415",
                    "5. Attempt to get user profile",
                    "6. Verify response code is 415",
                    "7. Attempt to update billing profile",
                    "8. Verify response code is 415",
            }
    )
    @Test
    public void testStoreEmailValidationGate() throws Exception {
        String userName = RandomFactory.getRandomStringOfAlphabet(6);
        String password = "Test1234";
        CreateUserRequest createUserRequest = testDataProvider.CreateUserRequest(userName);
        AuthTokenResponse authTokenResponse = testDataProvider.CreateUser(createUserRequest, true);
    }

}
