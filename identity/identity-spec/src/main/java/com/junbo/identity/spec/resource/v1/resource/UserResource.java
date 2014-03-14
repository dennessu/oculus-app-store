/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.spec.resource.v1.resource;

import com.junbo.common.id.UserId;
import com.junbo.identity.spec.model.common.ResultList;
import com.junbo.identity.spec.model.user.User;
import com.junbo.identity.spec.v1.model.options.UserGetOption;
import com.junbo.langur.core.RestResource;
import com.junbo.langur.core.promise.Promise;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

/**
 * Created by liangfu on 3/13/14.
 */
@RestResource
@Path("/users")
@Produces({MediaType.APPLICATION_JSON})
@Consumes({MediaType.APPLICATION_JSON})
public interface UserResource {
    @POST
    @Path("/")
    Promise<User> create(User user);

    @PUT
    @Path("/{userId}")
    Promise<User> update(@PathParam("userId")UserId userId, User user);

    @POST
    @Path("/{userId}")
    Promise<User> patch(@PathParam("userId")UserId userId, User user);

    @GET
    @Path("/{userId}")
    Promise<User> get(@PathParam("userId")UserId userId);

    @GET
    @Path("/")
    Promise<ResultList<User>> list(@BeanParam UserGetOption getOption);
}
