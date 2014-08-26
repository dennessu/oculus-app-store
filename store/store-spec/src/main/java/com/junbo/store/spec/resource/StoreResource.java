/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.store.spec.resource;

import com.junbo.langur.core.RestResource;
import com.junbo.langur.core.promise.Promise;
import com.junbo.store.spec.model.EntitlementsGetResponse;
import com.junbo.store.spec.model.PageParam;
import com.junbo.store.spec.model.billing.BillingProfileGetRequest;
import com.junbo.store.spec.model.billing.BillingProfileGetResponse;
import com.junbo.store.spec.model.billing.InstrumentUpdateRequest;
import com.junbo.store.spec.model.billing.InstrumentUpdateResponse;
import com.junbo.store.spec.model.browse.*;
import com.junbo.store.spec.model.iap.*;
import com.junbo.store.spec.model.identity.*;
import com.junbo.store.spec.model.purchase.*;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

/**
 * The wrapper api for store front.
 */
@Path("/horizon-api")
@Produces({MediaType.APPLICATION_JSON})
@Consumes({MediaType.APPLICATION_JSON})
@RestResource
public interface StoreResource {

    @POST
    @Path("/verify-email")
    // This doesn't require email verification
    Promise<VerifyEmailResponse> verifyEmail(VerifyEmailRequest request);

    @GET
    @Path("/user-profile")
    // This requires email verification
    Promise<UserProfileGetResponse> getUserProfile();

    @POST
    @Path("/user-profile")
    // This requires email verification
    Promise<UserProfileUpdateResponse> updateUserProfile(UserProfileUpdateRequest userProfileUpdateRequest);

    @GET
    @Path("/billing-profile")
    // This requires email verification
    Promise<BillingProfileGetResponse> getBillingProfile(@BeanParam BillingProfileGetRequest billingProfileGetRequest);

    @POST
    @Path("/billing-profile/instruments")
    // This requires email verification
    Promise<InstrumentUpdateResponse> updateInstrument(InstrumentUpdateRequest instrumentUpdateRequest);

    @GET
    @Path("/entitlements")
    // This requires email verification
    Promise<EntitlementsGetResponse> getEntitlements(@BeanParam PageParam pageParam);

    @POST
    @Path("/purchase/free")
    // This requires email verification
    Promise<MakeFreePurchaseResponse> makeFreePurchase(MakeFreePurchaseRequest makeFreePurchaseRequest);

    @POST
    @Path("/purchase/prepare")
    // This requires email verification
    Promise<PreparePurchaseResponse> preparePurchase(PreparePurchaseRequest preparePurchaseRequest);

    @POST
    @Path("/purchase/commit")
    // This requires email verification
    Promise<CommitPurchaseResponse> commitPurchase(CommitPurchaseRequest commitPurchaseRequest);

    @GET
    @Path("/iap/offers")
    // This requires email verification
    Promise<IAPOfferGetResponse> iapGetOffers(@BeanParam IAPOfferGetRequest iapOfferGetRequest);

    @GET
    @Path("/iap/entitlements")
    // This requires email verification
    Promise<IAPEntitlementGetResponse> iapGetEntitlements(@BeanParam IAPEntitlementGetRequest iapEntitlementGetRequest, @BeanParam PageParam pageParam);

    @POST
    @Path("/iap/consumption")
    // This requires email verification
    Promise<IAPEntitlementConsumeResponse> iapConsumeEntitlement(IAPEntitlementConsumeRequest iapEntitlementConsumeRequest);

    @GET
    @Path("/toc")
    Promise<TocResponse> getToc();

    @POST
    @Path("/accept-tos")
    Promise<AcceptTosResponse> acceptTos(AcceptTosRequest request);

    @GET
    @Path("/section-layout")
    Promise<SectionLayoutResponse> getSectionLayout(@BeanParam SectionLayoutRequest request);

    @GET
    @Path("/section-list")
    Promise<ListResponse> getList(@BeanParam ListRequest request);

    @GET
    @Path("/library")
    Promise<LibraryResponse> getLibrary();

    @GET
    @Path("/details")
    Promise<DetailsResponse> getDetails(@BeanParam DetailsRequest request);

    @GET
    @Path("/reviews")
    Promise<ReviewsResponse> getReviews(@BeanParam ReviewsRequest request);

    @POST
    @Path("/add-review")
    Promise<AddReviewResponse> addReview(AddReviewRequest request);

    @GET
    @Path("/delivery")
    Promise<DeliveryResponse> getDelivery(@BeanParam DeliveryRequest request);
}
