/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.test.store.apihelper;

import com.junbo.common.id.OfferId;
import com.junbo.store.spec.model.billing.*;
import com.junbo.store.spec.model.browse.*;
import com.junbo.store.spec.model.iap.*;
import com.junbo.store.spec.model.identity.*;
import com.junbo.store.spec.model.purchase.*;

import java.util.List;

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

    com.junbo.common.error.Error updateUserProfileReturnError(UserProfileUpdateRequest request, int expectedResponseCode, String errorCode) throws Exception;

    BillingProfileGetResponse getBillingProfile(BillingProfileGetRequest request) throws Exception;

    BillingProfileGetResponse getBillingProfile(BillingProfileGetRequest request, int expectedResponseCode) throws Exception;

    InstrumentUpdateResponse updateInstrument(InstrumentUpdateRequest instrumentUpdateRequest) throws Exception;

    InstrumentUpdateResponse updateInstrument(InstrumentUpdateRequest instrumentUpdateRequest, int expectedResponseCode) throws Exception;

    InstrumentDeleteResponse deleteInstrument(InstrumentDeleteRequest instrumentDeleteRequest) throws Exception;

    InstrumentDeleteResponse deleteInstrument(InstrumentDeleteRequest instrumentDeleteRequest, int expectedResponseCode) throws Exception;

    PreparePurchaseResponse preparePurchase(PreparePurchaseRequest preparePurchaseRequest, IAPParam iapParam) throws Exception;

    PreparePurchaseResponse preparePurchase(PreparePurchaseRequest preparePurchaseRequest,  IAPParam iapParam, int expectedResponseCode) throws Exception;

    com.junbo.common.error.Error preparePurchaseWithException(PreparePurchaseRequest preparePurchaseRequest, IAPParam iapParam,
                                                              int expectedResponseCode, String errorCode) throws Exception;

    CommitPurchaseResponse commitPurchase(CommitPurchaseRequest commitPurchaseRequest) throws Exception;

    CommitPurchaseResponse commitPurchase(CommitPurchaseRequest commitPurchaseRequest, int expectedResponseCode) throws Exception;

    IAPConsumeItemResponse iapConsumeEntitlement(IAPConsumeItemRequest iapEntitlementConsumeRequest, IAPParam iapParam) throws Exception;

    IAPConsumeItemResponse iapConsumeEntitlement(IAPConsumeItemRequest iapEntitlementConsumeRequest, IAPParam iapParam, int expectedResponseCode) throws Exception;

    MakeFreePurchaseResponse makeFreePurchase(MakeFreePurchaseRequest request) throws Exception;

    MakeFreePurchaseResponse makeFreePurchase(MakeFreePurchaseRequest request, int expectedResponseCode) throws Exception;

    IAPItemsResponse getIAPItems(IAPParam iapParam) throws Exception;

    IAPItemsResponse getIAPItems(IAPParam iapParam, int expectedResponseCode) throws Exception;

    LibraryResponse getIAPLibrary(IAPParam iapParam) throws Exception;

    LibraryResponse getIAPLibrary(IAPParam iapParam, int expectedResponseCode) throws Exception;

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

    List<DeliveryResponse> getDeliveryList(OfferId offerId, int expectedResponseCode) throws Exception;

    InitialDownloadItemsResponse getInitialDownloadItemsResponse() throws Exception;

}
