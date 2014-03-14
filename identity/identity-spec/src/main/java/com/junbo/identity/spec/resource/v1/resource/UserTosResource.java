/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.spec.resource.v1.resource;

import com.junbo.common.id.UserId;
import com.junbo.common.id.UserTosId;
import com.junbo.identity.spec.model.common.ResultList;
import com.junbo.identity.spec.v1.model.options.UserTosGetOption;
import com.junbo.identity.spec.v1.model.users.UserTos;
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
    Promise<UserTos> create(@PathParam("userId")UserId userId,
                          UserTos userTos);

    @PUT
    @Path("/{userTosId}")
    Promise<UserTos> update(@PathParam("userId")UserId userId, @PathParam("userTosId")UserTosId userTosId,
                         UserTos userTos);

    @POST
    @Path("/{userTosId}")
    Promise<UserTos> patch(@PathParam("userId")UserId userId, @PathParam("userTosId")UserTosId userTosId,
                           UserTos userTos);

    @GET
    @Path("/{userTosId}")
    Promise<UserTos> get(@PathParam("userId")UserId userId, @PathParam("userTosId")UserTosId userTosId);

    @DELETE
    @Path("/{userTosId}")
    Promise<Void> delete(@PathParam("userId")UserId userId, @PathParam("userTosId")UserTosId userTosId);

    @GET
    @Path("/")
    Promise<ResultList<UserTos>> list(@PathParam("userId")UserId userId, @BeanParam UserTosGetOption getOption);
}
