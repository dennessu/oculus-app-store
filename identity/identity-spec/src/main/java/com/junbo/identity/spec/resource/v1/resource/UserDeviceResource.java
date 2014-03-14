/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.spec.resource.v1.resource;

import com.junbo.common.id.UserDeviceId;
import com.junbo.common.id.UserId;
import com.junbo.identity.spec.model.common.ResultList;
import com.junbo.identity.spec.v1.model.options.UserDeviceGetOption;
import com.junbo.identity.spec.v1.model.users.UserDevice;
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
    Promise<UserDevice> create(@PathParam("userId")UserId userId,
                             UserDevice userDevice);

    @PUT
    @Path("/{userDeviceId}")
    Promise<UserDevice> update(@PathParam("userId")UserId userId,
                               @PathParam("userDeviceId")UserDeviceId userDeviceId,
                               UserDevice userDevice);

    @POST
    @Path("/{userDeviceId}")
    Promise<UserDevice> patch(@PathParam("userId")UserId userId,
                              @PathParam("userDeviceId")UserDeviceId userDeviceId,
                              UserDevice userDevice);

    @GET
    @Path("/{userDeviceId}")
    Promise<UserDevice> get(@PathParam("userId")UserId userId,
                            @PathParam("userDeviceId")UserDeviceId userDeviceId);

    @GET
    @Path("/")
    Promise<ResultList<UserDevice>> list(@PathParam("userId")UserId userId,
                                         @BeanParam UserDeviceGetOption getOption);
    @DELETE
    @Path("/{userDeviceId}")
    Promise<Void> delete(@PathParam("userId")UserId userId,
                         @PathParam("userDeviceId")UserDeviceId userDeviceId);
}
