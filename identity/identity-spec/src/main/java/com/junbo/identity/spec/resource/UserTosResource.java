/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.spec.resource;

import com.junbo.common.id.UserId;
import com.junbo.common.id.UserTosId;
import com.junbo.common.model.Results;
import com.junbo.identity.spec.model.users.UserTos;
import com.junbo.identity.spec.options.entity.UserTosGetOptions;
import com.junbo.identity.spec.options.list.UserTosListOptions;
import com.junbo.langur.core.RestResource;
import com.junbo.langur.core.promise.Promise;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

/**
 * Created by liangfu on 3/13/14.
 */
@RestResource
@Path("/users/{userId}/tos")
@Produces({MediaType.APPLICATION_JSON})
@Consumes({MediaType.APPLICATION_JSON})
public interface UserTosResource {
    @POST
    @Path("/")
    Promise<UserTos> create(@PathParam("userId") UserId userId,
                            UserTos userTos);

    @PUT
    @Path("/{userTosId}")
    Promise<UserTos> put(@PathParam("userId") UserId userId,
                         @PathParam("userTosId") UserTosId userTosId,
                         UserTos userTos);

    @POST
    @Path("/{userTosId}")
    Promise<UserTos> patch(@PathParam("userId") UserId userId,
                           @PathParam("userTosId") UserTosId userTosId,
                           UserTos userTos);

    @DELETE
    @Path("/{userTosId}")
    Promise<Void> delete(@PathParam("userId") UserId userId,
                         @PathParam("userTosId") UserTosId userTosId);

    @GET
    @Path("/{userTosId}")
    Promise<UserTos> get(@PathParam("userId") UserId userId,
                         @PathParam("userTosId") UserTosId userTosId,
                         @BeanParam UserTosGetOptions getOptions);

    @GET
    @Path("/")
    Promise<Results<UserTos>> list(@PathParam("userId") UserId userId,
                                      @BeanParam UserTosListOptions listOptions);
}
