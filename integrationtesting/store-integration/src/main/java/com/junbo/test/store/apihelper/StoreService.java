/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.store.apihelper;

import com.junbo.store.spec.model.EntitlementsGetResponse;
import com.junbo.store.spec.model.billing.BillingProfileGetRequest;
import com.junbo.store.spec.model.billing.BillingProfileGetResponse;
import com.junbo.store.spec.model.billing.InstrumentUpdateRequest;
import com.junbo.store.spec.model.billing.InstrumentUpdateResponse;
import com.junbo.store.spec.model.browse.*;
import com.junbo.store.spec.model.iap.*;
import com.junbo.store.spec.model.identity.*;
import com.junbo.store.spec.model.purchase.*;

//import com.junbo.store.spec.model.billing.InstrumentUpdateRequest;
//import com.junbo.store.spec.model.billing.InstrumentUpdateResponse;

/**
 * Created by weiyu_000 on 8/6/14.
 */
public interface StoreService {

    VerifyEmailResponse verifyEmail(VerifyEmailRequest request) throws Exception;

    VerifyEmailResponse verifyEmail(VerifyEmailRequest request, int expectedResponseCode) throws Exception;

    UserProfileGetResponse getUserProfile() throws Exception;

    UserProfileGetResponse getUserProfile(int expectedResponseCode) throws Exception;

    UserProfileUpdateResponse updateUserProfile(UserProfileUpdateRequest request) throws Exception;

    UserProfileUpdateResponse updateUserProfile(UserProfileUpdateRequest request, int expectedResponseCode) throws Exception;

    BillingProfileGetResponse getBillingProfile(BillingProfileGetRequest request) throws Exception;

    BillingProfileGetResponse getBillingProfile(BillingProfileGetRequest request, int expectedResponseCode) throws Exception;

    InstrumentUpdateResponse updateInstrument(InstrumentUpdateRequest instrumentUpdateRequest) throws Exception;

    InstrumentUpdateResponse updateInstrument(InstrumentUpdateRequest instrumentUpdateRequest, int expectedResponseCode) throws Exception;

    PreparePurchaseResponse preparePurchase(PreparePurchaseRequest preparePurchaseRequest) throws Exception;

    PreparePurchaseResponse preparePurchase(PreparePurchaseRequest preparePurchaseRequest, int expectedResponseCode) throws Exception;

    CommitPurchaseResponse commitPurchase(CommitPurchaseRequest commitPurchaseRequest) throws Exception;

    CommitPurchaseResponse commitPurchase(CommitPurchaseRequest commitPurchaseRequest, int expectedResponseCode) throws Exception;

    IAPEntitlementConsumeResponse iapConsumeEntitlement(IAPEntitlementConsumeRequest iapEntitlementConsumeRequest) throws Exception;

    IAPEntitlementConsumeResponse iapConsumeEntitlement(IAPEntitlementConsumeRequest iapEntitlementConsumeRequest, int expectedResponseCode) throws Exception;

    MakeFreePurchaseResponse makeFreePurchase(MakeFreePurchaseRequest request) throws Exception;

    MakeFreePurchaseResponse makeFreePurchase(MakeFreePurchaseRequest request, int expectedResponseCode) throws Exception;

    EntitlementsGetResponse getEntitlement() throws Exception;

    EntitlementsGetResponse getEntitlement(int expectedResponseCode) throws Exception;

    IAPOfferGetResponse getIAPOffers(IAPOfferGetRequest request) throws Exception;

    IAPOfferGetResponse getIAPOffers(IAPOfferGetRequest request, int expectedResponseCode) throws Exception;

    IAPEntitlementGetResponse getIAPEntitlement(IAPEntitlementGetRequest request) throws Exception;

    IAPEntitlementGetResponse getIAPEntitlement(IAPEntitlementGetRequest request, int expectedResponseCode) throws Exception;

    TocResponse getTOC() throws Exception;

    TocResponse getTOC(int expectedResponseCode) throws Exception;

    AcceptTosResponse acceptTos(AcceptTosRequest request) throws Exception;

    AcceptTosResponse acceptTos(AcceptTosRequest request, int expectedResponseCode) throws Exception;

    SectionLayoutResponse getSectionLayout(SectionLayoutRequest request) throws Exception;

    SectionLayoutResponse getSectionLayout(SectionLayoutRequest request, int expectedResponseCode) throws Exception;

    ListResponse getList(ListRequest request) throws Exception;

    ListResponse getList(ListRequest request, int expectedResponseCode) throws Exception;

    LibraryResponse getLibrary() throws Exception;

    LibraryResponse getLibrary(int expectedResponseCode) throws Exception;

    DetailsResponse getDetails(DetailsRequest request) throws Exception;

    DetailsResponse getDetails(DetailsRequest request, int expectedResponseCode) throws Exception;

    ReviewsResponse getReviews(ReviewsRequest request) throws Exception;

    ReviewsResponse getReviews(ReviewsRequest request, int expectedResponseCode) throws Exception;

    AddReviewResponse addReview(AddReviewRequest request) throws Exception;

    AddReviewResponse addReview(AddReviewRequest request, int expectedResponseCode) throws Exception;

    DeliveryResponse getDelivery(DeliveryRequest request) throws Exception;

    DeliveryResponse getDelivery(DeliveryRequest request, int expectedResponseCode) throws Exception;

}
