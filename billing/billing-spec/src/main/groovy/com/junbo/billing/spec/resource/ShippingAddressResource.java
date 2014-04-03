/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.billing.spec.resource;

import com.junbo.billing.spec.model.ShippingAddress;
import com.junbo.common.id.ShippingAddressId;
import com.junbo.common.id.UserId;
import com.junbo.common.model.Results;
import com.junbo.langur.core.RestResource;
import com.junbo.langur.core.promise.Promise;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Created by xmchen on 14-1-26.
 */
@Path("/users/{userId}/ship-to-info")
@Produces({MediaType.APPLICATION_JSON})
@Consumes({MediaType.APPLICATION_JSON})
@RestResource
public interface ShippingAddressResource {
    @POST
    Promise<ShippingAddress> postShippingAddress(@PathParam("userId") UserId userId, ShippingAddress address);

    @GET
    Promise<Results<ShippingAddress>> getShippingAddresses(@PathParam("userId") UserId userId);

    @GET
    @Path("/{addressId}")
    Promise<ShippingAddress> getShippingAddress(@PathParam("userId") UserId userId,
                                                @PathParam("addressId") ShippingAddressId addressId);

    @DELETE
    @Path("/{addressId}")
    Promise<Response> deleteShippingAddress(@PathParam("userId") UserId userId,
                                        @PathParam("addressId") ShippingAddressId addressId);
}
