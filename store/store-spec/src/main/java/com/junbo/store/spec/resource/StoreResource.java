/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.store.spec.resource;

import com.junbo.langur.core.RestResource;
import com.junbo.langur.core.promise.Promise;
import com.junbo.store.spec.model.*;
import com.junbo.store.spec.model.billing.BillingProfileGetRequest;
import com.junbo.store.spec.model.billing.BillingProfileGetResponse;
import com.junbo.store.spec.model.billing.BillingProfileUpdateRequest;
import com.junbo.store.spec.model.billing.BillingProfileUpdateResponse;
import com.junbo.store.spec.model.iap.IAPOfferGetRequest;
import com.junbo.store.spec.model.purchase.*;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

/**
 * The wrapper api for store front.
 */
@Path("/storeapi")
@Produces({MediaType.APPLICATION_JSON})
@Consumes({MediaType.APPLICATION_JSON})
@RestResource
public interface StoreResource {

    @GET
    @Path("/userprofile")
    Promise<UserProfileGetResponse> getUserProfile(@BeanParam UserProfileGetRequest userProfileGetRequest);

    @PUT
    @Path("/userprofile")
    Promise<UserProfileUpdateResponse> updateUserProfile(@BeanParam UserProfileUpdateRequest userProfileUpdateRequest);

    @GET
    @Path("/billingprofile")
    Promise<BillingProfileGetResponse> getBillingProfile(@BeanParam BillingProfileGetRequest billingProfileGetRequest);

    @PUT
    @Path("/billingprofile")
    Promise<BillingProfileUpdateResponse> updateBillingProfile(BillingProfileUpdateRequest billingProfileUpdateRequest);

    @GET
    @Path("/entitlements")
    Promise<EntitlementsGetResponse> getEntitlements(@BeanParam EntitlementsGetRequest entitlementsGetRequest,
                                                     @BeanParam PageParam pageParam);

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
    Promise<CommitPurchaseResponse> iapGetOffers(IAPOfferGetRequest iapOfferGetRequest);

    @POST
    @Path("/iap/consumption")
    Promise<Consumption> iapConsumeEntitlement(Consumption consumption);

}
