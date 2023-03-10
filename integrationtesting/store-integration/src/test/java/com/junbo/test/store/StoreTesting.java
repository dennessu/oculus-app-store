/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.store;


import com.junbo.catalog.spec.model.item.Item;
import com.junbo.catalog.spec.model.offer.Offer;
import com.junbo.catalog.spec.model.offer.OfferRevision;
import com.junbo.common.id.ItemId;
import com.junbo.common.id.PaymentInstrumentId;
import com.junbo.store.spec.model.billing.BillingProfileGetResponse;
import com.junbo.store.spec.model.billing.InstrumentDeleteRequest;
import com.junbo.store.spec.model.billing.InstrumentDeleteResponse;
import com.junbo.store.spec.model.billing.InstrumentUpdateResponse;
import com.junbo.store.spec.model.browse.LibraryResponse;
import com.junbo.store.spec.model.iap.IAPConsumeItemResponse;
import com.junbo.store.spec.model.iap.IAPItemsResponse;
import com.junbo.store.spec.model.identity.UserProfileGetResponse;
import com.junbo.store.spec.model.login.AuthTokenResponse;
import com.junbo.store.spec.model.login.CreateUserRequest;
import com.junbo.store.spec.model.login.UserCredentialRateResponse;
import com.junbo.store.spec.model.login.UserNameCheckResponse;
import com.junbo.store.spec.model.purchase.CommitPurchaseResponse;
import com.junbo.store.spec.model.purchase.MakeFreePurchaseResponse;
import com.junbo.store.spec.model.purchase.PreparePurchaseResponse;
import com.junbo.test.common.ConfigHelper;
import com.junbo.test.common.Entities.enums.ComponentType;
import com.junbo.test.common.RandomHelper;
import com.junbo.test.common.Utility.ValidationHelper;
import com.junbo.test.common.Validator;
import com.junbo.test.common.apihelper.oauth.OAuthService;
import com.junbo.test.common.apihelper.oauth.enums.GrantType;
import com.junbo.test.common.apihelper.oauth.impl.OAuthServiceImpl;
import com.junbo.test.common.blueprint.Master;
import com.junbo.test.common.exception.TestException;
import com.junbo.test.common.libs.IdConverter;
import com.junbo.test.common.libs.RandomFactory;
import com.junbo.test.common.property.Component;
import com.junbo.test.common.property.Priority;
import com.junbo.test.common.property.Property;
import com.junbo.test.common.property.Status;
import com.junbo.test.store.apihelper.TestContext;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by weiyu_000 on 8/6/14.
 */
public class StoreTesting extends BaseTestClass {

    OAuthService oAuthClient = OAuthServiceImpl.getInstance();

