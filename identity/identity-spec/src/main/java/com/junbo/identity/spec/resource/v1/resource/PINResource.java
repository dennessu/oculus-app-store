/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.spec.resource.v1.resource;

import com.junbo.common.id.UserId;
import com.junbo.common.id.UserPinId;
import com.junbo.identity.spec.model.common.ResultList;
import com.junbo.identity.spec.v1.model.options.UserPinGetOption;
import com.junbo.identity.spec.v1.model.users.User;
import com.junbo.identity.spec.v1.model.users.UserPin;
import com.junbo.langur.core.RestResource;
import com.junbo.langur.core.promise.Promise;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

/**
 * Created by liangfu on 3/13/14.
 */
@RestResource
@Path("/users/{userId}/pins")
@Produces({MediaType.APPLICATION_JSON})
@Consumes({MediaType.APPLICATION_JSON})
public interface PINResource {

    @GET
    @Path("/{userPinId}")
    Promise<UserPin> get(@PathParam("userId") UserId userId,
                         @PathParam("userPinId") UserPinId userPinId);

    @GET
    @Path("/")
    Promise<ResultList<UserPin>> list(@PathParam("userId") UserId userId,
                         @BeanParam UserPinGetOption userPinGetOption);

    @POST
    @Path("/")
    Promise<User> post(@PathParam("userId") UserId userId,
                       UserPin userPin);
}
