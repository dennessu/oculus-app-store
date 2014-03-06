/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.billing.spec.resource;

import com.junbo.billing.spec.model.ShippingAddress;
import com.junbo.langur.core.RestResource;
import com.junbo.langur.core.promise.Promise;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

/**
 * Created by xmchen on 14-1-26.
 */
@Produces({MediaType.APPLICATION_JSON})
@Consumes({MediaType.APPLICATION_JSON})
@RestResource
public interface ShippingAddressResource {
    @POST
    @Path("/users/{userId}/ship-to-info")
    Promise<ShippingAddress> postShippingAddress(@PathParam("userId") Long userId, ShippingAddress address);

    @GET
    @Path("/users/{userId}/ship-to-info")
    Promise<List<ShippingAddress>> getShippingAddresses(@PathParam("userId") Long userId);

    @GET
    @Path("/users/{userId}/ship-to-info/{addressId}")
    Promise<ShippingAddress> getShippingAddress(@PathParam("userId") Long userId, @PathParam("addressId") Long addressId);

    @DELETE
    @Path("/users/{userId}/ship-to-info/{addressId}")
    Promise<Void> deleteShippingAddress(@PathParam("userId") Long userId, @PathParam("addressId") Long addressId);
}
