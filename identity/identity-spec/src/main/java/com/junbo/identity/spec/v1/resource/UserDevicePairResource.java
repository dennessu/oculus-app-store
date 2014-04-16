/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.spec.v1.resource;

import com.junbo.common.id.UserDeviceId;
import com.junbo.common.model.Results;
import com.junbo.identity.spec.v1.model.UserDevice;
import com.junbo.identity.spec.v1.option.list.UserDeviceListOptions;
import com.junbo.identity.spec.v1.option.model.UserDeviceGetOptions;
import com.junbo.langur.core.RestResource;
import com.junbo.langur.core.promise.Promise;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

/**
 * Created by liangfu on 4/3/14.
 */
@Api(value = "user-device-pairs")
@RestResource
@Path("/user-device-pairs")
@Produces({MediaType.APPLICATION_JSON})
@Consumes({MediaType.APPLICATION_JSON})
public interface UserDevicePairResource {
    @ApiOperation("Create one user device pair")
    @POST
    @Path("/")
    Promise<UserDevice> create(UserDevice userDevice);

    @ApiOperation("Get one user device pair")
    @GET
    @Path("/{userDevicePairId}")
    Promise<UserDevice> get(@PathParam("userDevicePairId") UserDeviceId userDeviceId,
                            @BeanParam UserDeviceGetOptions getOptions);

    @ApiOperation("Partial update one user device pair")
    @POST
    @Path("/{userDevicePairId}")
    Promise<UserDevice> patch(@PathParam("userDevicePairId") UserDeviceId userDeviceId,
                              UserDevice userDevice);

    @ApiOperation("Update one user device pair")
    @PUT
    @Path("/{userDevicePairId}")
    Promise<UserDevice> put(@PathParam("userDevicePairId") UserDeviceId userDeviceId,
                            UserDevice userDevice);

    @ApiOperation("Delete one user device pair")
    @DELETE
    @Path("/{userDevicePairId}")
    Promise<Void> delete(@PathParam("userDevicePairId") UserDeviceId userDeviceId);

    @ApiOperation("Search user device pairs")
    @GET
    @Path("/")
    Promise<Results<UserDevice>> list(@BeanParam UserDeviceListOptions listOptions);
}