    @Property(
            priority = Priority.BVT,
            features = "Store checkout",
            component = Component.STORE,
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
                    "8. Get library",
                    "9. Verify library response",
                    "10. Consume iap entitlement",
            }
    )
    @Test
    public void testIAPCheckoutByCreditCard() throws Exception {
        CreateUserRequest createUserRequest = testDataProvider.CreateUserRequest();
        AuthTokenResponse authTokenResponse = testDataProvider.CreateUser(createUserRequest, true);
        validationHelper.verifyEmailInAuthResponse(authTokenResponse, createUserRequest.getEmail(), false);
        String uid = IdConverter.idToHexString(authTokenResponse.getUserId());
        //add new credit card to user
        InstrumentUpdateResponse instrumentUpdateResponse = testDataProvider.CreateCreditCard(uid);
        //verify decrypted credit card info
        validationHelper.verifyAddNewCreditCard(instrumentUpdateResponse);
        //get payment id in billing profile
        PaymentInstrumentId paymentId = instrumentUpdateResponse.getBillingProfile().getInstruments().get(0).getSelf();
        String offerId = testDataProvider.getOfferIdByName(offer_inApp_consumable);
        //post order without set payment instrument
        PreparePurchaseResponse preparePurchaseResponse = testDataProvider.preparePurchase(null, offerId, paymentId, null, null);
        assert preparePurchaseResponse.getChallenge() == null;

        preparePurchaseResponse = testDataProvider.preparePurchase(preparePurchaseResponse.getPurchaseToken(), offerId, paymentId, "1234", null, true, 200);

        if (preparePurchaseResponse.getChallenge() != null) {
            assert preparePurchaseResponse.getChallenge().getType().equalsIgnoreCase("TOS_ACCEPTANCE");
            assert preparePurchaseResponse.getChallenge().getTos() != null;

            preparePurchaseResponse = testDataProvider.preparePurchase(preparePurchaseResponse.getPurchaseToken(), offerId, paymentId, null,
                    preparePurchaseResponse.getChallenge().getTos().getTosId());
        }
        //verify formatted price
        validationHelper.verifyPreparePurchase(preparePurchaseResponse, true, null);

        String purchaseToken = preparePurchaseResponse.getPurchaseToken(); //get order id

        //IAPParam iapParam = testDataProvider.getIapParam(offerId);
        CommitPurchaseResponse commitPurchaseResponse = testDataProvider.commitPurchase(uid, purchaseToken);
        assert commitPurchaseResponse.getChallenge() != null;
        assert commitPurchaseResponse.getChallenge().getType().equals("PIN");

        commitPurchaseResponse = testDataProvider.commitPurchase(uid, purchaseToken, "1234");
        validationHelper.verifyCommitPurchase(commitPurchaseResponse, offerId);

        LibraryResponse libraryResponse = testDataProvider.getIapLibrary();

        DeliveryResponse deliveryResponse = testDataProvider.getDeliveryByOfferId(offerId);
        String downloadLink = deliveryResponse.getDownloadUrl();
        List<DeliveryResponse> deliveryResponseList = testDataProvider.getDeliveryListByOfferId(offerId);
        Assert.assertEquals(deliveryResponseList.size(), 1);
        Assert.assertEquals(deliveryResponse.getDownloadSize(), deliveryResponseList.get(0).getDownloadSize());
        Assert.assertEquals(deliveryResponse.getSignature(), deliveryResponseList.get(0).getSignature());

        HttpClientHelper.validateURLAccessibility(deliveryResponseList.get(0).getDownloadUrl(), 200);
        HttpClientHelper.validateURLAccessibility(downloadLink, 200);
    }

    @Property(
            priority = Priority.Dailies,
            features = "Store delete payment instrument",
            component = Component.STORE,
            owner = "ZhaoYunlong",
            status = Status.Enable,
            description = "Test iap offer checkout",
            steps = {
                    "1. Create user",
                    "2. Add credit card into billing profile",
                    "3. Delete paymentInstrument and check",
                    "4. Add another credit card into billing profile",
                    "5. Add storeValue into billing profile",
                    "6. Delete credit card paymentInstrument and check",
                    "7. Delete storedValue paymentInstrument and check"
            }
    )
    @Test
    public void testPaymentInstrumentDelete() throws Exception {
        CreateUserRequest createUserRequest = testDataProvider.CreateUserRequest();
        AuthTokenResponse authTokenResponse = testDataProvider.CreateUser(createUserRequest, true);
        validationHelper.verifyEmailInAuthResponse(authTokenResponse, createUserRequest.getEmail(), false);
        String uid = IdConverter.idToHexString(authTokenResponse.getUserId());
        //add new credit card to user

        InstrumentUpdateResponse instrumentUpdateResponse = testDataProvider.CreateCreditCard(uid);
        //verify decrypted credit card info
        validationHelper.verifyAddNewCreditCard(instrumentUpdateResponse);

        //get payment id in billing profile
        PaymentInstrumentId paymentId = instrumentUpdateResponse.getBillingProfile().getInstruments().get(0).getSelf();
        InstrumentDeleteRequest instrumentDeleteRequest = new InstrumentDeleteRequest();
        instrumentDeleteRequest.setSelf(paymentId);
        InstrumentDeleteResponse response = testDataProvider.DeleteInstrument(instrumentDeleteRequest);
        assert response != null;
        assert response.getBillingProfile().getInstruments().size() == 0;

        BillingProfileGetResponse billingProfileGetResponse = testDataProvider.getBillingProfile(null);
        assert billingProfileGetResponse != null;
        assert billingProfileGetResponse.getBillingProfile().getInstruments().size() == 0;

        InstrumentUpdateResponse creditCard = testDataProvider.CreateCreditCard(uid);
        InstrumentUpdateResponse storedValue = testDataProvider.CreateStoredValue();

        BillingProfileGetResponse getResponse = testDataProvider.getBillingProfile(null);
        assert getResponse != null;
        assert getResponse.getBillingProfile().getInstruments().size() == 2;

        instrumentDeleteRequest.setSelf(getResponse.getBillingProfile().getInstruments().get(0).getSelf());
        response = testDataProvider.DeleteInstrument(instrumentDeleteRequest);
        assert response != null;
        assert response.getBillingProfile().getInstruments().size() == 1;

        instrumentDeleteRequest.setSelf(getResponse.getBillingProfile().getInstruments().get(1).getSelf());
        response = testDataProvider.DeleteInstrument(instrumentDeleteRequest);
        assert response != null;
        assert response.getBillingProfile().getInstruments().size() == 0;
    }

    @Property(
            priority = Priority.BVT,
            features = "Store checkout",
            component = Component.STORE,
            owner = "ZhaoYunlong",
            status = Status.Disable,
            environment = "release",
            description = "Test prepare purchase digital good offer",
            steps = {
                    "1. Create user",
                    "2. Add credit card into billing profile",
                    "3. Post prepare purchase",
                    "4. Verify price response",
                    "5. Select payment instrument for purchase",
                    "6. Verify response"
            }
    )
    @Test
    public void testPreparePurchaseDigitalGood() throws Exception {
        CreateUserRequest createUserRequest = testDataProvider.CreateUserRequest();
        AuthTokenResponse authTokenResponse = testDataProvider.CreateUser(createUserRequest, true);
        validationHelper.verifyEmailInAuthResponse(authTokenResponse, createUserRequest.getEmail(), false);
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

        if (preparePurchaseResponse.getChallenge() != null) {
            assert preparePurchaseResponse.getChallenge().getType().equalsIgnoreCase("TOS_ACCEPTANCE");
            assert preparePurchaseResponse.getChallenge().getTos() != null;

            preparePurchaseResponse = testDataProvider.preparePurchase(preparePurchaseResponse.getPurchaseToken(), offerId, paymentId, null,
                    preparePurchaseResponse.getChallenge().getTos().getTosId());
        }
        //verify formatted price
        //validationHelper.verifyPreparePurchase(preparePurchaseResponse);

        if (preparePurchaseResponse.getPurchaseToken() == null || preparePurchaseResponse.getPurchaseToken().isEmpty()) {
            throw new TestException("missing purchase token in prepare purchase response");
        }

    }

    @Property(
            priority = Priority.Dailies,
            features = "Store checkout with invalid PIN",
            component = Component.STORE,
            owner = "ZhaoYunlong",
            status = Status.Enable,
            description = "Test iap offer checkout with invalid PIN",
            steps = {
            }
    )
    @Test
    public void testIAPCheckoutByCreditCardWithInvalidPIN() throws Exception {
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
        PreparePurchaseResponse preparePurchaseResponse = testDataProvider.preparePurchase(null, offerId, paymentId, null, null);
        assert preparePurchaseResponse.getChallenge() == null;
        for (int i = 0; i < 5; i++) {
            testDataProvider.commitPurchase(uid, preparePurchaseResponse.getPurchaseToken(), "5678", 400);
        }

        testDataProvider.commitPurchase(uid, preparePurchaseResponse.getPurchaseToken(), "5678", 400);
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
        Master.getInstance().initializeMaster();
        CreateUserRequest createUserRequest = testDataProvider.CreateUserRequest();
        AuthTokenResponse authTokenResponse = testDataProvider.CreateUser(createUserRequest, false);
        validationHelper.verifyEmailInAuthResponse(authTokenResponse, createUserRequest.getEmail(), false);

        Validator.Validate("validate authtoken response correct", createUserRequest.getUsername(), authTokenResponse.getUsername());

        oAuthClient.postAccessToken(GrantType.CLIENT_CREDENTIALS, ComponentType.SMOKETEST);
        List<String> links = oAuthClient.getEmailVerifyLink(IdConverter.idToHexString(authTokenResponse.getUserId()), createUserRequest.getEmail());
        Validator.Validate("validate email was sent", 1, links.size());

        UserNameCheckResponse userNameCheckResponse = testDataProvider.CheckUserName(createUserRequest.getUsername(), createUserRequest.getEmail());
        Validator.Validate("validate username is not available", false, userNameCheckResponse.getIsAvailable());

        userNameCheckResponse = testDataProvider.CheckUserName(RandomHelper.randomAlphabetic(10), RandomHelper.randomEmail());
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

        AuthTokenResponse newAuthTokenResponse = testDataProvider.SignIn(createUserRequest.getEmail(), createUserRequest.getPassword());
        validationHelper.verifyEmailInAuthResponse(newAuthTokenResponse, createUserRequest.getEmail(), false);
        Validator.Validate("validate token valid", authTokenResponse.getUsername(), newAuthTokenResponse.getUsername());

        // todo:    Add other conditions
    }

    @Property(
            priority = Priority.BVT,
            features = "Store checkout",
            component = Component.STORE,
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
                    "8. Get library",
                    "9. Verify library response",
                    "10. Refresh token",
            }
    )
    @Test
    public void testMakeFreePurchase() throws Exception {
        List<String> initialItems = new ArrayList<>(testDataProvider.getInitialItems("android-initial-app", "offers"));
        CreateUserRequest createUserRequest = testDataProvider.CreateUserRequest();
        AuthTokenResponse authTokenResponse = testDataProvider.CreateUser(createUserRequest, true);
        validationHelper.verifyEmailInAuthResponse(authTokenResponse, createUserRequest.getEmail(), false);
        String userName = authTokenResponse.getUsername();

        AuthTokenResponse signInResponse = testDataProvider.signIn(createUserRequest.getEmail());

        validationHelper.verifySignInResponse(authTokenResponse, signInResponse);
        validationHelper.verifyEmailInAuthResponse(signInResponse, createUserRequest.getEmail(), true);


        LibraryResponse libraryResponse = testDataProvider.getLibrary();

        if (initialItems.size() > 0) {
            for (String item : initialItems) {
                validationHelper.verifyLibraryResponse(libraryResponse, new ItemId(item));
            }
        }
        UserProfileGetResponse userProfileResponse = testDataProvider.getUserProfile();

        validationHelper.verifyUserProfile(userProfileResponse, authTokenResponse);

        String offerId;
        if (offer_iap_free.toLowerCase().contains("test")) {
            offerId = testDataProvider.getOfferIdByName(offer_digital_free);
        } else {
            offerId = offer_digital_free;
            Offer offer = testDataProvider.getOfferByOfferId(offerId);
            OfferRevision offerRevision = testDataProvider.getOfferRevision(offer.getCurrentRevisionId());
            Item item = testDataProvider.getItemByItemId(offerRevision.getItems().get(0).getItemId());
            testDataProvider.getItemRevision(item.getCurrentRevisionId());
        }

        MakeFreePurchaseResponse freePurchaseResponse = testDataProvider.makeFreePurchase(offerId, null);

        //String purchaseToken = IdConverter.idToHexString(freePurchaseResponse.getOrder()); //get order id

        if (freePurchaseResponse.getChallenge() != null) {
            freePurchaseResponse = testDataProvider.makeFreePurchase(offerId, freePurchaseResponse.getChallenge().getTos().getTosId());
        }

        libraryResponse = testDataProvider.getLibrary();
        validationHelper.verifyLibraryResponse(libraryResponse, freePurchaseResponse.getEntitlements().get(0).getItem());

        DeliveryResponse deliveryResponse = testDataProvider.getDeliveryByOfferId(offerId);
        String downloadLink = deliveryResponse.getDownloadUrl();
        /*
        List<DeliveryResponse> deliveryResponseList = testDataProvider.getDeliveryListByOfferId(offerId);
        Assert.assertEquals(deliveryResponseList.size(), 1);
        Assert.assertEquals(deliveryResponse.getDownloadSize(), deliveryResponseList.get(0).getDownloadSize());
        Assert.assertEquals(deliveryResponse.getSignature(), deliveryResponseList.get(0).getSignature());

        Assert.assertNotNull(deliveryResponseList.get(0).getDownloadUrl());*/
        Assert.assertNotNull(downloadLink);

        Master.getInstance().setCurrentUid(null);

        AuthTokenResponse tokenResponse = testDataProvider.getToken(signInResponse.getRefreshToken());

        validationHelper.verifyEmailInAuthResponse(tokenResponse, createUserRequest.getEmail(), true);
        validationHelper.verifySignInResponse(signInResponse, tokenResponse);

    }

    @Property(
            priority = Priority.BVT,
            features = "Store checkout",
            component = Component.STORE,
            owner = "ZhaoYunlong",
            environment = "release",
            status = Status.Enable,
            description = "Test iap offer checkout with multi endpoint",
            steps = {
                    "1. Create user in west dc",
                    "2. Sign in",
                    "3. Get user profile",
                    "4. Verify response",
                    "5. Make free purchase",
                    "6. Verify purchase response",
                    "8. Get library in east dc",
                    "9. Verify library response",
                    "10. Refresh token",
            }
    )
    @Test(groups = "int/ppe/prod/sewer")
    public void testMakeFreePurchaseWithMultiEndpoint() throws Exception {
        try {
            Master.getInstance().initializeMaster();
            if (ConfigHelper.getSetting("secondaryDcEndpoint") == null) return;
            Master.getInstance().setEndPointType(Master.EndPointType.Secondary);
            CreateUserRequest createUserRequest = testDataProvider.CreateUserRequest();
            AuthTokenResponse authTokenResponse = testDataProvider.RegisterUser(createUserRequest, 200);
            testDataProvider.verifyEmailLinks(createUserRequest, authTokenResponse);
            validationHelper.verifyEmailInAuthResponse(authTokenResponse, createUserRequest.getEmail(), false);
            String userName = authTokenResponse.getUsername();
            Master.getInstance().setEndPointType(Master.EndPointType.Primary);
            Thread.sleep(20000);
            AuthTokenResponse signInResponse = testDataProvider.signIn(createUserRequest.getEmail());

            validationHelper.verifySignInResponse(authTokenResponse, signInResponse);
            validationHelper.verifyEmailInAuthResponse(signInResponse, createUserRequest.getEmail(), true);

            UserProfileGetResponse userProfileResponse = testDataProvider.getUserProfile();

            validationHelper.verifyUserProfile(userProfileResponse, authTokenResponse);

            String offerId;
            if (offer_iap_free.toLowerCase().contains("test")) {
                offerId = testDataProvider.getOfferIdByName(offer_digital_free);
            } else {
                offerId = offer_digital_free;
                Offer offer = testDataProvider.getOfferByOfferId(offerId);
                OfferRevision offerRevision = testDataProvider.getOfferRevision(offer.getCurrentRevisionId());
                Item item = testDataProvider.getItemByItemId(offerRevision.getItems().get(0).getItemId());
                testDataProvider.getItemRevision(item.getCurrentRevisionId());
            }

            MakeFreePurchaseResponse freePurchaseResponse = testDataProvider.makeFreePurchase(offerId, null);

            //String purchaseToken = IdConverter.idToHexString(freePurchaseResponse.getOrder()); //get order id
            if (freePurchaseResponse.getChallenge() != null) {
                freePurchaseResponse = testDataProvider.makeFreePurchase(offerId, freePurchaseResponse.getChallenge().getTos().getTosId());
            }
            Master.getInstance().setEndPointType(Master.EndPointType.Secondary);
            Thread.sleep(20000);

            LibraryResponse libraryResponse = testDataProvider.getLibrary();
            validationHelper.verifyLibraryResponse(libraryResponse, freePurchaseResponse.getEntitlements().get(0).getItem());

            Master.getInstance().setCurrentUid(null);

            AuthTokenResponse tokenResponse = testDataProvider.getToken(signInResponse.getRefreshToken());
            validationHelper.verifyEmailInAuthResponse(tokenResponse, createUserRequest.getEmail(), true);
            validationHelper.verifySignInResponse(signInResponse, tokenResponse);
        } catch (Exception ex) {
        } finally {
            Master.getInstance().setEndPointType(Master.EndPointType.Primary);
        }

    }


    @Property(
            priority = Priority.BVT,
            features = "Store checkout",
            component = Component.STORE,
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
                    "9. Get library",
                    "10. Verify library response"
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
        PreparePurchaseResponse preparePurchaseResponse = testDataProvider.preparePurchase(null, offerId, paymentId, null, null, true, 200);

        assert preparePurchaseResponse.getChallenge() == null;
        if (preparePurchaseResponse.getChallenge() != null) {
            assert preparePurchaseResponse.getChallenge().getTos() != null;

            preparePurchaseResponse = testDataProvider.preparePurchase(preparePurchaseResponse.getPurchaseToken(), offerId, paymentId, null,
                    preparePurchaseResponse.getChallenge().getTos().getTosId(), true, 200);
        }

        //verify formatted price
        validationHelper.verifyPreparePurchase(preparePurchaseResponse, true, null);

        String purchaseToken = preparePurchaseResponse.getPurchaseToken(); //get order id

        CommitPurchaseResponse commitPurchaseResponse = testDataProvider.commitPurchase(uid, purchaseToken);
        assert commitPurchaseResponse.getChallenge() != null;
        assert commitPurchaseResponse.getChallenge().getType().equals("PIN");
        commitPurchaseResponse = testDataProvider.commitPurchase(uid, purchaseToken, "1234");
        validationHelper.verifyCommitPurchase(commitPurchaseResponse, offerId);

        LibraryResponse libraryResponse = testDataProvider.getLibrary();
        assert libraryResponse.getItems().size() == 0;
    }

    @Property(
            priority = Priority.Dailies,
            features = "Store checkout",
            component = Component.STORE,
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
        validationHelper.verifyEmailInAuthResponse(authTokenResponse, createUserRequest.getEmail(), false);

        assert authTokenResponse.getUsername().equals(userName);
        assert authTokenResponse.getAccessToken() != null;

        UserNameCheckResponse userNameCheckResponse = testDataProvider.CheckUserName(userName, createUserRequest.getEmail());
        assert !userNameCheckResponse.getIsAvailable();

        authTokenResponse = testDataProvider.SignIn(createUserRequest.getEmail(), password);
        validationHelper.verifyEmailInAuthResponse(authTokenResponse, createUserRequest.getEmail(), true);
        assert authTokenResponse.getUsername().equals(userName);
        assert authTokenResponse.getAccessToken() != null;
    }

    @Property(
            priority = Priority.Dailies,
            features = "Store checkout",
            component = Component.STORE,
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
        testDataProvider.CreateUser(createUserRequest, true);
    }

    @Property(
            priority = Priority.BVT,
            features = "Store checkout",
            component = Component.STORE,
            owner = "ZhaoYunlong",
            status = Status.Enable,
            description = "Test get iap items",
            steps = {
                    "1. test get iap items",
                    "2. Verify response",
            }
    )
    @Test
    public void testGetIapItems() throws Exception {
        CreateUserRequest createUserRequest = testDataProvider.CreateUserRequest();
        AuthTokenResponse authTokenResponse = testDataProvider.CreateUser(createUserRequest, true);
        validationHelper.verifyEmailInAuthResponse(authTokenResponse, createUserRequest.getEmail(), false);

        String offerId = testDataProvider.getOfferIdByName(offer_inApp_consumable);
        List<String> skus = new ArrayList<>();
        skus.add("upgrade_bird");
        IAPItemsResponse iapItemsResponse = testDataProvider.getIapItems(testDataProvider.getIapParam(offerId), skus);

        ValidationHelper.verifyEqual(iapItemsResponse.getItems().get(0).getIapDetails().getSku(), "upgrade_bird", "verify sku");
        ValidationHelper.verifyEqual(iapItemsResponse.getItems().get(0).getTitle(), "testOffer_IAP_Consumable","verify item title");

        ValidationHelper.verifyEqual(iapItemsResponse.getItems().get(1).getIapDetails().getSku(), "upgrade_bird", "verify sku");
        ValidationHelper.verifyEqual(iapItemsResponse.getItems().get(1).getTitle(), "testItem_IAP_Permanent","verify item title");
    }

    @Property(
            priority = Priority.Dailies,
            features = "Store checkout",
            component = Component.STORE,
            owner = "ZhaoYunlong",
            status = Status.Enable,
            description = "Test get iap items with non-existing sku name",
            steps = {
                    "1. test get iap item by invalid name",
                    "2. Verify response",
            }
    )
    @Test
    public void testGetIapItemsByInvalidSku() throws Exception {
        CreateUserRequest createUserRequest = testDataProvider.CreateUserRequest();
        AuthTokenResponse authTokenResponse = testDataProvider.CreateUser(createUserRequest, true);
        validationHelper.verifyEmailInAuthResponse(authTokenResponse, createUserRequest.getEmail(), false);

        String offerId = testDataProvider.getOfferIdByName(offer_inApp_consumable);
        List<String> skus = new ArrayList<>();
        skus.add("10_bird");
        IAPItemsResponse iapItemsResponse = testDataProvider.getIapItems(testDataProvider.getIapParam(offerId), skus);

        ValidationHelper.verifyEqual(iapItemsResponse.getItems().size(), 0, "verify nothing returns");

    }

    @Property(
            priority = Priority.Dailies,
            features = "Store get user Profile",
            component =  Component.STORE,
            owner = "ZhaoYunlong",
            status = Status.Enable,
            description = "Test userProfi pin set/notSet",
            steps = {
                    "1. Create user",
                    "2. No pin is set, check user-profile out",
                    "3. set pin, check user-profile out"
            }
    )
    @Test
    public void testStorePinSetReturn() throws Exception {
        String userName = RandomFactory.getRandomStringOfAlphabet(6);
        CreateUserRequest createUserRequest = testDataProvider.CreateUserRequest(userName);
        createUserRequest.setPin(null);
        AuthTokenResponse authTokenResponse = testDataProvider.CreateUser(createUserRequest, true);
        assert authTokenResponse.getUsername().equals(userName);
        assert authTokenResponse.getAccessToken() != null;

        UserProfileGetResponse getResponse = testDataProvider.getUserProfile();
        assert getResponse != null;
        assert getResponse.getUserProfile() != null;
        assert getResponse.getUserProfile().getPin().equals("");

        createUserRequest = testDataProvider.CreateUserRequest();
        authTokenResponse = testDataProvider.CreateUser(createUserRequest, true);
        assert authTokenResponse.getUsername().equals(createUserRequest.getUsername());
        assert authTokenResponse.getAccessToken() != null;

        getResponse = testDataProvider.getUserProfile();
        assert getResponse != null;
        assert getResponse.getUserProfile() != null;
        assert getResponse.getUserProfile().getPin().equals("****");
    }

    @Property(
            priority = Priority.Dailies,
            features = "Test a user-agent in black list will get 403 forbiden response",
            component =  Component.STORE,
            owner = "ZhaoYunlong",
            status = Status.Enable
    )
    @Test
    public void testClientBlock() throws Exception {
        testDataProvider.CheckUserName(RandomFactory.getRandomStringOfAlphabet(12), RandomFactory.getRandomEmailAddress());
        TestContext.getData().putHeader("user-agent", "client_will_be_blocked");
        testDataProvider.CheckEmailWithError(RandomFactory.getRandomEmailAddress(), 403, "199.003");
    }
}
