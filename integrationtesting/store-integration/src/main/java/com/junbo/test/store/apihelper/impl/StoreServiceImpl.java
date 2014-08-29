/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.store.apihelper.impl;

import com.junbo.common.json.JsonMessageTranscoder;
import com.junbo.langur.core.client.TypeReference;
import com.junbo.store.spec.model.EntitlementsGetResponse;
import com.junbo.store.spec.model.billing.BillingProfileGetRequest;
import com.junbo.store.spec.model.billing.BillingProfileGetResponse;
import com.junbo.store.spec.model.billing.InstrumentUpdateRequest;
import com.junbo.store.spec.model.billing.InstrumentUpdateResponse;
import com.junbo.store.spec.model.browse.*;
import com.junbo.store.spec.model.iap.*;
import com.junbo.store.spec.model.identity.*;
import com.junbo.store.spec.model.purchase.*;
import com.junbo.test.common.ConfigHelper;
import com.junbo.test.common.apihelper.HttpClientBase;
import com.junbo.test.common.libs.IdConverter;
import com.junbo.test.store.apihelper.StoreService;
import com.ning.http.client.FluentCaseInsensitiveStringsMap;
import org.apache.commons.lang3.RandomStringUtils;

import java.util.Collections;

//import com.junbo.store.spec.model.billing.InstrumentUpdateRequest;
//import com.junbo.store.spec.model.billing.InstrumentUpdateResponse;

/**
 * Created by weiyu_000 on 8/6/14.
 */
public class StoreServiceImpl extends HttpClientBase implements StoreService {
    private static String storeUrl = ConfigHelper.getSetting("defaultCommerceEndpoint") + "/horizon-api";

    private static StoreService instance;

    protected FluentCaseInsensitiveStringsMap getHeader(boolean isServiceScope) {
        FluentCaseInsensitiveStringsMap headers = super.getHeader(isServiceScope);
        headers.put("X-ANDROID-ID", Collections.singletonList(RandomStringUtils.randomAlphabetic(10)));
        headers.put("Accept-Language", Collections.singletonList("en_US"));
        headers.put("X-MCCMNC", Collections.singletonList("INT_TEST"));

        //for further header, we can set dynamic value from properties here
        return headers;
    }

    public static synchronized StoreService getInstance() {
        if (instance == null) {
            instance = new StoreServiceImpl();
        }
        return instance;
    }

    @Override
    public VerifyEmailResponse verifyEmail(VerifyEmailRequest request) throws Exception {
        return verifyEmail(request, 200);
    }

