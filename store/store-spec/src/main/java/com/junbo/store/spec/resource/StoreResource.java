/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.store.spec.resource;

import com.junbo.langur.core.RestResource;
import com.junbo.langur.core.promise.Promise;
import com.junbo.langur.core.routing.RouteByAccessToken;
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
    @RouteByAccessToken(switchable = true)
    // This doesn't require email verification
    Promise<VerifyEmailResponse> verifyEmail(VerifyEmailRequest request);

    @GET
    @Path("/user-profile")
    @RouteByAccessToken(switchable = true)
    // This requires email verification
    Promise<UserProfileGetResponse> getUserProfile();

    @POST
    @Path("/user-profile")
    @RouteByAccessToken(switchable = true)
    // This requires email verification
    Promise<UserProfileUpdateResponse> updateUserProfile(UserProfileUpdateRequest userProfileUpdateRequest);

    @GET
    @Path("/billing-profile")
    @RouteByAccessToken
    // This requires email verification
    Promise<BillingProfileGetResponse> getBillingProfile(@BeanParam BillingProfileGetRequest billingProfileGetRequest);

    @POST
    @Path("/billing-profile/instruments")
    @RouteByAccessToken
    // This requires email verification
    Promise<InstrumentUpdateResponse> updateInstrument(InstrumentUpdateRequest instrumentUpdateRequest);

    @POST
    @Path("/purchase/free")
    @RouteByAccessToken
    // This requires email verification
    Promise<MakeFreePurchaseResponse> makeFreePurchase(MakeFreePurchaseRequest makeFreePurchaseRequest);

    @POST
    @Path("/purchase/prepare")
    @RouteByAccessToken
    // This requires email verification
    Promise<PreparePurchaseResponse> preparePurchase(PreparePurchaseRequest preparePurchaseRequest);

    @POST
    @Path("/purchase/commit")
    @RouteByAccessToken
    // This requires email verification
    Promise<CommitPurchaseResponse> commitPurchase(CommitPurchaseRequest commitPurchaseRequest);

    @GET
    @Path("/iap/offers")
    @RouteByAccessToken(switchable = true)
    // This requires email verification
    Promise<IAPOfferGetResponse> iapGetOffers(@BeanParam IAPOfferGetRequest iapOfferGetRequest);

    @GET
    @Path("/iap/entitlements")
    @RouteByAccessToken(switchable = true)
    // This requires email verification
    Promise<IAPEntitlementGetResponse> iapGetEntitlements(@BeanParam IAPEntitlementGetRequest iapEntitlementGetRequest, @BeanParam PageParam pageParam);

    @POST
    @Path("/iap/consumption")
    @RouteByAccessToken(switchable = true)
    // This requires email verification
    Promise<IAPEntitlementConsumeResponse> iapConsumeEntitlement(IAPEntitlementConsumeRequest iapEntitlementConsumeRequest);

    @GET
    @Path("/toc")
    @RouteByAccessToken(switchable = true)
    Promise<TocResponse> getToc();

    @POST
    @Path("/accept-tos")
    @RouteByAccessToken(switchable = true)
    Promise<AcceptTosResponse> acceptTos(AcceptTosRequest request);

    @GET
    @Path("/section-layout")
    @RouteByAccessToken(switchable = true)
    Promise<SectionLayoutResponse> getSectionLayout(@BeanParam SectionLayoutRequest request);

    @GET
    @Path("/section-list")
    @RouteByAccessToken(switchable = true)
    Promise<ListResponse> getList(@BeanParam ListRequest request);

    @GET
    @Path("/library")
    @RouteByAccessToken(switchable = true)
    Promise<LibraryResponse> getLibrary();

    @GET
    @Path("/details")
    @RouteByAccessToken(switchable = true)
    Promise<DetailsResponse> getDetails(@BeanParam DetailsRequest request);

    @GET
    @Path("/reviews")
    @RouteByAccessToken(switchable = true)
    Promise<ReviewsResponse> getReviews(@BeanParam ReviewsRequest request);

    @POST
    @Path("/add-review")
    @RouteByAccessToken(switchable = true)
    Promise<AddReviewResponse> addReview(AddReviewRequest request);

    @GET
    @Path("/delivery")
    @RouteByAccessToken(switchable = true)
    Promise<DeliveryResponse> getDelivery(@BeanParam DeliveryRequest request);
}
