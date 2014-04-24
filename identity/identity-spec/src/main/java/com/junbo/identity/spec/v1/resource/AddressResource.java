/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */

package com.junbo.identity.spec.v1.resource;

import com.junbo.common.id.AddressId;
import com.junbo.identity.spec.v1.model.Address;
import com.junbo.langur.core.RestResource;
import com.junbo.langur.core.promise.Promise;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

/**
 * Created by xmchen on 14-4-15.
 */
@Api(value = "addresses")
@RestResource
@Path("/addresses")
@Produces({MediaType.APPLICATION_JSON})
@Consumes({MediaType.APPLICATION_JSON})
public interface AddressResource {

    @ApiOperation("Create an address for a user")
    @POST
    Promise<Address> create(Address address);

    @ApiOperation("Get an address")
    @GET
    @Path("/{addressId}")
    Promise<Address> get(@PathParam("addressId") AddressId addressId);


}
