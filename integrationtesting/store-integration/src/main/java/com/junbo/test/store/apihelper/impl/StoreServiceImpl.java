/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.store.apihelper.impl;

import com.junbo.common.json.JsonMessageTranscoder;
import com.junbo.langur.core.client.TypeReference;
import com.junbo.store.spec.model.EntitlementsGetResponse;
import com.junbo.store.spec.model.billing.BillingProfileUpdateRequest;
import com.junbo.store.spec.model.billing.BillingProfileUpdateResponse;
//import com.junbo.store.spec.model.billing.InstrumentUpdateRequest;
//import com.junbo.store.spec.model.billing.InstrumentUpdateResponse;
import com.junbo.store.spec.model.iap.IAPEntitlementConsumeRequest;
import com.junbo.store.spec.model.iap.IAPEntitlementConsumeResponse;
import com.junbo.store.spec.model.identity.UserProfileGetResponse;
import com.junbo.store.spec.model.purchase.*;
import com.junbo.test.common.ConfigHelper;
import com.junbo.test.common.apihelper.HttpClientBase;
import com.junbo.test.store.apihelper.StoreService;

/**
 * Created by weiyu_000 on 8/6/14.
 */
public class StoreServiceImpl extends HttpClientBase implements StoreService {
    private static String storeUrl = ConfigHelper.getSetting("defaultCommerceEndpoint") + "/horizon-api";

    private static StoreService instance;

    public static synchronized StoreService getInstance() {
        if (instance == null) {
            instance = new StoreServiceImpl();
        }
        return instance;
    }

    @Override
    public UserProfileGetResponse getUserProfile() throws Exception {
        return getUserProfile(200);
    }

    @Override
    public UserProfileGetResponse getUserProfile(int expectedResponseCode) throws Exception {
        String responseBody = restApiCall(HTTPMethod.GET, storeUrl + "/user-profile", expectedResponseCode);
        if (expectedResponseCode == 200) {
            UserProfileGetResponse response = new JsonMessageTranscoder().decode(new TypeReference<UserProfileGetResponse>() {
            }, responseBody);

            return response;
        }
        return null;
    }

    @Override
    public BillingProfileUpdateResponse updateBillingProfile(BillingProfileUpdateRequest billingProfileUpdateRequest) throws Exception {
        return updateBillingProfile(billingProfileUpdateRequest, 200);
    }

    @Override
    public BillingProfileUpdateResponse updateBillingProfile(BillingProfileUpdateRequest billingProfileUpdateRequest, int expectedResponseCode) throws Exception {
        String responseBody = restApiCall(HTTPMethod.POST, storeUrl + "/billing-profile", billingProfileUpdateRequest, expectedResponseCode);
        if (expectedResponseCode == 200) {
            BillingProfileUpdateResponse billingProfileUpdateResponse = new JsonMessageTranscoder().decode(new TypeReference<BillingProfileUpdateResponse>() {
            }, responseBody);

            return billingProfileUpdateResponse;
        }
        return null;
    }

    @Override
    public PreparePurchaseResponse preparePurchase(PreparePurchaseRequest preparePurchaseRequest) throws Exception {
        return preparePurchase(preparePurchaseRequest, 200);
    }

    @Override
    public PreparePurchaseResponse preparePurchase(PreparePurchaseRequest preparePurchaseRequest, int expectedResponseCode) throws Exception {
        String responseBody = restApiCall(HTTPMethod.POST, storeUrl + "/purchase/prepare", preparePurchaseRequest, expectedResponseCode);
        if (expectedResponseCode == 200) {
            PreparePurchaseResponse preparePurchaseResponse = new JsonMessageTranscoder().decode(new TypeReference<PreparePurchaseResponse>() {
            }, responseBody);

            return preparePurchaseResponse;
        }
        return null;
    }

    @Override
    public CommitPurchaseResponse commitPurchase(CommitPurchaseRequest commitPurchaseRequest) throws Exception {
        return commitPurchase(commitPurchaseRequest, 200);
    }

    @Override
    public CommitPurchaseResponse commitPurchase(CommitPurchaseRequest commitPurchaseRequest, int expectedResponseCode) throws Exception {
        String responseBody = restApiCall(HTTPMethod.POST, storeUrl + "/purchase/commit", commitPurchaseRequest, expectedResponseCode);
        if (expectedResponseCode == 200) {
            CommitPurchaseResponse commitPurchaseResponse = new JsonMessageTranscoder().decode(new TypeReference<CommitPurchaseResponse>() {
            }, responseBody);

            return commitPurchaseResponse;
        }
        return null;
    }

    @Override
    public IAPEntitlementConsumeResponse iapConsumeEntitlement(IAPEntitlementConsumeRequest request) throws Exception {
        return iapConsumeEntitlement(request, 200);
    }

    @Override
    public IAPEntitlementConsumeResponse iapConsumeEntitlement(IAPEntitlementConsumeRequest request, int expectedResponseCode) throws Exception {
        String responseBody = restApiCall(HTTPMethod.POST, storeUrl + "/iap/consumption", request, expectedResponseCode);
        if (expectedResponseCode == 200) {
            IAPEntitlementConsumeResponse response = new JsonMessageTranscoder().decode(new TypeReference<IAPEntitlementConsumeResponse>() {
            }, responseBody);

            return response;
        }
        return null;
    }

    @Override
    public MakeFreePurchaseResponse makeFreePurchase(MakeFreePurchaseRequest request) throws Exception {
        return makeFreePurchase(request, 200);
    }

    @Override
    public MakeFreePurchaseResponse makeFreePurchase(MakeFreePurchaseRequest request, int expectedResponseCode) throws Exception {
        String responseBody = restApiCall(HTTPMethod.POST, storeUrl + "/purchase/free", request, expectedResponseCode);
        if (expectedResponseCode == 200) {
            MakeFreePurchaseResponse response = new JsonMessageTranscoder().decode(new TypeReference<MakeFreePurchaseResponse>() {
            }, responseBody);

            return response;
        }
        return null;
    }

    @Override
    public EntitlementsGetResponse getEntitlement() throws Exception {
        return getEntitlement(200);
    }

    @Override
    public EntitlementsGetResponse getEntitlement(int expectedResponseCode) throws Exception {
        String responseBody = restApiCall(HTTPMethod.GET, storeUrl + "/entitlements", expectedResponseCode);
        if (expectedResponseCode == 200) {
            EntitlementsGetResponse response = new JsonMessageTranscoder().decode(new TypeReference<EntitlementsGetResponse>() {
            }, responseBody);

            return response;
        }
        return null;
    }
}
