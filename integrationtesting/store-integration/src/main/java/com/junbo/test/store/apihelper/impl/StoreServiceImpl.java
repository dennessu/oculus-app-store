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
import com.junbo.test.common.apihelper.HttpClientBase;
import com.junbo.test.common.blueprint.Master;
import com.junbo.test.common.libs.IdConverter;
import com.junbo.test.store.apihelper.StoreService;
import com.junbo.test.store.apihelper.TestContext;
import com.junbo.test.store.utility.DataGenerator;
import com.ning.http.client.FluentCaseInsensitiveStringsMap;

import java.util.Collections;
import java.util.Map;

//import com.junbo.store.spec.model.billing.InstrumentUpdateRequest;
//import com.junbo.store.spec.model.billing.InstrumentUpdateResponse;

/**
 * Created by weiyu_000 on 8/6/14.
 */
public class StoreServiceImpl extends HttpClientBase implements StoreService {

    private static StoreService instance;

    protected FluentCaseInsensitiveStringsMap getHeader(boolean isServiceScope) {
        FluentCaseInsensitiveStringsMap headers = super.getHeader(isServiceScope);
        headers.put("X-ANDROID-ID", Collections.singletonList(DataGenerator.instance().generateAndroidId()));
        headers.put("Accept-Language", Collections.singletonList("en-US"));
        for (Map.Entry<String, String> entry: TestContext.getData().getHeaders().entrySet()) {
            headers.put(entry.getKey(), Collections.singletonList(entry.getValue()));
        }

        if (currentEndPointType.equals(Master.EndPointType.Secondary)) {
            headers.put("Cache-Control", Collections.singletonList("no-cache"));
        }
        //for further header, we can set dynamic value from properties here
        return headers;
    }
    
    StoreServiceImpl(){
        endPointUrlSuffix = "/horizon-api";
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
        String responseBody = restApiCall(HTTPMethod.POST, getEndPointUrl() + "/verify-email", request, expectedResponseCode);
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
        String responseBody = restApiCall(HTTPMethod.GET, getEndPointUrl() + "/user-profile", expectedResponseCode);
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
        String responseBody = restApiCall(HTTPMethod.POST, getEndPointUrl() + "/user-profile", request, expectedResponseCode);
        if (expectedResponseCode == 200) {
            UserProfileUpdateResponse response = new JsonMessageTranscoder().decode(new TypeReference<UserProfileUpdateResponse>() {
            }, responseBody);

            return response;
        }
        return null;
    }

    @Override
    public com.junbo.common.error.Error updateUserProfileReturnError(UserProfileUpdateRequest request, int expectedResponseCode, String errorCode) throws Exception {
        String responseBody = restApiCall(HTTPMethod.POST, getEndPointUrl() + "/user-profile", request, expectedResponseCode);
        com.junbo.common.error.Error response = new JsonMessageTranscoder().decode(new TypeReference<com.junbo.common.error.Error>() {
        }, responseBody);

        assert response.getCode().equalsIgnoreCase(errorCode);
        return response;
    }

    @Override
    public BillingProfileGetResponse getBillingProfile(BillingProfileGetRequest request) throws Exception {
        return getBillingProfile(request, 200);
    }

    @Override
    public BillingProfileGetResponse getBillingProfile(BillingProfileGetRequest request, int expectedResponseCode) throws Exception {
        String url = getEndPointUrl() + "/billing-profile";
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
        String responseBody = restApiCall(HTTPMethod.POST, getEndPointUrl() + "/billing-profile/instruments", instrumentUpdateRequest, expectedResponseCode);
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
        String responseBody = restApiCall(HTTPMethod.POST, getEndPointUrl() + "/purchase/prepare", preparePurchaseRequest, expectedResponseCode);
        if (expectedResponseCode == 200) {
            PreparePurchaseResponse preparePurchaseResponse = new JsonMessageTranscoder().decode(new TypeReference<PreparePurchaseResponse>() {
            }, responseBody);

            return preparePurchaseResponse;
        }
        return null;
    }

    @Override
    public com.junbo.common.error.Error preparePurchaseWithException(PreparePurchaseRequest preparePurchaseRequest, int expectedResponseCode, String errorCode) throws Exception {
        String responseBody = restApiCall(HTTPMethod.POST, getEndPointUrl() + "/purchase/prepare", preparePurchaseRequest, expectedResponseCode);
        com.junbo.common.error.Error appErrorException = new JsonMessageTranscoder().decode(new TypeReference<com.junbo.common.error.Error>() {
        }, responseBody);
        assert appErrorException.getCode().equalsIgnoreCase(errorCode);
        return appErrorException;
    }

