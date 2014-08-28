/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.store;


import com.junbo.common.id.EntitlementId;
import com.junbo.common.id.PaymentInstrumentId;
import com.junbo.store.spec.model.EntitlementsGetResponse;
import com.junbo.store.spec.model.billing.BillingProfileUpdateResponse;
import com.junbo.store.spec.model.iap.IAPEntitlementConsumeResponse;
import com.junbo.store.spec.model.identity.UserProfileGetResponse;
import com.junbo.store.spec.model.login.AuthTokenResponse;
import com.junbo.store.spec.model.login.UserCredentialChangeResponse;
import com.junbo.store.spec.model.login.UserCredentialCheckResponse;
import com.junbo.store.spec.model.login.UserNameCheckResponse;
import com.junbo.store.spec.model.purchase.CommitPurchaseResponse;
import com.junbo.store.spec.model.purchase.MakeFreePurchaseResponse;
import com.junbo.store.spec.model.purchase.PreparePurchaseResponse;
import com.junbo.test.common.Entities.enums.Country;
import com.junbo.test.common.blueprint.Master;
import com.junbo.test.common.libs.IdConverter;
import com.junbo.test.common.libs.RandomFactory;
import com.junbo.test.common.property.Component;
import com.junbo.test.common.property.Priority;
import com.junbo.test.common.property.Property;
import com.junbo.test.common.property.Status;
import org.testng.annotations.Test;

import java.math.BigDecimal;

/**
 * Created by weiyu_000 on 8/6/14.
 */
public class StoreTesting extends BaseTestClass {

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
        AuthTokenResponse authTokenResponse = testDataProvider.CreateUser();
        String uid = IdConverter.idToHexString(authTokenResponse.getUserId());
        //add new credit card to user

        BillingProfileUpdateResponse billingProfileUpdateResponse = testDataProvider.CreateCreditCard(uid);
        //verify decrypted credit card info
        validationHelper.verifyAddNewCreditCard(billingProfileUpdateResponse);

        //get payment id in billing profile
        PaymentInstrumentId paymentId = billingProfileUpdateResponse.getBillingProfile().getInstruments().get(0).getSelf();

        String offerId = testDataProvider.getOfferIdByName(offer_iap_free);
        //post order without set payment instrument
        PreparePurchaseResponse preparePurchaseResponse = testDataProvider.preparePurchase(null, offerId, null, null);

        assert preparePurchaseResponse.getChallenge() != null;

        preparePurchaseResponse = testDataProvider.preparePurchase(preparePurchaseResponse.getPurchaseToken(),
                offerId, paymentId, "1234");

        //verify formatted price
        validationHelper.verifyPreparePurchase(preparePurchaseResponse);

        String purchaseToken = preparePurchaseResponse.getPurchaseToken(); //get order id

        CommitPurchaseResponse commitPurchaseResponse = testDataProvider.commitPurchase(uid, purchaseToken);
        validationHelper.verifyCommitPurchase(commitPurchaseResponse, offerId);

        EntitlementId entitlementId = commitPurchaseResponse.getEntitlements().get(0).getSelf();
        IAPEntitlementConsumeResponse iapEntitlementConsumeResponse = testDataProvider.iapConsumeEntitlement(entitlementId, offer_iap_normal);

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
        AuthTokenResponse authTokenResponse = testDataProvider.CreateUser();
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

        MakeFreePurchaseResponse freePurchaseResponse = testDataProvider.makeFreePurchase(offerId, Country.DEFAULT);

        //String purchaseToken = IdConverter.idToHexString(freePurchaseResponse.getOrder()); //get order id

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
        AuthTokenResponse authTokenResponse = testDataProvider.CreateUser();
        String uid = IdConverter.idToHexString(authTokenResponse.getUserId());
        //add new credit card to user

        BillingProfileUpdateResponse billingProfileUpdateResponse = testDataProvider.CreateStoredValue(uid);
        testDataProvider.CreditStoredValue(uid, new BigDecimal(100));

        //get payment id in billing profile
        PaymentInstrumentId paymentId = billingProfileUpdateResponse.getBillingProfile().getInstruments().get(0).getSelf();

        String offerId = testDataProvider.getOfferIdByName(offer_iap_normal);
        //post order without set payment instrument
        PreparePurchaseResponse preparePurchaseResponse = testDataProvider.preparePurchase(null, offerId, null, null);

        assert preparePurchaseResponse.getChallenge() != null;

        preparePurchaseResponse = testDataProvider.preparePurchase(preparePurchaseResponse.getPurchaseToken(),
                offerId, paymentId, "1234");

        //verify formatted price
        validationHelper.verifyPreparePurchase(preparePurchaseResponse);

        String purchaseToken = preparePurchaseResponse.getPurchaseToken(); //get order id

        CommitPurchaseResponse commitPurchaseResponse = testDataProvider.commitPurchase(uid, purchaseToken);
        validationHelper.verifyCommitPurchase(commitPurchaseResponse, offerId);

        EntitlementId entitlementId = commitPurchaseResponse.getEntitlements().get(0).getSelf();
        IAPEntitlementConsumeResponse iapEntitlementConsumeResponse = testDataProvider.iapConsumeEntitlement(entitlementId, offer_iap_normal);

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
        AuthTokenResponse authTokenResponse = testDataProvider.CreateUser(userName);

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
        AuthTokenResponse authTokenResponse = testDataProvider.CreateUser(userName);
    }




}
