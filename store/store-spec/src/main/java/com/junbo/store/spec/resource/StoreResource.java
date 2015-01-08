/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.store.spec.resource;

import com.junbo.langur.core.RestResource;
import com.junbo.langur.core.promise.Promise;
import com.junbo.langur.core.routing.RouteByAccessToken;
import com.junbo.store.spec.model.billing.BillingProfileGetRequest;
import com.junbo.store.spec.model.billing.BillingProfileGetResponse;
import com.junbo.store.spec.model.billing.InstrumentUpdateRequest;
import com.junbo.store.spec.model.billing.InstrumentUpdateResponse;
import com.junbo.store.spec.model.browse.*;
import com.junbo.store.spec.model.identity.*;
import com.junbo.store.spec.model.purchase.*;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

/**
 * The wrapper api for store front.
 */
@Path("/horizon-api")
@Produces({MediaType.APPLICATION_JSON})
@Consumes({MediaType.APPLICATION_JSON})
@RestResource
public interface StoreResource {

    @POST
    @Path("/resend-confirmation-email")
    @RouteByAccessToken(switchable = true)
    @Consumes()
    // This doesn't require email verification
    Promise<VerifyEmailResponse> verifyEmail();

    @GET
    @Path("/user-profile")
    @RouteByAccessToken(switchable = true)
    @Consumes()
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
    @Consumes()
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
    @Path("/toc")
    @RouteByAccessToken(switchable = true)
    @Consumes()
    Promise<TocResponse> getToc();

    @POST
    @Path("/accept-tos")
    @RouteByAccessToken(switchable = true)
    Promise<AcceptTosResponse> acceptTos(AcceptTosRequest request);

    @GET
    @Path("/initial-download-items")
    @RouteByAccessToken(switchable = true)
    @Consumes()
    Promise<InitialDownloadItemsResponse> getInitialDownloadItems(@QueryParam("initial-download-version") Integer version);

    @GET
    @Path("/section-layout")
    @RouteByAccessToken(switchable = true)
    @Consumes()
    Promise<SectionLayoutResponse> getSectionLayout(@BeanParam SectionLayoutRequest request);

    @GET
    @Path("/section-items")
    @RouteByAccessToken(switchable = true)
    @Consumes()
    Promise<ListResponse> getList(@BeanParam ListRequest request);

    @GET
    @Path("/library")
    @RouteByAccessToken(switchable = true)
    @Consumes()
    Promise<LibraryResponse> getLibrary();

    @GET
    @Path("/item-details")
    @RouteByAccessToken(switchable = true)
    @Consumes()
    Promise<DetailsResponse> getItemDetails(@BeanParam DetailsRequest request);

    @GET
    @Path("/reviews")
    @RouteByAccessToken(switchable = true)
    @Consumes()
    Promise<ReviewsResponse> getReviews(@BeanParam ReviewsRequest request);

    @POST
    @Path("/add-review")
    @RouteByAccessToken(switchable = true)
    Promise<AddReviewResponse> addReview(AddReviewRequest request);

    @GET
    @Path("/generate-download-info")
    @RouteByAccessToken(switchable = true)
    @Consumes()
    Promise<DeliveryResponse> getDelivery(@BeanParam DeliveryRequest request);

    @GET
    @Path("/generate-download-list")
    @RouteByAccessToken(switchable = true)
    @Consumes()
    Promise<List<DeliveryResponse>> getDeliveryFromOffer(@BeanParam DeliveryRequest request);

}
