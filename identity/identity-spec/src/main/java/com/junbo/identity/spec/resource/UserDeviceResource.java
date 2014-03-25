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

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

/**
 * Created by liangfu on 3/13/14.
 */
@RestResource
@Path("/users/{userId}/devices")
@Produces({MediaType.APPLICATION_JSON})
@Consumes({MediaType.APPLICATION_JSON})
public interface UserDeviceResource {

    @POST
    @Path("/")
    Promise<UserDevice> create(@PathParam("userId") UserId userId,
                               UserDevice userDevice);

    @PUT
    @Path("/{userDeviceId}")
    Promise<UserDevice> put(@PathParam("userId") UserId userId,
                            @PathParam("userDeviceId") UserDeviceId userDeviceId,
                            UserDevice userDevice);

    @POST
    @Path("/{userDeviceId}")
    Promise<UserDevice> patch(@PathParam("userId") UserId userId,
                              @PathParam("userDeviceId") UserDeviceId userDeviceId,
                              UserDevice userDevice);

    @DELETE
    @Path("/{userDeviceId}")
    Promise<Void> delete(@PathParam("userId") UserId userId,
                         @PathParam("userDeviceId") UserDeviceId userDeviceId);

    @GET
    @Path("/{userDeviceId}")
    Promise<UserDevice> get(@PathParam("userId") UserId userId,
                            @PathParam("userDeviceId") UserDeviceId userDeviceId,
                            @BeanParam UserDeviceGetOptions getOptions);

    @GET
    @Path("/")
    Promise<Results<UserDevice>> list(@PathParam("userId") UserId userId,
                                         @BeanParam UserDeviceListOptions listOptions);

}
