/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.store.spec.resource;

import com.junbo.common.id.UserId;
import com.junbo.common.model.Results;
import com.junbo.store.spec.model.*;
import com.junbo.langur.core.RestResource;
import com.junbo.langur.core.promise.Promise;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
/**
 * The wrapper api for IAP (In-App Purchase).
 */
@Path("/iap")
@Produces({MediaType.APPLICATION_JSON})
@Consumes({MediaType.APPLICATION_JSON})
@RestResource
public interface IAPResource {

    @GET
    @Path("/offers")
    Promise<Results<Offer>> getOffers(@QueryParam("packageName") String packageName, @QueryParam("type") String type);

    @GET
    @Path("/entitlements")
    Promise<Results<Entitlement>> getEntitlements(@QueryParam("packageName") String packageName,
                                                  @QueryParam("userId") UserId userId,
                                                  @QueryParam("type") String type,
                                                  @BeanParam PageParam pageParam);

    @POST
    @Path("/purchase")
    Promise<Results<Entitlement>> postPurchase(Purchase purchase);

    @POST
    @Path("/consumption")
    Promise<Consumption> postConsumption(Consumption consumption);

}
