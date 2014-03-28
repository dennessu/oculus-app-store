/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (C) 2014 Junbo and/or its affiliates. All rights reserved.
 */
package com.junbo.identity.spec.resource;

import com.junbo.common.id.UserId;
import com.junbo.common.model.Results;
import com.junbo.identity.spec.model.users.User;
import com.junbo.identity.spec.options.entity.UserGetOptions;
import com.junbo.identity.spec.options.list.UserListOptions;
import com.junbo.langur.core.RestResource;
import com.junbo.langur.core.promise.Promise;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

/**
 * Created by liangfu on 3/13/14.
 */
@Api("users")
@RestResource
@Path("/users")
@Produces({MediaType.APPLICATION_JSON})
@Consumes({MediaType.APPLICATION_JSON})
public interface UserResource {
    @ApiOperation("Create a new user")
    @POST
    @Path("/")
    Promise<User> create(User user);

    @ApiOperation("Put a user")
    @PUT
    @Path("/{userId}")
    Promise<User> put(@PathParam("userId") UserId userId, User user);

    @ApiOperation("PATCH user")
    @POST
    @Path("/{userId}")
    Promise<User> patch(@PathParam("userId") UserId userId, User user);

    @ApiOperation("Get a user")
    @GET
    @Path("/{userId}")
    Promise<User> get(@PathParam("userId") UserId userId, @BeanParam UserGetOptions getOptions);

    @ApiOperation("Get or search users")
    @GET
    @Path("/")
    Promise<Results<User>> list(@BeanParam UserListOptions listOptions);
}