    @Override
    public VerifyEmailResponse verifyEmail(VerifyEmailRequest request, int expectedResponseCode) throws Exception {
        String responseBody = restApiCall(HTTPMethod.POST, storeUrl + "/verify-email", request, expectedResponseCode);
        if (expectedResponseCode == 200) {
            VerifyEmailResponse response = new JsonMessageTranscoder().decode(new TypeReference<VerifyEmailResponse>() {
            }, responseBody);

            return response;
        }
        return null;
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
    public UserProfileUpdateResponse updateUserProfile(UserProfileUpdateRequest request) throws Exception {
        return updateUserProfile(request, 200);
    }

    @Override
    public UserProfileUpdateResponse updateUserProfile(UserProfileUpdateRequest request, int expectedResponseCode) throws Exception {
        String responseBody = restApiCall(HTTPMethod.POST, storeUrl + "/user-profile", request, expectedResponseCode);
        if (expectedResponseCode == 200) {
            UserProfileUpdateResponse response = new JsonMessageTranscoder().decode(new TypeReference<UserProfileUpdateResponse>() {
            }, responseBody);

            return response;
        }
        return null;
    }

    @Override
    public BillingProfileGetResponse getBillingProfile(BillingProfileGetRequest request) throws Exception {
        return getBillingProfile(request, 200);
    }

    @Override
    public BillingProfileGetResponse getBillingProfile(BillingProfileGetRequest request, int expectedResponseCode) throws Exception {
        String url = storeUrl + "/billing-profile";
        if (request.getOffer() != null) {
            url += String.format("?offerId=%s", IdConverter.idToHexString(request.getOffer()));
        }

        String responseBody = restApiCall(HTTPMethod.GET, url, expectedResponseCode);
        if (expectedResponseCode == 200) {
            BillingProfileGetResponse response = new JsonMessageTranscoder().decode(new TypeReference<BillingProfileGetResponse>() {
            }, responseBody);

            return response;
        }
        return null;
    }

    @Override
    public InstrumentUpdateResponse updateInstrument(InstrumentUpdateRequest instrumentUpdateRequest) throws Exception {
        return updateInstrument(instrumentUpdateRequest, 200);
    }

    @Override
    public InstrumentUpdateResponse updateInstrument(InstrumentUpdateRequest instrumentUpdateRequest, int expectedResponseCode) throws Exception {
        String responseBody = restApiCall(HTTPMethod.POST, storeUrl + "/billing-profile/instruments", instrumentUpdateRequest, expectedResponseCode);
        if (expectedResponseCode == 200) {
            InstrumentUpdateResponse instrumentUpdateResponse = new JsonMessageTranscoder().decode(new TypeReference<InstrumentUpdateResponse>() {
            }, responseBody);

            return instrumentUpdateResponse;
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

    @Override
    public IAPOfferGetResponse getIAPOffers(IAPOfferGetRequest request) throws Exception {
        return getIAPOffers(request, 200);
    }

    @Override
    public IAPOfferGetResponse getIAPOffers(IAPOfferGetRequest request, int expectedResponseCode) throws Exception {
        //TODO url
        String responseBody = restApiCall(HTTPMethod.GET, storeUrl + "/iap/offers", expectedResponseCode);
        if (expectedResponseCode == 200) {
            IAPOfferGetResponse response = new JsonMessageTranscoder().decode(new TypeReference<IAPOfferGetResponse>() {
            }, responseBody);

            return response;
        }
        return null;
    }

    @Override
    public IAPEntitlementGetResponse getIAPEntitlement(IAPEntitlementGetRequest request) throws Exception {
        return getIAPEntitlement(request, 200);
    }

    @Override
    public IAPEntitlementGetResponse getIAPEntitlement(IAPEntitlementGetRequest request, int expectedResponseCode) throws Exception {
        //TODO url
        String responseBody = restApiCall(HTTPMethod.GET, storeUrl + "/iap/entitlements", expectedResponseCode);
        if (expectedResponseCode == 200) {
            IAPEntitlementGetResponse response = new JsonMessageTranscoder().decode(new TypeReference<IAPEntitlementGetResponse>() {
            }, responseBody);

            return response;
        }
        return null;
    }

    @Override
    public TocResponse getTOC() throws Exception {
        return getTOC(200);
    }

    @Override
    public TocResponse getTOC(int expectedResponseCode) throws Exception {
        String responseBody = restApiCall(HTTPMethod.GET, storeUrl + "/toc", expectedResponseCode);
        if (expectedResponseCode == 200) {
            TocResponse response = new JsonMessageTranscoder().decode(new TypeReference<TocResponse>() {
            }, responseBody);

            return response;
        }
        return null;
    }

    @Override
    public AcceptTosResponse acceptTos(AcceptTosRequest request) throws Exception {
        return acceptTos(request, 200);
    }

    @Override
    public AcceptTosResponse acceptTos(AcceptTosRequest request, int expectedResponseCode) throws Exception {
        String responseBody = restApiCall(HTTPMethod.POST, storeUrl + "/accept-tos", request, expectedResponseCode);
        if (expectedResponseCode == 200) {
            AcceptTosResponse response = new JsonMessageTranscoder().decode(new TypeReference<AcceptTosResponse>() {
            }, responseBody);

            return response;
        }
        return null;
    }

    @Override
    public SectionLayoutResponse getSectionLayout(SectionLayoutRequest request) throws Exception {
        return getSectionLayout(request, 200);
    }

    @Override
    public SectionLayoutResponse getSectionLayout(SectionLayoutRequest request, int expectedResponseCode) throws Exception {
        //TODO url
        String responseBody = restApiCall(HTTPMethod.GET, storeUrl + "/section-layout", expectedResponseCode);
        if (expectedResponseCode == 200) {
            SectionLayoutResponse response = new JsonMessageTranscoder().decode(new TypeReference<SectionLayoutResponse>() {
            }, responseBody);

            return response;
        }
        return null;
    }

    @Override
    public ListResponse getList(ListRequest request) throws Exception {
        return null;
    }

    @Override
    public ListResponse getList(ListRequest request, int expectedResponseCode) throws Exception {
        //TODO url
        String responseBody = restApiCall(HTTPMethod.GET, storeUrl + "/section-list", expectedResponseCode);
        if (expectedResponseCode == 200) {
            ListResponse response = new JsonMessageTranscoder().decode(new TypeReference<ListResponse>() {
            }, responseBody);

            return response;
        }
        return null;
    }

    @Override
    public LibraryResponse getLibrary() throws Exception {
        return getLibrary(200);
    }

    @Override
    public LibraryResponse getLibrary(int expectedResponseCode) throws Exception {
        //TODO url
        String responseBody = restApiCall(HTTPMethod.GET, storeUrl + "/library", expectedResponseCode);
        if (expectedResponseCode == 200) {
            LibraryResponse response = new JsonMessageTranscoder().decode(new TypeReference<LibraryResponse>() {
            }, responseBody);

            return response;
        }
        return null;
    }

    @Override
    public DetailsResponse getDetails(DetailsRequest request) throws Exception {
        return getDetails(request, 200);
    }

    @Override
    public DetailsResponse getDetails(DetailsRequest request, int expectedResponseCode) throws Exception {
        //TODO url
        String responseBody = restApiCall(HTTPMethod.GET, storeUrl + "/details", expectedResponseCode);
        if (expectedResponseCode == 200) {
            DetailsResponse response = new JsonMessageTranscoder().decode(new TypeReference<DetailsResponse>() {
            }, responseBody);

            return response;
        }
        return null;
    }

    @Override
    public ReviewsResponse getReviews(ReviewsRequest request) throws Exception {
        return getReviews(request, 200);
    }

    @Override
    public ReviewsResponse getReviews(ReviewsRequest request, int expectedResponseCode) throws Exception {
        //TODO url
        String responseBody = restApiCall(HTTPMethod.GET, storeUrl + "/reviews", expectedResponseCode);
        if (expectedResponseCode == 200) {
            ReviewsResponse response = new JsonMessageTranscoder().decode(new TypeReference<ReviewsResponse>() {
            }, responseBody);

            return response;
        }
        return null;
    }

    @Override
    public AddReviewResponse addReview(AddReviewRequest request) throws Exception {
        return addReview(request, 200);
    }

    @Override
    public AddReviewResponse addReview(AddReviewRequest request, int expectedResponseCode) throws Exception {
        String responseBody = restApiCall(HTTPMethod.POST, storeUrl + "/add-review", request, expectedResponseCode);
        if (expectedResponseCode == 200) {
            AddReviewResponse response = new JsonMessageTranscoder().decode(new TypeReference<AddReviewResponse>() {
            }, responseBody);

            return response;
        }
        return null;
    }

    @Override
    public DeliveryResponse getDelivery(DeliveryRequest request) throws Exception {
        return getDelivery(request, 200);
    }

    @Override
    public DeliveryResponse getDelivery(DeliveryRequest request, int expectedResponseCode) throws Exception {
        //TODO url
        String responseBody = restApiCall(HTTPMethod.GET, storeUrl + "/delivery", expectedResponseCode);
        if (expectedResponseCode == 200) {
            DeliveryResponse response = new JsonMessageTranscoder().decode(new TypeReference<DeliveryResponse>() {
            }, responseBody);

            return response;
        }
        return null;
    }
}