    @Override
    public CommitPurchaseResponse commitPurchase(CommitPurchaseRequest commitPurchaseRequest) throws Exception {
        return commitPurchase(commitPurchaseRequest, 200);
    }

    @Override
    public CommitPurchaseResponse commitPurchase(CommitPurchaseRequest commitPurchaseRequest, int expectedResponseCode) throws Exception {
        String responseBody = restApiCall(HTTPMethod.POST, getEndPointUrl() + "/purchase/commit", commitPurchaseRequest, expectedResponseCode);
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
        String responseBody = restApiCall(HTTPMethod.POST, getEndPointUrl() + "/iap/consumption", request, expectedResponseCode);
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
        String responseBody = restApiCall(HTTPMethod.POST, getEndPointUrl() + "/purchase/free", request, expectedResponseCode);
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
        String responseBody = restApiCall(HTTPMethod.GET, getEndPointUrl() + "/entitlements", expectedResponseCode);
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
        String responseBody = restApiCall(HTTPMethod.GET, getEndPointUrl() + "/iap/offers", expectedResponseCode);
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
        String responseBody = restApiCall(HTTPMethod.GET, getEndPointUrl() + "/iap/entitlements", expectedResponseCode);
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
        String responseBody = restApiCall(HTTPMethod.GET, getEndPointUrl() + "/toc", expectedResponseCode);
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
        String responseBody = restApiCall(HTTPMethod.POST, getEndPointUrl() + "/accept-tos", request, expectedResponseCode);
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
        String url = getEndPointUrl() + "/section-layout?";
        url = appendQuery(url, "category", request.getCategory());
        url = appendQuery(url, "criteria", request.getCriteria());
        url = appendQuery(url, "count", request.getCount());
        String responseBody = restApiCall(HTTPMethod.GET, url, expectedResponseCode);
        if (expectedResponseCode == 200) {
            SectionLayoutResponse response = new JsonMessageTranscoder().decode(new TypeReference<SectionLayoutResponse>() {
            }, responseBody);

            return response;
        }
        return null;
    }

    @Override
    public ListResponse getList(ListRequest request) throws Exception {
        return getList(request, 200);
    }

    @Override
    public ListResponse getList(ListRequest request, int expectedResponseCode) throws Exception {
        //TODO url
        String url = getEndPointUrl() + "/section-list?";
        url = appendQuery(url, "category", request.getCategory());
        url = appendQuery(url, "criteria", request.getCriteria());
        url = appendQuery(url, "count", request.getCount());
        url = appendQuery(url, "cursor", request.getCursor());
        String responseBody = restApiCall(HTTPMethod.GET, url, expectedResponseCode);
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
        String responseBody = restApiCall(HTTPMethod.GET, getEndPointUrl() + "/library", expectedResponseCode);
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
        String responseBody = restApiCall(HTTPMethod.GET, getEndPointUrl() + "/details?itemId=" + request.getItemId().getValue() , expectedResponseCode);
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
        String url = getEndPointUrl() + "/reviews?";
        url = appendQuery(url, "itemId", request.getItemId());
        url = appendQuery(url, "count", request.getCount());
        url = appendQuery(url, "cursor", request.getCursor());

        String responseBody = restApiCall(HTTPMethod.GET, url, expectedResponseCode);
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
        String responseBody = restApiCall(HTTPMethod.POST, getEndPointUrl() + "/add-review", request, expectedResponseCode);
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
        String url = getEndPointUrl() + "/delivery?";
        url = appendQuery(url, "itemId", request.getItemId());
        url = appendQuery(url, "currentVersionCode", request.getCurrentVersionCode());
        url = appendQuery(url, "desiredVersionCode", request.getDesiredVersionCode());
        String responseBody = restApiCall(HTTPMethod.GET, url, expectedResponseCode);
        if (expectedResponseCode == 200) {
            DeliveryResponse response = new JsonMessageTranscoder().decode(new TypeReference<DeliveryResponse>() {
            }, responseBody);

            return response;
        }
        return null;
    }

    private String appendQuery(String url, String name, Object val) {
        if (val != null) {
            return url + "&" + name + "=" + val.toString();
        }
        return url;
    }

}
