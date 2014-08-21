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
    Promise<VerifyEmailResponse> verifyEmail(VerifyEmailRequest request);

    @GET
    @Path("/user-profile")
    Promise<UserProfileGetResponse> getUserProfile();

    @POST
    @Path("/user-profile")
    Promise<UserProfileUpdateResponse> updateUserProfile(UserProfileUpdateRequest userProfileUpdateRequest);

    @GET
    @Path("/billing-profile")
    Promise<BillingProfileGetResponse> getBillingProfile(@BeanParam BillingProfileGetRequest billingProfileGetRequest);

    @POST
    @Path("/billing-profile/instruments")
    Promise<InstrumentUpdateResponse> updateInstrument(InstrumentUpdateRequest instrumentUpdateRequest);


    @GET
    @Path("/entitlements")
    Promise<EntitlementsGetResponse> getEntitlements(@BeanParam PageParam pageParam);

    @POST
    @Path("/purchase/free")
    Promise<MakeFreePurchaseResponse> makeFreePurchase(MakeFreePurchaseRequest makeFreePurchaseRequest);

    @POST
    @Path("/purchase/prepare")
    Promise<PreparePurchaseResponse> preparePurchase(PreparePurchaseRequest preparePurchaseRequest);

    @POST
    @Path("/purchase/commit")
    Promise<CommitPurchaseResponse> commitPurchase(CommitPurchaseRequest commitPurchaseRequest);

    @GET
    @Path("/iap/offers")
    Promise<IAPOfferGetResponse> iapGetOffers(@BeanParam IAPOfferGetRequest iapOfferGetRequest);

    @GET
    @Path("/iap/entitlements")
    Promise<IAPEntitlementGetResponse> iapGetEntitlements(@BeanParam IAPEntitlementGetRequest iapEntitlementGetRequest, @BeanParam PageParam pageParam);

    @POST
    @Path("/iap/consumption")
    Promise<IAPEntitlementConsumeResponse> iapConsumeEntitlement(IAPEntitlementConsumeRequest iapEntitlementConsumeRequest);




    @GET
    @Path("/toc")
    Promise<TocResponse> getToc(@BeanParam TocRequest request);

    @POST
    @Path("/accept-tos")
    Promise<AcceptTosResponse> acceptTos(AcceptTosRequest request);

    @GET
    @Path("/layout")
    Promise<LayoutResponse> getLayout(@BeanParam LayoutRequest request);

    @GET
    @Path("/list")
    Promise<ListResponse> getList(@BeanParam ListRequest request);

    @GET
    @Path("/library")
    Promise<LibraryResponse> getLibrary(@BeanParam LibraryRequest request);

    @GET
    @Path("/details")
    Promise<DetailsResponse> getDetails(@BeanParam DetailsRequest request);

    @GET
    @Path("/reviews")
    Promise<ReviewsResponse> getReviews(@BeanParam ReviewsRequest request);

    @POST
    @Path("/add-review")
    Promise<AddReviewResponse> addReview(AddReviewRequest request);
}
