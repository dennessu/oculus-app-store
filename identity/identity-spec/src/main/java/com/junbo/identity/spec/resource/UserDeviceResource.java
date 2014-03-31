/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.spec.resource;

import com.junbo.common.id.UserDeviceId;
import com.junbo.common.id.UserId;
import com.junbo.common.model.Results;
import com.junbo.identity.spec.model.users.UserDevice;
import com.junbo.identity.spec.options.entity.UserDeviceGetOptions;
import com.junbo.identity.spec.options.list.UserDeviceListOptions;
import com.junbo.langur.core.RestResource;
import com.junbo.langur.core.promise.Promise;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

/**
 * Created by liangfu on 3/13/14.
 */
@Api("userDevice")
@RestResource
@Path("/users/{userId}/devices")
@Produces({MediaType.APPLICATION_JSON})
@Consumes({MediaType.APPLICATION_JSON})
public interface UserDeviceResource {
    @ApiOperation("Create one new user device")
    @POST
    @Path("/")
    Promise<UserDevice> create(@PathParam("userId") UserId userId,
                               UserDevice userDevice);

    @ApiOperation("Update one existing user device")
    @PUT
    @Path("/{userDeviceId}")
    Promise<UserDevice> put(@PathParam("userId") UserId userId,
                            @PathParam("userDeviceId") UserDeviceId userDeviceId,
                            UserDevice userDevice);

    @ApiOperation("Patch update one existing user device")
    @POST
    @Path("/{userDeviceId}")
    Promise<UserDevice> patch(@PathParam("userId") UserId userId,
                              @PathParam("userDeviceId") UserDeviceId userDeviceId,
                              UserDevice userDevice);

    @ApiOperation("Delete one existing user device")
    @DELETE
    @Path("/{userDeviceId}")
    Promise<Void> delete(@PathParam("userId") UserId userId,
                         @PathParam("userDeviceId") UserDeviceId userDeviceId);

    @ApiOperation("Get one existing user device")
    @GET
    @Path("/{userDeviceId}")
    Promise<UserDevice> get(@PathParam("userId") UserId userId,
                            @PathParam("userDeviceId") UserDeviceId userDeviceId,
                            @BeanParam UserDeviceGetOptions getOptions);

    @ApiOperation("Search one user's devices")
    @GET
    @Path("/")
    Promise<Results<UserDevice>> list(@PathParam("userId") UserId userId,
                                         @BeanParam UserDeviceListOptions listOptions);
}
