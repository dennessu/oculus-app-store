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
@Path("/shipping-addresses")
@Produces({MediaType.APPLICATION_JSON})
@Consumes({MediaType.APPLICATION_JSON})
@RestResource
public interface ShippingAddressResource {
    @POST
    Promise<ShippingAddress> postShippingAddress(ShippingAddress address);

    @GET
    Promise<List<ShippingAddress>> getShippingAddresses(@QueryParam("userId") Long userId);

    @GET
    @Path("/{addressId}")
    Promise<ShippingAddress> getShippingAddress(@PathParam("addressId") Long addressId);

    @DELETE
    @Path("/{addressId}")
    Promise<Void> deleteShippingAddress(@PathParam("addressId") Long addressId);
}
