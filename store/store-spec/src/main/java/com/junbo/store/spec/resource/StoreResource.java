/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.store.spec.resource;

import com.junbo.langur.core.AuthorizationNotRequired;
import com.junbo.langur.core.RestResource;
import com.junbo.langur.core.promise.Promise;
import com.junbo.langur.core.routing.RouteBy;
import com.junbo.store.spec.model.*;
import com.junbo.store.spec.model.billing.BillingProfileGetRequest;
import com.junbo.store.spec.model.billing.BillingProfileGetResponse;
import com.junbo.store.spec.model.billing.BillingProfileUpdateRequest;
import com.junbo.store.spec.model.billing.BillingProfileUpdateResponse;
import com.junbo.store.spec.model.iap.IAPEntitlementConsumeRequest;
import com.junbo.store.spec.model.iap.IAPEntitlementConsumeResponse;
import com.junbo.store.spec.model.iap.IAPOfferGetRequest;
import com.junbo.store.spec.model.iap.IAPOfferGetResponse;
import com.junbo.store.spec.model.identity.UserProfileGetRequest;
import com.junbo.store.spec.model.identity.UserProfileGetResponse;
import com.junbo.store.spec.model.identity.UserProfileUpdateRequest;
import com.junbo.store.spec.model.identity.UserProfileUpdateResponse;
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
// This is temporal, remove it after the authorization for this component is done
@AuthorizationNotRequired
public interface StoreResource {

    @GET
    @Path("/userprofile")
    @RouteBy("userProfileGetRequest.getUserId()")
    Promise<UserProfileGetResponse> getUserProfile(@BeanParam UserProfileGetRequest userProfileGetRequest);

    @POST
    @Path("/userprofile")
    @RouteBy("userProfileUpdateRequest.getUserId()")
    Promise<UserProfileUpdateResponse> updateUserProfile(UserProfileUpdateRequest userProfileUpdateRequest);

    @GET
    @Path("/billingprofile")
    @RouteBy("billingProfileGetRequest.getUserId()")
    Promise<BillingProfileGetResponse> getBillingProfile(@BeanParam BillingProfileGetRequest billingProfileGetRequest);

    @POST
    @RouteBy("billingProfileUpdateRequest.getUserId()")
    @Path("/billingprofile")
    Promise<BillingProfileUpdateResponse> updateBillingProfile(BillingProfileUpdateRequest billingProfileUpdateRequest);

    @GET
    @RouteBy("entitlementsGetRequest.getUserId()")
    @Path("/entitlements")
    Promise<EntitlementsGetResponse> getEntitlements(@BeanParam EntitlementsGetRequest entitlementsGetRequest,
                                                     @BeanParam PageParam pageParam);

    @POST
    @RouteBy("selectInstrumentRequest.getUserId()")
    @Path("/purchase/select-instrument")
    Promise<SelectInstrumentResponse> selectInstrumentForPurchase(SelectInstrumentRequest selectInstrumentRequest);

    @POST
    @RouteBy("makeFreePurchaseRequest.getUserId()")
    @Path("/purchase/free")
    Promise<MakeFreePurchaseResponse> makeFreePurchase(MakeFreePurchaseRequest makeFreePurchaseRequest);

    @POST
    @RouteBy("preparePurchaseRequest.getUserId()")
    @Path("/purchase/prepare")
    Promise<PreparePurchaseResponse> preparePurchase(PreparePurchaseRequest preparePurchaseRequest);

    @POST
    @RouteBy("commitPurchaseRequest.getUserId()")
    @Path("/purchase/commit")
    Promise<CommitPurchaseResponse> commitPurchase(CommitPurchaseRequest commitPurchaseRequest);

    @GET
    @Path("/iap/offers")
    Promise<IAPOfferGetResponse> iapGetOffers(@BeanParam IAPOfferGetRequest iapOfferGetRequest);

    @POST
    @RouteBy("iapEntitlementConsumeRequest.getUserId()")
    @Path("/iap/consumption")
    Promise<IAPEntitlementConsumeResponse> iapConsumeEntitlement(IAPEntitlementConsumeRequest iapEntitlementConsumeRequest);

}
